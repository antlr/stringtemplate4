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
parser grammar STParser;

options {
	tokenVocab=STLexer;
	TokenLabelType = CommonToken;
}

@header {
package org.stringtemplate.compiler;
import org.stringtemplate.misc.*;
import org.stringtemplate.*;
}

@members {
/** The name of the template enclosing a subtemplate or region. */
String enclosingTemplateName;
Compiler gen = Compiler.NOOP_GEN;

public STParser(TokenStream input, Compiler gen, String enclosingTemplateName) {
    this(input, new RecognizerSharedState(), gen, enclosingTemplateName);
}
public STParser(TokenStream input, RecognizerSharedState state, Compiler gen, String enclosingTemplateName) {
    super(null,null); // overcome bug in ANTLR 3.2
	this.input = input;
	this.state = state;
    if ( gen!=null ) this.gen = gen;
    this.enclosingTemplateName = enclosingTemplateName;
}

public void indent(String indent) {	gen.emit(Bytecode.INSTR_INDENT, indent); }

protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
	throws RecognitionException
{
	throw new MismatchedTokenException(ttype, input);
}
}

@rulecatch {
   catch (RecognitionException re) { throw re; }
}

templateAndEOF
	:	template EOF
	;

template
	:	element*
	;

element
	:	( i=INDENT )? {int start_address = gen.address();}
		ifstat
		// kill \n for <endif> on line by itself if multi-line IF
		( {$ifstat.start.getLine()!=input.LT(1).getLine()}? NEWLINE )?
		{
		if ( $i!=null && $ifstat.start.getLine() == input.LT(1).getLine() ) {
			// need to emit INDENT if we found indent for IF on one line
			gen.insert(start_address, Bytecode.INSTR_INDENT, $i.text);
			gen.emit(Bytecode.INSTR_DEDENT);
		}
		}

	|	i=INDENT       	 {indent($i.text);}
		exprTag          {gen.emit(Bytecode.INSTR_DEDENT);}
	|	exprTag
	|	i=INDENT         {indent($i.text);}
		text             {gen.emit(Bytecode.INSTR_DEDENT);}
	|	text
	|   (i=INDENT {indent($i.text);})? region
						 {
						 gen.emit(Bytecode.INSTR_NEW, $region.name,
						 		  $region.start.getStartIndex(), $region.stop.getStopIndex());
						 gen.emit(Bytecode.INSTR_WRITE,
						          $region.start.getStartIndex(),
						          $region.stop.getStartIndex());
						 }
	|	i=INDENT         {indent($i.text);}
	 	NEWLINE          {gen.emit(Bytecode.INSTR_NEWLINE);} 
	 	                 {gen.emit(Bytecode.INSTR_DEDENT);}
	|	NEWLINE          {gen.emit(Bytecode.INSTR_NEWLINE);}
	;

text
	:	TEXT
		{
		if ( $TEXT.text.length()>0 ) {
			gen.emit(Bytecode.INSTR_LOAD_STR, $TEXT.text,
					 $TEXT.getStartIndex(), $TEXT.getStopIndex());
			gen.emit(Bytecode.INSTR_WRITE,
					 $TEXT.getStartIndex(),$TEXT.getStopIndex());
		}
		}
	;

exprTag
	:	LDELIM
		expr
		(	';' exprOptions
			{gen.emit(Bytecode.INSTR_WRITE_OPT,
					  $LDELIM.getStartIndex(),((CommonToken)input.LT(1)).getStartIndex());}
		|	{gen.emit(Bytecode.INSTR_WRITE,
		              $LDELIM.getStartIndex(),((CommonToken)input.LT(1)).getStartIndex());}
		)
		RDELIM
	;

region returns [String name] // match $@foo$...$@end$
	:	LDELIM '@' ID RDELIM
		{$name = gen.compileRegion(enclosingTemplateName, $ID.text, input, state);}
		LDELIM '@end' RDELIM
	;
	
