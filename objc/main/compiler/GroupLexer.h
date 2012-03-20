// $ANTLR 3.4 /Users/acondit/source/antlr/code/stringtemplate4/objc/main/compiler/Group.g 2012-03-19 17:03:10

/* =============================================================================
 * Standard antlr OBJC runtime definitions
 */
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
/* End of standard antlr3 runtime definitions
 * =============================================================================
 */

/* =============================================================================
 * This is what the grammar programmer asked us to put at the top of every file.
 */

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
#import "ErrorType.h"
#import "STLexer.h"

@class STGroup;

/* End of Header action.
 * =============================================================================
 */

/* Start cyclicDFAInterface */
#pragma mark Cyclic DFA interface start DFA8
@interface DFA8 : DFA {
}
+ (DFA8 *) newDFA8WithRecognizer:(BaseRecognizer *)theRecognizer;
- initWithRecognizer:(BaseRecognizer *)recognizer;
@end /* end of DFA8 interface  */

#pragma mark Cyclic DFA interface end DFA8


#pragma mark Rule return scopes Interface start
#pragma mark Rule return scopes Interface end
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
#define T__14 14
#define T__15 15
#define T__16 16
#define T__17 17
#define T__18 18
#define T__19 19
#define T__20 20
#define T__21 21
#define T__22 22
#define T__23 23
#define T__24 24
#define T__25 25
#define T__26 26
#define T__27 27
#define T__28 28
#define T__29 29
#endif
/* interface lexer class */
@interface GroupLexer : Lexer { // line 283
DFA8 *dfa8;
/* ObjC start of actions.lexer.memVars */

STGroup *group;

/* ObjC end of actions.lexer.memVars */
}
+ (void) initialize;
+ (GroupLexer *)newGroupLexerWithCharStream:(id<CharStream>)anInput;
/* ObjC start actions.lexer.methodsDecl */

@property (retain, getter=getGroup, setter=setGroup:) STGroup *group;

- (void) reportError:(RecognitionException *)e;
- (NSString *) getSourceName;

/* ObjC end actions.lexer.methodsDecl */
- (void) mT__14 ; 
- (void) mT__15 ; 
- (void) mT__16 ; 
- (void) mT__17 ; 
- (void) mT__18 ; 
- (void) mT__19 ; 
- (void) mT__20 ; 
- (void) mT__21 ; 
- (void) mT__22 ; 
- (void) mT__23 ; 
- (void) mT__24 ; 
- (void) mT__25 ; 
- (void) mT__26 ; 
- (void) mT__27 ; 
- (void) mT__28 ; 
- (void) mT__29 ; 
- (void) mT_TRUE ; 
- (void) mT_FALSE ; 
- (void) mID ; 
- (void) mSTRING ; 
- (void) mBIGSTRING_NO_NL ; 
- (void) mBIGSTRING ; 
- (void) mANONYMOUS_TEMPLATE ; 
- (void) mCOMMENT ; 
- (void) mLINE_COMMENT ; 
- (void) mWS ; 
- (void) mTokens ; 

@end /* end of GroupLexer interface */

