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
package org.stringtemplate.interp;

public class BytecodeDefinition {
    public static final int OPND_SIZE_IN_BYTES = 2;
    public static final int STRING = 1;
    public static final int ADDR   = 2;
    public static final int INT    = 3;

    public static class Instruction {
        String name; // E.g., "load_str", "new"
        int[] type = new int[3];
        int n = 0;
        public Instruction(String name) { this(name,0,0,0); n=0; }
        public Instruction(String name, int a) { this(name,a,0,0); n=1; }
        public Instruction(String name, int a, int b) { this(name,a,b,0); n=2; }
        public Instruction(String name, int a, int b, int c) {
            this.name = name;
            type[0] = a;
            type[1] = b;
            type[2] = c;
            n = 3;
        }
    }

    // INSTRUCTION BYTECODES (byte is signed; use a short to keep 0..255)
    public static final short INSTR_LOAD_STR    = 1;
    public static final short INSTR_LOAD_ATTR   = 2;
    public static final short INSTR_LOAD_PROP   = 3;
    public static final short INSTR_STORE_ATTR  = 4;
    public static final short INSTR_NEW         = 5;  // create new template instance
    public static final short INSTR_WRITE       = 6;
    public static final short INSTR_MAP         = 7;  // <a:b()>, <a:b():c()>, <a:{...}>
    public static final short INSTR_ROT_MAP     = 8;  // <a:b(),c()>
    public static final short INSTR_PAR_MAP     = 9;  // <x,y:{...}>
    public static final short INSTR_BR          = 10;
    public static final short INSTR_BRF         = 11;
    public static final short INSTR_BRT         = 12;

    /** Used for assembly/disassembly; describes instruction set */
    // START: instr
    public static Instruction[] instructions = new Instruction[] {
        null, // <INVALID>
        new Instruction("load_str",STRING), // index is the opcode
        new Instruction("load_attr",STRING),
        new Instruction("load_prop",STRING),
        new Instruction("store_attr",STRING),
        new Instruction("new",STRING),
        new Instruction("write"),
        new Instruction("map"),
        new Instruction("rot_map", INT),
        new Instruction("par_map"),
        new Instruction("br", ADDR),
        new Instruction("brf", ADDR),
        new Instruction("brt", ADDR)
    };
    // END: instr
}
