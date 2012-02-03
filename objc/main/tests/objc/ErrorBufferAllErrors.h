#import "ErrorBuffer.h"
#import "STMessage.h"

@interface ErrorBufferAllErrors : ErrorBuffer {
}

+ (id) newErrorBuffer;

- (id) init;
- (void) runTimeError:(STMessage *)msg;
@end
