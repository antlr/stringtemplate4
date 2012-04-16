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
#import "STRuntimeMessage.h"
#import "Interval.h"
#import "Coordinate.h"
#import "Misc.h"
#import "ST.h"
#import "CompiledST.h"

@implementation STRuntimeMessage

@synthesize interp;
@synthesize ip;
@synthesize scope;

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:nil cause:nil arg:@"" arg2:@"" arg3:@""];
}

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:aWho cause:nil arg:@"" arg2:@"" arg3:@""];
}

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho arg:(id)anArg;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:aWho cause:nil arg:anArg arg2:@"" arg3:@""];
}

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:aWho cause:e arg:anArg arg2:@"" arg3:@""];
}

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:aWho cause:e arg:anArg arg2:anArg2 arg3:@""];
}

+ (id) newMessage:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2 arg3:(id)anArg3;
{
    return [[STRuntimeMessage alloc] init:anInterp error:anError ip:anIp who:aWho cause:e arg:anArg arg2:anArg2 arg3:anArg3];
}

- (id) init:(Interpreter *)anInterp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2 arg3:(id)anArg3
{
    self=[super init:anError who:aWho cause:e arg:anArg arg2:anArg2 arg3:anArg3];
    if ( self !=nil ) {
        interp = anInterp;
        if ( interp ) [interp retain];
        ip = anIp;
        scope = [anInterp.currentScope retain];
    }
    return self;
}

- (void)dealloc
{
    if ( interp ) [interp release];
    [super dealloc];
}


/**
 * Given an ip (code location), get it's range in source template then
 * return it's template line:col.
 */
- (NSString *) getSourceLocation
{
    if (ip < 0 || who.impl == nil)
        return nil;
    NSInteger i, j;
    Interval *intv;
    j = [((ST *)who).impl.sourceMap count];
    if ( ip < j ) {
        intv = [((ST *)who).impl.sourceMap objectAtIndex:ip];
    }
    else {
        for (i = 0; i < j; i++ ) {
            intv = [((ST *)who).impl.sourceMap objectAtIndex:i];
            if ( ip >= intv.a && ip <= intv.b ) break;
        }
    }
    if (intv == nil)
        return nil;
    i = intv.a;
    Coordinate *loc = [Misc getLineCharPosition:((ST *)who).impl.template index:i];
    return [loc description];
}

- (NSString *) description
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    NSString *loc = [self getSourceLocation];
    if (who != nil) {
        [buf appendString:@"context ["];
        if (interp != nil )
            [buf appendString:[interp getEnclosingInstanceStackString:scope]];
        [buf appendString:@"]"];
    }
    if (loc != nil) {
        [buf appendFormat:@" %@", loc];
    }
    [buf appendFormat:@" %@", [super description]];
    return [buf description];
}

- (NSString *) toString
{
    return [self description];
}

@end
