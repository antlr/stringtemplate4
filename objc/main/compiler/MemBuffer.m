//
//  MemBuffer.m
//
//  Created by Alan Condit on 6/9/10.
// [The "BSD licence"]
// Copyright (c) 2010 Alan Condit
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products
//    derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#define SUCCESS (0)
#define FAILURE (-1)

#import <ANTLR/ANTLR.h>
#import "MemBuffer.h"
#import "Bytecode.h"
/*
 * Start of MemBuffer
 */
@implementation MemBuffer

@synthesize BuffSize;
@synthesize buffer;
@synthesize ptrBuffer;
@synthesize count;
@synthesize ptr;

+(MemBuffer *)newMemBuffer
{
    return [[MemBuffer alloc] init];
}

+(MemBuffer *)newMemBufferWithLen:(NSInteger)cnt
{
    return [[MemBuffer alloc] initWithLen:cnt];
}

-(id)init
{
    NSInteger idx;
    
	self=[super init];
	if ( self != nil) {
        BuffSize  = BUFFSIZE;
        ptr = 0;
        buffer = [[NSMutableData dataWithLength:(NSUInteger)BuffSize] retain];
        ptrBuffer = (char *)[buffer mutableBytes];
        for( idx = 0; idx < BuffSize; idx++ ) {
            ptrBuffer[idx] = 0;
        }
	}
    return( self );
}

-(id)initWithLen:(NSInteger)cnt
{
    NSInteger idx;
    
	self=[super init];
	if ( self != nil) {
        BuffSize  = cnt;
        ptr = 0;
        buffer = [[NSMutableData dataWithLength:(NSUInteger)BuffSize] retain];
        ptrBuffer = (char *)[buffer mutableBytes];
        for( idx = 0; idx < BuffSize; idx++ ) {
            ptrBuffer[idx] = 0;
        }
	}
    return( self );
}

-(void)dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in MemBuffer" );
#endif
    if ( buffer ) [buffer release];
	[super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    MemBuffer *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    if ( buffer )
        copy.buffer = [buffer copyWithZone:aZone];
    copy.BuffSize = BuffSize;
    copy.ptrBuffer = [copy.buffer mutableBytes];
    copy.count = count;
    copy.ptr = ptr;
    return copy;
}

- (void)clear
{
    NSInteger idx;

    ptrBuffer = [buffer mutableBytes];
    for( idx = 0; idx < BuffSize; idx++ ) {
        ptrBuffer[idx] = 0;
    }
}

- (void) addChar:(char) v
{
	[self ensureCapacity:ptr];
	ptrBuffer[ptr++] = v;
    count++;
}

- (void) push:(char) v
{
    if ( ptr >= BuffSize - 1 ) {
        [self ensureCapacity:ptr];
    }
    ptrBuffer[ptr++] = v;
    count++;
}

- (char) pop
{
	char v = 0;
    if ( ptr > 0 ) {
        v = ptrBuffer[--ptr];
        ptrBuffer[ptr] = 0;
    }
    count--;
	return v;
}

- (char) peek
{
	char v = 0;
    if ( ptr > 0 ) {
        v = ptrBuffer[ptr-1];
    }
	return v;
}

- (NSInteger)count
{
    return count;
}

- (NSInteger)length
{
    return BuffSize;
}

- (NSInteger)size
{
    return BuffSize;
}

- (void) insertChar:(short)aChar atIndex:(NSInteger)idx
{
    if ( idx >= BuffSize ) {
        [self ensureCapacity:idx];
    }
    if (idx >= count) count = idx+1;
    ptrBuffer[idx] = (char)(aChar & 0xff);
}

- (char) charAtIndex:(NSInteger)idx
{
    if ( idx < BuffSize ) {
        return ptrBuffer[idx];
    }
    return 0;
}

- (void)addCharsFromArray:(MemBuffer *)anArray
{
    NSInteger cnt, i;
    cnt = [anArray count];
    for( i = 0; i < cnt; i++) {
        char tmp = [anArray charAtIndex:i];
        [self insertChar:tmp atIndex:i];
    }
    return;
}

- (void)removeAllChars
{
    int i;
    ptrBuffer = [buffer mutableBytes];
    for ( i = 0; i < BuffSize; i++ ) {
        ptrBuffer[i] = 0;
    }
    count = 0;
    ptr = 0;
}

- (short) shortAtIndex:(NSInteger)idx
{
    if ( idx >= BuffSize-1 )
        return -1;
    NSInteger b1 = ptrBuffer[idx] & 0xFF;
    NSInteger b2 = ptrBuffer[idx + 1] & 0xFF;
    NSInteger word = ((b1 << 8) | b2);
    return word;
}

/**
 * Write value at index into a byte array highest to lowest byte,
 * left to right.
 */
- (void) insertShort:(short)value atIndex:(NSInteger)idx
{
    if ( idx >= BuffSize )
        [self ensureCapacity:idx+1];
    ptrBuffer[idx] = (char)((value >> 8) & 0xFF);
    ptrBuffer[idx + 1] = (char)(value & 0xFF);
    if (idx >= count) count = idx+2;
}

- (void) ensureCapacity:(NSInteger) index
{
	if ( (index * sizeof(char)) >= [buffer length] )
	{
		NSInteger newSize = [buffer length] * 2;
		if (index > newSize) {
			newSize = index + 1;
		}
        BuffSize = newSize;
		[buffer setLength:BuffSize];
        ptrBuffer = [buffer mutableBytes];
	}
}

- (NSString *) description
{
    NSInteger i;
    NSMutableString *str;
    str = [NSMutableString stringWithCapacity:100];
    [str appendString:@"\n" ];
    for ( i = 0; i < 48;  ) {
        [str appendFormat:@"%02x%02x ", ptrBuffer[i+1], ptrBuffer[i]];
        i += 2;
        if ( !(i%16) ) [str appendString:@"\n" ];
    }
    [str appendString:@"Compiled Instructions -- use BytecodeDisassembler to view\n"];
    return str;
}

- (void) memcpy:(NSInteger)src dest:(NSInteger)dest length:(NSInteger)len
{
    NSInteger i;
    ptrBuffer = [buffer mutableBytes];
    if ( src+len < BuffSize && dest+len < BuffSize ) {
        for (i = 0; i < len; i++ ) {
            *(ptrBuffer+dest+i) = *(ptrBuffer+src+i);
        }
    }
}

@end
