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
#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "AutoIndentWriter.h"
#import "Writer.h"
#import "ST.h"
#import "Misc.h"

@implementation AutoIndentWriter
@synthesize indents;
@synthesize anchors;
@synthesize anchors_sp;
@synthesize newline;
@synthesize atStartOfLine;
@synthesize charPosition;
@synthesize charIndex;
@synthesize lineWidth;
@synthesize writer;

+ (id) newWriter
{
    return [[AutoIndentWriter alloc] initWithCapacity:1024];
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
    self = [super init];
    if (self != nil) {
        writer = self;
    }
    [Misc setNewline:@"\n"];
    return self;
}

- (id) init:(Writer *)aWriter newline:(NSString *)aNewline
{
    self=[super init];
    if ( self != nil ) {
        indents = [[AMutableArray arrayWithCapacity:5] retain];
        anchors = [[IntArray newArrayWithLen:5] retain];
        anchors_sp = -1;
        atStartOfLine = YES;
        charPosition = 0;
        charIndex = 0;
        lineWidth = ST.NO_WRAP;
        [indents addObject:@""];
        newline = aNewline;
        [newline retain];
        [Misc setNewline:newline];
        ip = 0;
        if (aWriter == nil) {
            writer = self;
        }
        else {
            writer = aWriter;
/*
            self.capacity = writer.capacity;
            self.data = writer.data;
            self.ptr = writer.ptr;
            self.ip = writer.ip;
 */
        }
        if ( writer != self) [writer retain];
        if ( newline != aNewline ) {
            if ( newline ) [newline release];
            if ( aNewline ) [aNewline retain];
            newline = aNewline;
        }
    }
    return self;
}

- (id) initWithCapacity:(NSInteger)sz
{
    self=[super initWithCapacity:(NSUInteger)sz];
    if ( self != nil ) {
        indents = [[AMutableArray arrayWithCapacity:5] retain];
        anchors = [[IntArray newArrayWithLen:5] retain];
        anchors_sp = -1;
        atStartOfLine = YES;
        charPosition = 0;
        charIndex = 0;
        lineWidth = ST.NO_WRAP;
        [indents addObject:@""];
        newline = @"\n";
        [newline retain];
        [Misc setNewline:newline];
        writer = self;
        ip = 0;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in AutoIndentWriter" );
#endif
    if ( anchors ) [anchors release];
    if ( data )    [data release];
    if ( indents ) [indents release];
    if ( newline ) [newline release];
    if ( writer ) [writer release];
    [super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    AutoIndentWriter *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    copy.capacity = capacity;
    if ( data ) {
        copy.data = [data copyWithZone:aZone];
    }
    copy.ptr = [copy.data mutableBytes];
    copy.ip = ip;
    copy.lock = lock;
    copy.indents = indents;
    copy.anchors = anchors;
    copy.anchors_sp = anchors_sp;
    copy.newline = newline;
    copy.writer = writer;
    copy.atStartOfLine = atStartOfLine;
    copy.charPosition = charPosition;
    copy.charIndex = charIndex;
    copy.lineWidth = lineWidth;
    return copy;
}

- (NSInteger) index
{
    return charIndex;
}

- (void) pushIndentation:(NSString *)anIndent
{
    [indents addObject:anIndent];
    //    NSLog( @"pushIndentation of \"%@\"\n", anIndent );
}

- (NSString *) popIndentation
{
    NSString *ret = [indents objectAtIndex:[indents count]-1];
    //    NSLog( @"popIndentation of \"%@\"\n", ret );
    [indents removeLastObject];
    return ret;
}

- (void) pushAnchorPoint
{
    anchors_sp++;
    [anchors push:charPosition];
}

- (NSInteger) popAnchorPoint
{
    anchors_sp--;
    return [anchors pop];
}

/**
 * Write out a string literal or attribute expression or expression element.
 * 
 * If doing line wrap, then check wrap before emitting this str.  If
 * at or beyond desired line width then emit a \n and any indentation
 * before spitting out this str.
 */
- (NSInteger) write:(NSString *)str wrap:(NSString *)wrap
{
    NSInteger n = [self writeWrap:wrap];
    return n + [self writeStr:str];
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
            for (NSInteger j=0; j < [newline length]; j++ )
                 [writer write:[newline characterAtIndex:j]];
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
        [writer write:c];
        charPosition++;
        charIndex++;
    }
    return n;
}

- (NSInteger) writeSeparator:(NSString *)str
{
    NSInteger len = [str length];
    [self writeStr:str];
    return len;
}


- (NSInteger) writeWrap:(NSString *)wrap
{
    NSInteger n = 0;
    NSInteger nll = [newline length];
    NSInteger sl = [self length];
    for (NSInteger i = 0; i < sl; i++) {
        unichar c = [wrap characterAtIndex:i];
        if (c == '\n') {
            atStartOfLine = YES;
            n += [newline length];
            charPosition = -nll;
            [writer writeStr:newline];
            n += nll;
            charIndex += nll;
            n += n;
            continue;
        }
        // normal character
        // check to see if we are at the start of a line; need indent if so
        if ( atStartOfLine ) {
            n += [self indent];
            [writer write:c];
            atStartOfLine = NO;
        }
        n++;
        charPosition++;
        charIndex++;
    }
    return n;
}

- (NSInteger) indent
{
    NSInteger n = 0;
    
    //    for (NSString *ind in indents) {
    NSString *ind;
    ArrayIterator *it = [ArrayIterator newIterator:indents];
    while ( [it hasNext] ) {
        ind = (NSString *)[it nextObject];
        if (ind != nil) {
            n += [ind length];
            for ( int i = 0; i < [ind length]; i++ )
                [writer write:[ind characterAtIndex:i]];
        }
    }
    
    NSInteger indentWidth = n;
    if (anchors_sp >= 0 && [anchors integerAtIndex:anchors_sp] > indentWidth) {
        NSInteger remainder = [anchors integerAtIndex:anchors_sp] - indentWidth;
        
        for (NSInteger i = 1; i <= remainder; i++)
            [writer write:' '];
        
        n += remainder;
    }
    charPosition += n;
    charIndex += n;
    return n;
}

@end
