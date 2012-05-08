/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#import <ANTLR/ANTLR.h>
#import "StringWriter.h"


@implementation StringWriter

+ (id) newWriter
{
    return [[StringWriter alloc] initWithCapacity:16];
}

+ (id) stringWithCapacity:(NSUInteger)aLen
{
    return [[StringWriter alloc] initWithCapacity:aLen];
}

- (id) initWithCapacity:(NSUInteger)aLen
{
    self=[super initWithCapacity:aLen];
    return self;
}

- (void) append:(NSInteger)c
{
    [self write:c];
}

/**
 * Write a single character.
 */
- (void) write:(NSInteger) c
{
    char c1[8] = { c, '\0' };
    [data appendBytes:c1 length:1];
    ptr = [data mutableBytes];
    ip++;
}

/**
 * Write a portion of an array of characters.
 *
 * @param  cbuf  Array of characters
 * @param  off   Offset from which to start writing characters
 * @param  len   Number of characters to write
 */
- (void) write:(NSData *)cbuf offset:(NSInteger) off len:(NSInteger) len
{
    if ((off < 0) || (off > cbuf.length) || (len < 0) ||
        ((off + len) > [cbuf length]) || ((off + len) < 0)) {
        @throw [NSException exceptionWithName:@"IndexOutOfBounds" reason:nil userInfo:nil]; //IndexOutOfBoundsException;
    } else if (len == 0) {
        return;
    }
    [data appendBytes:[cbuf bytes] length:len];
}

/**
 * Write a string.
 */
- (void) writeStr:(NSString *)str
{
    [data appendBytes:[[str dataUsingEncoding:NSASCIIStringEncoding] bytes] length:[str length]];
}

- (NSString *)description
{
    NSMutableString *oStr;
    
    oStr = [[NSMutableString alloc] initWithData:data encoding:NSASCIIStringEncoding];
    return oStr;
}

@end
