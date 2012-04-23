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
#import "STErrorListener.h"
#import "STLexer.h"
#import "STException.h"
#import "STParser.h"
#import "STToken.h"

#ifndef ANTLR3TokenTypeAlreadyDefined
#define ANTLR3TokenTypeAlreadyDefined
typedef enum {
    ANTLR_EOF = -1,
    INVALID,
    EOR,
    DOWN,
    UP,
    MIN
} ANTLR3TokenType;
#endif

#pragma mark Tokens
#ifndef TOKENLISTAlreadyDefined
#define TOKENLISTAlreadyDefined 1
#ifdef EOF
#undef EOF
#endif
#define EOF -1
#define ID 4
#define WS 5
#define STRING 6
#define ANONYMOUS_TEMPLATE 7
#define COMMENT 8
#define LINE_COMMENT 9
#define BIGSTRING 10
#define BIGSTRING_NO_NL 11
#define T_FALSE 12
#define T_TRUE 13
#define IF 14
#define ELSE 15
#define ELSEIF 16
#define ENDIF 17
#define SUPER 18
#define SEMI 19
#define BANG 20
#define ELLIPSIS 21
#define EQUALS 22
#define COLON 23
#define LPAREN 24
#define RPAREN 25
#define LBRACK 26
#define RBRACK 27
#define COMMA 28
#define DOT 29
#define LCURLY 30
//#define RCURLY 31
#define TEXT 32
//#define LDELIM 33
#define RDELIM 34
#define PIPE 35
#define OR 36
#define AND 37
#define INDENT 38
#define NEWLINE 39
#define AT 40
#define END 41
#define ARGS 42
#define ELEMENTS 43
#define EXEC_FUNC 44
#define EXPR 45
#define INCLUDE 46
#define INCLUDE_IND 47
#define INCLUDE_REGION 48
#define INCLUDE_SUPER 49
#define INCLUDE_SUPER_REGION 50
#define INDENTED_EXPR 51
#define LIST 52
#define MAP 53
#define OPTIONS 54
#define PROP 55
#define PROP_IND 56
#define REGION 57
#define SUBTEMPLATE 58
#define TNULL 59
#define TO_STR 60
#define ZIP 61
#endif

#undef RCURLY
#undef LDELIM
#ifndef REGION_END
#define REGION_END END
#endif

@implementation STLexer

static CommonToken *SKIP_TOK;
static char Token_EOF_CHAR = (char)-1;         // EOF char
static NSInteger EOF_TYPE_INT = TokenTypeEOF;  // EOF token type
static NSInteger RCURLY_INT = 31;
static NSInteger LDELIM_INT = 33;

+ (NSInteger) LDELIM
{
    return LDELIM_INT;
}

+ (NSInteger) RCURLY
{
    return RCURLY_INT;
}

+ (CommonToken *) SKIP
{
    return SKIP_TOK;
}

+ (char) Token_EOF
{
    return (char)Token_EOF_CHAR;
}

+ (NSInteger) EOF_TYPE
{
    return EOF_TYPE_INT;
}

+ (void) initialize
{
    SKIP_TOK = [CommonToken newToken:-1 Text:@"<skip>"];
}

+ (id) newSTLexer:(id<CharStream>)anInput
{
    return [[STLexer alloc] initWithInput:anInput];
}

+ (id) newSTLexer:(ErrorManager *)anErrMgr
            input:(id<CharStream>)anInput
    templateToken:(CommonToken *)aTemplateToken
{
    return [[STLexer alloc] init:anErrMgr input:anInput templateToken:aTemplateToken];
}

+ (id) newSTLexer:(ErrorManager *)anErrMgr
            input:(id<CharStream>)anInput
    templateToken:(CommonToken *)aTemplateToken
delimiterStartChar:(unichar)aStartChar
delimiterStopChar:(unichar)aStopChar
{
    return [[STLexer alloc] init:anErrMgr input:anInput templateToken:aTemplateToken delimiterStartChar:aStartChar delimiterStopChar:aStopChar];
}