subtemplate returns [String name]
	:	'{' ( ids+=ID (',' ids+=ID)* '|' )?
		{$name = gen.compileAnonTemplate(enclosingTemplateName, input, $ids, state);}
        '}'
    ;

/** The (...)* loop in rule template doesn't think '}' can follow it because
 *  we call template in an action (via compileAnonTemplate).  To avoid
 *  syntax errors upon '}' in rule templatee, we force '}' into FOLLOW set.
 *  I hope to make ANTLR ignore FOLLOW set for (...)* in future.
 */
addTemplateEndTokensToFollowOfTemplateRule : template ('}'|LDELIM '@end') ;

ifstat
@init {
    /** Tracks address of branch operand (in code block).  It's how
     *  we backpatch forward references when generating code for IFs.
     */
    int prevBranchOperand = -1;
    /** Branch instruction operands that are forward refs to end of IF.
     *  We need to update them once we see the endif.
     */
    List<Integer> endRefs = new ArrayList<Integer>();
}
	:	LDELIM 'if' '(' conditional ')' RDELIM
		{
        prevBranchOperand = gen.address()+1;
        gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
		}
		template
		(	INDENT? LDELIM 'elseif'
			{
			endRefs.add(gen.address()+1);
			gen.emit(Bytecode.INSTR_BR, -1); // br end
			// update previous branch instruction
			gen.write(prevBranchOperand, (short)gen.address());
			prevBranchOperand = -1;
			}
			'(' conditional ')' RDELIM
			{
        	prevBranchOperand = gen.address()+1;
        	gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
			}
			template
		)*
		(	INDENT? LDELIM 'else' RDELIM
			{
			endRefs.add(gen.address()+1);
			gen.emit(Bytecode.INSTR_BR, -1); // br end
			// update previous branch instruction
			gen.write(prevBranchOperand, (short)gen.address());
			prevBranchOperand = -1;
			}
			template
		)?
		INDENT? endif=LDELIM 'endif' RDELIM
		//( {true}? NEWLINE )? // kill \on for <endif> on line by itself
		{
		if ( prevBranchOperand>=0 ) {
			gen.write(prevBranchOperand, (short)gen.address());
		}
        for (int opnd : endRefs) gen.write(opnd, (short)gen.address());
		}
	;
		
conditional
	:	andConditional ('||' andConditional {gen.emit(Bytecode.INSTR_OR);})*
	;
	
andConditional
	:	notConditional ('&&' notConditional {gen.emit(Bytecode.INSTR_AND);})*
	;

notConditional
	:	'!' memberExpr  {gen.emit(Bytecode.INSTR_NOT);}
	|	memberExpr
	;
	
exprOptions
	:	{gen.emit(Bytecode.INSTR_OPTIONS);} option (',' option)*
	;

option
	:	ID ( '=' exprNoComma | {gen.defaultOption($ID);} ) {gen.setOption($ID);}
	;
	
exprNoComma
	:	memberExpr
		(	':' templateRef
						   {
						   gen.emit(Bytecode.INSTR_MAP,
								    $templateRef.start.getStartIndex(),
								    $templateRef.stop.getStopIndex());
						   }
		)?
	;

expr : mapExpr ;

mapExpr
@init {int nt=1, ne=1; int a=$start.getStartIndex();}
	:	memberExpr (c=',' memberExpr {ne++;} )*
		(	':' templateRef
			(	(',' templateRef {nt++;})+
						   {gen.emit(Bytecode.INSTR_ROT_MAP, nt, a,
						              ((CommonToken)input.LT(-1)).getStopIndex());}
			|	           {
			               if ( $c!=null ) gen.emit(Bytecode.INSTR_PAR_MAP, ne, a,
						                            ((CommonToken)input.LT(-1)).getStopIndex());
						   else gen.emit(Bytecode.INSTR_MAP, a,
							             ((CommonToken)input.LT(-1)).getStopIndex());
						   }
			)
		)*
	;

