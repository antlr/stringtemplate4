#import "ErrorBuffer.h"
#import "STMessage.h"

@interface ErrorBufferAllErrors : ErrorBuffer {
}

- (void) runTimeError:(STMessage *)msg;
@end
