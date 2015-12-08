/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
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
package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.misc.Interval;
import org.stringtemplate.v4.misc.Misc;

import java.util.ArrayList;
import java.util.List;

public class BytecodeDisassembler {
    CompiledST code;

    public BytecodeDisassembler(CompiledST code) { this.code = code; }

    public String instrs() {
        StringBuilder buf = new StringBuilder();
        int ip=0;
        while (ip<code.codeSize) {
            if ( ip>0 ) buf.append(", ");
            int opcode = code.instrs[ip];
            Bytecode.Instruction I = Bytecode.instructions[opcode];
            buf.append(I.name);
            ip++;
            for (int opnd=0; opnd<I.nopnds; opnd++) {
                buf.append(' ');
                buf.append(getShort(code.instrs, ip));
                ip += Bytecode.OPND_SIZE_IN_BYTES;
            }
        }
        return buf.toString();
    }

    public String disassemble() {
        StringBuilder buf = new StringBuilder();
        int i=0;
        while (i<code.codeSize) {
            i = disassembleInstruction(buf, i);
            buf.append('\n');
        }
        return buf.toString();
    }

    public int disassembleInstruction(StringBuilder buf, int ip) {
        int opcode = code.instrs[ip];
		if ( ip>=code.codeSize ) {
			throw new IllegalArgumentException("ip out of range: "+ip);
		}
        Bytecode.Instruction I =
            Bytecode.instructions[opcode];
        if ( I==null ) {
            throw new IllegalArgumentException("no such instruction "+opcode+
				" at address "+ip);
        }
        String instrName = I.name;
        buf.append( String.format("%04d:\t%-14s", ip, instrName) );
        ip++;
        if ( I.nopnds ==0 ) {
            buf.append("  ");
            return ip;
        }
        List<String> operands = new ArrayList<String>();
        for (int i=0; i<I.nopnds; i++) {
            int opnd = getShort(code.instrs, ip);
            ip += Bytecode.OPND_SIZE_IN_BYTES;
            switch ( I.type[i] ) {
                case STRING :
                    operands.add(showConstPoolOperand(opnd));
                    break;
                case ADDR :
                case INT :
                    operands.add(String.valueOf(opnd));
                    break;
                default:
                    operands.add(String.valueOf(opnd));
                    break;
            }
        }
        for (int i = 0; i < operands.size(); i++) {
            String s = operands.get(i);
            if ( i>0 ) buf.append(", ");
            buf.append( s );
        }
        return ip;
    }

    private String showConstPoolOperand(int poolIndex) {
        StringBuilder buf = new StringBuilder();
        buf.append("#");
        buf.append(poolIndex);
        String s = "<bad string index>";
        if ( poolIndex<code.strings.length ) {
            if ( code.strings[poolIndex]==null ) s = "null";
            else {
                s = code.strings[poolIndex];
                if (code.strings[poolIndex] != null) {
                    s = Misc.replaceEscapes(s);
                    s='"'+s+'"';
                }
            }
		}
        buf.append(":");
        buf.append(s);
        return buf.toString();
    }

    public static int getShort(byte[] memory, int index) {
        int b1 = memory[index]&0xFF; // mask off sign-extended bits
        int b2 = memory[index+1]&0xFF;
        int word = b1<<(8*1) | b2;
        return word;
    }

    public String strings() {
		StringBuilder buf = new StringBuilder();
		int addr = 0;
		if ( code.strings!=null ) {
			for (Object o : code.strings) {
				if ( o instanceof String ) {
					String s = (String)o;
					s = Misc.replaceEscapes(s);
					buf.append( String.format("%04d: \"%s\"\n", addr, s) );
				}
				else {
					buf.append( String.format("%04d: %s\n", addr, o) );
				}
				addr++;
			}
		}
        return buf.toString();
    }

    public String sourceMap() {
        StringBuilder buf = new StringBuilder();
        int addr = 0;
        for (Interval I : code.sourceMap) {
            if ( I!=null ) {
                String chunk = code.template.substring(I.a,I.b+1);
                buf.append( String.format("%04d: %s\t\"%s\"\n", addr, I, chunk) );
            }
            addr++;
        }
        return buf.toString();
    }
}
