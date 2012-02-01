/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** Build an AST from a single StringTemplate template */
parser grammar STParser;

options {
    tokenVocab=STLexer;
    TokenLabelType=ANTLRCommonToken;
    output=AST;
    ASTLabelType=ANTLRCommonTree;
    language=ObjC;
}

tokens {
    EXPR; OPTIONS; PROP; PROP_IND; INCLUDE; INCLUDE_IND; EXEC_FUNC; INCLUDE_SUPER;
    INCLUDE_SUPER_REGION; INCLUDE_REGION; TO_STR; LIST; MAP; ZIP; SUBTEMPLATE; ARGS;
    ELEMENTS; REGION; A_NULL;
    }

@header {
#import "Compiler.h"
#import "ErrorManager.h"
#import "ErrorType.h"
}

@memVars {
ErrorManager *errMgr;
ANTLRCommonToken *templateToken;
}

@methodsDecl {
+ (id) newSTParser:(id<ANTLRTokenStream>)anInput error:(ErrorManager *)anErrMgr token:(ANTLRCommonToken *)aTemplateToken;
- (id) init:(id<ANTLRTokenStream>)anInput error:(ErrorManager *)anErrMgr token:(ANTLRCommonToken *)aTemplateToken;
- (id) recoverFromMismatchedToken:(id<ANTLRIntStream>)anInput type:(NSInteger)ttype follow:(ANTLRBitSet *)follow;
}

@methods {
+ (id) newSTParser:(id<ANTLRTokenStream>)anInput error:(ErrorManager *)anErrMgr token:(ANTLRCommonToken *)aTemplateToken
{
    return [[STParser alloc] init:anInput error:anErrMgr token:aTemplateToken];
}

- (id) init:(id<ANTLRTokenStream>)anInput error:(ErrorManager *)anErrMgr token:(ANTLRCommonToken *)aTemplateToken
{
    if (self = [super initWithTokenStream:(id<ANTLRTokenStream>)anInput]) {
        errMgr = anErrMgr;
        templateToken = aTemplateToken;
    }
    return self;
}

- (id) recoverFromMismatchedToken:(id<ANTLRIntStream>)anInput type:(NSInteger)ttype follow:(ANTLRBitSet *)follow
{
    @throw [ANTLRMismatchedTokenException newANTLRMismatchedTokenException:ttype Stream:anInput];
}
}

@rulecatch {
    @catch (ANTLRRecognitionException *re) {
        @throw re;
    }
}

templateAndEOF : template EOF -> template? ;

template : element* ;

element
    :   INDENT element -> ^(INDENT element)
    |   ifstat
    |   exprTag
    |   text
    |   region
    |   NEWLINE
    ;

text : TEXT ;

exprTag
    :   LDELIM expr ( ';' exprOptions )? RDELIM
        -> ^(EXPR[$LDELIM,@"EXPR"] expr exprOptions?)
    ;

region : LDELIM '@' ID RDELIM template LDELIM '@end' RDELIM -> ^(REGION ID template) ;

subtemplate
    :   lc='{' (ids+= ID ( ',' ids+= ID )* '|' )? template INDENT? '}'
        // ignore final INDENT before } as it's not part of outer indent
        -> ^(SUBTEMPLATE[$lc,@"SUBTEMPLATE"] ^(ARGS $ids)* template)
    ;

ifstat // ignore INDENTs in front of elseif ...
    :   LDELIM 'if' '(' c1=conditional ')' RDELIM
            t1=template
            ( INDENT? LDELIM 'elseif' '(' c2+=conditional ')' RDELIM t2+=template )*
            ( INDENT? LDELIM 'else' RDELIM t3=template )?
            INDENT? endif= LDELIM 'endif'
        RDELIM
        // kill \n for <endif> on line by itself if multi-line IF
        ({ [$ifstat.start getLine] != [[input LT:1] getLine] }?=> NEWLINE)?
        -> ^('if' $c1 $t1? ^('elseif' $c2 $t2)* ^('else' $t3?)?)
    ;

conditional : andConditional ( '||'^ andConditional )* ;

andConditional : notConditional ( '&&'^ notConditional )* ;

notConditional : ( '!'^ notConditionalExpr | '!'^ '('! conditional ')'! | memberExpr );

