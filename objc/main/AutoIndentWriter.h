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

/**
 * Essentially a char filter that knows how to auto-indent output
 * by maintaining a stack of indent levels.
 * 
 * The indent stack is a stack of strings so we can repeat original indent
 * not just the same number of columns (don't have to worry about tabs vs
 * spaces then).
 * 
 * Anchors are char positions (tabs won't work) that indicate where all
 * future wraps should justify to.  The wrap position is actually the
 * larger of either the last anchor or the indentation level.
 * 
 * This is a filter on a Writer.
 * 
 * \n is the proper way to say newline for options and templates.
 * Templates can mix \r\n and \n them but use \n for sure in options like
 * wrap="\n". ST will generate the right thing. Override the default (locale)
 * newline by passing in a string to the constructor.
 */
@class Writer;
#import <ANTLR/ANTLR.h>
@class AMutableArray;
#import "Writer.h"

@interface AutoIndentWriter : Writer {
    /**
     * stack of indents; use List as it's much faster than Stack. Grows
     * from 0..n-1.
     */
    AMutableArray *indents;
    
    /**
     * Stack of integer anchors (char positions in line); avoid Integer
     * creation overhead.
     */
    IntArray *anchors;
    NSInteger anchors_sp;
    
    /**
     * \n or \r\n?
     */
    NSString *newline;

    Writer *writer;
    BOOL atStartOfLine;
    
    /**
     * Track char position in the line (later we can think about tabs).
     * Indexed from 0.  We want to keep charPosition <= lineWidth.
     * This is the position we are *about* to write not the position
     * last written to.
     */
    NSInteger charPosition;
    
    /**
     * The absolute char index into the output of the next char to be written.
     */
    NSInteger charIndex;
    NSInteger lineWidth;
    
}

+ (id) newWriter;
+ (id) newWriter:(Writer *)aWriter;
+ (id) newWriter:(Writer *)aWriter newLine:(NSString *)aStr;

- (id) initWithCapacity:(NSInteger)sz;
- (id) init:(Writer *)aWriter newline:(NSString *)newline;
- (id) copyWithZone:(NSZone *)aZone;
- (void) dealloc;
- (NSInteger) index;
- (void) pushAnchorPoint;
- (NSInteger) popAnchorPoint;
- (void) pushIndentation:(NSString *)indent;
- (NSString *) popIndentation;
- (NSInteger) writeStr:(NSString *)str;
- (NSInteger) writeSeparator:(NSString *)str;
- (NSInteger) write:(NSString *)str wrap:(NSString *)wrap;
- (NSInteger) writeWrap:(NSString *)wrap;
- (NSInteger) indent;

@property (retain) AMutableArray *indents;
@property (retain) IntArray *anchors;
@property (assign) NSInteger anchors_sp;
@property (retain) NSString *newline;
@property (assign) BOOL atStartOfLine;
@property (assign) NSInteger charPosition;
@property (assign) NSInteger charIndex;
@property (assign) NSInteger lineWidth;
@property (retain) Writer *writer;
@end
