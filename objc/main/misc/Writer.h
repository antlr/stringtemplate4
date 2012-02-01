/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Alan Condit
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
#import <ANTLR/ANTLR.h>
#import "STWriter.h"

@interface Writer : NSMutableString<STWriter> {
    NSInteger capacity;
    NSMutableData *data;
    __strong char *ptr;
    NSInteger ip;
    id lock;

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

+ (NSInteger) NO_WRAP;
+ (id) newWriter;
+ (id) newWriterWithWriter:(id)aWriter;
+ (id) newWriterWithCapacity:(NSUInteger)len;
+ (id) stringWithCapacity:(NSUInteger)len;

- (id) init;
- (id) initWithCapacity:(NSUInteger)aLen;
- (id) initWithWriter:(Writer *)aWriter;  // just for initializing subclasses
- (void) dealloc;
- (id) copyWithZone:(NSZone *)aZone;
- (NSUInteger) count;
- (NSUInteger) length;
- (unichar) characterAtIndex:(NSUInteger)index;
- (void) appendString:(NSString *)aString;
- (void) write:(NSInteger)aChar;
- (NSInteger) writeStr:(NSString *)str;
//- (void) writeChunk:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len;
- (void) replaceCharactersInRange:(NSRange)range withString:(NSString *)aString;
- (void) ensureCapacity:(NSInteger)len;
- (void) close;
- (NSString *) description;
- (NSString *) toString;

- (void) pushIndentation:(NSString *)indent;
- (NSString *) popIndentation;
- (void) pushAnchorPoint;
- (NSInteger) popAnchorPoint;
- (NSInteger) index;
- (NSInteger) writeSeparator:(NSString *)str;
- (NSInteger) write:(NSString *)str wrap:(NSString *)wrap;
- (NSInteger) writeWrap:(NSString *)wrap;
- (NSInteger) indent;
- (void) print:(id)msg;
- (void) println:(id)msg;


@property (assign) NSInteger capacity;
@property (retain) NSMutableData *data;
@property (assign) char *ptr;
@property (assign) NSInteger ip;
@property (retain) id lock;

@property (retain) AMutableArray *indents;
@property (retain) IntArray *anchors;
@property (assign) NSInteger anchors_sp;
@property (retain) NSString *newline;
@property (retain) Writer *writer;
@property (assign) BOOL atStartOfLine;
@property (assign) NSInteger charPosition;
@property (assign) NSInteger charIndex;
@property (assign, setter=setLineWidth:) NSInteger lineWidth;


@end

@interface BufferedWriter : Writer {
}

+ (id) newWriter;
+ (id) newWriter:(NSInteger)len;
+ (id) newWriterWithWriter:(Writer *)op;

- (id) initWithCapacity:(NSInteger)sz;
- (id) initWithWriter:(Writer *)op;
- (void) close;
- (void) flush;
//- (void) newline;
//- (void) write:(char)c;
//- (void) writeChunk:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len;
//- (void) writeStr:(NSString *)str offset:(NSInteger)off len:(NSInteger)len;

@end

@interface OutputStreamWriter : Writer {

}

+ (id) newWriter:(id)writer;
+ (id) newWriter:(id)writer charSet:(NSCharacterSet *)charSet;
+ (id) newWriter:(id)writer encoding:(NSStringEncoding)encoding;
+ (id) newWriter:(id)writer charSetName:(NSString *)charSetName;

- (id) init:(id)writer charSet:(NSCharacterSet *)charSet encoding:(NSStringEncoding)encoding;
- (id) initWithWriter:(id)writer;
//- (void) write:(char)c;
//- (void) write:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len;
//- (void) writeStr:(NSString *)str offset:(NSInteger)off len:(NSInteger)len;

@end

@interface FileWriter : OutputStreamWriter {
    NSInteger fd;
    NSFileHandle *fh;
    NSString *fn;
    BOOL append;
}

+ (id) newWriterWithFD:(NSInteger)fd;
+ (id) newWriterWithFH:(NSFileHandle *)file;
+ (id) newWriterWithFH:(NSFileHandle *)file append:(BOOL)append;
+ (id) newWriterWithFN:(NSString *)filename;
+ (id) newWriterWithFN:(NSString *)filename append:(BOOL)append;

- (id) initWithFD:(NSInteger)fd;
- (id) initWithFH:(NSFileHandle *)file append:(BOOL)append;
- (id) initWithFN:(NSString *)filename append:(BOOL)append;
- (void) dealloc;
- (NSInteger) writeStr:(NSString *)str;

- (void) close;

@property (assign) NSInteger fd;
@property (retain) NSFileHandle *fh;
@property (retain) NSString *fn;
@property (assign) BOOL append;

@end

