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

public class Compiler implements ExprParserListener {
    public static final String ATTR_NAME_REGEX = "[a-zA-Z/][a-zA-Z0-9_/]*";
    /** Given a template of length n, how much code will result?
     *  For now, let's assume n/5. Later, we can test in practice.
     */
    public static final double CODE_SIZE_FACTOR = 5.0;

    public static final int OPTION_ANCHOR       = 0;
    public static final int OPTION_FORMAT       = 1;
    public static final int OPTION_NULL         = 2;
    public static final int OPTION_SEPARATOR    = 3;
    public static final int OPTION_WRAP         = 4;

    public static final Map<String, Integer> supportedOptions =
        new HashMap<String, Integer>() {
        {
            put("anchor",       OPTION_ANCHOR);
            put("format",       OPTION_FORMAT);
            put("null",         OPTION_NULL);
            put("separator",    OPTION_SEPARATOR);
            put("wrap",         OPTION_WRAP);
        }
    };

    public static final int NUM_OPTIONS = supportedOptions.size();

    public static final Map<String,String> defaultOptionValues =
        new HashMap<String,String>() {
        {
            put("anchor", "true");
            put("wrap",   "\n");
        }
    };

    StringTable strings;
    byte[] instrs;
    int ip = 0;
    Stack<Chunk> ifs = new Stack<Chunk>();

    /** Track list of anonymous subtemplates. We need to name them
     *  here not in their eventual group because we need to generate
     *  code that references their names.
     */
    Map<String, String> subtemplates = new HashMap<String, String>();

    public static int subtemplateCount = 0;

    public CompiledST compile(String template) throws Exception {
        strings = new StringTable();
        int initialSize = Math.max(5, (int)(template.length() / CODE_SIZE_FACTOR));
        instrs = new byte[initialSize];
        //System.out.println("compile "+template);
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        for (Chunk c : chunks) {
            //System.out.println("compile chunk "+c.text);
            if ( c.isExpr() ) {
                STLexer lexer = new STLexer(new ANTLRStringStream(c.text));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                STParser parser = new STParser(tokens, this);
                int firstTokenType = tokens.LA(1);

                if ( firstTokenType==STLexer.IF ) ifs.push(c);

                parser.stexpr(); // parse, trigger compile actions for single expr 

                if ( firstTokenType==STLexer.ENDIF ) ifs.pop();
                
                if ( !(firstTokenType==STLexer.IF ||
                       firstTokenType==STLexer.ELSE ||
                       firstTokenType==STLexer.ELSEIF ||
                       firstTokenType==STLexer.ENDIF) )
                {
                    if ( parser.exprHasOptions ) gen(BytecodeDefinition.INSTR_WRITE_OPT);
                    else gen(BytecodeDefinition.INSTR_WRITE);
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
            code.strings = strings.toArray();
        }
        code.instrs = instrs;
        code.codeSize = ip;
        if ( subtemplates.size()>0 ) System.out.println("subtemplates="+subtemplates);
        //code.dump();
        return code;
    }

    public int defineString(String s) {
        return strings.add(s);
    }

    public static LinkedHashMap<String,FormalArgument> parseSubtemplateArg(String block) {
        LinkedHashMap<String,FormalArgument> args = null;
        int pipe = block.indexOf('|');
        String[] elems = block.substring(0,pipe+1).split(" ");
        if ( elems.length==2 &&
             elems[0].matches(Compiler.ATTR_NAME_REGEX) )
        {
            args = new LinkedHashMap<String,FormalArgument>();
            args.put(elems[0],null);
        }
        return args;
    }

    // LISTEN TO PARSER

    public void map() {
        gen(BytecodeDefinition.INSTR_MAP);
    }

    public void mapAlternating(int numTemplates) {
        gen(BytecodeDefinition.INSTR_ROT_MAP, numTemplates);
    }

    public String defineAnonTemplate(Token subtemplate) {
        subtemplateCount++;
        String name = "_sub"+subtemplateCount;
        subtemplates.put(name, subtemplate.getText());
        return name;
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

    public void options() {
        gen(BytecodeDefinition.INSTR_OPTIONS);        
    }

    public void setOption(Token id) {
        Integer I = supportedOptions.get(id.getText());
        if ( I==null ) {
            System.err.println("no such option: "+id.getText());
            return;
        }
        gen(BytecodeDefinition.INSTR_STORE_OPTION, I);
    }

    public void defaultOption(Token id) {
        String v = defaultOptionValues.get(id.getText());
        if ( v==null ) {
            System.err.println("no def value for "+id.getText());
            return;
        }
        gen(BytecodeDefinition.INSTR_LOAD_STR, v);
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
        if ( (ip+3) >= instrs.length ) { // ensure room for full instruction
            byte[] c = new byte[instrs.length*2];
            System.arraycopy(instrs, 0, c, 0, instrs.length);
            instrs = c;
        }
    }

    /** Write value at index into a byte array highest to lowest byte,
     *  left to right.
     */
    public static void writeShort(byte[] memory, int index, short value) {
        memory[index+0] = (byte)((value>>(8*1))&0xFF);
        memory[index+1] = (byte)(value&0xFF);
    }
}
