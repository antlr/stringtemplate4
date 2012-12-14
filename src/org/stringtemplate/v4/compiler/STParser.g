/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
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

/** Build an AST from a single StringTemplate template */
parser grammar STParser;

options {
	language=Java;
	tokenVocab=STLexer;
	TokenLabelType=CommonToken;
	output=AST;
	ASTLabelType=CommonTree;
}

tokens {
	EXPR; OPTIONS; PROP; PROP_IND; INCLUDE; INCLUDE_IND; EXEC_FUNC; INCLUDE_SUPER;
	INCLUDE_SUPER_REGION; INCLUDE_REGION; TO_STR; LIST; MAP; ZIP; SUBTEMPLATE; ARGS;
	ELEMENTS; REGION; NULL; INDENTED_EXPR;
	}

@header {
package org.stringtemplate.v4.compiler;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
}

@members {
ErrorManager errMgr;
Token templateToken;
public STParser(TokenStream input, ErrorManager errMgr, Token templateToken) {
	this(input);
	this.errMgr = errMgr;
	this.templateToken = templateToken;
}
@Override
protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
	throws RecognitionException
{
	throw new MismatchedTokenException(ttype, input);
}
}

@rulecatch {
   catch (RecognitionException re) { throw re; }
}

templateAndEOF : template EOF -> template? ;

template : element* ;

element
	:	{input.LT(1).getCharPositionInLine()==0}? INDENT? COMMENT NEWLINE -> // throw away
	|	INDENT singleElement -> ^(INDENTED_EXPR INDENT singleElement?) // singleElement is optional to handle error returning nil
	|	singleElement
	|	compoundElement
	;

singleElement
	:	exprTag
	|	TEXT
	|	NEWLINE
	|	COMMENT! // throw away
	;

compoundElement
	:	ifstat
	|	region
	;

exprTag
	:	LDELIM expr ( ';' exprOptions )? RDELIM
		-> ^(EXPR[$LDELIM,"EXPR"] expr exprOptions?)
	;

region
@init {Token indent=null;}
	:	i=INDENT? x=LDELIM '@' ID RDELIM {if (input.LA(1)!=NEWLINE) indent=$i;}
		template
		INDENT? LDELIM '@end' RDELIM
		// kill \n for <@end> on line by itself if multi-line embedded region
		({$region.start.getLine()!=input.LT(1).getLine()}?=> NEWLINE)?
		-> {indent!=null}?
		   ^(INDENTED_EXPR $i ^(REGION[$x] ID template?))
		->                    ^(REGION[$x] ID template?)
	;

subtemplate
	:	lc='{' (ids+= ID ( ',' ids+= ID )* '|' )? template INDENT? '}'
		// ignore final INDENT before } as it's not part of outer indent
		-> ^(SUBTEMPLATE[$lc,"SUBTEMPLATE"] ^(ARGS $ids)* template?)
	;

ifstat
@init {Token indent=null;}
	:	i=INDENT? LDELIM 'if' '(' c1=conditional ')' RDELIM {if (input.LA(1)!=NEWLINE) indent=$i;}
			t1=template
			( INDENT? LDELIM 'elseif' '(' c2+=conditional ')' RDELIM t2+=template )*
			( INDENT? LDELIM 'else' RDELIM t3=template )?
			INDENT? endif= LDELIM 'endif'
		RDELIM
		// kill \n for <endif> on line by itself if multi-line IF
		({$ifstat.start.getLine()!=input.LT(1).getLine()}?=> NEWLINE)?
		-> {indent!=null}?
		   ^(INDENTED_EXPR $i ^('if' $c1 $t1? ^('elseif' $c2 $t2)* ^('else' $t3?)?))
		->                    ^('if' $c1 $t1? ^('elseif' $c2 $t2)* ^('else' $t3?)?)
	;

conditional
scope {
	boolean inside;
}
	: andConditional ( '||'^ andConditional )*
	;

andConditional : notConditional ( '&&'^ notConditional )* ;

notConditional
	:	'!'^ notConditional
	|	memberExpr
	;

notConditionalExpr
	:	(ID->ID)
		(	p='.' prop=ID						-> ^(PROP[$p,"PROP"] $notConditionalExpr $prop)
		|	p='.' '(' mapExpr ')'				-> ^(PROP_IND[$p,"PROP_IND"] $notConditionalExpr mapExpr)
		)*
	;

