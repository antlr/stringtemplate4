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
#import <ANTLR/ANTLR.h>
#import "Misc.h"
#import <FOUNDATION/Foundation.h>
#import <objc/runtime.h>
#import "STException.h"

@implementation Misc

static NSString *const newline = @"\n";

+ (NSString *) newline
{
    return newline;
}

+ (NSString *) join:(ArrayIterator *)iter separator:(NSString *)separator
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    NSString *obj;
    while ( (obj = [iter nextObject]) != nil ) {
        [buf appendString:obj];
        if ([iter hasNext]) {
            [buf appendString:separator];
        }
    };
    return [buf description];
}

+ (NSString *) strip:(NSString *)s n:(NSInteger)n
{
    return [s substringWithRange:NSMakeRange(n, [s length]-(2*n))];
}

+ (NSString *) trimOneStartingNewline:(NSString *)s
{
    if ([s hasPrefix:@"\r\n"])
        s = [s substringFromIndex:2];
    else if ([s hasPrefix:@"\n"])
        s = [s substringFromIndex:1];
    return s;
}

+ (NSString *) trimOneTrailingNewline:(NSString *)s
{
    if ([s hasSuffix:@"\r\n"])
        s = [s substringToIndex:[s length] - 2];
    else if ([s hasSuffix:@"\n"])
        s = [s substringToIndex:[s length] - 1];
    return s;
}

/**
 * Given, say, file:/tmp/test.jar!/org/foo/templates/main.stg
 * convert to file:/tmp/test.jar!/org/foo/templates
 */
+ (NSString *) stripLastPathElement:(NSString *)f
{
    NSRange r;
    r = [f rangeOfString:@"/" options:NSBackwardsSearch];
    if (r.location == NSNotFound) 
        return f;
    return [f substringWithRange:NSMakeRange(0, r.location)];
}

+ (NSString *) getFileNameNoSuffix:(NSString *)f
{
    if (f == nil)
        return nil;
    f = [self getFileName:f];
    NSRange r;
    r = [f rangeOfString:@"." options:NSBackwardsSearch];
    if (r.location == NSNotFound) 
        return f;
    return [f substringWithRange:NSMakeRange(0, r.location)];
}

#ifdef DONTUSEYET
+ (NSString *) getFileName:(NSString *)fullFileName
{
    if (fullFileName == nil)
        return nil;
    NSString *f = [fullFileName lastPathComponent];
    return f;
}
#endif

+ (NSString *) getFileName:(NSString *)fullFileName
{
    NSError *anError;
    NSURL *aURL = [NSURL fileURLWithPath:[fullFileName stringByStandardizingPath]];
    NSFileWrapper *f = [[NSFileWrapper alloc] initWithURL:aURL options:NSFileWrapperReadingWithoutMapping error:&anError];
    return [f filename];
}

+ (NSString *) getPrefix:(NSString *)name
{
    if (name == nil)
        return nil;
    name = [name stringByDeletingLastPathComponent];
    if ([name length] > 1 )
        return name;
    return @"";
}

+ (NSString *) replaceEscapes:(NSString *)s
{
    s = [s stringByReplacingOccurrencesOfString:@"\n" withString:@"\\\\n"];
    s = [s stringByReplacingOccurrencesOfString:@"\r" withString:@"\\\\r"];
    s = [s stringByReplacingOccurrencesOfString:@"\t" withString:@"\\\\t"];
    return s;
}


+ (BOOL) urlExists:(NSURL *)url
{
    @try {
		NSInputStream *is = [NSInputStream inputStreamWithURL:url];
		return [is hasBytesAvailable];
	}
	@catch (IOException *ioe) {
		return NO;
	}
}

/** Given index into string, compute the line and char position in line */
+ (Coordinate *) getLineCharPosition:(NSString *)s index:(NSInteger)index
{
    NSInteger line = 1;
    NSInteger charPos = 0;
    NSInteger p = 0;
    
    while (p < index) {
        if ([s characterAtIndex:p] == '\n') {
            line++;
            charPos = 0;
        }
        else
            charPos++;
        p++;
    }
    
    return [Coordinate newCoordinate:line b:charPos];
}

#pragma mark error fix accessField
+ (id) accessField:(Ivar)f obj:(id)obj value:(id)value
{
    
    @try {
        // [f setAccessible:YES];
        ;
    }
    @catch (RuntimeException *se) {
    }
    return object_getIvar(obj, f);
}

+ (id) invokeMethod:(SEL)m obj:(id)obj value:(id)value
{
    
    @try {
        if ([obj respondsToSelector:m]) {
            value = [obj performSelector:m];
        //        [m setAccessible:YES];
        }
        else {
            @throw [STNoSuchPropertyException newException:NSStringFromSelector(m)];
        }
    }
    @catch (RuntimeException *se) {
        if ( [se isKindOfClass:[STNoSuchPropertyException class]] )
            @throw [STNoSuchPropertyException newException:NSStringFromSelector(m)];
    }
#pragma mark error
    return value;
}

+ (SEL) getMethod:(NSString *)methodName
{
    SEL m;
    
    @try {
        m = NSSelectorFromString(methodName);
    }
    @catch (STNoSuchMethodException *nsme) {
        m = nil;
    }
    return m;
}

@end
