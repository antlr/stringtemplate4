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
#import <ANTLR/RuntimeException.h>
#import "STErrorListener.h"
#import "BytecodeDisassembler.h"
#import "Bytecode.h"
#import "Interval.h"
#import "Misc.h"
#import "CompiledST.h"

@implementation BytecodeDisassembler

+ (NSInteger) getShort:(id)memBuffer index:(NSInteger)index
{
/*
    NSInteger b1 = memory[index] & 0xFF;
    NSInteger b2 = memory[index + 1] & 0xFF;
    NSInteger word = b1 << (8 * 1) | b2;
    return word;
*/
    return [memBuffer shortAtIndex:index];
}

+ (id) newBytecodeDisassembler:(CompiledST *)aCode
{
    return [[BytecodeDisassembler alloc] initWithCode:aCode];
}

- (id) initWithCode:(CompiledST *)aCode
{
    self=[super init];
    if ( self ) {
        code = aCode;
        if ( code ) [code retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Instruction" );
#endif
    if ( code ) [code release];
    [super dealloc];
}

- (NSString *) instrs
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    NSInteger ip = 0;

    while (ip < code.codeSize) {
        if (ip > 0)
            [buf appendString:@", "];
        NSInteger opcode = (NSInteger) [code.instrs charAtIndex:ip];
        Instruction *I = Bytecode.instructions[opcode];
        [buf appendString:I.name];
        ip++;
        for (NSInteger opnd = 0; opnd < I.nopnds; opnd++) {
            [buf appendFormat:@" %d", [code.instrs shortAtIndex:ip]];
            ip += Bytecode.OPND_SIZE_IN_BYTES;
        }
    }
    return [buf description];
}

- (NSString *) disassemble
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:200];
    NSInteger i = 0;
    while (i < code.codeSize) {
        i = [self disassembleInstruction:buf ip:i];
        [buf appendString:@"\n"];
    }
    return [buf description];
}

- (NSInteger) disassembleInstruction:(NSMutableString *)buf ip:(NSInteger)ip
{
    NSInteger opcode = [code.instrs charAtIndex:ip];
    if (ip >= code.codeSize) {
        @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"ip out of range: %d", ip]];
    }
    Instruction *I = Bytecode.instructions[opcode];
    if (I == nil) {
        @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"no such instruction %d at address %d", opcode, ip]];
    }
    NSString *instrName = I.name;
    [buf appendFormat:@"%04d:\t%-14s", ip, [instrName cStringUsingEncoding:NSASCIIStringEncoding]];
    ip++;
    if ( I.nopnds == 0 ) {
        [buf appendString:@"  "];
        return ip;
    }
    AMutableArray *operands = [AMutableArray arrayWithCapacity:100];
    for (NSInteger i = 0; i < I.nopnds; i++) {
        NSInteger opnd = [code.instrs shortAtIndex:ip];
        ip += Bytecode.OPND_SIZE_IN_BYTES;
        switch ([I getType:i]) {
        case T_STRING:
            [operands addObject:[self showConstPoolOperand:opnd]];
            break;
        case T_ADDR:
        case T_INT:
            [operands addObject:[NSString stringWithFormat:@"%d", opnd]];
            break;
        default:
            [operands addObject:[NSString stringWithFormat:@"%d", opnd]];
            break;
        }
    }
    for (NSInteger i = 0; i < [operands count]; i++) {
        NSString *s = [operands objectAtIndex:i];
        if ( i > 0 )
            [buf appendString:@", "];
        [buf appendString:s];
    }
    return ip;
}

- (NSString *) showConstPoolOperand:(NSInteger)poolIndex
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:100];
    [buf appendFormat:@"#%d", poolIndex];
    NSString *s = @"<bad string index>";
    if (poolIndex < [code.strings count]) {
        s = [code.strings objectAtIndex:poolIndex];
        if ( s == nil || ![s isKindOfClass:[NSString class]])
            s = @"";
        else {
            s = [s description];
            if ([s isKindOfClass:[NSString class]]) {
                s = [Misc replaceEscapes:s];
                s = [NSString stringWithFormat:@"\"%@\"", s];
            }
        }
    }
    [buf appendFormat:@":%@", s];
    return [buf description];
}

- (NSString *) strings
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:200];
    NSInteger addr = 0;
    if (code.strings != nil) {
        for (id obj in code.strings) {
/*
        id obj;
        ArrayIterator *it = [ArrayIterator newIterator:code.strings];
        while ( [it hasNext] ) {
            obj = [it nextObject];
 */
            if ([obj isKindOfClass:[NSString class]]) {
                NSString *s = (NSString *)obj;
                s = [Misc replaceEscapes:s];
                [buf appendString:[NSString stringWithFormat:@"%04d: \"%@\"\n", addr, s]];
            }
             else {
                [buf appendString:[NSString stringWithFormat:@"%04d: %@\n", addr, obj]];
            }
            addr++;
        }
    }
    return [buf description];
}

- (NSString *) sourceMap
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:200];
    NSInteger addr = 0;
//    for (Interval *I in code.sourceMap) {
    Interval *I;
    ArrayIterator *it = [code.sourceMap objectEnumerator];
    while ( [it hasNext] ) {
        I = (Interval *)[it nextObject];
        if (I != nil) {
            NSString *chunk = [code.template substringWithRange:NSMakeRange(I.a, (I.b + 1)-I.a)];
            [buf appendString:[NSString stringWithFormat:@"%04d: %@\t\"%@\"\n", addr, I, chunk]];
        }
        addr++;
    }
    return [buf description];
}

@synthesize code;
@end
