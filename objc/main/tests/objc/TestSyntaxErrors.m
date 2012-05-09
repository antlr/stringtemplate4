#import "TestSyntaxErrors.h"

@implementation TestSyntaxErrors

- (void) test01EmptyExpr
{
    NSString *template = @" <> ";
    STGroup *group = [[STGroup newSTGroup] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    
    @try {
        [group defineTemplate:@"test" template:template];
    }
    @catch (STException *se) {
        NSAssert(NO, @"assert failed");
    }
    NSString *result = [errors description];
    NSString *expected = @"test 1:0: this doesn't look like a template: \" <> \"\n";
    [self assertEquals:expected result:result];
}

- (void) test02EmptyExpr2
{
    NSString *template = @"hi <> ";
    STGroup *group = [[STGroup newSTGroup] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    
    @try {
        [group defineTemplate:@"test" template:template];
    }
    @catch (STException *se) {
        NSAssert(NO, @"assert failed");
    }
    NSString *result = [errors description];
    NSString *expected = @"test 1:3: doesn't look like an expression\n";
    [self assertEquals:expected result:result];
}

- (void) test03UnterminatedExpr
{
    NSString *template = @"hi <t()$";
    STGroup *group = [[STGroup newSTGroup] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    
    @try {
        [group defineTemplate:@"test" template:template];
    }
    @catch (STException *se) {
        NSAssert(NO, @"assert failed");
    }
    NSString *result = [errors description];
    NSString *expected = @"test 1:7: invalid character '$'\ntest 1:7: invalid character '<EOF>'\ntest 1:7: premature EOF\n";
    [self assertEquals:expected result:result];
}

- (void) test04WeirdChar
{
    NSString *template = @"   <*>";
    STGroup *group = [[STGroup newSTGroup] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    
    @try {
        [group defineTemplate:@"test" template:template];
    }
    @catch (STException *se) {
        NSAssert(NO, @"assert failed");
    }
    NSString *result = [errors description];
    NSString *expected = @"test 1:4: invalid character '*'\ntest 1:0: this doesn't look like a template: \"   <*>\"\n";
    [self assertEquals:expected result:result];
}

- (void) test05WeirdChar2
{
    NSString *template = @"\n<\\\n";
    STGroup *group = [[STGroup newSTGroup] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    
    @try {
        [group defineTemplate:@"test" template:template];
    }
    @catch (STException *se) {
        NSAssert(NO, @"assert failed");
    }
    NSString *result = [errors description];
    NSString *expected = @"test 1:2: invalid escaped char: '<EOF>'\ntest 1:2: expecting '>', found '<EOF>'\n";
    [self assertEquals:expected result:result];
}

- (void) test06ValidButOutOfPlaceChar
{
    NSString *templates = @"foo() ::= <<hi <.> mom>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:15: doesn't look like an expression\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test07ValidButOutOfPlaceCharOnDifferentLine
{
    NSString *templates = @"foo() ::= \"hi <\n.> mom\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[t.stg 1:15: \\n in string, t.stg 1:14: doesn't look like an expression]";
    NSString *result = [errors.errors description];
    [self assertEquals:expected result:result];
}

- (void) test08ErrorInNestedTemplate
{
    NSString *templates = @"foo() ::= \"hi <name:{[<aaa.bb!>]}> mom\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:29: '!' came as a complete surprise to me\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test09EOFInExpr
{
    NSString *templates = @"foo() ::= \"hi <name\"";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:19: premature EOF\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test10EOFInExpr2
{
    NSString *templates = @"foo() ::= \"hi <name:{x|[<aaa.bb>]}\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:34: premature EOF\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test11EOFInString
{
    NSString *templates = @"foo() ::= << <f(\"foo>>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroupFile *group = nil;
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:20: EOF in string\nt.stg 1:20: premature EOF\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test12NonterminatedComment
{
    NSString *templates = @"foo() ::= << <!foo> >>";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroupFile *group = nil;
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:20: Nonterminated comment starting at 1:1: '!>' missing\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test13MissingRPAREN
{
    NSString *templates = @"foo() ::= \"hi <foo(>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroupFile *group = nil;
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:19: '>' came as a complete surprise to me\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

- (void) test14RotPar
{
    NSString *templates = @"foo() ::= \"<a,b:t(),u()>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroupFile *group = nil;
    id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
    group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat: @"%@/t.stg", tmpdir]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = @"t.stg 1:19: mismatched input ',' expecting RDELIM\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
}

@end
