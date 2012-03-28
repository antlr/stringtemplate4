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

#import <ANTLR/ANTLR.h>
#import "PrintWriter.h"


@implementation PrintWriter

+ (id) newWriter
{
    return [[PrintWriter alloc] initWithCapacity:16];
}

+ (id) newWriter:(StringWriter *)sw
{
    return [[PrintWriter alloc] initWithWriter:sw];
}

+ (id) stringWithCapacity:(NSUInteger)aLen
{
    return [[PrintWriter alloc] initWithCapacity:aLen];
}

- (id) initWithCapacity:(NSUInteger)aLen
{
    self = [super initWithCapacity:aLen];
    if ( self != nil ) {
    writer = self;
    }
    return self;
}

- (id) initWithWriter:(Writer *)aWriter
{
    self = [super init];
    writer = aWriter;
    return self;
}

- (void) print:(id)msg
{
//    [writer appendString:msg];
    [writer writeStr:msg];
    //NSLog( @"self=%@--msg=%@", [self className], [msg description] );
}

- (void) println:(id)msg
{
//    [writer appendString:msg];
    [writer writeStr:msg];
    //NSLog( @"self=%@--msg=%@\n", [self className], [msg description] );
}

/*
 - (void) close;
{
}
*/

@end
