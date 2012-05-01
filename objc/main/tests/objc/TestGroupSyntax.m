#import "TestGroupSyntax.h"

@implementation TestGroupSyntax

- (void) testSimpleGroup
{
    NSString *templates = @"t() ::= <<foo>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t() ::= <<\nfoo\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testMultiTemplates
{
    NSString *templates = @"ta(x) ::= \"[<x>]\"\nduh() ::= <<hi there>>\nwow() ::= <<last>>\\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"ta(x) ::= <<\n[<x>]\n>>\nduh() ::= <<\nhi there\n>>\nwow() ::= <<\nlast\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testSingleTemplateWithArgs
{
    NSString *templates = @"t(a,b) ::= \"[<a>]\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t(a,b) ::= <<\n[<a>]\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testDefaultValues
{
    NSString *templates = @"t(a={def1},b=\"def2\") ::= \"[<a>]\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t(a={def1},b=\"def2\") ::= <<\n[<a>]\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testDefaultValueTemplateWithArg
{
    NSString *templates = @"t(a={x | 2*<x>}) ::= \"[<a>]\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t(a={x | 2*<x>}) ::= <<\n[<a>]\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testNestedTemplateInGroupFile
{
    NSString *templates = @"t(a) ::= \"<a:{x | <x:{y | <y>}>}>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t(a) ::= <<\n<a:{x | <x:{y | <y>}>}>\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testNestedDefaultValueTemplate
{
    NSString *templates = @"t(a={x | <x:{y|<y>}>}) ::= \"ick\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group load];
    NSString *expected = @"t(a={x | <x:{y|<y>}>}) ::= <<\nick\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testNestedDefaultValueTemplateWithEscapes
{
    NSString *templates = @"t(a={x | \\< <x:{y|<y>\\}}>}) ::= \"[<a>]\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    NSString *expected = @"t(a={x | \\< <x:{y|<y>\\}}>}) ::= <<\n[<a>]\n>>\n";
    NSString *result = [group show];
    [self assertEquals:expected result:result];
}

- (void) testMessedUpTemplateDoesntCauseRuntimeError
{
    NSString *templates = @"main(p) ::= <<\n<f(x=\"abc\")>\n>>\n\nf() ::= <<\n<x>\n>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroupFile *group = nil;
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    ST *st = [group getInstanceOf:@"main"];
    [st render];
    NSString *expected = @"[context [/main] 1:1 passed 1 arg(s) to template /f with 0 declared arg(s), context [/main] 1:1 attribute x isn't defined, context [/main /f] 1:1 attribute x isn't defined]";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
    }
    
    @end