notConditionalExpr
    :   (ID->ID)
        (   p='.' prop=ID                       -> ^(PROP[$p,@"PROP"] $notConditionalExpr $prop)
        |   p='.' '(' mapExpr ')'               -> ^(PROP_IND[$p,@"PROP_IND"] $notConditionalExpr mapExpr)
        )*
    ;

exprOptions : option ( ',' option )* -> ^(OPTIONS option*) ;

option
@init {
    NSString *IDstr = [[input LT:1] getText];
    NSString *defVal = [[Compiler defaultOptionValues] getName:IDstr];
    BOOL validOption = ([[Compiler supportedOptions] getName:IDstr] != nil);
}
    :   ID
        {
        if ( !validOption ) {
            [errMgr compileTimeError:ErrorType.NO_SUCH_OPTION templateToken:templateToken t:$ID arg:$ID.text];
        }
        }
        (   '=' exprNoComma                     -> {validOption}? ^('=' ID exprNoComma)
                                                ->
        |   {
            if ( defVal==nil ) {
                [errMgr compileTimeError:ErrorType.NO_DEFAULT_VALUE templateToken:templateToken t:$ID  arg:$ID.text];
            }
            }
                                                -> {validOption&&defVal!=nil}?
                                                   ^(EQUALS[@"="] ID STRING[$ID, defVal)
                                                ->
        )
    ;

exprNoComma
    :   memberExpr
        ( ':' mapTemplateRef                    -> ^(MAP memberExpr mapTemplateRef)
        |                                       -> memberExpr
        )
    ;

expr : mapExpr ;

// more complicated than necessary to avoid backtracking, which ruins
// error handling
mapExpr
    :   memberExpr
        ( (c=',' memberExpr)+ col=':' mapTemplateRef
                                                -> ^(ZIP[$col] ^(ELEMENTS memberExpr+) mapTemplateRef)
        |                                       -> memberExpr
        )
        (   {if ($x != nil) [$x clear];} // don't keep queueing x; new list for each iteration
            col=':' x+=mapTemplateRef ({$c==nil}?=> ',' x+=mapTemplateRef )*
                                                -> ^(MAP[$col] $mapExpr $x+)
        )*
    ;

/**
expr:template(args)  apply template to expr
expr:{arg | ...}     apply subtemplate to expr
expr:(e)(args)       convert e to a string template name and apply to expr
*/
mapTemplateRef
    :   ID '(' args ')'                         -> ^(INCLUDE ID args?)
    |   subtemplate
    |   lp='(' mapExpr rp=')' '(' argExprList? ')'-> ^(INCLUDE_IND mapExpr argExprList?)
    ;

memberExpr
    :   (includeExpr->includeExpr)
        (   p='.' ID                            -> ^(PROP[$p,@"PROP"] $memberExpr ID)
        |   p='.' '(' mapExpr ')'               -> ^(PROP_IND[$p,@"PROP_IND"] $memberExpr mapExpr)
        )*
    ;

includeExpr
options {k=2;} // prevent full LL(*), which fails, falling back on k=1; need k=2
    :   {[[Compiler funcs] getName:[[input LT:1] getText]]}? // predefined function
        ID '(' expr? ')'                        -> ^(EXEC_FUNC ID expr?)
    |   'super' '.' ID '(' args ')'             -> ^(INCLUDE_SUPER ID args?)
    |   ID '(' args ')'                         -> ^(INCLUDE ID args?)
    |   '@' 'super' '.' ID '(' rp=')'           -> ^(INCLUDE_SUPER_REGION ID)
    |   '@' ID '(' rp=')'                       -> ^(INCLUDE_REGION ID)
    |   primary
    ;

primary
    :   ID
    |   STRING
    |   subtemplate
    |   list
    |   lp='(' expr ')'
        (   '(' argExprList? ')'                -> ^(INCLUDE_IND[$lp] expr argExprList?)
        |                                       -> ^(TO_STR[$lp] expr)
        )
    ;

args:   argExprList
    |   namedArg ( ',' namedArg )* -> namedArg+
    |
    ;

argExprList : arg ( ',' arg )* -> arg+ ;

arg : exprNoComma ;

namedArg : ID '=' arg -> ^('=' ID arg) ;

list:   {[input LA:2] == RBRACK}? // hush warning; [] special case
        lb='[' ']' -> LIST[$lb]
    |   lb='[' listElement ( ',' listElement )* ']' -> ^(LIST[$lb] listElement*)
    ;

listElement : exprNoComma | -> A_NULL ;
