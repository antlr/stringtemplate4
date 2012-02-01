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
#import "STGroup.h"
#import "ErrorManager.h"
#import "Misc.h"


@class STToken;
/**
 * We build STToken tokens instead of relying on CommonToken so we
 * can override toString(). It just converts token types to
 * token names like 23 to LDELIM.
 */

/**
 * This class represents the tokenizer for templates. It operates in two modes:
 * inside and outside of expressions. It behaves like an ANTLR TokenSource,
 * implementing nextToken().  Outside of expressions, we can return these
 * token types: TEXT, INDENT, LDELIM (start of expr), RCURLY (end of subtemplate),
 * and NEWLINE.  Inside of an expression, this lexer returns all of the tokens
 * needed by the STParser. From the parser's point of view, it can treat a
 * template as a simple stream of elements.
 * 
 * This class defines the token types and communicates these values to STParser.g
 * via STLexer.tokens file (which must remain consistent).
 */

@interface STLexer : NSObject <TokenSource> {
    
    /**
     * What char starts an expression?
     */
    unichar delimiterStartChar;
    unichar delimiterStopChar;
    
    /**
     * This keep track of the mode of the lexer. Are we inside or outside
     * an ST expression?
     */
    BOOL scanningInsideExpr;
    
    /**
     * To be able to properly track the inside/outside mode, we need to
     * track how deeply nested we are in some templates. Otherwise, we
     * know whether a '}' and the outermost subtemplate to send this back to
     * outside mode.
     */
    NSInteger subtemplateDepth;
    ErrorManager *errMgr;
    STToken *templateToken;
    id<CharStream> input;
    unichar c;
    
    /**
     * When we started token, track initial coordinates so we can properly
     * build token objects.
     */
    NSInteger startCharIndex;
    NSInteger startLine;
    NSInteger startCharPositionInLine;
    
    /**
     * Our lexer routines might have to emit more than a single token. We
     * buffer everything through this list.
     */
    AMutableArray *tokens;
}

+ (NSInteger) RCURLY;
+ (NSInteger) LDELIM;
+ (STToken *) SKIP;
+ (char) Token_EOF;
+ (NSInteger) EOF_TYPE;
+ (NSString *)str:(NSInteger)aChar;

+ (void) initialize;

+ (id) newSTLexer:(id<CharStream>)input;
+ (id) newSTLexer:(ErrorManager *)errMgr input:(id<CharStream>)input templateToken:(STToken *)templateToken;
+ (id) newSTLexer:(ErrorManager *)errMgr
            input:(id<CharStream>)input
    templateToken:(STToken *)templateToken
delimiterStartChar:(unichar)delimiterStartChar
delimiterStopChar:(unichar)delimiterStopChar;

- (id) initWithInput:(id<CharStream>)input;
- (id) init:(ErrorManager *)errMgr input:(id<CharStream>)input templateToken:(STToken *)templateToken;
- (id) init:(ErrorManager *)errMgr
      input:(id<CharStream>)input templateToken:(STToken *)templateToken
delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;

- (STToken *) nextToken;
- (void) match:(unichar)x;
- (void) consume;
- (void) emit:(STToken *)token;
- (STToken *) _nextToken;
- (STToken *) outside;
- (STToken *) inside;
- (STToken *) subTemplate;
- (STToken *) mESCAPE;
- (STToken *) mUNICODE;
- (STToken *) mTEXT;
- (STToken *) mID;
- (STToken *) mSTRING;
- (void) mWS;
- (STToken *) mCOMMENT;
- (void) mLINEBREAK;
+ (BOOL) isIDStartLetter:(unichar)c;
+ (BOOL) isIDLetter:(unichar)c;
+ (BOOL) isWS:(unichar)c;
+ (BOOL) isUnicodeLetter:(unichar)c;
- (STToken *) newToken:(NSInteger)ttype;
- (STToken *) newTokenFromPreviousChar:(NSInteger)ttype;
- (STToken *) newToken:(NSInteger)ttype text:(NSString *)text pos:(NSInteger)pos;
- (STToken *) newToken:(NSInteger)ttype text:(NSString *)text;
- (id) copyWithZone:(NSZone *)aZone;
- (NSString *) getSourceName;

@property (assign) unichar delimiterStartChar;
@property (assign) unichar delimiterStopChar;
@property (assign) BOOL scanningInsideExpr;
@property (assign) NSInteger subtemplateDepth;
@property (retain) ErrorManager *errMgr;
@property (retain) STToken *templateToken;
@property (retain) id<CharStream> input;
@property (assign) unichar c;
@property (assign) NSInteger startCharIndex;
@property (assign) NSInteger startLine;
@property (assign) NSInteger startCharPositionInLine;
@property (retain) AMutableArray *tokens;

//@property (nonatomic, retain, readonly) NSString *errorHeader;
//@property (nonatomic, retain, readonly) NSString *sourceName;

@end

@interface STLexer_NO_NL : STLexer {
}

+ (STLexer_NO_NL *) newSTLexer_NO_NL:(ErrorManager *)errMgr
                               input:(id<CharStream>)anInput
                       templateToken:(STToken *)aTemplateToken
                  delimiterStartChar:(unichar)aStartChar
                   delimiterStopChar:(unichar)aStopChar;

- (id) init:(ErrorManager *)anErrMgr
             input:(id<CharStream>)anInput                             
     templateToken:(STToken *)aTemplateToken
delimiterStartChar:(unichar)aStartChar
 delimiterStopChar:(unichar)aStopChar;

- (STToken *)nextToken;

@end
