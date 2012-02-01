#import "TestGroupSyntaxErrors.h"

@implementation TestGroupSyntaxErrors

- (void) testMissingImportString {
  NSString *templates = [@"import\n" stringByAppendingString:@"foo() ::= <<>>\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [[@"t.stg 2:0: mismatched input 'foo' expecting STRING" stringByAppendingString:newline] stringByAppendingString:@"t.stg 2:3: required (...)+ loop did not match anything at input '('"] + newline;
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testImportNotString {
  NSString *templates = [@"import Super.stg\n" stringByAppendingString:@"foo() ::= <<>>\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:7: mismatched input 'Super' expecting STRING" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testMissingTemplate {
  NSString *templates = @"foo() ::= \n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 2:0: missing template at '<EOF>'" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testUnclosedTemplate {
  NSString *templates = @"foo() ::= {";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [[@"t.stg 1:11: missing final '}' in {...} anonymous template" stringByAppendingString:newline] stringByAppendingString:@"t.stg 1:10: no viable alternative at input '{'"] + newline;
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testParen {
  NSString *templates = @"foo( ::= << >>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:5: no viable alternative at input '::='" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testNewlineInString {
  NSString *templates = @"foo() ::= \"\nfoo\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:11: \\n in string" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testParen2 {
  NSString *templates = [@"foo) ::= << >>\n" stringByAppendingString:@"bar() ::= <<bar>>\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:0: garbled template definition starting at 'foo'" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testArg {
  NSString *templates = @"foo(a,) ::= << >>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:6: mismatched input ')' expecting ID" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testArg2 {
  NSString *templates = @"foo(a,,) ::= << >>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"[t.stg 1:6: mismatched input ',' expecting ID, " stringByAppendingString:@"t.stg 1:7: mismatched input ')' expecting ID]"];
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testArg3 {
  NSString *templates = @"foo(a b) ::= << >>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:4: no viable alternative at input 'a', t.stg 1:6: garbled template definition starting at 'b']";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testErrorWithinTemplate {
  NSString *templates = @"foo(a) ::= \"<a b>\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:15: 'b' came as a complete surprise to me]";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testMap {
  NSString *templates = @"d ::= []\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:7: missing dictionary entry at ']']";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testMap2 {
  NSString *templates = @"d ::= [\"k\":]\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:11: missing value for key at ']']";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testMap3 {
  NSString *templates = @"d ::= [\"k\":{dfkj}}]\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:17: invalid character '}']";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testUnterminatedString {
  NSString *templates = @"f() ::= \"";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:9: unterminated string, t.stg 1:9: missing template at '<EOF>']";
  NSString *result = [errors.errors description];
  [self assertEquals:expected arg1:result];
}

@end
