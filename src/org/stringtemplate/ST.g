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
@members {
ParserListener listener;
public STParser(TokenStream input, ParserListener listener) {
    this(input, new RecognizerSharedState());
    this.listener = listener;
}
}

stexpr
scope { int level; }
@init { $stexpr::level = 0; int n=1;}
	:	expr
		(	':' t=ID {listener.refString($t);}
			(	(',' t=ID {n++; listener.refString($t);})+  {listener.applyAlternating(n);}
			|										        {listener.apply();}
			)
		)*
	|	i='if(' not='!'? {listener.ifExpr($i);} expr ')'
										{listener.ifExprClause($i,$not!=null);}
	|	i='elseif(' not='!'? {listener.elseifExpr($i);} expr ')'
										{listener.elseifExprClause($i,$not!=null);}
	|	'else'							{listener.elseClause();}
	|	'endif'							{listener.endif();}
	;

expr:	call
	|	'super.' ('.' ID )*
	|	o=ID	  {listener.refAttr($o);}
		('.' p=ID {listener.refProp($p);} )*
	|	STRING    {listener.refString($STRING);}
	;
	
call
	:	ID {listener.instance($ID);} '(' args? ')'
//	|	'{' template... '}'
	;
	
args:	arg (',' arg)* ;

arg :	ID '=' expr {listener.setArg($ID);}
//	|	expr set sole arg
	;

ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')*
    ;

STRING
    :	'"' ( '\\"' | ~('\\'|'"') )* '"'
    	{setText(getText().substring(1, getText().length()-1));}
    ;

WS  :       (' '|'\t'|'\r'|'\n')+ {skip();}
    ;
