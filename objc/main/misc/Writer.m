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
    return [[Writer alloc] initWithCapacity:16];
}

+ (id) newWriterWithWriter:(id)aWriter{
    return [[Writer alloc] initWithWriter:aWriter];
}

+ (id) newWriterWithCapacity:(NSUInteger)len
{
    return [[Writer alloc] initWithCapacity:len];
}

+ (id) stringWithCapacity:(NSUInteger)len
{
    return [[Writer alloc] initWithCapacity:len];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        capacity = 16;
        data = [[NSMutableData dataWithCapacity:capacity] retain];
        ptr = [data mutableBytes];
        for (int i = 0; i < capacity; i++) {
            ptr[i] = '\0';
        }
        ip = 0;
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
    }
    return self;
}

- (id) initWithWriter:(Writer *)aWriter
{
    self=[super init];
    if ( self != nil ) {
        if (aWriter == nil) {
            self.capacity = 16;
            data = [[NSMutableData dataWithCapacity:capacity] retain];
            ptr = [data mutableBytes];
            for (int i = 0; i < capacity; i++) {
                ptr[i] = '\0';
            }
            ip = 0;
        }
        else {
            writer = aWriter;
            self.capacity = writer.capacity;
            self.data = writer.data;
            self.ptr = writer.ptr;
            self.ip = writer.ip;
        }
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
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Writer" );
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
    Writer *copy;
    
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

- (void) write:(NSInteger)aChar
{
    if ( ptr[ip] != '\0') [self length];
    ptr[ip++] = aChar;
    ptr[ip] = '\0';
    //[self appendFormat:@"%c", aChar];
}

- (NSInteger) writeStr:(NSString *)str
{
    NSInteger len;
    len = [str length];
    [self appendString:str];
    //ip += len;
    if ( ptr[ip] != '\0') ptr[ip] = '\0';
    return len;
}

- (void) writeStr:(NSString *)str pos:(NSInteger)offset len:(NSInteger)len
{
    NSRange aRange = NSMakeRange(offset, len);
    [self appendString:[str substringWithRange:aRange]];
}

- (NSString *) description
{
    if ( ptr == nil ) {
        return @"";
    }
    //NSLog( @"%@", [NSString stringWithCString:(const char *)ptr encoding:NSASCIIStringEncoding] );
    return [NSString stringWithCString:ptr encoding:NSASCIIStringEncoding];
}

- (NSString *) toString
{
    return [self description];
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

- (void) close
{
}

- (void) pushIndentation:(NSString *)anIndent
{
    [indents addObject:anIndent];
}

- (NSString *) popIndentation
{
    NSString *ret = [indents objectAtIndex:[indents count]-1];
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

- (NSInteger) index
{
    return charIndex;
}

- (NSInteger) writeSeparator:(NSString *)str
{
    NSInteger len = [str length];
    [self writeStr:str];
    return len;
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
            [self writeStr:newline];
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
            [writer writeStr:ind];
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

@synthesize indents;
@synthesize anchors;
@synthesize anchors_sp;
@synthesize newline;
@synthesize writer;
@synthesize atStartOfLine;
@synthesize charPosition;
@synthesize charIndex;
@synthesize lineWidth;

@end

@implementation BufferedWriter

+ (id) newWriter
{
    return [[BufferedWriter alloc] initWithCapacity:30];
}

+ (id) newWriter:(NSInteger)len
{
    return [[BufferedWriter alloc] initWithCapacity:len];
}

+ (id) newWriterWithWriter:(Writer *)op
{
    return [[BufferedWriter alloc] initWithWriter:op];
}

- (id) initWithWriter:(Writer *)op
{
    self=[super initWithWriter:op];
    return self;
}

- (id) initWithCapacity:(NSInteger)sz
{
    self=[super initWithCapacity:sz];
    return self;
}

- (void) close
{
}

- (void) flush
{
}

@end

@implementation OutputStreamWriter

+ (id) newWriter:(id)writer
{
    return [[OutputStreamWriter alloc] init:writer charSet:nil encoding:NSASCIIStringEncoding];
}

+ (id) newWriter:(id)writer charSet:(NSCharacterSet *)charSet
{
    return [[OutputStreamWriter alloc] init:(id)writer charSet:charSet encoding:NSASCIIStringEncoding];
}

+ (id) newWriter:(id)writer encoding:(NSStringEncoding)encoding
{
    return [[OutputStreamWriter alloc] init:(id)writer charSet:nil encoding:(NSStringEncoding)encoding];
}

+ (id) newWriter:(id)writer charSetName:(NSString *)charSetName
{
    return [[OutputStreamWriter alloc] init];
}

- (id) init
{
    self=[super init];
    return self;
}

- (id) init:(id)aWriter charSet:(NSCharacterSet *)charSet encoding:(NSStringEncoding)encoding
{
    self=[super initWithWriter:aWriter];
    return self;
}

- (id) initWithWriter:(id)aWriter
{
    self=[super initWithWriter:aWriter];
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

- (NSInteger) writeStr:(NSString *)str
{
    NSMutableData *d;
    //- (BOOL)writeToFile:(NSString *)path atomically:(BOOL)useAuxiliaryFile encoding:(NSStringEncoding)enc error:(NSError **)error;
    [super writeStr:str];
    d = [NSMutableData dataWithBytes:ptr length:[self length]];
    [fh truncateFileAtOffset:0];
    [fh writeData:d];
    return [d length];
}

- (void) close
{
    if (fh != nil)
        [fh closeFile];
}

@end