- (id) initWithInput:(id<CharStream>)anInput
{
    self=[super init];
    if ( self != nil ) {
        delimiterStartChar = '<';
        delimiterStopChar = '>';
        scanningInsideExpr = NO;
        subtemplateDepth = 0;
        tokens = [[AMutableArray arrayWithCapacity:16] retain];
        errMgr = STGroup.DEFAULT_ERR_MGR;
        if ( errMgr ) [errMgr retain];
        input = anInput;
        if ( input ) [input retain];
        c = (unichar)[input LA:1];
        templateToken = nil;
    }
    return self;
}

- (id) init:(ErrorManager *)anErrMgr input:(id<CharStream>)anInput templateToken:(CommonToken *)aTemplateToken
{
    self=[super init];
    if ( self != nil ) {
        delimiterStartChar = '<';
        delimiterStopChar = '>';
        scanningInsideExpr = NO;
        subtemplateDepth = 0;
        tokens = [[AMutableArray arrayWithCapacity:16] retain];
        errMgr = anErrMgr;
        if ( errMgr ) [errMgr retain];
        input = anInput;
        if ( input ) [input retain];
        c = (unichar)[input LA:1];
        templateToken = aTemplateToken;
    }
    return self;
}

- (id) init:(ErrorManager *)anErrMgr input:(id<CharStream>)anInput templateToken:(CommonToken *)aTemplateToken delimiterStartChar:(unichar)aStartChar delimiterStopChar:(unichar)aStopChar
{
    self=[super init];
    if ( self != nil ) {
        delimiterStartChar = aStartChar;
        delimiterStopChar = aStopChar;
        scanningInsideExpr = NO;
        subtemplateDepth = 0;
        tokens = [[AMutableArray arrayWithCapacity:16] retain];
        errMgr = anErrMgr;
        if ( errMgr ) [errMgr retain];
        input = anInput;
        if ( input ) [input retain];
        c = (unichar)[input LA:1];
        templateToken = aTemplateToken;
        if ( templateToken ) [templateToken retain];
    }
    return self;
}


- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STLexer" );
#endif
    if ( errMgr ) [errMgr release];
    if ( input ) [input release];
    if ( tokens ) [tokens release];
    if ( templateToken ) [templateToken release];
    [super dealloc];
}

- (CommonToken *) nextToken
{
    CommonToken *t = nil;
    if ([tokens count] > 0) {
        t = [tokens objectAtIndex:0];
        [tokens removeObjectAtIndex:0];
        return t;
    }
    else {
        t = [self _nextToken];
    }
    return t;
}

/**
 * Ensure x is next character on the input stream
 */
- (void) match:(unichar)x
{
    if (c != x) {
        @throw [NSException exceptionWithName:@"No Viable Alt Exception" reason:[NSString stringWithFormat:@"%@: expecting '%c' found '%@'", [input getSourceName], x, [STLexer str:c]] userInfo:nil];
    }
    [self consume];
}

- (void) consume
{
    [input consume];
    c = (unichar)[input LA:1];
}

- (void) emit:(CommonToken *)token
{
    [tokens addObject:token];
}

- (CommonToken *) _nextToken
{
    
    while (YES) {
        startCharIndex = input.index;
        startLine = [input getLine];
        startCharPositionInLine = [input getCharPositionInLine];
        if (c == (unichar) EOF_TYPE_INT)
            return [self newToken:TokenTypeEOF];
        CommonToken *t;
        if (scanningInsideExpr)
            t = [self inside];
        else
            t = [self outside];
        if (t != SKIP_TOK)
            return t;
    }
}

