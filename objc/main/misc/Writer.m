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
#import "STErrorListener.h"
#import "Writer.h"
#import "ST.h"

@implementation Writer

+ (NSInteger) NO_WRAP
{
    return 1000;
}

+ (id) newWriter
{
    return [[Writer alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        capacity = 1024;
        data = [[NSMutableData dataWithCapacity:capacity] retain];
        ptr = [data mutableBytes];
        for (int i = 0; i < capacity; i++) {
            ptr[i] = '\0';
        }
        ip = 0;
    }
    return self;
}

- (id) initWithCapacity:(NSUInteger)len
{
    self=[super init];
    if ( self != nil ) {
        capacity = len;
        data = [[NSMutableData dataWithCapacity:capacity] retain];
        ptr = [data mutableBytes];
        for (int i = 0; i < capacity; i++) {
            ptr[i] = '\0';
        }
        ip = 0;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Writer" );
#endif
    [super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    Writer *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    copy.capacity = capacity;
    if ( data ) {
        copy.data = [data copyWithZone:aZone];
    }
    copy.ptr = [copy.data mutableBytes];
    copy.ip = ip;
    copy.lock = lock;
    return copy;
}

- (NSUInteger) count
{
    return [self length];
}

- (NSUInteger) length
{
    NSInteger i = 0;
    for (i = 0; i < capacity; i++ ) {
        if ( ptr[i] == '\0' ) break;
    }
    ip = i;
    return i;
}

- (unichar) characterAtIndex:(NSUInteger)index
{
    if ( index < capacity ) return (unichar) ptr[index];
    return (unichar) '\0';
}

- (void) append:(NSInteger)c
{
    char c1[8] = { c, '\0' };
    [data appendBytes:c1 length:1];
    ptr = [data mutableBytes];
    ip++;
}

- (void) appendString:(NSString *)aString
{
    NSInteger i, len, len2;
    len = [self length];
    len2 = [aString length];
    [self ensureCapacity:len+len2+2];
    for (i = 0; i < len2; i++) {
        ptr[len+i] = (char) [aString characterAtIndex:i];
    }
    ip = len+i;
}

- (void) write:(NSMutableData *)d offset:(NSInteger)off len:(NSInteger)len
{
    char *buf;
    buf = [d mutableBytes];
    for (NSInteger i=0; i<len; i++) {
        [self write:buf[i+off]];
    }
}

- (void) write:(NSInteger)aChar
{
    if ( ptr[ip] != '\0') [self length];
    ptr[ip++] = aChar;
    ptr[ip] = '\0';
    putchar(aChar);
}

- (void) writeStr:(NSString *)str pos:(NSInteger)offset len:(NSInteger)len
{
    [self ensureCapacity:len+2];
//    [self getChars:str offset:offset srcLen:len dest:ptr];
    NSRange aRange = NSMakeRange(offset, len);
    [self appendString:[str substringWithRange:aRange]];
    [self write:data offset:0 len:len];
}

- (NSInteger) writeStr:(NSString *)str
{
    NSInteger len;
    [self setString:@""];
    len = [str length];
    [self appendString:str];
    //ip += len;
    if ( ptr[ip] != '\0') ptr[ip] = '\0';
    [self write:data offset:0 len:[str length]];
    return len;
}

- (NSString *) description
{
    if ( ptr == nil ) {
        return @"<nilFromWriter>";
    }
    //NSLog( @"%@", [NSString stringWithCString:(const char *)ptr encoding:NSASCIIStringEncoding] );
    return [NSString stringWithCString:ptr encoding:NSASCIIStringEncoding];
}

- (void) replaceCharactersInRange:(NSRange)range withString:(NSString *)aString 
{
    NSInteger i, start;
    start = range.location;
    [self ensureCapacity:start+range.length];
    for (i = 0; i < range.length; i++) {
        ptr[start+i] = (char) [aString characterAtIndex:i];
    }
    if ( ptr[range.location+range.length] != '\0' ) [self length];
}

- (void) ensureCapacity:(NSInteger)len
{
    if ( len >= capacity ) {
        capacity = len+2;
        [data setLength:capacity];
        ptr = [data mutableBytes];
    }
}

- (void) getChars:(NSString *)orig offset:(NSInteger)offset srcLen:(NSInteger)sLen dest:(char *)buf dstLen:(NSInteger)dLen
{
    NSString *src;
    if (offset >= 0 && (offset + sLen) < [orig length]) {
        src = [orig substringWithRange:NSMakeRange(offset, sLen)];
        for (NSInteger i=0; i<sLen; i++) {
            buf[i] = [src characterAtIndex:i];
        }
    }
}

- (void) close
{
}

- (void) print:(id)msg
{
    return;
}

- (void) println:(id)msg
{
    return;
}

@synthesize capacity;
@synthesize data;
@synthesize ptr;
@synthesize ip;
@synthesize lock;

@end

@implementation BufferedWriter
@synthesize writer;
@synthesize nChars;
@synthesize nextChar;

+ (id) newWriter:(Writer *)op
{
    return [[BufferedWriter alloc] initWithWriter:op len:8192];
}

+ (id) newWriter:(Writer *)op withLen:(NSInteger)len
{
    return [[BufferedWriter alloc] initWithWriter:op len:len];
}

- (id) initWithWriter:(Writer *)op len:(NSInteger)len
{
    self=[super initWithCapacity:len];
    if ( self != nil ) {
        writer = op;
    }
    return self;
}

/**
 * Flushes the output buffer to the underlying character stream, without
 * flushing the stream itself.  This method is non-private only so that it
 * may be invoked by PrintStream.
 */
- (void) flushBuffer
{
//    synchronized (lock) {
//    ensureOpen();
    if (nextChar == 0)
        return;
    [self ensureCapacity:nextChar+2];
    [writer write:ptr offset:0 len:nextChar];
    nextChar = 0;
//    }
}


/**
 * Writes a single character.
 *
 * @exception  IOException  If an I/O error occurs
 */
- (void) write:(int) c
{
//    synchronized (lock) {
//        ensureOpen();
    if (nextChar >= nChars)
        [self flushBuffer];
    ptr[nextChar++] = (char) c;
    ptr[nextChar] = '\0';
//    }
}

- (void) write:(NSData *)cbuf offset:(NSInteger)off len:(NSInteger)len
{
    char *src;
    [self ensureCapacity:len];
    src = [cbuf mutableBytes];
//    synchronized (lock) {
//        ensureOpen();
        if ((off < 0) || (off > [data length]) || (len < 0) ||
            ((off + len) > [data length]) || ((off + len) < 0)) {
//            @throw [IndexOutOfBoundsException newException];
            @throw [RuntimeException newException:@"IndexOutOfBounds" ];
        } else if (len == 0) {
            return;
        } 
        
        if (len >= nChars) {
            /* If the request length exceeds the size of the output buffer,
             flush the buffer and then write the data directly.  In this
             way buffered streams will cascade harmlessly. */
            [self flushBuffer];
            [self write:cbuf offset:off len:len];
            return;
        }
        
        int b = off, t = off + len;
        while (nextChar < t) {
            int d = (nChars - nextChar) < (t - b) ? (nChars - nextChar) : (t - b);
//            System.arraycopy(cbuf, b, cb, nextChar, d);
            for ( NSInteger i = 0; i < len; i++ ) {
                ptr[nextChar++] = src[i];
            }
            ptr[nextChar] = '\0';
            if (nextChar >= nChars)
                [self flushBuffer];
        }
//    }
}

/**
 * Writes a portion of a String.
 *
 * <p> If the value of the <tt>len</tt> parameter is negative then no
 * characters are written.  This is contrary to the specification of this
 * method in the {@linkplain java.io.Writer#write(java.lang.String,int,int)
 * superclass}, which requires that an {@link IndexOutOfBoundsException} be
 * thrown.
 *
 * @param  s     String to be written
 * @param  off   Offset from which to start reading characters
 * @param  len   Number of characters to be written
 *
 * @exception  IOException  If an I/O error occurs
 */
- (void) writeStr:(NSString *)s pos:(NSInteger)off len:(NSInteger)len
{
//    synchronized (lock) {
//        ensureOpen();
    NSString *dest;
        NSInteger b = off, t = off + len;
        while (b < t) {
            int d = (nChars - nextChar) < (t - b) ? (nChars - nextChar) : (t - b);
//            s.getChars(b, b + d, cb, nextChar);
            [s substringWithRange:NSMakeRange(b, b + d)];
            b += d;
            nextChar += d;
            if (nextChar >= nChars)
                [self flushBuffer];
        }
//    }
}

- (NSInteger) writeStr:(NSString *)str
{
    NSInteger len;
    [self setString:@""];
    len = [str length];
    [data setLength:len];
    [self appendString:str];
    //ip += len;
    if ( ptr[ip] != '\0') ptr[ip] = '\0';
//    [writer writeStr:str pos:0 len:[str length]];
    [writer write:data offset:0 len:[str length]];
    return len;
}

- (void) close
{
}

@end

@implementation OutputStreamWriter

+ (id) newWriter:(NSOutputStream *)anOS
{
    return [[OutputStreamWriter alloc] init:anOS charSet:nil encoding:NSASCIIStringEncoding];
}

+ (id) newWriter:(NSOutputStream *)anOS charSet:(NSCharacterSet *)charSet
{
    return [[OutputStreamWriter alloc] init:anOS charSet:charSet encoding:NSASCIIStringEncoding];
}

+ (id) newWriter:(NSOutputStream *)anOS encoding:(NSStringEncoding)encoding
{
    return [[OutputStreamWriter alloc] init:anOS charSet:nil encoding:(NSStringEncoding)encoding];
}

+ (id) newWriter:(NSOutputStream *)anOS charSetName:(NSString *)charSetName
{
    return [[OutputStreamWriter alloc] init:anOS charSet:nil encoding:NSASCIIStringEncoding];
}

- (id) init:(NSOutputStream *)anOS charSet:(NSCharacterSet *)charSet encoding:(NSStringEncoding)encoding
{
    self=[super init];
    if ( self != nil ) {
        os = anOS;
    }
    return self;
}

#ifdef DONTUSEYET
- (void) write:(char)c
{
}

- (void) write:(char *)cbuf offset:(NSInteger)off len:(NSInteger)len
{
}

- (NSInteger) writeStr:(NSString *)str offset:(NSInteger)off len:(NSInteger)len
{
}
#endif

@end

@implementation FileWriter

@synthesize fd;
@synthesize fn;
@synthesize fh;
@synthesize append;

+ (id) newWriterWithFD:(NSInteger)anFD
{
    return [[FileWriter alloc] initWithFD:(NSInteger)anFD];
}

+ (id) newWriterWithFH:(NSFileHandle *)aFH
{
    return [[FileWriter alloc] initWithFH:aFH append:NO];
}

+ (id) newWriterWithFH:(NSFileHandle *)file append:(BOOL)append
{
    return [[FileWriter alloc] initWithFH:file append:append];
}

+ (id) newWriterWithFN:(NSString *)filename
{
    return [[FileWriter alloc] initWithFN:filename append:NO];
}

+ (id) newWriterWithFN:(NSString *)filename append:(BOOL)append
{
    return [[FileWriter alloc] initWithFN:filename append:append];
}

- (id) initWithFD:(NSInteger)anFD
{
    self=[super init];
    if ( self != nil ) {
        fd = anFD;
        fh = [[NSFileHandle alloc] initWithFileDescriptor:fd];
    }
    return self;
}

- (id) initWithFH:(NSFileHandle *)aFH append:(BOOL)theAppend
{
    self=[super init];
    if ( self != nil ) {
        fh = aFH;
        append = theAppend;
    }
    return self;
}

- (id) initWithFN:(NSString *)filename append:(BOOL)theAppend
{
    self=[super init];
    if ( self != nil ) {
        fn = filename;
        fh = [NSFileHandle fileHandleForReadingAtPath:filename];
        append = theAppend;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in FileWriter" );
#endif
    if ( fn ) [fn release];
    if ( fh ) [fh release];
    [super dealloc];
}



- (void) write:(NSData *)cbuf offset:(NSInteger)off len:(NSInteger)len
{
    //    synchronized (lock) {
    //        ensureOpen();
    if ((off < 0) || (off > [cbuf length]) || (len < 0) ||
        ((off + len) > [cbuf length]) || ((off + len) < 0)) {
        //            @throw [IndexOutOfBoundsException newException];
        @throw [RuntimeException newException:@"IndexOutOfBounds" ];
    } else if (len == 0) {
        return;
    } 
    [fh truncateFileAtOffset:0];
    [fh writeData:cbuf];
    return;
}

- (void) writeStr:(NSString *)str pos:(NSInteger)off len:(NSInteger)len
{
    NSMutableData *d;
    //    synchronized (lock) {
    //        ensureOpen();
    if ((off < 0) || (off > [str length]) || (len < 0) ||
        ((off + len) > [str length]) || ((off + len) < 0)) {
        //            @throw [IndexOutOfBoundsException newException];
        @throw [RuntimeException newException:@"IndexOutOfBounds" ];
    } else if (len == 0) {
        return;
    } 
    d = [NSMutableData dataWithContentsOfFile:str];
    [fh truncateFileAtOffset:0];
    [fh writeData:d];
    return;
}

- (NSInteger) writeStr:(NSString *)str
{
    NSInteger len;
    len = [str length];
    [self writeStr:str pos:0 len:len];
    return len;
}

- (void) close
{
    if (fh != nil)
        [fh closeFile];
}

@end

