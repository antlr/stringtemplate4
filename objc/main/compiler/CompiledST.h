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
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "ST.h"
#import "STGroup.h"
#import "MemBuffer.h"
#import "FormalArgument.h"
#import "Compiler.h"
#import "Interval.h"

/**
 * The result of compiling an ST.  Contains all the bytecode instructions,
 * string table, bytecode address to source code map, and other bookkeeping
 * info.  It's the implementation of an ST you might say.  All instances
 * of the same template share a single implementation (impl field).
 */

@interface CompiledST : NSObject {
    __strong NSString *name;
    
/** The original, immutable pattern (not really used again after
 * initial "compilation"). Useful for debugging.  Even for
 * subtemplates, this is entire overall template.
 */
    __strong NSString *prefix;
    __strong NSString *template;
    
    /** The token that begins template definition; could be <@r> of region. */
    __strong CommonToken *templateDefStartToken;
    /** Overall token stream for template (debug only) */
    __strong CommonTokenStream *tokens;
    
	/** How do we interpret syntax of template? (debug only) */
    __strong CommonTree *ast;
    
    /** Must be non null map if !noFormalArgs */
    __strong AMutableDictionary *formalArguments;
    BOOL hasFormalArgs;
    NSInteger numberOfArgsWithDefaultValues;

    /** A list of all regions and subtemplates */
    __strong AMutableArray *implicitlyDefinedTemplates;
    
    /** The group that physically defines this ST definition.  We use it to initiate
     *  interpretation via ST.toString().  From there, it becomes field 'group'
     *  in interpreter and is fixed until rendering completes.
     */
    __strong STGroup *nativeGroup;
    
    /** Does this template come from a <@region>...<@end> embedded in
     *  another template?
     */
    BOOL isRegion;
    
    /** If someone refs <@r()> in template t, an implicit
     *
     *   @t.r() ::= ""
     *
     *  is defined, but you can overwrite this def by defining your
     *  own.  We need to prevent more than one manual def though.  Between
     *  this var and isEmbeddedRegion we can determine these cases.
     */
    RegionTypeEnum regionDefType;
    BOOL isAnonSubtemplate;
    __strong AMutableArray *strings;     // string operands of instructions
    __strong MemBuffer *instrs;          // byte-addressable code memory.
    NSInteger codeSize;
    __strong AMutableArray *sourceMap;   // maps IP to range in template pattern
}

@property (retain) NSString *name;
@property (retain) NSString *prefix;
@property (retain) NSString *template;
@property (retain) CommonToken *templateDefStartToken;
@property (retain) CommonTokenStream *tokens;
@property (retain) CommonTree *ast;
@property (retain) AMutableDictionary *formalArguments;
@property (assign) BOOL hasFormalArgs;
@property (assign) NSInteger numberOfArgsWithDefaultValues;
@property (retain) AMutableArray *implicitlyDefinedTemplates;
@property (retain, getter=nativeGroup, setter = setNativeGroup:) STGroup *nativeGroup;
@property (assign) BOOL isRegion;
@property (assign) RegionTypeEnum regionDefType;
@property (assign) BOOL isAnonSubtemplate;
@property (retain) AMutableArray *strings;
@property (retain) MemBuffer *instrs;
@property (assign) NSInteger codeSize;
@property (retain) AMutableArray *sourceMap;

+ (CompiledST *) newCompiledST;
- (id) init;
- (id) copyWithZone:(NSZone *)aZone;
- (void) addImplicitlyDefinedTemplate:(CompiledST *)sub;
- (void) defineArgDefaultValueTemplates:(STGroup *)group;
- (void) defineFormalArgs:(AMutableArray *)args;
- (void) addArg:(FormalArgument *)a;
- (void) defineImplicitlyDefinedTemplates:(STGroup *)group;
- (NSString *) getTemplateSource;
- (Interval *) getTemplateRange;
- (NSString *) dis_instrs;
- (void) dump;
- (NSString *) disasm;

// getters and setters
@end
