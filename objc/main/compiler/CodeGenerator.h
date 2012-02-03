// $ANTLR 3.4 /Users/acondit/source/antlr/code/stringtemplate4/objc/main/compiler/CodeGenerator.g 2012-02-01 18:16:42

/* =============================================================================
 * Standard antlr3 OBJC runtime definitions
 */
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
/* End of standard antlr3 runtime definitions
 * =============================================================================
 */

/* treeParserHeaderFile */
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
#import <ANTLR/CommonToken.h>
#import "Compiler.h"
#import "CompiledST.h"
#import "CompilationState.h"
#import "ErrorManager.h"
#import "Bytecode.h"
#import "Misc.h"

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
#pragma mark Dynamic Global Scopes globalAttributeScopeInterface
#pragma mark Dynamic Rule Scopes ruleAttributeScopeInterface
/* start of ruleAttributeScopeInterface */
@interface template_Scope : SymbolsScope {
 CompilationState * cstate;
 
}

/* start property declarations */
@property (assign, getter=getcstate, setter=setcstate:) CompilationState * cstate;

/* start method declarations */
+ (template_Scope *)newtemplate_Scope;
- (id) init;
- (CompilationState *)getcstate;
- (void)setcstate:(CompilationState *)aVal;

@end /* end of ruleAttributeScopeInterface */


#pragma mark Rule Return Scopes returnScopeInterface
/* returnScopeInterface CodeGenerator_region_return */
@interface CodeGenerator_region_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

 NSString * name;
 

}
/* start property declarations */


@property (assign, getter=getname, setter=setname:) NSString * name;


/* start of method declarations */

+ (CodeGenerator_region_return *)newCodeGenerator_region_return;
/* this is start of set and get methods */
  /* methodsDecl */

/* start of iterated get and set functions */

- (NSString *)getname;
- (void)setname:(NSString *)aVal;


@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_subtemplate_return */
@interface CodeGenerator_subtemplate_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

 NSString * name;

 NSInteger nargs;
 

}
/* start property declarations */


@property (assign, getter=getname, setter=setname:) NSString * name;

@property (assign, getter=getnargs, setter=setnargs:) NSInteger nargs;


/* start of method declarations */

+ (CodeGenerator_subtemplate_return *)newCodeGenerator_subtemplate_return;
/* this is start of set and get methods */
  /* methodsDecl */

/* start of iterated get and set functions */

- (NSString *)getname;
- (void)setname:(NSString *)aVal;

- (NSInteger)getnargs;
- (void)setnargs:(NSInteger)aVal;


@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_conditional_return */
@interface CodeGenerator_conditional_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

}
/* start property declarations */


/* start of method declarations */

+ (CodeGenerator_conditional_return *)newCodeGenerator_conditional_return;
/* this is start of set and get methods */
  /* methodsDecl */

@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_mapTemplateRef_return */
@interface CodeGenerator_mapTemplateRef_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

}
/* start property declarations */


/* start of method declarations */

+ (CodeGenerator_mapTemplateRef_return *)newCodeGenerator_mapTemplateRef_return;
/* this is start of set and get methods */
  /* methodsDecl */

@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_includeExpr_return */
@interface CodeGenerator_includeExpr_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

}
/* start property declarations */


/* start of method declarations */

+ (CodeGenerator_includeExpr_return *)newCodeGenerator_includeExpr_return;
/* this is start of set and get methods */
  /* methodsDecl */

@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_primary_return */
@interface CodeGenerator_primary_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

}
/* start property declarations */


/* start of method declarations */

+ (CodeGenerator_primary_return *)newCodeGenerator_primary_return;
/* this is start of set and get methods */
  /* methodsDecl */

@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_args_return */
@interface CodeGenerator_args_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

 NSInteger n;

 BOOL namedArgs;

 BOOL passThru;
 

}
/* start property declarations */


@property (assign, getter=getn, setter=setn:) NSInteger n;

@property (assign, getter=getnamedArgs, setter=setnamedArgs:) BOOL namedArgs;

@property (assign, getter=getpassThru, setter=setpassThru:) BOOL passThru;


/* start of method declarations */

+ (CodeGenerator_args_return *)newCodeGenerator_args_return;
/* this is start of set and get methods */
  /* methodsDecl */

/* start of iterated get and set functions */

