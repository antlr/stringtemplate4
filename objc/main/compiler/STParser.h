// $ANTLR ${project.version} ${buildNumber} /Users/acondit/source/antlr3/acondit_localhost/code/ST4/objc/main/compiler/STParser.g 2011-05-09 15:08:49

/* =============================================================================
 * Standard antlr3 OBJC runtime definitions
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
#import "Compiler.h"
#import "ErrorManager.h"
#import "ErrorType.h"

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

#ifdef DONTUSENOMO
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
#define RCURLY 31
#define TEXT 32
#define LDELIM 33
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
#pragma mark Dynamic Global Scopes globalAttributeScopeInterface
#pragma mark Dynamic Rule Scopes ruleAttributeScopeInterface
/* start of ruleAttributeScopeInterface */

@interface conditional_Scope : SymbolsScope {
    BOOL inside;

}

/* start property declarations */

@property (assign, getter=getinside, setter=setinside:) BOOL inside;

/* start method declarations */

+ (conditional_Scope *)newconditional_Scope;
- (id) init;
- (BOOL)getinside;
- (void)setinside:(BOOL)aVal;

@end /* end of ruleAttributeScopeInterface */


#pragma mark Rule Return Scopes returnScopeInterface
/* returnScopeInterface STParser_templateAndEOF_return */
@interface STParser_templateAndEOF_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_templateAndEOF_return *)newSTParser_templateAndEOF_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_template_return */
@interface STParser_template_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_template_return *)newSTParser_template_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_element_return */
@interface STParser_element_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_element_return *)newSTParser_element_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_singleElement_return */
@interface STParser_singleElement_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_singleElement_return *)newSTParser_singleElement_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_compoundElement_return */
@interface STParser_compoundElement_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_compoundElement_return *)newSTParser_compoundElement_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_exprTag_return */
@interface STParser_exprTag_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_exprTag_return *)newSTParser_exprTag_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_region_return */
@interface STParser_region_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_region_return *)newSTParser_region_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_subtemplate_return */
@interface STParser_subtemplate_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_subtemplate_return *)newSTParser_subtemplate_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_ifstat_return */
@interface STParser_ifstat_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_ifstat_return *)newSTParser_ifstat_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_conditional_return */
@interface STParser_conditional_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_conditional_return *)newSTParser_conditional_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_andConditional_return */
@interface STParser_andConditional_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_andConditional_return *)newSTParser_andConditional_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_notConditional_return */
@interface STParser_notConditional_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_notConditional_return *)newSTParser_notConditional_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_notConditionalExpr_return */
@interface STParser_notConditionalExpr_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_notConditionalExpr_return *)newSTParser_notConditionalExpr_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_exprOptions_return */
@interface STParser_exprOptions_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_exprOptions_return *)newSTParser_exprOptions_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_option_return */
@interface STParser_option_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_option_return *)newSTParser_option_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_exprNoComma_return */
@interface STParser_exprNoComma_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_exprNoComma_return *)newSTParser_exprNoComma_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_expr_return */
@interface STParser_expr_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_expr_return *)newSTParser_expr_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_mapExpr_return */
@interface STParser_mapExpr_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_mapExpr_return *)newSTParser_mapExpr_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_mapTemplateRef_return */
@interface STParser_mapTemplateRef_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_mapTemplateRef_return *)newSTParser_mapTemplateRef_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_memberExpr_return */
@interface STParser_memberExpr_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_memberExpr_return *)newSTParser_memberExpr_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_includeExpr_return */
@interface STParser_includeExpr_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_includeExpr_return *)newSTParser_includeExpr_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_primary_return */
@interface STParser_primary_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_primary_return *)newSTParser_primary_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_args_return */
@interface STParser_args_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_args_return *)newSTParser_args_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_argExprList_return */
@interface STParser_argExprList_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_argExprList_return *)newSTParser_argExprList_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_arg_return */
@interface STParser_arg_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_arg_return *)newSTParser_arg_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_namedArg_return */
@interface STParser_namedArg_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_namedArg_return *)newSTParser_namedArg_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_list_return */
@interface STParser_list_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_list_return *)newSTParser_list_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */


/* returnScopeInterface STParser_listElement_return */
@interface STParser_listElement_return : ParserRuleReturnScope { /* returnScopeInterface line 1838 */
/* AST returnScopeInterface.memVars */
CommonTree *tree; /* ObjC start of memVars() */
}
/* start property declarations */
/* AST returnScopeInterface.properties */
@property (retain, getter=getTree, setter=setTree:) CommonTree *tree;
/* start of method declarations */

