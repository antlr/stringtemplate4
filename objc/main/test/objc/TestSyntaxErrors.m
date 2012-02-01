#import "TestSyntaxErrors.h"

@implementation TestSyntaxErrors

- (void) testEmptyExpr {
  NSString *template = @" <> ";
  STGroup *group = [[[STGroup alloc] init] autorelease];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  [group setListener:errors];

  @try {
    [group defineTemplate:@"test" arg1:template];
  }
  @catch (STException *se) {
    NSAssert(NO, @"assert failed");
  }
  NSString *result = [errors toString];
  NSString *expected = [@"test 1:0: this doesn't look like a template: \" <> \"" stringByAppendingString:newline];
  [self assertEquals:expected arg1:result];
}

- (void) testEmptyExpr2 {
  NSString *template = @"hi <> ";
  STGroup *group = [[[STGroup alloc] init] autorelease];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  [group setListener:errors];

  @try {
    [group defineTemplate:@"test" arg1:template];
  }
  @catch (STException *se) {
    NSAssert(NO, @"assert failed");
  }
  NSString *result = [errors toString];
  NSString *expected = [@"test 1:3: doesn't look like an expression" stringByAppendingString:newline];
  [self assertEquals:expected arg1:result];
}

- (void) testUnterminatedExpr {
  NSString *template = @"hi <t()$";
  STGroup *group = [[[STGroup alloc] init] autorelease];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  [group setListener:errors];

  @try {
    [group defineTemplate:@"test" arg1:template];
  }
  @catch (STException *se) {
    NSAssert(NO, @"assert failed");
  }
  NSString *result = [errors toString];
  NSString *expected = [[[@"test 1:7: invalid character '$'" stringByAppendingString:newline] stringByAppendingString:@"test 1:7: invalid character '<EOF>'"] + newline stringByAppendingString:@"test 1:7: premature EOF"] + newline;
  [self assertEquals:expected arg1:result];
}

- (void) testWeirdChar {
  NSString *template = @"   <*>";
  STGroup *group = [[[STGroup alloc] init] autorelease];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  [group setListener:errors];

  @try {
    [group defineTemplate:@"test" arg1:template];
  }
  @catch (STException *se) {
    NSAssert(NO, @"assert failed");
  }
  NSString *result = [errors toString];
  NSString *expected = [[@"test 1:4: invalid character '*'" stringByAppendingString:newline] stringByAppendingString:@"test 1:0: this doesn't look like a template: \"   <*>\""] + newline;
  [self assertEquals:expected arg1:result];
}

- (void) testWeirdChar2 {
  NSString *template = @"\n<\\\n";
  STGroup *group = [[[STGroup alloc] init] autorelease];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  [group setListener:errors];

  @try {
    [group defineTemplate:@"test" arg1:template];
  }
  @catch (STException *se) {
    NSAssert(NO, @"assert failed");
  }
  NSString *result = [errors toString];
  NSString *expected = [@"test 1:2: invalid escaped char: '<EOF>'\n" stringByAppendingString:@"test 1:2: expecting '>', found '<EOF>'"] + newline;
  [self assertEquals:expected arg1:result];
}

- (void) testValidButOutOfPlaceChar {
  NSString *templates = @"foo() ::= <<hi <.> mom>>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  STGroupFile *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:15: doesn't look like an expression" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testValidButOutOfPlaceCharOnDifferentLine {
  NSString *templates = [@"foo() ::= \"hi <\n" stringByAppendingString:@".> mom\"\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroupFile *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[t.stg 1:15: \\n in string, t.stg 1:14: doesn't look like an expression]";
  NSString *result = [errors.errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testErrorInNestedTemplate {
  NSString *templates = @"foo() ::= \"hi <name:{[<aaa.bb!>]}> mom\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:29: '!' came as a complete surprise to me" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testEOFInExpr {
  NSString *templates = @"foo() ::= \"hi <name\"";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:19: premature EOF" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testEOFInExpr2 {
  NSString *templates = @"foo() ::= \"hi <name:{x|[<aaa.bb>]}\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:34: premature EOF" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testEOFInString {
  NSString *templates = @"foo() ::= << <f(\"foo>>\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [[@"t.stg 1:20: EOF in string" stringByAppendingString:newline] stringByAppendingString:@"t.stg 1:20: premature EOF"] + newline;
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testNonterminatedComment {
  NSString *templates = @"foo() ::= << <!foo> >>";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:20: Nonterminated comment starting at 1:1: '!>' missing" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testMissingRPAREN {
  NSString *templates = @"foo() ::= \"hi <foo(>\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:19: '>' came as a complete surprise to me" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

- (void) testRotPar {
  NSString *templates = @"foo() ::= \"<a,b:t(),u()>\"\n";
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroupFile *group = nil;
  id<STErrorListener> errors = [ErrorBuffer newErrorBuffer];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = [@"t.stg 1:19: mismatched input ',' expecting RDELIM" stringByAppendingString:newline];
  NSString *result = [errors toString];
  [self assertEquals:expected arg1:result];
}

@end
