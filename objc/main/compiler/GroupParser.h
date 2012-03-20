// $ANTLR 3.4 /Users/acondit/source/antlr/code/stringtemplate4/objc/main/compiler/Group.g 2012-03-19 17:03:10

/* =============================================================================
 * Standard antlr OBJC runtime definitions
 */
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
/* End of standard antlr3 runtime definitions
 * =============================================================================
 */

/* parserHeaderFile */
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
#import "STGroup.h"
#import "ErrorType.h"
#import "STLexer.h"
#import "Misc.h"
#import "GroupLexer.h"
#import "FormalArgument.h"
#import "ACNumber.h"

/* End of Header action.
 * =============================================================================
 */

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
#pragma mark Dynamic Global Scopes globalAttributeScopeInterface
#pragma mark Dynamic Rule Scopes ruleAttributeScopeInterface
/* start of ruleAttributeScopeInterface */
@interface formalArgs_Scope : SymbolsScope {
BOOL hasOptionalParameter;
 
}

/* start property declarations */
@property (assign, getter=gethasOptionalParameter, setter=sethasOptionalParameter:) BOOL hasOptionalParameter;

/* start method declarations */
+ (formalArgs_Scope *)newformalArgs_Scope;
- (id) init;
- (BOOL)gethasOptionalParameter;
- (void)sethasOptionalParameter:(BOOL)aVal;

@end /* end of ruleAttributeScopeInterface */


#pragma mark Rule Return Scopes returnScopeInterface

/* Interface grammar class */
@interface GroupParser  : Parser { /* line 572 */
#pragma mark Dynamic Rule Scopes ruleAttributeScopeDecl
#pragma mark Dynamic Global Rule Scopes globalAttributeScopeMemVar


/* ObjC start of actions.(actionScope).memVars */

STGroup *group;

/* ObjC end of actions.(actionScope).memVars */
/* ObjC start of memVars */
/* ObjC end of memVars */

 }

/* ObjC start of actions.(actionScope).properties */

@property (retain) STGroup *group;

/* ObjC end of actions.(actionScope).properties */
/* ObjC start of properties */
/* ObjC end of properties */

+ (void) initialize;
+ (GroupParser *) newGroupParser:(id<TokenStream>)aStream;
/* ObjC start of actions.(actionScope).methodsDecl */

+ (NSInteger) TANONYMOUS_TEMPLATE;
+ (NSInteger) TBIGSTRING;
+ (NSInteger) TBIGSTRING_NO_NL;
+ (NSInteger) TID;
+ (NSInteger) TTRUE;
- (void) displayRecognitionError:(AMutableArray *) tokenNames e:(RecognitionException *)e;
- (NSString *) getSourceName;
- (void) error:(NSString *)msg;
- (NSString *) getErrorMessage:(NSException *)e TokenNames:(AMutableArray *)TokenNames;

/* ObjC end of actions.(actionScope).methodsDecl */

/* ObjC start of methodsDecl */
/* ObjC end of methodsDecl */

- (void)group:(STGroup *)aGroup arg1:(NSString *)prefix ; 
- (void)oldStyleHeader; 
- (NSString *)groupName; 
- (void)delimiters; 
- (void)def:(NSString *)prefix ; 
- (void)templateDef:(NSString *)prefix ; 
- (AMutableArray *)formalArgs; 
- (void)formalArg:(AMutableArray *)args ; 
- (void)dictDef; 
- (AMutableDictionary *)dict; 
- (void)dictPairs:(AMutableDictionary *)mapping ; 
- (void)defaultValuePair:(AMutableDictionary *)mapping ; 
- (void)keyValuePair:(AMutableDictionary *)mapping ; 
- (id)keyValue; 


@end /* end of GroupParser interface */

