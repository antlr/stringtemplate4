/*
 [The "BSD licence"]
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

/** Recognize a single StringTemplate template text, expressions, and conditionals */
parser grammar ST;

options {
	tokenVocab=MyLexer;
}

/*
tokens {
	IF='if('; ELSE='else'; ELSEIF='elseif('; ENDIF='endif'; SUPER='super.';
	SEMI=';'; BANG='!'; ELLIPSIS='...'; EQUALS='='; COLON=':';
	LPAREN='('; RPAREN=')'; LBRACK='['; RBRACK=']'; COMMA=','; DOT='.';
	LCURLY='{'; RCURLY='}'; PIPE='|';
	TEXT; LDELIM; RDELIM;
}
*/

@header { package org.stringtemplate; }

@members {
ExprParserListener listener;
public STParser(TokenStream input,
				ExprParserListener listener)
{
    this(input, new RecognizerSharedState());
    this.listener = listener;
}

protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
	throws RecognitionException
{
	throw new MismatchedTokenException(ttype, input);
}
}

@rulecatch {
   catch (RecognitionException re) { throw re; }
}

st	:	stNoEof EOF
	;
	
stNoEof
	:	( TEXT | LDELIM ( conditional | expr (';' exprOptions)? ) RDELIM )*
	;

conditional
	:	i='if' '(' not='!'? {listener.ifExpr($i);} primary ')'
							{listener.ifExprClause($i,$not!=null);}
	|	i='elseif' '(' not='!'? {listener.elseifExpr($i);} primary ')'
							{listener.elseifExprClause($i,$not!=null);}
	|	'else'				{listener.elseClause();}
	|	'endif'				{listener.endif();}
	;

exprOptions
	:	{exprHasOptions=true; listener.options();} option (',' option)*
	;

option
	:	ID ( '=' exprNoComma | {listener.defaultOption($ID);} ) {listener.setOption($ID);}
	;
	
exprNoComma
	:	callExpr ( ':' template {listener.map();} )?
	|	'{'
		{
//		String name = listener.defineAnonTemplate($ANONYMOUS_TEMPLATE);
//        listener.instance(new CommonToken(STRING,name)); // call anon template
        }
	;

expr : mapExpr ;

mapExpr
@init {int n=1;}
	:	callExpr
		(	':' template
			(	(',' template {n++;})+  {listener.mapAlternating(n);}
			|						    {listener.map();}
			)
		)*
	;


callExpr
options {k=2;} // prevent full LL(*) which fails, falling back on k=1; need k=2
	:	{Compiler.funcs.containsKey(input.LT(1).getText())}?
		ID '(' arg ')' {listener.func($ID);}
	|	ID {listener.instance($ID);} '(' args? ')'
	|	primary
	;
	
primary
	:	'super.' ('.' ID )*
	|	o=ID	  {listener.refAttr($o);}
		(	'.' p=ID {listener.refProp($p);}
		|	'.' '(' mapExpr ')' {listener.refProp(null);}
		)*
	|	STRING    {listener.refString($STRING);}
	|	list
	|	'(' expr ')' {listener.eval();}
		( {listener.instance(null);} '(' args? ')' )? // indirect call
	;

args:	arg (',' arg)* ;

arg :	ID '=' exprNoComma {listener.setArg($ID);}
	|	exprNoComma        {listener.setArg(null);}
	|	elip='...'		   {listener.setPassThroughArg($elip);}
	;

template
	:	ID			{listener.refString($ID);}
	|	'{'
		{
//		String name = listener.defineAnonTemplate($ANONYMOUS_TEMPLATE);
//		listener.refString(new CommonToken(STRING,name));
		}
	|	'(' mapExpr ')' {listener.eval();}
	;
	
list:	{listener.list();} '[' listElement (',' listElement)* ']'
	|	{listener.list();} '[' ']'
	;

listElement
    :   exprNoComma {listener.add();}
    ;
