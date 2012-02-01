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
#import "STGroupCompiletimeMessage.h"

@implementation STGroupCompiletimeMessage

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause
{
    return [[STGroupCompiletimeMessage alloc] init:anError srcName:aSrcName t:t cause:aCause arg:nil arg2:nil];
}

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg
{
    return [[STGroupCompiletimeMessage alloc] init:anError srcName:aSrcName t:t cause:aCause arg:anArg arg2:nil];
}

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2
{
    return [[STGroupCompiletimeMessage alloc] init:anError srcName:aSrcName t:t cause:aCause arg:anArg arg2:anArg2];
}

#ifdef DONTUSENOMO
- (id) init:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause
{
    self=[super init:anError who:nil cause:aCause arg:nil arg2:nil arg3:nil];
    if ( self != nil ) {
        token = t;
        srcName = srcName;
    }
    return self;
}

- (id) init:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg
{
    self=[super init:anError who:nil cause:aCause arg:nil arg2:nil arg3:nil];
    if ( self != nil ) {
        token = t;
        if ( token ) [token retain];
        srcName = srcName;
        if ( srcName ) [srcName retain];
    }
    return self;
}
#endif

- (id) init:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2
{
    self=[super init:anError who:nil cause:aCause arg:anArg arg2:anArg2 arg3:nil];
    if ( self != nil ) {
        token = t;
        if ( token ) [token retain];
        srcName = aSrcName;
        if ( srcName ) [srcName retain];
    }
    return self;
}

- (void) dealloc {
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STGroupCompiletimeMessage" );
#endif
    if ( token ) [token release];
    if ( srcName ) [srcName release];
    [super dealloc];
}

- (NSString *) description
{
    RecognitionException *re = (RecognitionException *)cause;
    NSInteger line = 0;
    NSInteger charPos = -1;
    if ( token != nil ) {
        line = token.line;
        charPos = token.charPositionInLine;
    }
    else if (re != nil) {
        line = re.line;
        charPos = re.charPositionInLine;
    }
    NSString *filepos = [NSString stringWithFormat:@"%d:%d", line, charPos];
    if (srcName != nil) {
        return [NSString stringWithFormat:@"%@ %@: %@", srcName, filepos, [NSString stringWithFormat:[ErrorType ErrorNum:error], arg, arg2]];
    }
    return [NSString stringWithFormat:@"%d: %@",filepos , [NSString stringWithFormat:[ErrorType ErrorNum:error], arg, arg2]];
}

- (NSString *) toString
{
    return [self description];
}

@synthesize token;
@synthesize srcName;
@end