- (NSInteger)getn;
- (void)setn:(NSInteger)aVal;

- (BOOL)getnamedArgs;
- (void)setnamedArgs:(BOOL)aVal;

- (BOOL)getpassThru;
- (void)setpassThru:(BOOL)aVal;


@end /* end of returnScopeInterface interface */


/* returnScopeInterface CodeGenerator_listElement_return */
@interface CodeGenerator_listElement_return : TreeRuleReturnScope { /* returnScopeInterface line 1838 */
 /* ObjC start of memVars() */

}
/* start property declarations */


/* start of method declarations */

+ (CodeGenerator_listElement_return *)newCodeGenerator_listElement_return;
/* this is start of set and get methods */
  /* methodsDecl */

@end /* end of returnScopeInterface interface */



/* Interface grammar class */
@interface CodeGenerator  : TreeParser { /* line 572 */
#pragma mark Dynamic Rule Scopes ruleAttributeScopeDecl
#pragma mark Dynamic Global Rule Scopes globalAttributeScopeMemVar


/* ObjC start of actions.(actionScope).memVars */

	template_Scope *template_scope;
	NSString *outermostTemplateName;	// name of overall template
	CompiledST *outermostImpl;
	CommonToken *templateToken;			    // overall template token
	NSString *template;  				// overall template text
	ErrorManager *errMgr;

/* ObjC end of actions.(actionScope).memVars */
/* ObjC start of memVars */
/* ObjC end of memVars */

 }

/* ObjC start of actions.(actionScope).properties */

	@property(retain) template_Scope *template_scope;
	@property(retain) NSString *outermostTemplateName; // name of overall template
	@property(retain) CompiledST *outermostImpl;
	@property(retain) CommonToken *templateToken;// overall template token
	@property(retain) NSString *template;    // overall template text
	@property(retain) ErrorManager *errMgr;

/* ObjC end of actions.(actionScope).properties */
/* ObjC start of properties */
/* ObjC end of properties */

+ (void) initialize;
+ (id) newCodeGenerator:(id<TreeNodeStream>)aStream;
/* ObjC start of actions.(actionScope).methodsDecl */

+ (id) newCodeGenerator:(id<TreeNodeStream>)input
                 errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken;

- (id) init:(id<TreeNodeStream>)input
                     errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken;

- (void) dealloc;
// convience funcs to hide offensive sending of emit messages to
// CompilationState temp data object.

- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)arg;
- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)arg;
- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)anArg arg2:(NSInteger)anArg2;
- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)s arg2:(NSInteger)anArg;
- (void) emit:(short)anOpcode;
- (void) emit:(CommonTree *)opAST opcode:(short)anOpcode;
- (void) insert:(NSInteger)addr opcode:(short)anOpcode s:(NSString *)s;
- (void) setOption:(CommonTree *)anID;
- (void) write:(NSInteger)addr value:(short)value;
- (NSInteger) address;
- (void) func:(CommonTree *)aTree;
- (void) refAttr:(CommonTree *)aTree;
- (NSInteger) defineString:(NSString *)s;

/* ObjC end of actions.(actionScope).methodsDecl */

/* ObjC start of methodsDecl */
/* ObjC end of methodsDecl */

- (void)templateAndEOF; 
- (CompiledST *)template:(NSString *)name arg1:(AMutableArray *)args ; 
- (void)chunk; 
- (void)element; 
- (void)singleElement; 
- (void)compoundElement:(CommonTree *)indent ; 
- (void)exprElement; 
- (CodeGenerator_region_return *)region:(CommonTree *)indent ; 
- (CodeGenerator_subtemplate_return *)subtemplate; 
- (void)ifstat:(CommonTree *)indent ; 
- (CodeGenerator_conditional_return *)conditional; 
- (void)exprOptions; 
- (void)option; 
- (void)expr; 
- (void)prop; 
- (CodeGenerator_mapTemplateRef_return *)mapTemplateRef:(NSInteger)num_exprs ; 
- (CodeGenerator_includeExpr_return *)includeExpr; 
- (CodeGenerator_primary_return *)primary; 
- (void)arg; 
- (CodeGenerator_args_return *)args; 
- (void)list; 
- (CodeGenerator_listElement_return *)listElement; 


@end /* end of CodeGenerator interface */

