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
#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "ErrorType.h"
#import "STMessage.h"
#import "ModelAdaptor.h"
#import "PrintWriter.h"
#import "StringWriter.h"

@implementation STMessage

+ (id) newMessage:(ErrorTypeEnum)anError;
{
    return [[STMessage alloc] init:anError who:nil cause:nil arg:@"" arg2:@"" arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho;
{
    return [[STMessage alloc] init:anError who:aWho cause:nil arg:@"" arg2:@"" arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause;
{
    return [[STMessage alloc] init:anError who:aWho cause:aCause arg:@"" arg2:@"" arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg;
{
    return [[STMessage alloc] init:anError who:aWho cause:aCause arg:arg arg2:@"" arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause where:(CommonToken *)where  arg:(id)arg;
{
    return [[STMessage alloc] init:anError who:aWho cause:aCause arg:where arg2:arg arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2;
{
    return [[STMessage alloc] init:anError who:aWho cause:aCause arg:arg arg2:arg2 arg3:@""];
}

+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2 arg3:(id)arg3
{
    return [[STMessage alloc] init:anError who:aWho cause:aCause arg:arg arg2:arg2 arg3:arg3];
}

- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2 arg3:(id)anArg3
{
    self=[super init];
    if ( self != nil ) {
        error = anError;
        who = aWho;
        if ( who ) [who retain];
        cause = aCause;
        if ( cause ) [cause retain];
        arg = anArg;
        arg2 = anArg2;
        arg3 = anArg3;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STMessage" );
#endif
    if ( who ) [who release];
    if ( cause ) [cause release];
    [super dealloc];
}

- (NSString *) description
{
    StringWriter *sw = [StringWriter stringWithCapacity:16];
    PrintWriter  *pw = [PrintWriter newWriter:sw];
    NSMutableString *msg = [NSMutableString stringWithFormat:[ErrorType ErrorNum:error], arg, arg2, arg3];
    [pw print:msg];
    if (cause != nil) {
        [pw print:@"\nCaused by: "];
        NSArray *cs = [cause callStackSymbols];
        NSString *str;
        for ( int i = 0; i < [cs count]; i++ ) {
            str = [cs objectAtIndex:i];
            NSLog( @"CallStack = %@\n", str );
        }
    }
    return [sw description];
}

@synthesize who;
@synthesize error;
@synthesize arg;
@synthesize arg2;
@synthesize arg3;
@synthesize cause;

@end