- (CommonToken *) outside
{
    if ([input getCharPositionInLine] == 0 && (c == ' ' || c == '\t')) {
        while (c == ' ' || c == '\t') // scarf indent
            [self consume];
        if (c != (unichar) EOF_TYPE_INT)
            return [self newToken:INDENT];
        return [self newToken:TEXT];
    }
    if (c == delimiterStartChar) {
        [self consume];
        if (c == '!')  return [self mCOMMENT];
        if (c == '\\') return [self mESCAPE];
        scanningInsideExpr = YES;
        return [self newToken:LDELIM_INT];
    }
    if (c == '\r') {
        if ([input LA:2] == '\n')
            [self consume];
        [self consume];
        return [self newToken:NEWLINE];
    }
    if (c == '\n') {
        [self consume];
        return [self newToken:NEWLINE];
    }
    if (c == '}' && subtemplateDepth > 0) {
        scanningInsideExpr = YES;
        subtemplateDepth--;
        [self consume];
        return [self newTokenFromPreviousChar:RCURLY_INT];
    }
    return [self mTEXT];
}

- (CommonToken *) inside
{
    
    while (YES) {
        
        switch (c) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                [self consume];
                return SKIP_TOK;
            case '.':
                [self consume];
                if ([input LA:1] == '.' && [input LA:2] == '.') {
                    [self consume];
                    [self match:'.'];
                    return [self newToken:ELLIPSIS];
                }
                return [self newToken:DOT];
            case ',': [self consume]; return [self newToken:COMMA];
            case ':': [self consume]; return [self newToken:COLON];
            case ';': [self consume]; return [self newToken:SEMI];
            case '(': [self consume]; return [self newToken:LPAREN];
            case ')': [self consume]; return [self newToken:RPAREN];
            case '[': [self consume]; return [self newToken:LBRACK];
            case ']': [self consume]; return [self newToken:RBRACK];
            case '=': [self consume]; return [self newToken:EQUALS];
            case '!': [self consume]; return [self newToken:BANG];
            case '@':
                [self consume];
                if (c == 'e' && [input LA:2] == 'n' && [input LA:3] == 'd') {
                    [self consume]; [self consume]; [self consume];
                    return [self newToken:REGION_END];
                }
                return [self newToken:AT];
            case '"':
                return [self mSTRING];
            case '&': [self consume]; [self match:'&']; return [self newToken:AND];
            case '|': [self consume]; [self match:'|']; return [self newToken:OR];
            case '{': return [self subTemplate];
            default:
                if (c == delimiterStopChar) {
                    [self consume];
                    scanningInsideExpr = NO;
                    return [self newToken:RDELIM];
                }
                if ([STLexer isIDStartLetter:c]) {
                    CommonToken *anID = [self mID];
                    NSString *name = anID.text;
                    if ([name isEqualToString:@"if"])
                        return [self newToken:IF];
                    else if ([name isEqualToString:@"endif"])
                        return [self newToken:ENDIF];
                    else if ([name isEqualToString:@"else"])
                        return [self newToken:ELSE];
                    else if ([name isEqualToString:@"elseif"])
                        return [self newToken:ELSEIF];
                    else if ([name isEqualToString:@"super"])
                        return [self newToken:SUPER];
                    else if ([name isEqualToString:@"true"])
                        return [self newToken:T_TRUE];
                    else if ([name isEqualToString:@"false"])
                        return [self newToken:T_FALSE];
                    return anID;
                }
                NoViableAltException *re = [NoViableAltException newException:0 state:0 stream:input];
                re.line = startLine;
                re.charPositionInLine = startCharPositionInLine;
                [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid character '%c'", c] templateToken:templateToken e:re];
                if (c == (unichar)Token_EOF_CHAR) {
                    return [self newToken:EOF_TYPE_INT];
                }
                [self consume];
        }
    }
}