memberExpr
	:	callExpr
		(	'.' ID         {gen.emit(Bytecode.INSTR_LOAD_PROP, $ID.text,
					                 $ID.getStartIndex(), $ID.getStopIndex());}
		|	'.' lp='(' mapExpr rp=')'
						   {gen.emit(Bytecode.INSTR_LOAD_PROP_IND,
						   		     $lp.getStartIndex(),$rp.getStartIndex());}
		)*
	;
	
callExpr
options {k=2;} // prevent full LL(*), which fails, falling back on k=1; need k=2
	:	{Compiler.funcs.containsKey(input.LT(1).getText())}?
		ID '(' expr ')'    {gen.func($ID);}
	|	(s='super' '.')? ID
						   {gen.emit($s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
								     gen.prefixedName($ID.text),
								     $start.getStartIndex(), $ID.getStopIndex());}
		'(' args? ')'
	|	'@' (s='super' '.')? ID '(' rp=')'	// convert <@r()> to <region__enclosingTemplate__r()>
						   {
						   gen.defineBlankRegion(enclosingTemplateName, $ID.text);
						   String mangled = STGroup.getMangledRegionName(enclosingTemplateName, $ID.text);
						   gen.emit($s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
							   	    gen.prefixedName(mangled),
								    $start.getStartIndex(), $rp.getStartIndex());
						   }
	|	primary
	;

primary
	:	o=ID	           {gen.refAttr($o);}
	|	STRING             {gen.emit(Bytecode.INSTR_LOAD_STR,
									 Misc.strip($STRING.text,1),
							 		 $STRING.getStartIndex(), $STRING.getStopIndex());}
	|	subtemplate
		                   {gen.emit(Bytecode.INSTR_NEW, gen.prefixedName($subtemplate.name),
									 $subtemplate.start.getStartIndex(),
									 $subtemplate.stop.getStopIndex());}
	|	list
	|	lp='(' expr rp=')' {gen.emit(Bytecode.INSTR_TOSTR,
									 $lp.getStartIndex(),$rp.getStartIndex());}
		(	               {gen.emit(Bytecode.INSTR_NEW_IND,
                        		     $lp.getStartIndex(),$rp.getStartIndex());}
			'(' args? ')' // indirect call
		)? 
	;

args:	arg (',' arg)* ;

arg :	ID '=' exprNoComma {gen.emit(Bytecode.INSTR_STORE_ATTR, $ID.text,
					 				 $ID.getStartIndex(), $exprNoComma.stop.getStopIndex());}
	|	exprNoComma        {gen.emit(Bytecode.INSTR_STORE_SOLE_ARG,
									 $exprNoComma.start.getStartIndex(),
									 $exprNoComma.stop.getStopIndex());}
	|	elip='...'		   {gen.emit(Bytecode.INSTR_SET_PASS_THRU);}
	;

/**
expr:template()      apply template to expr
expr:{arg | ...}     apply subtemplate to expr
expr:(e)()           convert e to a string template name and apply to expr
*/
templateRef
	:	ID  '(' ')'		   {gen.emit(Bytecode.INSTR_LOAD_STR,gen.prefixedName($ID.text),
                   		 		     $ID.getStartIndex(), $ID.getStopIndex());}
	|	subtemplate        {gen.emit(Bytecode.INSTR_LOAD_STR,
	                                 gen.prefixedName($subtemplate.name),
									 $subtemplate.start.getStartIndex(),
									 $subtemplate.stop.getStopIndex());}
	|	lp='(' mapExpr rp=')' '(' ')'
		                   {gen.emit(Bytecode.INSTR_TOSTR,
		                             $lp.getStartIndex(),$rp.getStartIndex());}
	;
	
list:	{gen.emit(Bytecode.INSTR_LIST);} '[' listElement (',' listElement)* ']'
	|	{gen.emit(Bytecode.INSTR_LIST);} '[' ']'
	;

listElement
    :   exprNoComma        {gen.emit(Bytecode.INSTR_ADD,
								    $exprNoComma.start.getStartIndex(),
								    $exprNoComma.stop.getStopIndex());}
    ;
    