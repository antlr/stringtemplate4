//
//  StringWriter.h
//  ST4
//
//  Created by Alan Condit on 1/26/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import "Writer.h"

@interface StringWriter : Writer {

}

+ (id) newWriter;
+ (id) stringWithCapacity:(NSUInteger)aLen;

- (id) initWithCapacity:(NSUInteger)aLen;
- (id) initWithWriter:(Writer *)aWriter;
- (void) write:(NSInteger) c;
- (void) write:(NSData *)cbuf offset:(NSInteger) off len:(NSInteger) len;
- (void) writeStr:(NSString *)str;
- (NSString *)description;
- (NSString *) toString;

@end
