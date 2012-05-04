#import "TestGroupSyntaxErrors.h"

@implementation TestGroupSyntaxErrors

- (void) test01MissingImportString
{
    NSString *templates = @"import\nfoo() ::= <<>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 2:0: mismatched input 'foo' expecting STRING\nt.stg 2:3: required (...)+ loop did not match anything at input '('\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test02ImportNotString
{
    NSString *templates = @"import Super.stg\nfoo() ::= <<>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:7: mismatched input 'Super' expecting STRING\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test03MissingTemplate
{
    NSString *templates = @"foo() ::= \n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 2:0: missing template at '<EOF>'\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test04UnclosedTemplate
{
    NSString *templates = @"foo() ::= {";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:11: missing final '}' in {...} anonymous template\nt.stg 1:10: no viable alternative at input '{'\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test05Paren
{
    NSString *templates = @"foo( ::= << >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:5: no viable alternative at input '::='\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test06NewlineInString
{
    NSString *templates = @"foo() ::= \"\nfoo\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:11: \\n in string\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test07Paren2
{
    NSString *templates = @"foo) ::= << >>\nbar() ::= <<bar>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:0: garbled template definition starting at 'foo'\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test08Arg
{
    NSString *templates = @"foo(a,) ::= << >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:6: mismatched input ')' expecting ID\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test09Arg2
{
    NSString *templates = @"foo(a,,) ::= << >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:6: mismatched input ',' expecting ID, t.stg 1:7: mismatched input ')' expecting ID]";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test10Arg3
{
    NSString *templates = @"foo(a b) ::= << >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:6: no viable alternative at input 'b']";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test11ErrorWithinTemplate
{
    NSString *templates = @"foo(a) ::= \"<a b>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:15: 'b' came as a complete surprise to me]";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test12Map
{
    NSString *templates = @"d ::= []\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:7: missing dictionary entry at ']']";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test13Map2
{
    NSString *templates = @"d ::= [\"k\":]\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:11: missing value for key at ']']";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test14Map3
{
    NSString *templates = @"d ::= [\"k\":{dfkj}}]\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:17: invalid character '}']";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test15UnterminatedString
{
    NSString *templates = @"f() ::= \"";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:9: unterminated string, t.stg 1:9: missing template at '<EOF>']";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

@end