+ (STParser_listElement_return *)newSTParser_listElement_return;
/* this is start of set and get methods */
/* AST returnScopeInterface.methodsDecl */
- (CommonTree *)getTree;

- (void) setTree:(CommonTree *)aTree;
  /* methodsDecl */
@end /* end of returnScopeInterface interface */



/* Interface grammar class */
@interface STParser  : Parser { /* line 572 */
#pragma mark Dynamic Rule Scopes ruleAttributeScopeDecl
#pragma mark Dynamic Global Rule Scopes globalAttributeScopeMemVar


/* ObjC start of actions.(actionScope).memVars */

conditional_Scope *conditional_scope;
ErrorManager *errMgr;
STToken *templateToken;

/* ObjC end of actions.(actionScope).memVars */
/* ObjC start of memVars */
/* AST parserHeaderFile.memVars */
NSInteger ruleLevel;
NSArray *ruleNames;
  /* AST super.memVars */
/* AST parserMemVars */
id<TreeAdaptor> treeAdaptor;   /* AST parserMemVars */
/* ObjC end of memVars */

 }

/* ObjC start of actions.(actionScope).properties */

@property (retain) conditional_Scope *conditional_scope;
@property (retain, getter=getErrorManager, setter=setErrorManager:) ErrorManager *errMgr;
@property (retain, getter=getTemplateToken, setter=setTemplateToken:) STToken *templateToken;

/* ObjC end of actions.(actionScope).properties */
/* ObjC start of properties */
/* AST parserHeaderFile.properties */
  /* AST super.properties */
/* AST parserProperties */
@property (retain, getter=getTreeAdaptor, setter=setTreeAdaptor:) id<TreeAdaptor> treeAdaptor;   /* AST parserproperties */
/* ObjC end of properties */

+ (void) initialize;
+ (id) newSTParser:(id<TokenStream>)aStream;
/* ObjC start of actions.(actionScope).methodsDecl */

+ (id) newSTParser:(id<TokenStream>)anInput error:(ErrorManager *)anErrMgr token:(STToken *)aTemplateToken;
- (id) init:(id<TokenStream>)anInput error:(ErrorManager *)anErrMgr token:(STToken *)aTemplateToken;
- (id) recoverFromMismatchedToken:(id<IntStream>)anInput type:(NSInteger)ttype follow:(ANTLRBitSet *)follow;

/* ObjC end of actions.(actionScope).methodsDecl */

/* ObjC start of methodsDecl */
/* AST parserHeaderFile.methodsDecl */
  /* AST super.methodsDecl */
/* AST parserMethodsDecl */
- (id<TreeAdaptor>) getTreeAdaptor;
- (void) setTreeAdaptor:(id<TreeAdaptor>)theTreeAdaptor;   /* AST parsermethodsDecl */
/* ObjC end of methodsDecl */

- (STParser_templateAndEOF_return *)templateAndEOF; 
- (STParser_template_return *)template; 
- (STParser_element_return *)element; 
- (STParser_singleElement_return *)singleElement; 
- (STParser_compoundElement_return *)compoundElement; 
- (STParser_exprTag_return *)exprTag; 
- (STParser_region_return *)region; 
- (STParser_subtemplate_return *)subtemplate; 
- (STParser_ifstat_return *)ifstat; 
- (STParser_conditional_return *)conditional; 
- (STParser_andConditional_return *)andConditional; 
- (STParser_notConditional_return *)notConditional; 
- (STParser_notConditionalExpr_return *)notConditionalExpr; 
- (STParser_exprOptions_return *)exprOptions; 
- (STParser_option_return *)option; 
- (STParser_exprNoComma_return *)exprNoComma; 
- (STParser_expr_return *)expr; 
- (STParser_mapExpr_return *)mapExpr; 
- (STParser_mapTemplateRef_return *)mapTemplateRef; 
- (STParser_memberExpr_return *)memberExpr; 
- (STParser_includeExpr_return *)includeExpr; 
- (STParser_primary_return *)primary; 
- (STParser_args_return *)args; 
- (STParser_argExprList_return *)argExprList; 
- (STParser_arg_return *)arg; 
- (STParser_namedArg_return *)namedArg; 
- (STParser_list_return *)list; 
- (STParser_listElement_return *)listElement; 


@end /* end of STParser interface */

/** Build an AST from a single StringTemplate template */