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
import org.stringtemplate.v4.misc.Misc;
}

@members {
Compiler2 compiler;
}

templateAndEOF : root[""] EOF; // hush warning

root[String name] returns [CompiledST impl]
scope {
    CompilationState state; // automatically get a new one per invocation
}
@init {
	$root::state = new CompilationState();
	$root::state.tokens = input.getTokenStream();
	$impl = $root::state.impl;
    $impl.name = name;
}
	:	template
		{
        if ( $impl.stringtable!=null ) $impl.strings = $impl.stringtable.toArray();
        $impl.codeSize = $impl.ip;
		}
	;

template
	:	element*
	;
	
element
	:	^(INDENT {$root::state.indent($INDENT.text);} element {$root::state.emit(Bytecode.INSTR_DEDENT);})
	|	ifstat
	|	exprElement
	|	TEXT
		{
		if ( $TEXT.text.length()>0 ) {
			$root::state.emit1($TEXT,Bytecode.INSTR_LOAD_STR, $TEXT.text);
			$root::state.emit($TEXT,Bytecode.INSTR_WRITE);
		}
		}
	|	region
	|	NEWLINE {$root::state.emit(Bytecode.INSTR_NEWLINE);}
	;

exprElement
@init { short op = Bytecode.INSTR_WRITE; }
	:	^( EXPR expr (exprOptions {op=Bytecode.INSTR_WRITE_OPT;})? )
		{$root::state.emit($EXPR, op);}
	;

region : LDELIM '@' ID RDELIM LDELIM '@end' RDELIM ;

subtemplate returns [String name]
@init {
        compiler.subtemplateCount++;
        $name = Compiler2.SUBTEMPLATE_PREFIX+compiler.subtemplateCount;
}
	:	^(SUBTEMPLATE (^(ARGS ID+))* root[$name])
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
	:	^(INCLUDE ID args)
	|	subtemplate
	|	^(INCLUDE_IND ID args)
	;

includeExpr
	:	^(EXEC_FUNC ID expr?)		{$root::state.func($ID);}
	|	^(INCLUDE ID args)
									{
									$root::state.emit2($start,Bytecode.INSTR_NEW,
									          $ID.text,
									    	  $args.n);
									}
	|	^(INCLUDE_SUPER ID args)
									{
									$root::state.emit2($start,Bytecode.INSTR_SUPER_NEW,
									          $ID.text,
									    	  $args.n);
									}
	|	^(INCLUDE_REGION ID)
	|	^(INCLUDE_SUPER_REGION ID)
	|	primary
	;

primary
	:	ID				{$root::state.refAttr($ID);}
	|	STRING			{$root::state.emit1($STRING,Bytecode.INSTR_LOAD_STR,
									  Misc.strip($STRING.text,1));}	
	|	subtemplate		// push a subtemplate but ignore args since we can't pass any to it here
		                {$root::state.emit2($start,Bytecode.INSTR_NEW, $subtemplate.name, 0);}
	|	list
	|	^(INCLUDE_IND expr args)
			{
			$root::state.emit1($INCLUDE_IND, Bytecode.INSTR_NEW_IND, $args.n);
			}
	|	^(TO_STR expr)	{$root::state.emit($TO_STR, Bytecode.INSTR_TOSTR);}
	;

args returns [int n=0] : ( arg {n++;} )* ;

arg : expr ;

list:	^(LIST expr*) {$root::state.emit(Bytecode.INSTR_LIST);}
	;
