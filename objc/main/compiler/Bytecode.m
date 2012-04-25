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
#import <Foundation/Foundation.h>
#import "Bytecode.h"

OperandType OperandTypeValueOf(NSString *text)
{
    if (text) {
        if ([text isEqualToString:@"NONE"])
            return T_NONE;
        else if ([text isEqualToString:@"STRING"])
            return T_STRING;
        else if ([text isEqualToString:@"ADDR"])
            return T_ADDR;
        else if ([text isEqualToString:@"INT"])
            return T_INT;
    }
    return -1;
}

NSString *OperandTypeDescription(NSInteger value)
{
    switch (value) {
        case T_NONE:
            return @"NONE";
        case T_STRING:
            return @"STRING";
        case T_ADDR:
            return @"ADDR";
        case T_INT:
            return @"INT";
    }
    return nil;
}

@implementation Instruction

@synthesize name;
//@synthesize ttype;
//@synthesize nopnds;

+ (id) newInstruction:(NSString *)aName
{
    return [[Instruction alloc] init:aName];
}

+ (id) newInstruction:(NSString *)aName a:(OperandType)a
{
    return [[Instruction alloc] init:aName a:a];
}

+ (id) newInstruction:(NSString *)aName a:(OperandType)a b:(OperandType)b
{
    return [[Instruction alloc] init:aName a:a b:b];
}

- (id) init:(NSString *)aName
{
    if ( (self = [super init]) != nil ) {
        name = aName;
        ttype[0]=T_NONE;
        ttype[1]=T_NONE;
        nopnds = 0;
    }
    return self;
}

- (id) init:(NSString *)aName a:(OperandType)a
{
    if ( (self = [super init]) != nil ) {
        name = aName;
        ttype[0]=a;
        ttype[1]=T_NONE;
        nopnds = 1;
    }
    return self;
}

