#import "TestInterptimeErrors.h"

@implementation UserHiddenName

- (id) initWithName:(NSString *)name {
  if ( (self=[super init]) != nil ) {
    name = name;
  }
  return self;
}

- (NSString *) getName {
  return name;
}

- (void) dealloc {
  [name release];
  [super dealloc];
}

@end

@implementation UserHiddenNameField

- (id) initWithName:(NSString *)name {
  if ( (self=[super init]) != nil ) {
    name = name;
  }
  return self;
}

- (void) dealloc {
  [name release];
  [super dealloc];
}

@end

@implementation TestInterptimeErrors

- (void) testMissingEmbeddedTemplate {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t() ::= \"<foo()>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  [st render];
  NSString *expected = [@"context [t] 1:0 no such template: foo" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testMissingSuperTemplate {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t() ::= \"<super.t()>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  NSString *templates2 = [@"u() ::= \"blech\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t2.stg" arg2:templates2];
  STGroup *group2 = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t2.stg"]] autorelease];
  [group importTemplates:group2];
  ST *st = [group getInstanceOf:@"t"];
  [st render];
  NSString *expected = [@"context [t] 1:1 no such template: super.t" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testNoPropertyNotError {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t(u) ::= \"<u.x>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  [st add:@"u" arg1:[[[User alloc] init:32 arg1:@"parrt"] autorelease]];
  [st render];
  NSString *expected = @"";
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testHiddenPropertyNotError {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t(u) ::= \"<u.name>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  [st add:@"u" arg1:[[[UserHiddenName alloc] init:@"parrt"] autorelease]];
  [st render];
  NSString *expected = @"";
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testHiddenFieldNotError {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t(u) ::= \"<u.name>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  [st add:@"u" arg1:[[[UserHiddenNameField alloc] init:@"parrt"] autorelease]];
  [st render];
  NSString *expected = @"";
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testSoleArg {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t() ::= \"<u({9})>\"\n" stringByAppendingString:@"u(x,y) ::= \"<x>\"\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  [st render];
  NSString *expected = [@"context [t] 1:1 passed 1 arg(s) to template u with 2 declared arg(s)" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testSoleArgUsingApplySyntax {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t() ::= \"<{9}:u()>\"\n" stringByAppendingString:@"u(x,y) ::= \"<x>\"\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST *st = [group getInstanceOf:@"t"];
  NSString *expected = @"9";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
  expected = [@"context [t] 1:5 passed 1 arg(s) to template u with 2 declared arg(s)" stringByAppendingString:newline];
  result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testUndefinedAttr {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  NSString *templates = [@"t() ::= \"<u()>\"\n" stringByAppendingString:@"u() ::= \"<x>\"\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  STGroup.debug = YES;
  ST *st = [group getInstanceOf:@"t"];
  [st render];
  NSString *expected = [@"context [t u] 1:1 attribute x isn't defined" stringByAppendingString:newline];
  NSString *result = [errors description];
  [self assertEquals:expected arg1:result];
}

- (void) testParallelAttributeIterationWithMissingArgs {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group setListener:errors];
  ST *e = [[[ST alloc] init:group arg1:@"<names,phones,salaries:{n,p | <n>@<p>}; separator=\", \">"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"names" arg1:@"Tom"];
  [e add:@"phones" arg1:@"1"];
  [e add:@"phones" arg1:@"2"];
  [e add:@"salaries" arg1:@"big"];
  [e render];
  NSString *errorExpecting = [[@"1:23: anonymous template has 2 arg(s) but mapped across 3 value(s)\n" stringByAppendingString:@"context [anonymous] 1:23 passed 3 arg(s) to template _sub1 with 2 declared arg(s)\n"] stringByAppendingString:@"context [anonymous] 1:1 iterating through 3 values in zip map but template has 2 declared arguments\n"];
  [self assertEquals:errorExpecting arg1:[errors description]];
  NSString *expecting = @"Ter@1, Tom@2";
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testStringTypeMismatch {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group setListener:errors];
  ST *e = [[[ST alloc] init:group arg1:@"<trim(s)>"] autorelease];
  [e add:@"s" arg1:34];
  [e render];
  NSString *errorExpecting = [@"context [anonymous] 1:1 function trim expects a string not java.lang.Integer" stringByAppendingString:newline];
  [self assertEquals:errorExpecting arg1:[errors description]];
}

- (void) testStringTypeMismatch2 {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group setListener:errors];
  ST *e = [[[ST alloc] init:group arg1:@"<strlen(s)>"] autorelease];
  [e add:@"s" arg1:34];
  [e render];
  NSString *errorExpecting = [@"context [anonymous] 1:1 function strlen expects a string not java.lang.Integer" stringByAppendingString:newline];
  [self assertEquals:errorExpecting arg1:[errors description]];
}

@end
