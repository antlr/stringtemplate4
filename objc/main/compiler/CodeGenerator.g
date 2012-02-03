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

tree grammar CodeGenerator;

options {
	language=ObjC;
	tokenVocab=STParser;
	ASTLabelType=CommonTree;
}

@header {
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
#import <ANTLR/CommonToken.h>
#import "Compiler.h"
#import "CompiledST.h"
#import "CompilationState.h"
#import "ErrorManager.h"
#import "Bytecode.h"
#import "Misc.h"
}

@memVars {
	template_Scope *template_scope;
	NSString *outermostTemplateName;	// name of overall template
	CompiledST *outermostImpl;
	CommonToken *templateToken;			    // overall template token
	NSString *template;  				// overall template text
	ErrorManager *errMgr;
}

@properties {
	@property(retain) template_Scope *template_scope;
	@property(retain) NSString *outermostTemplateName; // name of overall template
	@property(retain) CompiledST *outermostImpl;
	@property(retain) CommonToken *templateToken;// overall template token
	@property(retain) NSString *template;    // overall template text
	@property(retain) ErrorManager *errMgr;
}

@methodsDecl {
+ (id) newCodeGenerator:(id<TreeNodeStream>)input
                 errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken;

- (id) init:(id<TreeNodeStream>)input
                     errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken;

// convience funcs to hide offensive sending of emit messages to
// CompilationState temp data object.

- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)arg;
- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)arg;
- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)anArg arg2:(NSInteger)anArg2;
- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)s arg2:(NSInteger)anArg;
- (void) emit:(short)anOpcode;
- (void) emit:(CommonTree *)opAST opcode:(short)anOpcode;
- (void) insert:(NSInteger)addr opcode:(short)anOpcode s:(NSString *)s;
- (void) setOption:(CommonTree *)anID;
- (void) write:(NSInteger)addr value:(short)value;
- (NSInteger) address;
- (void) func:(CommonTree *)aTree;
- (void) refAttr:(CommonTree *)aTree;
- (NSInteger) defineString:(NSString *)s;
}

@synthesize {
	@synthesize template_scope;
	@synthesize outermostTemplateName; // name of overall template
	@synthesize outermostImpl;
	@synthesize templateToken;// overall template token
	@synthesize template; // overall template text
	@synthesize errMgr;
}

