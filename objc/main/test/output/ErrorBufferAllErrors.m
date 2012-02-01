#import "ErrorBufferAllErrors.h"

@implementation ErrorBufferAllErrors

- (void) runTimeError:(STMessage *)msg {
  [errors add:msg];
}

@end
