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
#import "Coordinate.h"
#import <objc/runtime.h>


@class OBJCMethod;
@class ArrayIterator;

@interface Misc : NSObject {
}

+ (NSString *) newline;
+ (NSString *) join:(ArrayIterator *)iter separator:(NSString *)separator;
+ (NSString *) strip:(NSString *)s n:(NSInteger)n;
+ (NSString *) trimOneStartingNewline:(NSString *)s;
+ (NSString *) trimOneTrailingNewline:(NSString *)s;
+ (NSString *) stripLastPathElement:(NSString *)f;
+ (NSString *) getFileNameNoSuffix:(NSString *)f;
+ (NSString *) getFileName:(NSString *)fullFileName;
+ (NSString *) getParent:(NSString *)name;
+ (NSString *) getPrefix:(NSString *)name;
+ (NSString *) replaceEscapes:(NSString *)s;
+ (Coordinate *) getLineCharPosition:(NSString *)s index:(NSInteger)index;
+ (BOOL) fileExists:(NSString *)aPath;
+ (BOOL) urlExists:(NSURL *)url;
#pragma mark error fix accessField
+ (id) accessField:(Ivar)f obj:(id)obj value:(id)value;
+ (id) invokeMethod:(SEL)m obj:(id)obj value:(id)value;
+ (SEL) getMethod:(NSString *)methodName;
+ (NSInteger) lastIndexOf:(char)aChar inString:(NSString *)aString;
@end