@methods {
+ (id) newCodeGenerator:(id<TreeNodeStream>)anInput
                 errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken
{
    return [[[CodeGenerator alloc] init:anInput
                                errMgr:anErrMgr
                                  name:aName
                              template:aTemplate
                                 token:aTemplateToken] retain];
}

- (id) init:(id<TreeNodeStream>)anInput
                     errMgr:(ErrorManager *)anErrMgr
                   name:(NSString *)aName
               template:(NSString *)aTemplate
                  token:(CommonToken *)aTemplateToken
{
    self=[super initWithStream:anInput State:[RecognizerSharedState newRecognizerSharedState]];
    if ( self != nil ) {
        /* ruleAttributeScopeInit */
        template_scope = [[template_Scope newtemplate_Scope] retain];
        template_stack = [[SymbolStack newSymbolStackWithLen:30] retain];
        errMgr = anErrMgr;
        if ( errMgr ) [errMgr retain];
        outermostTemplateName = aName;
        if ( outermostTemplateName ) [outermostTemplateName retain];
        template = aTemplate;
        if ( template ) [template retain];
        templateToken = aTemplateToken;
        if ( templateToken ) [templateToken retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in CodeGenerator" );
#endif
    if ( outermostTemplateName ) [outermostTemplateName release];
    if ( outermostImpl ) [outermostImpl release];
    if ( templateToken ) [templateToken release];
    if ( template ) [template release];
    if ( errMgr ) [errMgr release];
    [super dealloc];
}

// convience funcs to hide offensive sending of emit messages to
// CompilationState temp data object.

- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)arg
{
    [$template::cstate emit1:opAST opcode:anOpcode arg:arg];
}

- (void) emit1:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)arg
{
    [$template::cstate emit1:opAST opcode:anOpcode s:arg];
}

- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode arg:(NSInteger)anArg arg2:(NSInteger)anArg2
{
    [$template::cstate emit2:opAST opcode:anOpcode arg:anArg arg2:anArg2];
}

- (void) emit2:(CommonTree *)opAST opcode:(short)anOpcode s:(NSString *)s arg2:(NSInteger)anArg
{
    [$template::cstate emit2:opAST opcode:anOpcode s:s arg2:anArg];
}

- (void) emit:(short)anOpcode
{
    [$template::cstate emit:anOpcode];
}

- (void) emit:(CommonTree *)opAST opcode:(short)anOpcode
{
    [$template::cstate emit:opAST opcode:anOpcode];
}

- (void) insert:(NSInteger)addr opcode:(short)anOpcode s:(NSString *)s
{
    [$template::cstate insert:addr opcode:anOpcode s:s];
}

- (void) setOption:(CommonTree *)anID
{
    [$template::cstate setOption:anID];
}

- (void) write:(NSInteger)addr value:(short)value
{
    [$template::cstate write:addr value:value];
}

- (NSInteger) address { return $template::cstate.ip; }
- (void) func:(CommonTree *)aTree { [$template::cstate func:templateToken tree:aTree]; }
- (void) refAttr:(CommonTree *)aTree { [$template::cstate refAttr:templateToken tree:aTree]; }
- (NSInteger) defineString:(NSString *)s { return [$template::cstate defineString:s]; }
}

templateAndEOF : template[nil ,nil] EOF; // hush warning; ignore

template[NSString *name, AMutableArray *args] returns [CompiledST *impl]
scope {
    CompilationState *cstate; // automatically get a new cstate pointer per invocation
}
@init {
 	$template::cstate = [[CompilationState newCompilationState:errMgr name:name stream:[input getTokenStream]] retain];
	$impl = $template::cstate.impl;
 	if ( [$template count] == 1 ) outermostImpl = $impl;
	[$impl defineFormalArgs:$args]; // make sure args are defined prior to compilation
	if ( name != nil && [name hasPrefix:Compiler.SUBTEMPLATE_PREFIX] ) {
	    [$impl addArg:[FormalArgument newFormalArgument:@"i"]];
	    [$impl addArg:[FormalArgument newFormalArgument:@"i0"]];
    }
	$impl.template = template; // always forget the entire template; char indexes are relative to it
}
	:	chunk
		{ // finish off the CompiledST result
        if ( $template::cstate.stringtable != nil ) $impl.strings = [$template::cstate.stringtable toArray];
        $impl.codeSize = $template::cstate.ip;
		}
	;

chunk
	:	element*
	;

element
	:	^(INDENTED_EXPR INDENT compoundElement[$INDENT]) // ignore indent in front of IF and region blocks
	|	compoundElement[nil]
    |	^(INDENTED_EXPR INDENT {[$template::cstate indent:$INDENT];} singleElement {[$template::cstate emit:Bytecode.INSTR_DEDENT];})
	|	singleElement
    ;

singleElement
	:	exprElement
	|	TEXT
		{
		if ( [$TEXT.text length]>0 ) {
            [self emit1:$TEXT opcode:Bytecode.INSTR_WRITE_STR s:$TEXT.text];
		}
        }
	|	NEWLINE {[self emit:Bytecode.INSTR_NEWLINE];}
	;

compoundElement[CommonTree *indent]
	:	ifstat[indent]
	|	region[indent]
	;

exprElement
@init { short op = Bytecode.INSTR_WRITE; }
	:	^( EXPR expr (exprOptions {op=Bytecode.INSTR_WRITE_OPT;})? )
		{[self emit:$EXPR opcode:op];}
	;

