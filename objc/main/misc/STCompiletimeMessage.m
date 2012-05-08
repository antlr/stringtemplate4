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
#import "STCompiletimeMessage.h"
#import "GroupLexer.h"

@class CommonToken;


@implementation STCompiletimeMessage

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t
{
    return [[STCompiletimeMessage alloc] init:anError srcName:aSrcName templateToken:aTemplateToken t:t cause:nil arg:nil arg2:nil];
}

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause
{
    return [[STCompiletimeMessage alloc] init:anError srcName:aSrcName templateToken:aTemplateToken t:t cause:aCause arg:nil arg2:nil];
}
            
+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg
{
    return [[STCompiletimeMessage alloc] init:anError srcName:aSrcName templateToken:aTemplateToken t:t cause:aCause arg:anArg arg2:nil];
}

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2
{
    return [[STCompiletimeMessage alloc] init:anError srcName:aSrcName templateToken:aTemplateToken t:t cause:aCause arg:anArg arg2:anArg2];
}

- (id) init:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2
{
    self=[super init:anError who:nil cause:aCause arg:anArg arg2:anArg2 arg3:nil];
    if ( self != nil ) {
        templateToken = aTemplateToken;
        if ( templateToken ) [templateToken retain];
        token = t;
        if ( token ) [token retain];
        srcName = aSrcName;
        if ( srcName ) [srcName retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in PrintWriter" );
#endif
    if ( templateToken ) [templateToken release];
    if ( token ) [token release];
    if ( srcName ) [srcName release];
    [super dealloc];
}

- (NSString *) description
{
    NSInteger line = 0;
    NSInteger charPos = -1;
    if ( token != nil ) {
        line = token.line;
        charPos = token.charPositionInLine;
        if ( templateToken != nil ) {
            NSInteger templateDelimiterSize = 1;
            if ( templateToken.type == BIGSTRING ) {
                templateDelimiterSize = 2;
            }
            line += templateToken.line - 1;
            charPos += templateToken.charPositionInLine + templateDelimiterSize;
        }
    }
    NSString *filepos = [NSString stringWithFormat:@"%d:%d", line, charPos];
    NSString *fmtMsg = [ErrorType ErrorNum:error];
    NSString *result = [NSString stringWithFormat:fmtMsg, arg, arg2];
    if (srcName != nil) {
        return [NSString stringWithFormat:@"%@ %@: %@", srcName, filepos, result];
    }
    return [NSString stringWithFormat:@"%@: %@", filepos, result ];
}

@synthesize templateToken;
@synthesize token;
@synthesize srcName;
@end