- (CommonToken *) subTemplate
{
    subtemplateDepth++;
    NSInteger m = [input mark];
    NSInteger curlyStartChar = startCharIndex;
    NSInteger curlyLine = startLine;
    NSInteger curlyPos = startCharPositionInLine;
    AMutableArray *argTokens = [AMutableArray arrayWithCapacity:16];
    [self consume];
    CommonToken *curly = [self newTokenFromPreviousChar:LCURLY];
    [self mWS];
    [argTokens addObject:[self mID]];
    [self mWS];
    while (c == ',') {
        [self consume];
        [argTokens addObject:[self newTokenFromPreviousChar:COMMA]];
        [self mWS];
        [argTokens addObject:[self mID]];
        [self mWS];
    }
    [self mWS];
    if (c == '|') {
        [self consume];
        [argTokens addObject:[self newTokenFromPreviousChar:PIPE]];
        if ([STLexer isWS:c])
            [self consume];
//        CommonToken *t;
        ArrayIterator *it = [ArrayIterator newIterator:argTokens];
        while ( [it hasNext] )
           [self emit:[it nextObject]];
//        for (CommonToken *t in argTokens)
//            [self emit:t];
        [input release:m];
        scanningInsideExpr = NO;
        startCharIndex = curlyStartChar;
        startLine = curlyLine;
        startCharPositionInLine = curlyPos;
        return curly;
    }
    [input rewind:m];
    startCharIndex = curlyStartChar;
    startLine = curlyLine;
    startCharPositionInLine = curlyPos;
    [self consume];
    scanningInsideExpr = NO;
    return curly;
}