region[CommonTree *indent] returns [NSString *name]
@init {
    if ( indent != nil ) [$template::cstate indent:indent];
}
@after {
    if ( indent != nil ) [$template::cstate emit:Bytecode.INSTR_DEDENT];
}
	:	^(	REGION ID
			{$name = [STGroup getMangledRegionName:outermostTemplateName name:$ID.text];}
			template[$name,nil]
			{
			CompiledST *sub = $template.impl;
	        sub.isRegion = true;
	        sub.regionDefType = /* ST.RegionType. */ EMBEDDED;
	        sub.templateDefStartToken = $ID.token;
			//sub.dump();
			[outermostImpl addImplicitlyDefinedTemplate:sub];
            [self emit2:$start opcode:Bytecode.INSTR_NEW s:$region.name arg2:0];
            [self emit:$start opcode:Bytecode.INSTR_WRITE];
			}
		 )
	;

subtemplate returns [NSString *name, NSInteger nargs]
@init {
    $name = [Compiler getNewSubtemplateName];
	AMutableArray *args = [AMutableArray arrayWithCapacity:5];
}
	:	^(	SUBTEMPLATE
			(^(ARGS (ID {[args addObject:[FormalArgument newFormalArgument:$ID.text]];})+))*
			{$nargs = [args count];}
			template[$name,args]
			{
			CompiledST *sub = $template.impl;
			sub.isAnonSubtemplate = YES;
	        sub.templateDefStartToken = $SUBTEMPLATE.token;
			sub.ast = $SUBTEMPLATE;
			[sub.ast setUnknownTokenBoundaries];
			sub.tokens = [input getTokenStream];
			//sub.dump();
			[outermostImpl addImplicitlyDefinedTemplate:sub];
			}
		 )
	|	SUBTEMPLATE // {}
			{
			CompiledST *sub = [CompiledST newCompiledST];
			sub.name = $name;
			sub.template = @"";
			[sub addArg:[FormalArgument newFormalArgument:@"i"]];
			[sub addArg:[FormalArgument newFormalArgument:@"i0"]];
			sub.isAnonSubtemplate = YES;
	        sub.templateDefStartToken = $SUBTEMPLATE.token;
            sub.ast = $SUBTEMPLATE;
            [sub.ast setUnknownTokenBoundaries];
            sub.tokens = [input getTokenStream];
			//sub.dump();
			[outermostImpl addImplicitlyDefinedTemplate:sub];
			}
	;

ifstat[CommonTree *indent]
@init {
    /** Tracks address of branch operand (in code block).  It's how
     *  we backpatch forward references when generating code for IFs.
     */
    NSInteger prevBranchOperand = -1;
    /** Branch instruction operands that are forward refs to end of IF.
     *  We need to update them once we see the endif.
     */
    IntArray *endRefs = [[IntArray newArrayWithLen:16] retain];
    if ( indent!=nil ) [$template::cstate indent:indent];
}
@after {
	if ( indent!=nil ) [$template::cstate emit:Bytecode.INSTR_DEDENT];
}
	:	^(	i='if' conditional
			{
	        prevBranchOperand = [self address]+1;
	        [self emit1:$i opcode:Bytecode.INSTR_BRF arg:-1]; // write placeholder as branch target
			}
			chunk
			(	^(eif='elseif'
				{
				[endRefs addInteger:[self address]+1];
				[self emit1:$eif opcode:Bytecode.INSTR_BR arg:-1]; // br end
				// update previous branch instruction
				[self write:prevBranchOperand value:(short)[self address]];
				prevBranchOperand = -1;
				}
				ec=conditional
				{
		       	prevBranchOperand = [self address]+1;
		       	// write placeholder as branch target
		       	[self emit1:$ec.start opcode:Bytecode.INSTR_BRF arg:-1];
				}
				chunk
				)
			)*
			(	^(	el='else'
					{
					[endRefs addInteger:[self address]+1];
					[self emit1:$el opcode:Bytecode.INSTR_BR arg:-1]; // br end
					// update previous branch instruction
                    [self write:prevBranchOperand value:(short)[self address]];
					prevBranchOperand = -1;
					}
					chunk
				 )
			)?
		 )
		{
		if ( prevBranchOperand>=0 ) {
            [self write:prevBranchOperand value:(short)[self address]];
		}
        for (int i = 0; i < [endRefs count]; i++) {
            [self write:[endRefs integerAtIndex:i] value:(short)[self address]];
        }
		}
	;
