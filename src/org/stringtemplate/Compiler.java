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

import java.util.*;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;

public class Compiler implements ParserListener {
    public static final int INITIAL_CODE_SIZE = 100;
    List<String> strings;
    byte[] instrs;
    int ip = 0;
    Stack<Chunk> ifs = new Stack<Chunk>();

    public CompiledST compile(String template) throws Exception {
        strings = new ArrayList<String>();
        instrs = new byte[INITIAL_CODE_SIZE];
        //System.out.println("compile "+template);
        List<Chunk> chunks = breakTemplateIntoChunks(template, '<', '>');
        for (Chunk c : chunks) {
            //System.out.println("compile chunk "+c.text);
            if ( c.isExpr() ) {
                STLexer lexer = new STLexer(new ANTLRStringStream(c.text));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                STParser parser = new STParser(tokens, this);
                int firstTokenType = tokens.LA(1);
                if ( firstTokenType==STLexer.IF ) ifs.push(c);

                parser.stexpr();
                
                if ( firstTokenType==STLexer.ENDIF ) ifs.pop();
                if ( !(firstTokenType==STLexer.IF ||
                       firstTokenType==STLexer.ELSE ||
                       firstTokenType==STLexer.ELSEIF ||
                       firstTokenType==STLexer.ENDIF) )
                {
                    gen(BytecodeDefinition.INSTR_WRITE);                    
                }
            }
            else {
                gen(BytecodeDefinition.INSTR_LOAD_STR, c.text);                
                gen(BytecodeDefinition.INSTR_WRITE);
            }
        }
        CompiledST code = new CompiledST();
        code.template = template;
        if ( strings!=null ) {
            code.strings = strings.toArray(new String[strings.size()]);
        }
        code.instrs = instrs;
        code.codeSize = ip;
        //code.dump();
        return code;
    }

    public int defineString(String s) {
        strings.add(s);
        return strings.size()-1;
    }

    // LISTEN TO PARSER
    
    public void apply() {
        gen(BytecodeDefinition.INSTR_MAP);
    }

    public void applyAlternating(int numTemplates) {
        gen(BytecodeDefinition.INSTR_ROT_MAP, numTemplates);
    }

    public void instance(Token id) {
        gen(BytecodeDefinition.INSTR_NEW, id.getText());
    }

    public void refAttr(Token id) {
        gen(BytecodeDefinition.INSTR_LOAD_ATTR, id.getText());
    }

    public void refProp(Token id) {
        gen(BytecodeDefinition.INSTR_LOAD_PROP, id.getText());
    }

    public void refString(Token str) {
        gen(BytecodeDefinition.INSTR_LOAD_STR, str.getText());
    }

    public void setArg(Token arg) {
        gen(BytecodeDefinition.INSTR_STORE_ATTR, arg.getText());
    }        
    
    public void ifExpr(Token t) {
        //System.out.println("ifExpr @ "+ip);
    }
    
    public void ifExprClause(Token t, boolean not) {
        //System.out.println("ifExprClause @ "+ip);
        ifs.peek().prevBranch = ip+1;
        short i = BytecodeDefinition.INSTR_BRF;
        if ( not ) i = BytecodeDefinition.INSTR_BRT;
        gen(i, -1); // write placeholder
    }
    
    public void elseifExpr(Token t) {
        //System.out.println("elseifExpr @ "+ip);
        ifs.peek().endRefs.add(ip+1);
        gen(BytecodeDefinition.INSTR_BR, -1); // write placeholder
        writeShort(instrs, ifs.peek().prevBranch, (short)ip);
        ifs.peek().prevBranch = -1;
    }
    
    public void elseifExprClause(Token t, boolean not) {
        //System.out.println("elseifExprClause of "+ifs.peek()+" @ "+ip);
        ifs.peek().prevBranch = ip+1;
        short i = BytecodeDefinition.INSTR_BRF;
        if ( not ) i = BytecodeDefinition.INSTR_BRT;
        gen(i, -1); // write placeholder
    }
    
    public void elseClause() {
        //System.out.println("else of "+ifs.peek());
        ifs.peek().endRefs.add(ip+1);
        gen(BytecodeDefinition.INSTR_BR, -1); // write placeholder
        writeShort(instrs, ifs.peek().prevBranch, (short)ip);
        ifs.peek().prevBranch = -1;
    }

    public void endif() {
        if ( ifs.peek().prevBranch>=0 ) writeShort(instrs, ifs.peek().prevBranch, (short)ip);
        List<Integer> ends = ifs.peek().endRefs;
        //System.out.println("endrefs="+ends);
        for (int opnd : ends) {
            writeShort(instrs, opnd, (short)ip);
        }
        //System.out.println("endif end");
    }

    // GEN
    
    public void gen(short opcode) {
        ensureCapacity();
        instrs[ip++] = (byte)opcode;
    }

    public void gen(short opcode, int arg) {
        ensureCapacity();
        instrs[ip++] = (byte)opcode;
        writeShort(instrs, ip, (short)arg);
        ip += 2;
    }
    
    public void gen(short opcode, String s) {
        int i = defineString(s);
        gen(opcode, i);
    }
    
    protected void ensureCapacity() {
        if ( (ip+1) >= instrs.length ) {
            byte[] c = new byte[instrs.length*2];
            System.arraycopy(instrs, 0, c, 0, instrs.length-1);
            instrs = c;
        }
    }

    public List<Chunk> breakTemplateIntoChunks(String template,
                                               char start,
                                               char stop)
    {
        List<Chunk> chunks = new ArrayList<Chunk>();
        int i = 0;
        int n = template.length();
        int strStart = i;
        while ( i < n ) {
            char c = template.charAt(i);
            if ( c==start ) {       // match everything inside delimiters
                if ( i>strStart ) {
                    String text = template.substring(strStart,i);
                    chunks.add(new Chunk(text));
                }
                i++;                // skip over start delimiter
                c = template.charAt(i);
                int exprStart = i;
                int exprStop = i;
                while ( i < n ) {   // scan for stop delimiter
                    if ( c=='\\' ) { i+=2; continue; }
                    if ( c==stop ) { exprStop=i-1; break; }
                    i++;
                    c = template.charAt(i);
                }
                if ( i >= n ) {
                    System.err.println("missing terminating delimiter expression; i="+i);
                }
                String expr = template.substring(exprStart, exprStop+1);
                chunks.add(new ExprChunk(expr));
                strStart = i+1; // string starts again after stop delimiter
            }
            i++;
        }
        if ( strStart < n ) {
            String expr = template.substring(strStart, n);            
            chunks.add(new Chunk(expr));
        }
        return chunks;
    }

    /*
    void forwardRef(String label, int opndAddress) {
    }
    
    void defineLabel(String label, int addr) {
    }
    */
    
    /** Write value at index into a byte array highest to lowest byte,
     *  left to right.
     */
    public static void writeShort(byte[] memory, int index, short value) {
        memory[index+0] = (byte)((value>>(8*1))&0xFF);
        memory[index+1] = (byte)(value&0xFF);
    }
}
