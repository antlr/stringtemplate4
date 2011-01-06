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
import org.stringtemplate.v4.misc.*;
import org.stringtemplate.v4.*;
}

@members {
	String outermostTemplateName; // name of overall template
	CompiledST outermostImpl;
	String template;
	public CodeGenerator(TreeNodeStream input, String name, String template) {
		this(input, new RecognizerSharedState());
		this.outermostTemplateName = name;
		this.template = template;
	}
}

templateAndEOF : template[null,null] EOF; // hush warning; ignore

template[String name, List<FormalArgument> args] returns [CompiledST impl]
scope {
    CompilationState state; // automatically get a new state pointer per invocation
}
@init {
 	$template::state = new CompilationState(name, input.getTokenStream());
	$impl = $template::state.impl;
 	if ( $template.size() == 1 ) outermostImpl = $impl;
	$impl.defineFormalArgs($args); // make sure args are defined prior to compilation
	$impl.template = template;
}
	:	chunk
		{ // finish off the CompiledST result
        if ( $impl.stringtable!=null ) $impl.strings = $impl.stringtable.toArray();
        $impl.codeSize = $impl.ip;
		}
	;

chunk
	:	element*
	;
	
element
	:	^(INDENT {$template::state.indent($INDENT.text);} element {$template::state.emit(Bytecode.INSTR_DEDENT);})
	|	ifstat
	|	exprElement
	|	TEXT
		{
		if ( $TEXT.text.length()>0 ) {
			$template::state.emit1($TEXT,Bytecode.INSTR_LOAD_STR, $TEXT.text);
			$template::state.emit($TEXT,Bytecode.INSTR_WRITE);
		}
		}
	|	region
		{
		$template::state.emit2($region.start, Bytecode.INSTR_NEW, $region.name, 0);
		$template::state.emit($region.start, Bytecode.INSTR_WRITE);
		}
	|	NEWLINE {$template::state.emit(Bytecode.INSTR_NEWLINE);}
	;

exprElement
@init { short op = Bytecode.INSTR_WRITE; }
	:	^( EXPR expr (exprOptions {op=Bytecode.INSTR_WRITE_OPT;})? )
		{$template::state.emit($EXPR, op);}
	;

region returns [String name]
	:	^(	REGION ID
			{$name = STGroup.getMangledRegionName(outermostTemplateName, $ID.text);}
			template[$name,null]
			{
			CompiledST sub = $template.impl;
	        sub.isRegion = true;
	        sub.regionDefType = ST.RegionType.EMBEDDED;
			sub.dump();
			outermostImpl.addImplicitlyDefinedTemplate(sub);
			}
		 )
	;

subtemplate returns [String name, int nargs]
@init {
    $name = Compiler2.getNewSubtemplateName();
	List<FormalArgument> args = new ArrayList<FormalArgument>();
}
	:	^(	SUBTEMPLATE
			(^(ARGS (ID {args.add(new FormalArgument($ID.text));})+))*
			{$nargs = args.size();}
			template[$name,args]
			{
			CompiledST sub = $template.impl;
			sub.isAnonSubtemplate = true;
			sub.dump();
			outermostImpl.addImplicitlyDefinedTemplate(sub);
			}
		 )
	;

ifstat
	:	^(	'if' conditional chunk
			(^('elseif' conditional chunk))*
			(^('else' chunk))?
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

expr
@init {int nt = 0, ne = 0;}
	:	^(ZIP ^(ELEMENTS (expr {ne++;})+) mapTemplateRef[ne])
	|	^(MAP expr (mapTemplateRef[1] {nt++;})+)
		{$template::state.emit1($MAP, Bytecode.INSTR_ROT_MAP, nt);}
	|	prop
	|	includeExpr
	;

prop:	^(PROP expr ID)
	|	^(PROP_IND expr expr)
	;
	
mapTemplateRef[int num_exprs]
	:	^(INCLUDE ID args)
		{$template::state.emit2($INCLUDE, Bytecode.INSTR_NEW,$ID.text, $args.n+$num_exprs);}
	|	subtemplate
		{
		if ( $subtemplate.nargs != $num_exprs ) {
            ErrorManager.compileTimeError(ErrorType.ANON_ARGUMENT_MISMATCH,
            							  $subtemplate.start.token, $subtemplate.nargs, $num_exprs);
		}
        $template::state.emit2($subtemplate.start, Bytecode.INSTR_NEW,
	              $subtemplate.name,
	              $num_exprs);
		}

	|	^(INCLUDE_IND ID args)
	;

includeExpr
	:	^(EXEC_FUNC ID expr?)		{$template::state.func($ID);}
	|	^(INCLUDE ID args)
									{
									$template::state.emit2($start,Bytecode.INSTR_NEW,
									          $ID.text,
									    	  $args.n);
									}
	|	^(INCLUDE_SUPER ID args)
									{
									$template::state.emit2($start,Bytecode.INSTR_SUPER_NEW,
									          $ID.text,
									    	  $args.n);
									}
	|	^(INCLUDE_REGION ID)
								   {
								   CompiledST impl = Compiler2.defineBlankRegion(outermostImpl, $ID.text);
								   impl.dump();
								   $template::state.emit2($INCLUDE_REGION,Bytecode.INSTR_NEW,
									   	    impl.name,
									   	    0);
								   }
	|	^(INCLUDE_SUPER_REGION ID)
	|	primary
	;

primary
	:	ID				{$template::state.refAttr($ID);}
	|	STRING			{$template::state.emit1($STRING,Bytecode.INSTR_LOAD_STR,
									  Misc.strip($STRING.text,1));}	
	|	subtemplate		// push a subtemplate but ignore args since we can't pass any to it here
		                {$template::state.emit2($start,Bytecode.INSTR_NEW, $subtemplate.name, 0);}
	|	list
	|	^(INCLUDE_IND expr args)
						{$template::state.emit1($INCLUDE_IND, Bytecode.INSTR_NEW_IND, $args.n);}
	|	^(TO_STR expr)	{$template::state.emit($TO_STR, Bytecode.INSTR_TOSTR);}
	;

args returns [int n=0] : ( arg {$n++;} )* ;

arg : expr ;

list:	^(LIST expr*) {$template::state.emit(Bytecode.INSTR_LIST);}
	;