// STARTHERE
conditional
	:	^('||' conditional conditional)		{[self emit:Bytecode.INSTR_OR];}
	|	^('&&' conditional conditional)		{[self emit:Bytecode.INSTR_AND];}
	|	^('!' conditional)					{[self emit:Bytecode.INSTR_NOT];}
	|	expr // not all expr are valid, but reuse code gen (parser restricts syntax)
	;

exprOptions : {[self emit:Bytecode.INSTR_OPTIONS];} ^(OPTIONS option*) ;

option : ^('=' ID expr) {[self setOption:$ID];} ;

expr
@init {NSInteger nt = 0, ne = 0;}
	:	^(ZIP ^(ELEMENTS (expr {ne++;})+) mapTemplateRef[ne])
		{[self emit1:$ZIP opcode:Bytecode.INSTR_ZIP_MAP arg:ne];}
	|	^(MAP expr (mapTemplateRef[1] {nt++;})+)
		{
		if ( nt>1 )
            [self emit1:$MAP opcode:Bytecode.INSTR_ROT_MAP arg:nt];
		else
            [self emit:$MAP opcode:Bytecode.INSTR_MAP];
		}
	|	prop
	|	includeExpr
	;

prop:	^(PROP expr ID)						{[self emit1:$PROP opcode:Bytecode.INSTR_LOAD_PROP s:$ID.text];}
	|	^(PROP_IND expr expr)				{[self emit:$PROP_IND opcode:Bytecode.INSTR_LOAD_PROP_IND];}
	;

mapTemplateRef[NSInteger num_exprs]
	:	^(	INCLUDE ID
			{for (NSInteger i=1; i<=$num_exprs; i++) [self emit:$INCLUDE opcode:Bytecode.INSTR_NULL];}
			args
		)
		{
		if ( $args.passThru ) [self emit1:$start opcode:Bytecode.INSTR_PASSTHRU s:$ID.text];
		if ( $args.namedArgs )
			[self emit1:$INCLUDE opcode:Bytecode.INSTR_NEW_BOX_ARGS s:$ID.text];
		else
			[self emit2:$INCLUDE opcode:Bytecode.INSTR_NEW s:$ID.text arg2:($args.n+$num_exprs)];
		}
	|	subtemplate
		{
		if ( $subtemplate.nargs != $num_exprs ) {
            [errMgr compileTimeError:ANON_ARGUMENT_MISMATCH
                       templateToken:templateToken
                                   t:$subtemplate.start.token
                                 arg:$subtemplate.nargs
                                arg2:$num_exprs];
		}
		for (NSInteger i=1; i<=$num_exprs; i++) [self emit:$subtemplate.start opcode:Bytecode.INSTR_NULL];
        [self emit2:$subtemplate.start
             opcode:Bytecode.INSTR_NEW
                  s:$subtemplate.name
               arg2:$num_exprs];
		}

	|	^(	INCLUDE_IND expr
			{
			[self emit:$INCLUDE_IND opcode:Bytecode.INSTR_TOSTR];
			for (NSInteger i=1; i<=$num_exprs; i++) [self emit:$INCLUDE_IND opcode:Bytecode.INSTR_NULL];
			}
			args
			{
			[self emit1:$INCLUDE_IND opcode:Bytecode.INSTR_NEW_IND arg:($args.n+$num_exprs)];
			}
		 )
	;

