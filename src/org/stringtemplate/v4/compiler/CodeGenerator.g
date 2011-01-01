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

tree grammar CodeGenerator;

options {
	tokenVocab=STTreeBuilder;
	ASTLabelType=CommonTree;
}

@header {
package org.stringtemplate.v4.compiler;
}

templateAndEOF : template ;

template : element* ;

element
	:	^(INDENT element)
	|	ifstat
	|	exprElement
	|	TEXT
	|	region
	|	NEWLINE
	;

exprElement
	:	^(EXPR expr exprOptions?)
	;

region : LDELIM '@' ID RDELIM LDELIM '@end' RDELIM ;

subtemplate
	:	^(SUBTEMPLATE (^(ARGS ID+))* template)
	;

ifstat
	:	^(	'if' conditional template
			(^('elseif' conditional template))*
			(^('else' template))?
		 )
	;

conditional
	:	^('||' conditional conditional)
	|	^('&&' conditional conditional)
	|	^('!' conditional)
	|	prop
	|	ID
	;

exprOptions : ^(OPTIONS option+) ;

option
	:	^('=' ID expr)
	;

expr:	^(ZIP ^(ELEMENTS expr+) mapTemplateRef)
	|	^(MAP expr mapTemplateRef+)
	|	prop
	|	includeExpr
	;

prop:	^(PROP expr ID)
	|	^(PROP_IND expr expr)
	;
	
mapTemplateRef
	:	^(INCLUDE ID arg*)
	|	subtemplate
	|	^(INCLUDE_IND ID arg*)
	;

includeExpr
	:	^(EXEC_FUNC ID expr?)
	|	^(INCLUDE ID arg*)
	|	^(INCLUDE_SUPER ID arg*)
	|	^(INCLUDE_REGION ID)
	|	^(INCLUDE_SUPER_REGION ID)
	|	primary
	;

primary
	:	ID
	|	STRING
	|	subtemplate
	|	list
	|	^(INCLUDE_IND expr arg*)
	|	^(TO_STR expr)
	;

arg : expr ;

list:	^(LIST expr*)
	;
