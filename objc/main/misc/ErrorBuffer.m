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
#import "ErrorBuffer.h"

@class CommonToken;
@class STMessage;

@implementation ErrorBuffer

@synthesize errors;

+ (id) newErrorBuffer
{
    return [[ErrorBuffer alloc] init];
}

- (id) init {
    self=[super init];
    if ( self != nil ) {
        errors = [[AMutableArray arrayWithCapacity:5] retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in ErrorBuffer" );
#endif
    if ( errors ) [errors release];
    [super dealloc];
}

- (void) compileTimeError:(STMessage *)msg {
    [errors addObject:msg];
}

- (void) runTimeError:(STMessage *)msg
{
    if (msg.error != NO_SUCH_PROPERTY) {
        [errors addObject:msg];
    }
}

- (void) IOError:(STMessage *)msg
{
    [errors addObject:msg];
}

- (void) internalError:(STMessage *)msg
{
    [errors addObject:msg];
}

- (NSString *) description
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];

//    for (id m in errors) {
    id m;
    ArrayIterator *it = [ArrayIterator newIterator:errors];
    while ( [it hasNext] ) {
        m = [it nextObject];
        //[buf appendFormat:@"%@%@", [m description], Misc.newline];
        return [m description];
    }
    return (([buf length] > 0) ? buf : @"buf=<nil>");
}

- (NSString *) toString
{
    return [self description];
}

@end
