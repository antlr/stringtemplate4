#import "TestDebugEvents.h"

@implementation TestDebugEvents

- (void) testString {
  NSString *templates = [@"t() ::= <<foo>>" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  STGroup.debug = YES;
  DebugST *st = (DebugST *)[group getInstanceOf:@"t"];
  AMutableArray *events = [st events];
  NSString *expected = [@"[EvalExprEvent{self=t(), start=0, stop=2, expr=foo}," stringByAppendingString:@" EvalTemplateEvent{self=t(), start=0, stop=2}]"];
  NSString *result = [events description];
  [self assertEquals:expected arg1:result];
}

- (void) testAttribute {
  NSString *templates = [@"t(x) ::= << <x> >>" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  STGroup.debug = YES;
  DebugST *st = (DebugST *)[group getInstanceOf:@"t"];
  AMutableArray *events = [st events];
  NSString *expected = [[@"[EvalExprEvent{self=t(), start=0, stop=-1, expr=<x>}," stringByAppendingString:@" EvalExprEvent{self=t(), start=0, stop=0, expr= },"] stringByAppendingString:@" EvalTemplateEvent{self=t(), start=0, stop=0}]"];
  NSString *result = [events description];
  [self assertEquals:expected arg1:result];
}

- (void) testTemplateCall {
  NSString *templates = [@"t(x) ::= <<[<u()>]>>\n" stringByAppendingString:@"u() ::= << <x> >>\n"];
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  STGroup.debug = YES;
  DebugST *st = (DebugST *)[group getInstanceOf:@"t"];
  AMutableArray *events = [st events];
  NSString *expected = [[[[[[@"[EvalExprEvent{self=t(), start=0, stop=0, expr=[}," stringByAppendingString:@" EvalExprEvent{self=u(), start=1, stop=0, expr=<x>},"] stringByAppendingString:@" EvalExprEvent{self=u(), start=1, stop=1, expr= },"] stringByAppendingString:@" EvalTemplateEvent{self=u(), start=1, stop=1},"] stringByAppendingString:@" EvalExprEvent{self=t(), start=1, stop=1, expr=<u()>},"] stringByAppendingString:@" EvalExprEvent{self=t(), start=2, stop=2, expr=]},"] stringByAppendingString:@" EvalTemplateEvent{self=t(), start=0, stop=2}]"];
  NSString *result = [events description];
  [self assertEquals:expected arg1:result];
}

@end
