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

@end