- (id) init:(NSString *)aName a:(OperandType)a b:(OperandType)b
{
    if ( (self = [super init]) != nil ) {
        name = aName;
        ttype[0]=a;
        ttype[1]=b;
        nopnds = DEF_MAX_OPNDS;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Instruction" );
#endif
    if ( name ) [name release];
    [super dealloc];
}

// getters and setters

- (short) nopnds
{
    return nopnds;
}

- (void)setNopnds:(short)idx
{
    if (idx >= 0 && idx <= DEF_MAX_OPNDS)
        nopnds = idx;
    else
        // @throw error here
        return;
}

- (OperandType)getType:(NSInteger)idx
{
    if (idx >= 0 && idx < DEF_MAX_OPNDS)
        return ttype[idx];
    return -1;
}

- (void)setType:(OperandType)aType idx:(NSInteger)idx
{
    if (idx >= 0 && idx < DEF_MAX_OPNDS)
        ttype[idx] = aType;
    else
        // @throw error here
        return;
}

@end

@implementation Bytecode
static NSInteger MAX_OPNDS                  = DEF_MAX_OPNDS;
static NSInteger OPND_SIZE_IN_BYTES         = 2;
static const short INSTR_LOAD_STR           = 1;
static const short INSTR_LOAD_ATTR          = 2;
static const short INSTR_LOAD_LOCAL         = 3; // load stuff like it, i, i0
static const short INSTR_LOAD_PROP          = 4;
static const short INSTR_LOAD_PROP_IND      = 5;
static const short INSTR_STORE_OPTION       = 6;
static const short INSTR_STORE_ARG          = 7;
static const short INSTR_NEW                = 8;  // create new template instance
static const short INSTR_NEW_IND            = 9;  // create new instance using value on stack
static const short INSTR_NEW_BOX_ARGS       = 10; // create new instance using args in Map on stack
static const short INSTR_SUPER_NEW          = 11; // create new instance using value on stack
static const short INSTR_SUPER_NEW_BOX_ARGS = 12; // create new instance using args in Map on stack
static const short INSTR_WRITE              = 13;
static const short INSTR_WRITE_OPT          = 14;
static const short INSTR_MAP                = 15; // <a:b()>, <a:b():c()>, <a:{...}>
static const short INSTR_ROT_MAP            = 16; // <a:b(),c()>
static const short INSTR_ZIP_MAP            = 17; // <names,phones:{n,p | ...}>
static const short INSTR_BR                 = 18;
static const short INSTR_BRF                = 19; // push options map
static const short INSTR_OPTIONS            = 20; // push args map
static const short INSTR_ARGS               = 21;
static const short INSTR_PASSTHRU           = 22;
static const short INSTR_PASSTHRU_IND       = 23;
static const short INSTR_LIST               = 24;
static const short INSTR_ADD                = 25;
static const short INSTR_TOSTR              = 26;

// Predefined function
static const short INSTR_FIRST              = 27;
static const short INSTR_LAST               = 28;
static const short INSTR_REST               = 29;
static const short INSTR_TRUNC              = 30;
static const short INSTR_STRIP              = 31;
static const short INSTR_TRIM               = 32;
static const short INSTR_LENGTH             = 33;
static const short INSTR_STRLEN             = 34;
static const short INSTR_REVERSE            = 35;
static const short INSTR_NOT                = 36;
static const short INSTR_OR                 = 37;
static const short INSTR_AND                = 38;
static const short INSTR_INDENT             = 39;
static const short INSTR_DEDENT             = 40;
static const short INSTR_NEWLINE            = 41;
static const short INSTR_NOOP               = 42; // do nothing
static const short INSTR_POP                = 43;
static const short INSTR_NULL               = 44; // push null value
static const short INSTR_TRUE               = 45; // push true value
static const short INSTR_FALSE              = 46;

// combined instructions

static const short INSTR_WRITE_STR          = 47; // load_str n, write
static const short INSTR_WRITE_LOCAL        = 48; // load_local n, write

static const short MAX_BYTECODE             = 48;
#define INSTR_ARRAY_SIZE MAX_BYTECODE+2

/**
 * Used for assembly/disassembly; describes instruction set
 */
static Instruction *instructions[INSTR_ARRAY_SIZE];

+ (void) initialize
{
    instructions[0]                         = [[Instruction newInstruction:@"nil"] retain];
    instructions[INSTR_LOAD_STR]            = [[Instruction newInstruction:@"load_str" a:T_STRING] retain];
    instructions[INSTR_LOAD_ATTR]           = [[Instruction newInstruction:@"load_attr" a:T_STRING] retain];
    instructions[INSTR_LOAD_LOCAL]          = [[Instruction newInstruction:@"load_local" a:T_INT] retain];
    instructions[INSTR_LOAD_PROP]           = [[Instruction newInstruction:@"load_prop" a:T_STRING] retain];
    instructions[INSTR_LOAD_PROP_IND]       = [[Instruction newInstruction:@"load_prop_ind"] retain];
    instructions[INSTR_STORE_OPTION]        = [[Instruction newInstruction:@"store_option" a:T_INT] retain];
    instructions[INSTR_STORE_ARG]           = [[Instruction newInstruction:@"store_arg" a:T_STRING] retain];
    instructions[INSTR_NEW]                 = [[Instruction newInstruction:@"new" a:T_STRING b:T_INT] retain];
    instructions[INSTR_NEW_IND]             = [[Instruction newInstruction:@"new_ind" a:T_INT] retain];
    instructions[INSTR_NEW_BOX_ARGS]        = [[Instruction newInstruction:@"new_box_args" a:T_STRING] retain];
    instructions[INSTR_SUPER_NEW]           = [[Instruction newInstruction:@"super_new" a:T_STRING b:T_INT] retain];
    instructions[INSTR_SUPER_NEW_BOX_ARGS]  = [[Instruction newInstruction:@"super_new_box_args" a:T_STRING] retain];
    instructions[INSTR_WRITE]               = [[Instruction newInstruction:@"write"] retain];
    instructions[INSTR_WRITE_OPT]           = [[Instruction newInstruction:@"write_opt"] retain];
    instructions[INSTR_MAP]                 = [[Instruction newInstruction:@"map"] retain];
    instructions[INSTR_ROT_MAP]             = [[Instruction newInstruction:@"rot_map" a:T_INT] retain];
    instructions[INSTR_ZIP_MAP]             = [[Instruction newInstruction:@"zip_map" a:T_INT] retain];
    instructions[INSTR_BR]                  = [[Instruction newInstruction:@"br" a:T_ADDR] retain];
    instructions[INSTR_BRF]                 = [[Instruction newInstruction:@"brf" a:T_ADDR] retain];
    instructions[INSTR_OPTIONS]             = [[Instruction newInstruction:@"options"] retain];
    instructions[INSTR_ARGS]                = [[Instruction newInstruction:@"args"] retain];
    instructions[INSTR_PASSTHRU]            = [[Instruction newInstruction:@"passthru" a:T_ADDR] retain];
    instructions[INSTR_PASSTHRU_IND]        = nil; //[[Instruction newInstruction:@"passthru_ind" a:T_INT] retain];
    instructions[INSTR_LIST]                = [[Instruction newInstruction:@"list"] retain];
    instructions[INSTR_ADD]                 = [[Instruction newInstruction:@"add"] retain];
    instructions[INSTR_TOSTR]               = [[Instruction newInstruction:@"tostr"] retain];
    
    // Predefined functions
    instructions[INSTR_FIRST]               = [[Instruction newInstruction:@"first"] retain];
    instructions[INSTR_LAST]                = [[Instruction newInstruction:@"last"] retain];
    instructions[INSTR_REST]                = [[Instruction newInstruction:@"rest"] retain];
    instructions[INSTR_TRUNC]               = [[Instruction newInstruction:@"trunc"] retain];
    instructions[INSTR_STRIP]               = [[Instruction newInstruction:@"strip"] retain];
    instructions[INSTR_TRIM]                = [[Instruction newInstruction:@"trim"] retain];
    instructions[INSTR_LENGTH]              = [[Instruction newInstruction:@"length"] retain];
    instructions[INSTR_STRLEN]              = [[Instruction newInstruction:@"strlen"] retain];
    instructions[INSTR_REVERSE]             = [[Instruction newInstruction:@"reverse"] retain];
    instructions[INSTR_NOT]                 = [[Instruction newInstruction:@"not"] retain];
    instructions[INSTR_OR]                  = [[Instruction newInstruction:@"or"] retain];
    instructions[INSTR_AND]                 = [[Instruction newInstruction:@"and"] retain];
    instructions[INSTR_INDENT]              = [[Instruction newInstruction:@"indent" a:T_STRING] retain];
    instructions[INSTR_DEDENT]              = [[Instruction newInstruction:@"dedent"] retain];
    instructions[INSTR_NEWLINE]             = [[Instruction newInstruction:@"newline"] retain];
    instructions[INSTR_NOOP]                = [[Instruction newInstruction:@"noop"] retain];
    instructions[INSTR_POP]                 = [[Instruction newInstruction:@"pop"] retain];
    instructions[INSTR_NULL]                = [[Instruction newInstruction:@"null"] retain];
    instructions[INSTR_POP]                 = [[Instruction newInstruction:@"true"] retain];
    instructions[INSTR_FALSE]               = [[Instruction newInstruction:@"false"] retain];

	// combined instructions
    instructions[INSTR_WRITE_STR]           = [[Instruction newInstruction:@"write_str" a:T_STRING] retain];
    instructions[INSTR_WRITE_LOCAL]         = [[Instruction newInstruction:@"write_local" a:T_INT] retain];
    instructions[MAX_BYTECODE+1]            = nil;
    
}

+ (OperandType)T_NONE { return T_NONE; }
+ (OperandType)T_STRING { return T_STRING; }
+ (OperandType)T_ADDR { return T_ADDR; }
+ (OperandType)T_INT { return T_INT; }

+ (NSInteger)MAX_OPNDS
{
    return MAX_OPNDS;
}

+ (short) INSTR_LOAD_STR
{
    return INSTR_LOAD_STR;
}

+ (short) INSTR_LOAD_ATTR
{
    return INSTR_LOAD_ATTR;
}

+ (short) INSTR_LOAD_LOCAL
{
    return INSTR_LOAD_LOCAL;
}

+ (short) INSTR_LOAD_PROP
{
    return INSTR_LOAD_PROP;
}

+ (short) INSTR_LOAD_PROP_IND
{
    return INSTR_LOAD_PROP_IND;
}

+ (short) INSTR_STORE_OPTION
{
    return INSTR_STORE_OPTION;
}

+ (short) INSTR_STORE_ARG
{
    return INSTR_STORE_ARG;
}

+ (short) INSTR_NEW
{
    return INSTR_NEW;
}

+ (short) INSTR_NEW_IND
{
    return INSTR_NEW_IND;
}

+ (short) INSTR_NEW_BOX_ARGS
{
    return INSTR_NEW_BOX_ARGS;
}

+ (short) INSTR_SUPER_NEW
{
    return INSTR_SUPER_NEW;
}

+ (short) INSTR_SUPER_NEW_BOX_ARGS
{
    return INSTR_SUPER_NEW_BOX_ARGS;
}

+ (short) INSTR_WRITE
{
    return INSTR_WRITE;
}

+ (short) INSTR_WRITE_OPT
{
    return INSTR_WRITE_OPT;
}

+ (short) INSTR_MAP
{
    return INSTR_MAP;
}

+ (short) INSTR_ROT_MAP
{
    return INSTR_ROT_MAP;
}

+ (short) INSTR_ZIP_MAP
{
    return INSTR_ZIP_MAP;
}

+ (short) INSTR_BR
{
    return INSTR_BR;
}

+ (short) INSTR_BRF
{
    return INSTR_BRF;
}

+ (short) INSTR_OPTIONS
{
    return INSTR_OPTIONS;
}

+ (short) INSTR_ARGS
{
    return INSTR_ARGS;
}

+ (short) INSTR_PASSTHRU
{
    return INSTR_PASSTHRU;
}

+ (short) INSTR_LIST
{
    return INSTR_LIST;
}

+ (short) INSTR_ADD
{
    return INSTR_ADD;
}

+ (short) INSTR_TOSTR
{
    return INSTR_TOSTR;
}

+ (short) INSTR_FIRST
{
    return INSTR_FIRST;
}

+ (short) INSTR_LAST
{
    return INSTR_LAST;
}

+ (short) INSTR_REST
{
    return INSTR_REST;
}

+ (short) INSTR_TRUNC
{
    return INSTR_TRUNC;
}

+ (short) INSTR_STRIP
{
    return INSTR_STRIP;
}

+ (short) INSTR_TRIM
{
    return INSTR_TRIM;
}

+ (short) INSTR_LENGTH
{
    return INSTR_LENGTH;
}

+ (short) INSTR_STRLEN
{
    return INSTR_STRLEN;
}

+ (short) INSTR_REVERSE
{
    return INSTR_REVERSE;
}

+ (short) INSTR_NOT
{
    return INSTR_NOT;
}

+ (short) INSTR_OR
{
    return INSTR_OR;
}

+ (short) INSTR_AND
{
    return INSTR_AND;
}

+ (short) INSTR_INDENT
{
    return INSTR_INDENT;
}

+ (short) INSTR_DEDENT
{
    return INSTR_DEDENT;
}

+ (short) INSTR_NEWLINE
{
    return INSTR_NEWLINE;
}

+ (short) INSTR_NOOP
{
    return INSTR_NOOP;
}

+ (short) INSTR_POP
{
    return INSTR_POP;
}

+ (short) INSTR_NULL
{
    return INSTR_NULL;
}

+ (short) INSTR_TRUE;
{
    return INSTR_TRUE;
}

+ (short) INSTR_FALSE;
{
    return INSTR_FALSE;
}

+ (short) INSTR_WRITE_STR;
{
    return INSTR_WRITE_STR;
}

+ (short) INSTR_WRITE_LOCAL;
{
    return INSTR_WRITE_LOCAL;
}

+ (short) MAX_BYTECODE;
{
    return MAX_BYTECODE;
}

+ (NSInteger)OPND_SIZE_IN_BYTES
{
    return OPND_SIZE_IN_BYTES;
}

+ (Instruction **)instructions
{
    return instructions;
}


@end