exprOptions : option ( ',' option )* -> ^(OPTIONS option*) ;

option
@init {
	String id = input.LT(1).getText();
	String defVal = Compiler.defaultOptionValues.get(id);
	boolean validOption = Compiler.supportedOptions.get(id)!=null;
}
	:	ID
		{
		if ( !validOption ) {
            errMgr.compileTimeError(ErrorType.NO_SUCH_OPTION, templateToken, $ID, $ID.text);
		}
		}
		(	'=' exprNoComma 					-> {validOption}? ^('=' ID exprNoComma)
												->
		|	{
			if ( defVal==null ) {
				errMgr.compileTimeError(ErrorType.NO_DEFAULT_VALUE, templateToken, $ID);
			}
			}
												-> {validOption&&defVal!=null}?
												   ^(EQUALS["="] ID STRING[$ID,'"'+defVal+'"'])
												->
		)
	;

exprNoComma
	:	memberExpr
		( ':' mapTemplateRef					-> ^(MAP memberExpr mapTemplateRef)
		|										-> memberExpr
		)
	;

expr : mapExpr ;

// more complicated than necessary to avoid backtracking, which ruins
// error handling
mapExpr
	:	memberExpr
		( (c=',' memberExpr)+ col=':' mapTemplateRef
												-> ^(ZIP[$col] ^(ELEMENTS memberExpr+) mapTemplateRef)
		|										-> memberExpr
		)
		(	{if ($x!=null) $x.clear();} // don't keep queueing x; new list for each iteration
			col=':' x+=mapTemplateRef ({$c==null}?=> ',' x+=mapTemplateRef )*
												-> ^(MAP[$col] $mapExpr $x+)
		)*
	;

/**
expr:template(args)  apply template to expr
expr:{arg | ...}     apply subtemplate to expr
expr:(e)(args)       convert e to a string template name and apply to expr
*/
mapTemplateRef
	:	ID '(' args ')'							-> ^(INCLUDE ID args?)
	|	subtemplate
	|	lp='(' mapExpr rp=')' '(' argExprList? ')' -> ^(INCLUDE_IND mapExpr argExprList?)
	;

memberExpr
	:	(includeExpr->includeExpr)
		(	p='.' ID							-> ^(PROP[$p,"PROP"] $memberExpr ID)
		|	p='.' '(' mapExpr ')'				-> ^(PROP_IND[$p,"PROP_IND"] $memberExpr mapExpr)
		)*
	;

includeExpr
options {k=2;} // prevent full LL(*), which fails, falling back on k=1; need k=2
	:	{Compiler.funcs.containsKey(input.LT(1).getText())}? // predefined function
		ID '(' expr? ')'						-> ^(EXEC_FUNC ID expr?)
	|	'super' '.' ID '(' args ')'				-> ^(INCLUDE_SUPER ID args?)
	|	ID '(' args ')'							-> ^(INCLUDE ID args?)
	|	'@' 'super' '.' ID '(' rp=')'			-> ^(INCLUDE_SUPER_REGION ID)
	|	'@' ID '(' rp=')'						-> ^(INCLUDE_REGION ID)
	|	primary
	;

primary
	:	ID
	|	STRING
	|	TRUE
	|	FALSE
	|	subtemplate
	|	list
	|	{$conditional.size()>0}?=>  '('! conditional ')'!
	|	{$conditional.size()==0}?=> lp='(' expr ')'
		(	'(' argExprList? ')'		        -> ^(INCLUDE_IND[$lp] expr argExprList?)
		|										-> ^(TO_STR[$lp] expr)
		)
	;

args:	argExprList
	|	namedArg ( ',' namedArg )* (',' '...')? -> namedArg+ '...'?
    |   '...'
	|
	;

argExprList : arg ( ',' arg )* -> arg+ ;

arg : exprNoComma ;

namedArg : ID '=' arg -> ^('=' ID arg) ;

list:	{input.LA(2)==RBRACK}? // hush warning; [] special case
		lb='[' ']' -> LIST[$lb]
	|	lb='[' listElement ( ',' listElement )* ']' -> ^(LIST[$lb] listElement*)
	;

listElement : exprNoComma | -> NULL ;
