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

#define DEF_MAX_OPNDS 2

typedef enum {
    T_NONE,
    T_STRING,
    T_ADDR,
    T_INT
} OperandType;

OperandType OperandTypeValueOf(NSString *text);
NSString *OperandTypeDescription(NSInteger value);

@interface Instruction : NSObject {
    NSString *name;
    __strong OperandType ttype[DEF_MAX_OPNDS];
    short nopnds;
}

@property (retain) NSString *name;
//@property (assign) short ttype[DEF_MAX_OPNDS];
@property (assign, getter=nopnds, setter = setNopnds:) short nopnds;

+ (id) newInstruction:(NSString *)aName;
+ (id) newInstruction:(NSString *)aName a:(OperandType)a;
+ (id) newInstruction:(NSString *)aName a:(OperandType)a b:(OperandType)b;

- (id) init:(NSString *)aName;
- (id) init:(NSString *)aName a:(OperandType)a;
- (id) init:(NSString *)aName a:(OperandType)a b:(OperandType)b;

- (short) nopnds;
- (void) setNopnds:(short)idx;

- (OperandType)getType:(NSInteger)idx;
- (void)setType:(OperandType)aType idx:(NSInteger)idx;

@end

/**
 * Used for assembly/disassembly; describes instruction set
 */
@interface Bytecode : NSObject {
}

+ (NSInteger)MAX_OPNDS;
+ (NSInteger)OPND_SIZE_IN_BYTES;
+ (OperandType)T_NONE;
+ (OperandType)T_STRING;
+ (OperandType)T_ADDR;
+ (OperandType)T_INT;

+ (Instruction **)instructions;

+ (void) initialize;
+ (short) INSTR_LOAD_STR;
+ (short) INSTR_LOAD_ATTR;
+ (short) INSTR_LOAD_LOCAL;
+ (short) INSTR_LOAD_PROP;
+ (short) INSTR_LOAD_PROP_IND;
+ (short) INSTR_STORE_OPTION;
+ (short) INSTR_STORE_ARG;
+ (short) INSTR_NEW;
+ (short) INSTR_NEW_IND;
+ (short) INSTR_NEW_BOX_ARGS;
+ (short) INSTR_SUPER_NEW;
+ (short) INSTR_SUPER_NEW_BOX_ARGS;
+ (short) INSTR_WRITE;
+ (short) INSTR_WRITE_OPT;
+ (short) INSTR_MAP;
+ (short) INSTR_ROT_MAP;
+ (short) INSTR_ZIP_MAP;
+ (short) INSTR_BR;
+ (short) INSTR_BRF;
+ (short) INSTR_OPTIONS;
+ (short) INSTR_ARGS;
+ (short) INSTR_PASSTHRU;
+ (short) INSTR_LIST;
+ (short) INSTR_ADD;
+ (short) INSTR_TOSTR;
+ (short) INSTR_FIRST;
+ (short) INSTR_LAST;
+ (short) INSTR_REST;
+ (short) INSTR_TRUNC;
+ (short) INSTR_STRIP;
+ (short) INSTR_TRIM;
+ (short) INSTR_LENGTH;
+ (short) INSTR_STRLEN;
+ (short) INSTR_REVERSE;
+ (short) INSTR_NOT;
+ (short) INSTR_OR;
+ (short) INSTR_AND;
+ (short) INSTR_INDENT;
+ (short) INSTR_DEDENT;
+ (short) INSTR_NEWLINE;
+ (short) INSTR_NOOP;
+ (short) INSTR_POP;
+ (short) INSTR_NULL;
+ (short) INSTR_TRUE;
+ (short) INSTR_FALSE;
+ (short) INSTR_WRITE_STR;
+ (short) INSTR_WRITE_LOCAL;
+ (short) MAX_BYTECODE;


@end
