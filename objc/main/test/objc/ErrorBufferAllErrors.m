#import "ErrorBufferAllErrors.h"

@implementation ErrorBufferAllErrors

+ (id) newErrorBuffer
{
    return [[ErrorBufferAllErrors alloc] init];
}

- (id) init
{
    self = [super init];
    return self;
}

- (void) runTimeError:(STMessage *)msg {
  [errors addObject:msg];
}

@end