- (CommonToken *) mESCAPE
{
    startCharIndex = input.index;
    startCharPositionInLine = [input getCharPositionInLine];
    [self consume];
    if (c == 'u') return [self mUNICODE];
    NSString *text = nil;
    NoViableAltException *e;
    
    switch (c) {
        case '\\': [self mLINEBREAK]; return SKIP_TOK;
        case 'n': text = @"\n"; break;
        case 't': text = @"\t"; break;
        case ' ': text = @" "; break;
        default:
            e = [NoViableAltException newException:0 state:0 stream:input];
            [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid escaped char: '%c'", c] templateToken:templateToken e:e];
            [self consume];
            [self match:delimiterStopChar];
            return SKIP_TOK;
    }
    [self consume];
    CommonToken *t = [self newToken:TEXT text:text pos:[input getCharPositionInLine]-2];
    [self match:delimiterStopChar];
    return t;
}

- (CommonToken *) mUNICODE
{
    [self consume];
    char chars[5];
    if (![STLexer isUnicodeLetter:c]) {
        NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
        [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid unicode char: '%c'", c] templateToken:templateToken e:e];
    }
    chars[0] = c;
    [self consume];
    if (![STLexer isUnicodeLetter:c]) {
        NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
        [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid unicode char: '%c'", c] templateToken:templateToken e:e];
    }
    chars[1] = c;
    [self consume];
    if (![STLexer isUnicodeLetter:c]) {
        NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
        [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid unicode char: '%c'", c] templateToken:templateToken e:e];
    }
    chars[2] = c;
    [self consume];
    if (![STLexer isUnicodeLetter:c]) {
        NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
        [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"invalid unicode char: '%c'", c] templateToken:templateToken e:e];
    }
    chars[3] = c;
    chars[4] = '\0';
        // ESCAPE kills >
        //NSString *utext = [NSString stringWithCString:chars encoding:NSASCIIStringEncoding];
    unichar uc = (unichar)[[NSString stringWithCString:chars encoding:NSASCIIStringEncoding] intValue];
    CommonToken *t = [self newToken:TEXT text:[NSString stringWithFormat:@"%4x", uc] pos:[input getCharPositionInLine]-6];
    [self consume];
    [self match:delimiterStopChar];
    return t;
}

- (CommonToken *) mTEXT
{
    BOOL modifiedText = NO;
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    
    while (c != (unichar) EOF_TYPE_INT && c != delimiterStartChar) {
        if (c == '\r' || c == '\n') break;
        if (c == '}' && subtemplateDepth > 0) break;
        if (c == '\\') {
            if ([input LA:2] == '\\') {
                [self consume]; [self consume]; [buf appendFormat:@"%c", '\\'];
                modifiedText = YES;
                continue;
            }
            if ([input LA:2] == delimiterStartChar || [input LA:2] == '}') {
                modifiedText = YES;
                [self consume];
                [buf appendFormat:@"%c", c];
                [self consume];
            }
            else {
                [buf appendFormat:@"%c", c];
                [self consume];
            }
            continue;
        }
        [buf appendFormat:@"%c", c];
        [self consume];
    }
    
    if (modifiedText)
        return [self newToken:TEXT text:buf];
    else
        return [self newToken:TEXT];
}


/**
 * ID  :   ('a'..'z'|'A'..'Z'|'_'|'/') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')* ;
 */
- (CommonToken *) mID
{
    startCharIndex = input.index;
    startLine = [input getLine];
    startCharPositionInLine = [input getCharPositionInLine];
    [self consume];
    while ([STLexer isIDLetter:c]) {
        [self consume];
    }
    return [self newToken:ID];
}


/**
 * STRING : '"' ( '\\' '"' | '\\' ~'"' | ~('\\'|'"') )* '"' ;
 */
- (CommonToken *) mSTRING
{
    BOOL sawEscape = NO;
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    [buf appendFormat:@"%c", c];
    [self consume];
    
    while (c != '"') {
        if (c == '\\') {
            sawEscape = YES;
            [self consume];
            switch (c) {
                case 'n': [buf appendFormat:@"%c", '\n']; break;
                case 'r': [buf appendFormat:@"%c", '\r']; break;
                case 't': [buf appendFormat:@"%c", '\t']; break;
                default:
                    [buf appendFormat:@"%c", c]; break;
            }
            [self consume];
            continue;
        }
        [buf appendFormat:@"%c", c];
        [self consume];
        if (c == (unichar) EOF_TYPE_INT) {
            RecognitionException *re = [MismatchedTokenException newException:'"' Stream:input];
            re.line = [input getLine];
            re.charPositionInLine = [input getCharPositionInLine];
            [errMgr lexerError:[input getSourceName] msg:@"EOF in string" templateToken:templateToken e:re];
        }
    }
    [buf appendFormat:@"%c", c];
    [self consume];
    if (sawEscape)
        return [self newToken:STRING text:buf];
    else
        return [self newToken:STRING];
}

- (void) mWS
{
    while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
        [self consume];
}

- (id) mCOMMENT
{
    [self match:'!'];
    while (!(c == '!' && [input LA:2] == delimiterStopChar)) {
        if (c == (unichar)EOF) {
            RecognitionException *re = [MismatchedTokenException newException:(NSInteger)'!' Stream:input];
            re.line = [input getLine];
            re.charPositionInLine = [input getCharPositionInLine];
            [errMgr lexerError:[input getSourceName] msg:[NSString stringWithFormat:@"Nonterminated comment starting at %d:%d: '!%c' missing", startLine, startCharPositionInLine, delimiterStopChar] templateToken:templateToken e:re];
        break;
        }
        [self consume];
    }
    [self consume]; // grab !>
    [self consume];
	return [self newToken:COMMENT];
}

- (void) mLINEBREAK
{
    [self match:'\\'];
    [self match:delimiterStopChar]; // only kill 2nd \ as outside() kills first on
    while (c == ' ' || c == '\t')
        [self consume];
    if (c == '\r') [self consume]; // scarf WS after <\\>
    if (c == '\n') [self match:'\n'];
    while (c == ' ' || c == '\t') [self consume]; // scarf any indent
}

+ (BOOL) isIDStartLetter:(unichar)c
{
    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
}

+ (BOOL) isIDLetter:(unichar)c
{
    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '/';
}

+ (BOOL) isWS:(unichar)c
{
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
}

+ (BOOL) isUnicodeLetter:(unichar)c
{
    return c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F' || c >= '0' && c <= '9';
}

- (CommonToken *) newToken:(NSInteger)ttype
{
    STToken *t = [STToken newToken:input Type:ttype Channel:TokenChannelDefault Start:startCharIndex Stop:input.index-1];
    [t setLine:startLine];
    [t setCharPositionInLine:startCharPositionInLine];
    return (CommonToken *)t;
}

- (CommonToken *) newTokenFromPreviousChar:(NSInteger)ttype
{
    STToken *t = [STToken newToken:input Type:ttype Channel:TokenChannelDefault Start:input.index-1 Stop:input.index-1];
    [t setLine:[input getLine]];
    [t setCharPositionInLine:[input getCharPositionInLine]-1];
    return (CommonToken *)t;
}

- (CommonToken *) newToken:(NSInteger)ttype text:(NSString *)text pos:(NSInteger)pos
{
    STToken *t = [STToken newToken:ttype Text:text];
    [t setStart:startCharIndex];
    [t setStop:input.index-1];
    [t setLine:[input getLine]];
    [t setCharPositionInLine:pos];
    return (CommonToken *)t;
}

- (CommonToken *) newToken:(NSInteger)ttype text:(NSString *)text
{
    CommonToken *t = [STToken newToken:ttype Text:text];
    [t setStart:startCharIndex];
    [t setStop:input.index - 1];
    [t setLine:startLine];
    [t setCharPositionInLine:startCharPositionInLine];
    return (CommonToken *)t;
}

- (NSString *) errorHeader
{
    return [NSString stringWithFormat:@"%d:%d", startLine, startCharPositionInLine];
}

- (NSString *) getSourceName
{
    return @"no idea";
}

- (id) copyWithZone:(NSZone *)aZone
{
    return self;
}

+ (NSString *) str:(NSInteger)aChar {
    if (aChar == (unichar) TokenTypeEOF)
        return @"<EOF>";
    return [NSString stringWithFormat:@"\\u%4x", (unichar)aChar];
}

@synthesize delimiterStartChar;
@synthesize delimiterStopChar;
@synthesize scanningInsideExpr;
@synthesize subtemplateDepth;
@synthesize errMgr;
@synthesize templateToken;
@synthesize input;
@synthesize c;
@synthesize startCharIndex;
@synthesize startLine;
@synthesize startCharPositionInLine;
@synthesize tokens;
//@synthesize errorHeader;
//@synthesize sourceName;

@end

@implementation STLexer_NO_NL

+ (STLexer_NO_NL *) newSTLexer_NO_NL:(ErrorManager *)anErrMgr
                               input:(id<CharStream>)anInput
                       templateToken:(CommonToken *)aTemplateToken
                  delimiterStartChar:(unichar)aStartChar
                   delimiterStopChar:(unichar)aStopChar
{
    return [[STLexer_NO_NL alloc] init:anErrMgr
                                input:anInput
                        templateToken:aTemplateToken
                   delimiterStartChar:aStartChar
                    delimiterStopChar:aStopChar];
}

- (id) init:(ErrorManager *)anErrMgr
      input:(id<CharStream>)anInput
      templateToken:(CommonToken *)aTemplateToken
      delimiterStartChar:(unichar)aStartChar
      delimiterStopChar:(unichar)aStopChar
{
    self=[super init];
    if ( self != nil ) {
        delimiterStartChar = aStartChar;
        delimiterStopChar = aStopChar;
        scanningInsideExpr = NO;
        subtemplateDepth = 0;
        tokens = [AMutableArray arrayWithCapacity:16];
        errMgr = anErrMgr;
        input = anInput;
        c = (unichar)[input LA:1];
        templateToken = aTemplateToken;
    }
    return self;
}

/** Throw out \n tokens inside BIGSTRING_NO_NL */
- (CommonToken *)nextToken
{
    CommonToken *t = [super nextToken];
	while ( t.type == NEWLINE ||
	        t.type == INDENT ) {
		t = [super nextToken];
	}
	return t;
}

@end
