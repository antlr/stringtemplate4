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
package org.stringtemplate;

import org.stringtemplate.BytecodeDefinition;

import java.util.List;
import java.util.ArrayList;

public class BytecodeDisassembler {
    // TODO: make disassembler point at compiledST code?
    byte[] code;
    int codeSize;
    protected Object[] strings;
    BytecodeDefinition def;

    public BytecodeDisassembler(byte[] code,
                                int codeSize,
                                String[] strings)
    {
        this.code = code;
        this.codeSize = codeSize;
        this.strings = strings;
    }

    public String instrs() {
        StringBuilder buf = new StringBuilder();
        int ip=0;
        while (ip<codeSize) {
            if ( ip>0 ) buf.append(", ");
            int opcode = code[ip];
            BytecodeDefinition.Instruction I = BytecodeDefinition.instructions[opcode];
            buf.append(I.name);
            ip++;
            for (int opnd=0; opnd<I.n; opnd++) {
                buf.append(' ');
                buf.append(getShort(code, ip));
                ip += BytecodeDefinition.OPND_SIZE_IN_BYTES;
            }
        }
        return buf.toString();
    }

    public String disassemble() {
        StringBuilder buf = new StringBuilder();
        int i=0;
        while (i<codeSize) {
            i = disassembleInstruction(buf, i);
            buf.append('\n');
        }
        return buf.toString();
    }

    public int disassembleInstruction(StringBuilder buf, int ip) {
        int opcode = code[ip];
        BytecodeDefinition.Instruction I =
            BytecodeDefinition.instructions[opcode];
        if ( I==null ) {
            System.err.println("no such instruction "+opcode);
        }
        String instrName = I.name;
        buf.append( String.format("%04d:\t%-11s", ip, instrName) );
        ip++;
        if ( I.n==0 ) {
            buf.append( String.format("  ") );
            return ip;
        }
        List<String> operands = new ArrayList<String>();
        for (int i=0; i<I.n; i++) {
            int opnd = getShort(code, ip);
            ip += BytecodeDefinition.OPND_SIZE_IN_BYTES;
            switch ( I.type[i] ) {
                case BytecodeDefinition.STRING :
                    operands.add(showConstPoolOperand(opnd));
                    break;
                case BytecodeDefinition.ADDR :
                case BytecodeDefinition.INT :
                    operands.add(String.valueOf(opnd));
                    break;
                default:
                    operands.add(String.valueOf(opnd));
                    break;
            }
        }
        for (int i = 0; i < operands.size(); i++) {
            String s = (String) operands.get(i);
            if ( i>0 ) buf.append(", ");
            buf.append( String.format(s) );
        }
        return ip;
    }

    private String showConstPoolOperand(int poolIndex) {
        StringBuffer buf = new StringBuffer();
        buf.append("#");
        buf.append(poolIndex);
        String s = strings[poolIndex].toString();
        if ( strings[poolIndex] instanceof String ) s='"'+s+'"';
        buf.append(":");
        buf.append(s);
        return buf.toString();
    }
    
    public static int getShort(byte[] memory, int index) {
        int b1 = memory[index++]&0xFF; // mask off sign-extended bits
        int b2 = memory[index++]&0xFF;
        int word = b1<<(8*1) | b2;
        return word;
    }

    public String strings() {
        StringBuffer buf = new StringBuffer();
        int addr = 0;
        for (Object o : strings) {
            if ( o instanceof String ) {
                buf.append( String.format("%04d: \"%s\"\n", addr, o) );
            }
            else {
                buf.append( String.format("%04d: %s\n", addr, o) );
            }
            addr++;
        }
        return buf.toString();
    }
}
