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
#import "STGroupString.h"

/** A group derived from a string not a file or dir. */
@implementation STGroupString

@synthesize sourceName;
@synthesize text;
@synthesize alreadyLoaded;

+ (id) newSTGroupString:(NSString *)aTemplate
{
    return [[STGroupString alloc] init:@"<string>" text:aTemplate delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (id) newSTGroupString:(NSString *)aSourceName text:(NSString *)aTemplate
{
    return [[STGroupString alloc] init:aSourceName text:aTemplate delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (id) newSTGroupString:(NSString *)aSourceName
                   text:(NSString *)aTemplate
     delimiterStartChar:(unichar)aStartChar
      delimiterStopChar:(unichar)aStopChar
{
    return [[STGroupString alloc] init:aSourceName text:aTemplate delimiterStartChar:aStartChar delimiterStopChar:aStopChar];
}

- (id) init:(NSString *)aSourceName text:(NSString *)theText
                     delimiterStartChar:(unichar)aDelimiterStartChar
                      delimiterStopChar:(unichar)aDelimiterStopChar
{
    self=[super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        alreadyLoaded = NO;
        sourceName = aSourceName;
        if ( sourceName ) [sourceName retain];
        text = theText;
        if ( text ) [text retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STGroupString" );
#endif
    if ( sourceName ) [sourceName release];
    if ( text ) [text release];
    [super dealloc];
}

- (BOOL) isDictionary:(NSString *)name
{
    if ( !alreadyLoaded )
        [self load];
    return [super isDictionary:name];
}

- (BOOL) isDefined:(NSString *)name
{
    if (!alreadyLoaded)
        [self load];
    return [super isDefined:name];
}

- (CompiledST *) load:(NSString *)name
{
    if ( !alreadyLoaded )
        [self load];
    return [self rawGetTemplate:name];
}

- (void) load
{
    if (alreadyLoaded)
        return;
    alreadyLoaded = YES;
    GroupParser *parser = nil;
    
    @try {
        ANTLRStringStream *fs = [ANTLRStringStream newANTLRStringStream:text];
        fs.name = sourceName;
        GroupLexer *lexer = [GroupLexer newGroupLexerWithCharStream:fs];
        CommonTokenStream *tokens = [CommonTokenStream newCommonTokenStreamWithTokenSource:lexer];
        parser = [GroupParser newGroupParser:tokens];
        [parser group:self prefix:@"/"];
    }
    @catch (NSException * e) {
        [errMgr IOError:nil error:CANT_LOAD_GROUP_FILE e:e arg:@"<string>"];
    }
}

- (NSString *)getFileName
{
    return @"<string>";
}

@end
