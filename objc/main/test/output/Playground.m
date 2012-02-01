#import "Playground.h"

@implementation Playground

+ (void) main:(NSArray *)args {
  ErrorBufferAllErrors * errors = [[[ErrorBufferAllErrors alloc] init] autorelease];
  STGroup * g = [[[STGroupFile alloc] init:@"/tmp/g.stg"] autorelease];
  [g setListener:errors];
  ST * t = [g getInstanceOf:@"u"];
  if (t != nil)
    [System.out println:[t render]];
  [System.err println:[@"errors: " stringByAppendingString:errors]];
}

@end
