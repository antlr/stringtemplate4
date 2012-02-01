#import "TestScopes.h"

@implementation TestScopes

- (void) testSeesEnclosingAttr {
  NSString * templates = [@"t(x,y) ::= \"<u()>\"\n" stringByAppendingString:@"u() ::= \"<x><y>\""];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"t"];
  [st add:@"x" param1:@"x"];
  [st add:@"y" param1:@"y"];
  NSString * result = [st render];
  NSString * expectedError = @"";
  [self assertEquals:expectedError param1:[errors description]];
  NSString * expected = @"xy";
  [self assertEquals:expected param1:result];
}

- (void) testMissingArg {
  NSString * templates = [@"t() ::= \"<u()>\"\n" stringByAppendingString:@"u(z) ::= \"\""];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"t"];
  NSString * result = [st render];
  NSString * expectedError = [@"context [t] 1:1 passed 0 arg(s) to template u with 1 declared arg(s)" stringByAppendingString:newline];
  [self assertEquals:expectedError param1:[errors description]];
}

- (void) testUnknownAttr {
  NSString * templates = @"t() ::= \"<x>\"\n";
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"t"];
  NSString * result = [st render];
  NSString * expectedError = [@"context [t] 1:1 attribute x isn't defined" stringByAppendingString:newline];
  [self assertEquals:expectedError param1:[errors description]];
}

- (void) testArgWithSameNameAsEnclosing {
  NSString * templates = [@"t(x,y) ::= \"<u(x)>\"\n" stringByAppendingString:@"u(y) ::= \"<x><y>\""];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"t"];
  [st add:@"x" param1:@"x"];
  [st add:@"y" param1:@"y"];
  NSString * result = [st render];
  NSString * expectedError = @"";
  [self assertEquals:expectedError param1:[errors description]];
  NSString * expected = @"xx";
  [self assertEquals:expected param1:result];
  [group setListener:ErrorManager.DEFAULT_ERROR_LISTENER];
}

- (void) testIndexAttrVisibleLocallyOnly {
  NSString * templates = [@"t(names) ::= \"<names:{n | <u(n)>}>\"\n" stringByAppendingString:@"u(x) ::= \"<i>:<x>\""];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"t"];
  [st add:@"names" param1:@"Ter"];
  NSString * result = [st render];
  [[group getInstanceOf:@"u"].impl dump];
  NSString * expectedError = [@"t.stg 2:11: attribute i isn't defined" stringByAppendingString:newline];
  [self assertEquals:expectedError param1:[errors description]];
  NSString * expected = @":Ter";
  [self assertEquals:expected param1:result];
  [group setListener:ErrorManager.DEFAULT_ERROR_LISTENER];
}

@end
