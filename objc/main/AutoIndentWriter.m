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
#import <Cocoa/Cocoa.h>
#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "AutoIndentWriter.h"
#import "Writer.h"
#import "ST.h"

@implementation AutoIndentWriter

+ (id) newWriter
{
    return [[AutoIndentWriter alloc] init];
}

+ (id) newWriter:(Writer *)aWriter
{
    return [[AutoIndentWriter alloc] init:aWriter newline:@"\n"];
}

+ (id) newWriter:(Writer *)aWriter newLine:(NSString *)aStr
{
    return [[AutoIndentWriter alloc] init:aWriter newline:aStr];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
    }
    return self;
}

- (id) init:(Writer *)aWriter newline:(NSString *)aNewline
{
    self=[super initWithWriter:aWriter];
    if ( self != nil ) {
        writer = aWriter;
        if ( writer ) [writer retain];
        if ( newline != aNewline ) {
            if ( newline ) [newline release];
            if ( aNewline ) [aNewline retain];
            newline = aNewline;
        }
    }
    return self;
}

- (id) initWithWriter:(Writer *)aWriter
{
    self=[super initWithWriter:aWriter];
    if ( self != nil ) {
        writer = aWriter;
        if ( writer ) [writer retain];
    }
    return self;
}

- (id) initWithCapacity:(NSInteger)sz
{
    self=[super initWithCapacity:(NSUInteger)sz];
    if ( self != nil ) {
        writer = nil;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in AutoIndentWriter" );
#endif
    [super dealloc];
}

/**
 * Write out a string literal or attribute expression or expression element.
 */
- (NSInteger) writeStr:(NSString *)str
{
    NSInteger n = 0;
    
    for (NSInteger i = 0; i < [str length]; i++) {
        unichar c = [str characterAtIndex:i];
        if (c == '\r')
            continue;
        if (c == '\n') {
            atStartOfLine = YES;
            charPosition = -1;
            if ( writer != nil )
                [writer writeStr:newline];
            else
                [super writeStr:newline];
            n += [newline length];
            charIndex += [newline length];
            charPosition += n;
            continue;
        }
        if (atStartOfLine) {
            n += [self indent];
            atStartOfLine = NO;
        }
        n++;
        [self write:c];
        charPosition++;
        charIndex++;
    }
    return n;
}

@end
