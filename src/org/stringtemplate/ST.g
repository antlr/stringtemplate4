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
grammar ST;

tokens { IF='if('; ELSE='else'; ELSEIF='elseif('; ENDIF='endif'; }

@header { package org.stringtemplate; }
@lexer::header { package org.stringtemplate; }

@lexer::members {
char delimiterStartChar;
char delimiterStopChar;
}

@members {
public boolean exprHasOptions = false;
ExprParserListener listener;
public STParser(TokenStream input,
				ExprParserListener listener,
                char delimiterStartChar,
                char delimiterStopChar)
{
    this(input, new RecognizerSharedState());
    this.listener = listener;
    STLexer lex = (STLexer)input.getTokenSource();
    lex.delimiterStartChar = delimiterStartChar;
    lex.delimiterStopChar = delimiterStopChar;
}
}

stexpr
	:	mapExpr (';' exprOptions)?
	|	i='if(' not='!'? {listener.ifExpr($i);} expr ')'
										{listener.ifExprClause($i,$not!=null);}
	|	i='elseif(' not='!'? {listener.elseifExpr($i);} expr ')'
										{listener.elseifExprClause($i,$not!=null);}
	|	'else'							{listener.elseClause();}
	|	'endif'							{listener.endif();}
	;

mapExpr
@init {int n=1;}
	:	expr
		(	':' template
			(	(',' template {n++;})+  {listener.mapAlternating(n);}
			|						    {listener.map();}
			)
		)*
	;

exprOptions
	:	{exprHasOptions=true; listener.options();} option (',' option)*
	;

option
	:	ID ( '=' value | {listener.defaultOption($ID);} ) {listener.setOption($ID);}
	;
	
value
	:	expr ( ':' template {listener.map();} )?
	|	ANONYMOUS_TEMPLATE
		{
		String name = listener.defineAnonTemplate($ANONYMOUS_TEMPLATE);
        listener.instance(new CommonToken(STRING,name)); // call anon template
        }
	;

expr:	call
	|	primary
	;

call:	ID {listener.instance($ID);} '(' args? ')' ;
	
primary
	:	'super.' ('.' ID )*
	|	'it'      {listener.refIteratorValue();}
	|	o=ID	  {listener.refAttr($o);}
		('.' p=ID {listener.refProp($p);} )*
	|	STRING    {listener.refString($STRING);}
	|	list
	|	'(' mapExpr ')' {listener.eval();}
		( {listener.instance(null);} '(' args? ')' )?
	;

template
	:	ID			{listener.refString($ID);}
	|	ANONYMOUS_TEMPLATE
					{String name = listener.defineAnonTemplate($ANONYMOUS_TEMPLATE);
	                 listener.refString(new CommonToken(STRING,name));}
	;
	
args:	arg (',' arg)* ;

arg :	ID '=' expr {listener.setArg($ID);}
//	|	expr set sole arg
	;

list:	{listener.list();} '[' listElement (',' listElement)* ']'
	|	{listener.list();} '[' ']'
	;

listElement
    :   value {listener.add();}
    ;

ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')*
    ;

STRING
    :	'"' ( '\\"' | ~('\\'|'"') )* '"'
    	{setText(getText().substring(1, getText().length()-1));}
    ;

ANONYMOUS_TEMPLATE
    :	'{'  { new Chunkifier(input,delimiterStartChar,delimiterStopChar).matchBlock(); }
    	{setText(getText().substring(1, getText().length()-1));}
    ;

WS  :	(' '|'\t'|'\r'|'\n')+ {skip();} ;
