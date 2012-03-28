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
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "CompilationState.h"
#import "Bytecode.h"
#import "STException.h"
#import "Misc.h"

@implementation CompilationState

@synthesize impl;
@synthesize stringtable;
@synthesize ip;
@synthesize tokens;
@synthesize errMgr;

+ (id) newCompilationState:(ErrorManager *)anErrMgr
                      name:(NSString *)aName
                    stream:(CommonTokenStream *)theTokens
{
    return [[CompilationState alloc] init:anErrMgr name:aName stream:theTokens];
}

- (id) init:(ErrorManager *)anErrMgr name:(NSString *)aName stream:(CommonTokenStream *)theTokens
{
    self=[super init];
    if ( self != nil ) {
        impl = [[CompiledST newCompiledST] retain];
        stringtable = [[[StringTable alloc] init] retain];
        ip = 0;
        errMgr = anErrMgr;
        if ( errMgr ) [errMgr retain];
        tokens = theTokens;
        if ( tokens ) [tokens retain];
        impl.name = aName;
        if ( impl.name ) [impl.name retain];
        impl.prefix = [Misc getPrefix:aName];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in CompilationState" );
#endif
    if ( impl ) [impl release];
    if ( stringtable ) [stringtable release];
    if ( tokens ) [tokens release];
    if ( errMgr ) [errMgr release];
    [super dealloc];
}

- (NSInteger) defineString:(NSString *)s
{
    return [stringtable addObject:s];
}

- (void) refAttr:(CommonToken *)templateToken tree:(CommonTree *)aTree
{
    if ( aTree == nil )
        @throw [STNoSuchAttributeException newException:@"nil tree in refAttr()"];
    NSString *name = aTree.text;
    if ( (impl.formalArguments != nil) && ([impl.formalArguments objectForKey:name] != nil) ) {
        FormalArgument *arg = [impl.formalArguments objectForKey:name];
        NSInteger index = (([arg isKindOfClass:[FormalArgument class]])? arg.index:0);
        [self emit1:aTree opcode:Bytecode.INSTR_LOAD_LOCAL arg:index];
    }
    else {
        if ( [[Interpreter predefinedAnonSubtemplateAttributes] objectForKey:name] != nil ) {
            [errMgr compileTimeError:REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE templateToken:templateToken t:(CommonToken *)aTree.token];
            [self emit:aTree opcode:Bytecode.INSTR_NULL];
        }
        else {
            [self emit1:aTree opcode:Bytecode.INSTR_LOAD_ATTR s:name];
        }
    }
}

- (void) setOption:(CommonTree *)aTree
{
    NSInteger Opt;
    Opt = (NSInteger)[[[Compiler getSupportedOptions] objectForKey:aTree.text] intValue];
    [self emit1:aTree opcode:Bytecode.INSTR_STORE_OPTION arg:Opt];
}

- (void) func:(CommonToken *)templateToken tree:(CommonTree *)aTree
{
    NSString *funcBytecode = [[[Compiler funcs] getDict] objectForKey:aTree.text];
    if (funcBytecode == nil) {
        [errMgr compileTimeError:NO_SUCH_FUNCTION templateToken:templateToken t:(CommonToken *)aTree.token];
        [self emit:aTree opcode:Bytecode.INSTR_POP];
    }
    else {
        [self emit:aTree opcode:(short)[funcBytecode intValue]];
    }
}

- (void) emit:(short)opcode
{
    [self emit:nil opcode:opcode];
}

- (void) emit:(CommonTree *)opAST opcode:(short)opcode
{
    [self ensureCapacity:1];
    if (opAST != nil) {
        NSInteger i = [opAST getTokenStartIndex];
        NSInteger j = [opAST getTokenStopIndex];
        NSInteger p = [((CommonToken *)[[tokens getTokens] objectAtIndex:i]) getStart];
        NSInteger q = [((CommonToken *)[[tokens getTokens] objectAtIndex:j]) getStop];
        if (!(p < 0 || q < 0)) {
            j = [impl.sourceMap count];
            if ( j <= ip ) {
                for (NSInteger i = j; i <= ip; i++ )
                    [impl.sourceMap addObject:[NSNull null]];
            }
            [impl.sourceMap replaceObjectAtIndex:ip withObject:[Interval newInterval:p b:q]];
        }
    }
    [impl.instrs insertChar:opcode atIndex:ip++];
}

- (void) emit1:(CommonTree *)opAST opcode:(short)opcode arg:(NSInteger)arg
{
    [self emit:opAST opcode:opcode];
    [self ensureCapacity:Bytecode.OPND_SIZE_IN_BYTES];
    [impl.instrs insertShort:(short)arg atIndex:ip];
    ip += Bytecode.OPND_SIZE_IN_BYTES;
}

- (void) emit2:(CommonTree *)opAST opcode:(short)opcode arg:(NSInteger)arg arg2:(NSInteger)arg2
{
    [self emit:opAST opcode:opcode];
    [self ensureCapacity:Bytecode.OPND_SIZE_IN_BYTES * 2];
    [impl.instrs insertShort:(short)arg atIndex:ip];
    ip += Bytecode.OPND_SIZE_IN_BYTES;
    [impl.instrs insertShort:(short)arg2 atIndex:ip];
    ip += Bytecode.OPND_SIZE_IN_BYTES;
}

- (void) emit2:(CommonTree *)opAST opcode:(short)opcode s:(NSString *)s arg2:(NSInteger)arg2
{
    NSInteger i = [self defineString:s];
    [self emit2:opAST opcode:opcode arg:i arg2:arg2];
}

- (void) emit1:(CommonTree *)opAST opcode:(short)opcode s:(NSString *)s
{
    NSInteger i = [self defineString:s];
    [self emit1:opAST opcode:opcode arg:i];
}

- (void) insert:(NSInteger)addr opcode:(short)opcode s:(NSString *)s
{
    [self ensureCapacity:1 + Bytecode.OPND_SIZE_IN_BYTES];
    NSInteger instrSize = 1 + Bytecode.OPND_SIZE_IN_BYTES;
    [impl.instrs memcpy:addr dest:addr+instrSize length:ip-addr];
    NSInteger save = ip;
    ip = addr;
    [self emit1:nil opcode:opcode s:s];
    ip = save + instrSize;
    NSInteger a = addr + instrSize;
    
    while (a < ip) {
        char op = [impl.instrs charAtIndex:a];
        Instruction *I = Bytecode.instructions[op];
        if (op == Bytecode.INSTR_BR || op == Bytecode.INSTR_BRF) {
            NSInteger opnd = [impl.instrs shortAtIndex:a + 1];
            [impl.instrs insertShort:(short)(opnd + instrSize) atIndex:a + 1];
        }
        a += I.nopnds * Bytecode.OPND_SIZE_IN_BYTES + 1;
    }
	//NSLog([NSString stringWithFormat:@"after  insert of $d(%@):", opcode, [impl.instrs description]]);
}

- (void) write:(NSInteger)addr value:(short)value
{
    [impl.instrs insertShort:value atIndex:addr];
}

- (void) ensureCapacity:(NSInteger)n
{
    if ((ip + n) >= [impl.instrs size]) {
        [impl.instrs ensureCapacity:(ip + n)];
    }
}

- (void) indent:(CommonTree *)indent
{
    [self emit1:indent opcode:Bytecode.INSTR_INDENT s:indent.text];
}

#ifdef DONTUSENOMO
/**
 * Write value at index into a byte array highest to lowest byte,
 * left to right.
 */
+ (void) writeShort:(char *)memory index:(NSInteger)index value:(short)value
{
    memory[index + 0] = (char)((value >> (8 * 1)) & 0xFF);
    memory[index + 1] = (char)(value & 0xFF);
}
#endif

@end
