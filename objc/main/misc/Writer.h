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

}

+ (NSInteger) NO_WRAP;
+ (id) newWriter;
+ (id) newWriter:(id)aWriter;
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
- (void) append:(NSInteger) c;
- (void) write:(NSInteger)aChar;
- (void) write:(NSData *)cbuf offset:(NSInteger)off len:(NSInteger)len;
- (NSInteger) writeStr:(NSString *)str;
//- (void) writeChunk:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len;
- (void) replaceCharactersInRange:(NSRange)range withString:(NSString *)aString;
- (void) ensureCapacity:(NSInteger)len;
- (void) close;
- (NSString *) description;
- (NSString *) toString;

- (void) print:(id)msg;
- (void) println:(id)msg;
- (void) getChars:(NSString *)orig offset:(NSInteger)offset srcLen:(NSInteger)sLen dest:(char *)buf dstLen:(NSInteger)dLen;

@property (assign) NSInteger capacity;
@property (retain) NSMutableData *data;
@property (assign) char *ptr;
@property (assign) NSInteger ip;
@property (retain) id lock;

@end

@interface BufferedWriter : Writer {
    Writer *writer;
    NSInteger nChars;
    NSInteger nextChar;
}

+ (id) newWriter:(Writer *)op;
+ (id) newWriter:(Writer *)op len:(NSInteger)len;

- (id) initWithWriter:(Writer *)op;
- (id) initWithWriter:(Writer *)op len:(NSInteger)sz;
- (void) close;
- (void) flushBuffer;
//- (void) newline;
//- (void) write:(char)c;
//- (void) writeChunk:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len;
//- (void) writeStr:(NSString *)str offset:(NSInteger)off len:(NSInteger)len;

@property (retain) Writer *writer;
@property (assign) NSInteger nChars;
@property (assign) NSInteger nextChar;
@end

@interface OutputStreamWriter : Writer {
    NSOutputStream *os;
}

+ (id) newWriter:(NSOutputStream *)anOS;
+ (id) newWriter:(NSOutputStream *)anOS charSet:(NSCharacterSet *)charSet;
+ (id) newWriter:(NSOutputStream *)anOS encoding:(NSStringEncoding)encoding;
+ (id) newWriter:(NSOutputStream *)anOS charSetName:(NSString *)charSetName;

- (id) init:(NSOutputStream *)anOS charSet:(NSCharacterSet *)charSet encoding:(NSStringEncoding)encoding;
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
- (void) write:(NSData *)cbuf offset:(NSInteger)off len:(NSInteger)len;
- (NSInteger) writeStr:(NSString *)str;

- (void) close;

@property (assign) NSInteger fd;
@property (retain) NSFileHandle *fh;
@property (retain) NSString *fn;
@property (assign) BOOL append;

@end

