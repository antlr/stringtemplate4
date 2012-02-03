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
#import "STLexerMessage.h"
#import "STLexer.h"
#import "GroupLexer.h"

@implementation STLexerMessage

@synthesize msg;
@synthesize templateToken;
@synthesize srcName;

+ (id) newMessage:(NSString *)aSrcName msg:(NSString *)aMsg templateToken:(CommonToken *)aTemplateToken cause:(NSException *)aCause
{
    return [[STLexerMessage alloc] init:(NSString *)aSrcName msg:(NSString *)aMsg templateToken:(CommonToken *)aTemplateToken cause:(NSException *)aCause];
}

- (id) init:(NSString *)aSrcName msg:(NSString *)aMsg templateToken:(CommonToken *)aTemplateToken cause:(NSException *)aCause
{
    self=[super init:LEXER_ERROR who:nil cause:aCause arg:nil arg2:nil arg3:nil];
    if ( self != nil ) {
        msg = aMsg;
        if (msg == nil) {
            msg = @"nil";
        }
        [msg retain];
        templateToken = aTemplateToken;
        if ( templateToken ) [templateToken retain];
        srcName = aSrcName;
        if ( srcName ) [srcName retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STLexerMessage" );
#endif
    if ( msg ) [msg release];
    if ( templateToken ) [templateToken release];
    if ( srcName ) [srcName release];
    [super dealloc];
}

- (NSString *) description
{
    RecognitionException *re = (RecognitionException *)cause;
    NSInteger line = re.line;
    NSInteger charPos = re.charPositionInLine;
    if (templateToken != nil) {
        NSInteger templateDelimiterSize = 1;
        if ( templateToken.type == BIGSTRING) {
            templateDelimiterSize = 2;
        }
        line += templateToken.line - 1;
        charPos += templateToken.charPositionInLine + templateDelimiterSize;
    }
    NSString *filepos = [NSString stringWithFormat:@"%d:%d", line, charPos];
    if (srcName != nil) {
        return [srcName stringByAppendingFormat:@" %d:%@", filepos, [NSString stringWithFormat:[ErrorType ErrorNum:error], msg]];
    }
    return [filepos stringByAppendingFormat:@": %@", [NSString stringWithFormat:[ErrorType ErrorNum:error], msg]];
}

- (NSString *) toString
{
    return [self description];
}

@end