includeExpr
	:	^(EXEC_FUNC ID expr?)		{[self func:$ID];}
	|	^(INCLUDE ID args)
		{
		if ( $args.passThru ) [self emit1:$start opcode:Bytecode.INSTR_PASSTHRU s:$ID.text];
		if ( $args.namedArgs ) [self emit1:$INCLUDE opcode:Bytecode.INSTR_NEW_BOX_ARGS s:$ID.text];
		else [self emit2:$INCLUDE opcode:Bytecode.INSTR_NEW s:$ID.text arg2:$args.n];
		}
	|	^(INCLUDE_SUPER ID args)
		{
		if ( $args.passThru ) [self emit1:$start opcode:Bytecode.INSTR_PASSTHRU s:$ID.text];
		if ( $args.namedArgs ) [self emit1:$INCLUDE_SUPER opcode:Bytecode.INSTR_SUPER_NEW_BOX_ARGS s:$ID.text];
		else [self emit2:$INCLUDE_SUPER opcode:Bytecode.INSTR_SUPER_NEW s:$ID.text arg2:$args.n];
		}
	|	^(INCLUDE_REGION ID)		{
									CompiledST *impl =
										[Compiler defineBlankRegion:outermostImpl token:$ID.token];
									//impl.dump();
									[self emit2:$INCLUDE_REGION opcode:Bytecode.INSTR_NEW s:impl.name arg2:0];
									}
	|	^(INCLUDE_SUPER_REGION ID)	{
									 NSString *mangled =
		                                [STGroup getMangledRegionName:outermostImpl.name name:$ID.text];
									[self emit2:$INCLUDE_SUPER_REGION opcode:Bytecode.INSTR_SUPER_NEW s:mangled arg2:0];
									}
	|	primary
	;

primary
	:	ID				{[self refAttr:$ID];}
	|	STRING			{[self emit1:$STRING opcode:Bytecode.INSTR_LOAD_STR s:[Misc strip:$STRING.text n:1]];}	
	|	T_TRUE			{[self emit:$T_TRUE opcode:Bytecode.INSTR_TRUE];}
	|	T_FALSE			{[self emit:$T_FALSE opcode:Bytecode.INSTR_FALSE];}
	|	subtemplate		// push a subtemplate but ignore args since we can't pass any to it here
		                {[self emit2:$start opcode:Bytecode.INSTR_NEW s:$subtemplate.name arg2:0];}
	|	list
	|	^(	INCLUDE_IND
			expr        {[self emit:$INCLUDE_IND opcode:Bytecode.INSTR_TOSTR];}
			args        {[self emit1:$INCLUDE_IND opcode:Bytecode.INSTR_NEW_IND arg:$args.n];}
		 )
	|	^(TO_STR expr)	{[self emit:$TO_STR opcode:Bytecode.INSTR_TOSTR];}
	;

arg : expr ;

args returns [NSInteger n=0, BOOL namedArgs=NO, BOOL passThru]
	:	( arg {$n++;} )+
	|	{[self emit:$args.start opcode:Bytecode.INSTR_ARGS]; $namedArgs=YES;}
		(	^(eq='=' ID expr)
			{$n++; [self emit1:$eq opcode:Bytecode.INSTR_STORE_ARG arg:[self defineString:$ID.text]];}
		)+
		( '...' {$passThru=YES;} )?
    |   '...' {$passThru=YES; [self emit:$args.start opcode:Bytecode.INSTR_ARGS]; $namedArgs=YES;}
	|
 	;

list:	{[self emit:Bytecode.INSTR_LIST];}
		^(LIST (listElement {[self emit:$listElement.start opcode:Bytecode.INSTR_ADD];})* )
	;

listElement : expr | TNULL {[self emit:$TNULL opcode:Bytecode.INSTR_NULL];} ;
