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

import org.antlr.runtime.*;

public class Compiler implements CodeGenerator {
    public static final String ATTR_NAME_REGEX = "[a-zA-Z/][a-zA-Z0-9_/]*";
    /** Given a template of length n, how much code will result?
     *  For now, let's assume n/5. Later, we can test in practice.
     */
    public static final double CODE_SIZE_FACTOR = 5.0;
	public static final int SUBTEMPLATE_INITIAL_CODE_SIZE = 15;

    public static final Map<String, Integer> supportedOptions =
        new HashMap<String, Integer>() {
        {
            put("anchor",       Interpreter.OPTION_ANCHOR);
            put("format",       Interpreter.OPTION_FORMAT);
            put("null",         Interpreter.OPTION_NULL);
            put("separator",    Interpreter.OPTION_SEPARATOR);
            put("wrap",         Interpreter.OPTION_WRAP);
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

    public static Map<String, Short> funcs = new HashMap<String, Short>() {
        {
            put("first", Bytecode.INSTR_FIRST);
            put("last", Bytecode.INSTR_LAST);
            put("rest", Bytecode.INSTR_REST);
            put("trunc", Bytecode.INSTR_TRUNC);
            put("strip", Bytecode.INSTR_STRIP);
            put("trim", Bytecode.INSTR_TRIM);
            put("length", Bytecode.INSTR_LENGTH);
            put("strlen", Bytecode.INSTR_STRLEN);
            put("reverse", Bytecode.INSTR_REVERSE);
        }
    };

    StringTable strings = new StringTable();
    byte[] instrs;
    int ip = 0;
    CompiledST code = new CompiledST();

    public static int subtemplateCount = 0; // public for testing access

	public Compiler() {;}

    public CompiledST compile(String template) {
        return compile(template, '<', '>');
    }

	public CompiledST compile(String template,
							  char delimiterStartChar,
							  char delimiterStopChar)
	{
		int initialSize = Math.max(5, (int)(template.length() / CODE_SIZE_FACTOR));
		instrs = new byte[initialSize];
		code.template = template;

		STLexer lexer =
			new STLexer(new ANTLRStringStream(template), delimiterStartChar, delimiterStopChar);
		//CommonTokenStream tokens = new CommonTokenStream(lexer);
		UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
		STParser parser = new STParser(tokens, this);
		try {
			parser.templateAndEOF(); // parse, trigger compile actions for single expr
		}
		catch (RecognitionException re) {
			String msg = parser.getErrorMessage(re, parser.getTokenNames());
			throw new STRecognitionException(msg, re);
		}

		if ( strings!=null ) code.strings = strings.toArray();
		code.instrs = instrs;
		code.codeSize = ip;
		return code;
	}

	public CompiledST compile(TokenStream tokens, RecognizerSharedState state) {
		instrs = new byte[SUBTEMPLATE_INITIAL_CODE_SIZE];
		STParser parser = new STParser(tokens, state, this);
		try {
			parser.template(); // parse, trigger compile actions for single expr
		}
		catch (RecognitionException re) {
			String msg = parser.getErrorMessage(re, parser.getTokenNames());
			throw new STRecognitionException(msg, re);
		}

		if ( strings!=null ) code.strings = strings.toArray();
		code.instrs = instrs;
		code.codeSize = ip;
		return code;
	}

	/*
    public CompiledST compile(int startCharIndex,
                              String template,
                              char delimiterStartChar,
                              char delimiterStopChar)
    {
        System.out.println("compile("+template+"), enclosingChunk="+enclosingChunk);
        code = new CompiledST();
        code.enclosingChunk = enclosingChunk;
        code.start = startCharIndex;
        code.stop = code.start + template.length()-1;
        code.template = template;
        strings = new StringTable();
        int initialSize = Math.max(5, (int)(template.length() / CODE_SIZE_FACTOR));
        instrs = new byte[initialSize];
        //System.out.println("compile "+template);
        List<Chunk> chunks = new Chunkifier(template, delimiterStartChar, delimiterStopChar).chunkify();
        for (Chunk chunk : chunks) {
            currentChunk = chunk;
            System.out.println("compile chunk "+chunk.text);
            //chunk.enclosingChunk = enclosingChunk;
            chunk.code = code;
            compile(chunk, delimiterStartChar, delimiterStopChar);
        }
        if ( strings!=null ) code.strings = strings.toArray();
        code.instrs = instrs;
        code.codeSize = ip;

        // We may have found embedded subtemplates; compile those too
        // and store in code
        //compileSubtemplates();
        
        //code.dump();
        return code;
    }
    protected void compile(Chunk chunk, char delimiterStartChar, char delimiterStopChar) {
        if ( chunk.isExpr() ) {
            STLexer lexer = new STLexer(new ANTLRStringStream(chunk.text));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            STParser parser = new STParser(tokens, this);
            int firstTokenType = tokens.LA(1);

            if ( firstTokenType==STLexer.IF ) ifs.push(chunk);

            try {
                parser.st(); // parse, trigger compile actions for single expr
            }
            catch (RecognitionException re) {
                String msg = parser.getErrorMessage(re, parser.getTokenNames());
                throw new STRecognitionException(chunk, msg, re);
            }

            if ( firstTokenType==STLexer.ENDIF ) ifs.pop();

            if ( !(firstTokenType==STLexer.IF ||
                   firstTokenType==STLexer.ELSE ||
                   firstTokenType==STLexer.ELSEIF ||
                   firstTokenType==STLexer.ENDIF) )
            {
                if ( parser.exprHasOptions ) emit(Bytecode.INSTR_WRITE_OPT);
                else emit(Bytecode.INSTR_WRITE);
            }
        }
        else {
            emit(Bytecode.INSTR_LOAD_STR, chunk.text);
            emit(Bytecode.INSTR_WRITE);
        }
    }
*/

    public int defineString(String s) {
        return strings.add(s);
    }

	/*
    public static LinkedHashMap<String,FormalArgument> parseSubtemplateArg(String block) {
        LinkedHashMap<String,FormalArgument> args = null;
        int pipe = block.indexOf('|');
        if ( pipe<0 ) return null;
        String argStr = block.substring(0,pipe+1).trim();
        String[] elems = argStr.split("[, |]");
        if ( elems.length==1 && // only allow 1 arg for now
             elems[0].matches(ATTR_NAME_REGEX) )
        {
            args = new LinkedHashMap<String,FormalArgument>();
            args.put(elems[0],null);
        }
        return args;
    }
*/
    /*
    protected void compileSubtemplates() {
        if ( subtemplates.size()>0 ) System.out.println("subtemplates="+subtemplates);
        for (String subname : subtemplates.keySet()) {
            TrackSubtemplate s = subtemplates.get(subname);
            LinkedHashMap<String,FormalArgument> args =
                Compiler.parseSubtemplateArg(s.template);
            String t = s.template;
            if ( args!=null ) {
                int pipe = s.template.indexOf('|');
                t = s.template.substring(pipe+1);
            }
            t = t.trim();
            Compiler c = new Compiler(s.enclosingChunk);
            CompiledST sub = c.compile(s.start, t);
            sub.name = subname;
            sub.formalArguments = args;
            if ( code.compiledSubtemplates==null ) {
                code.compiledSubtemplates = new ArrayList<CompiledST>();
            }
            code.compiledSubtemplates.add(sub);
        }
    }
*/
    
    // LISTEN TO PARSER

/*    public String defineAnonTemplate(Token subtemplate) {
        CommonToken tok= (CommonToken)subtemplate;
        subtemplateCount++;
        String name = "_sub"+subtemplateCount;
		return name;
		*/
        /*
        TrackSubtemplate s =
            new TrackSubtemplate(name, tok.getText(), (ExprChunk)currentChunk);
        s.start = tok.getStartIndex() + 1; // don't count '{' on left
        subtemplates.put(name,s);
        return name;
        */

		/*
        String text = subtemplate.getText();

        System.out.println("define sub template "+text+" in "+code.template);

        LinkedHashMap<String,FormalArgument> args =
            Compiler.parseSubtemplateArg(text);
        String t = text;
        if ( args!=null ) {
            int pipe = text.indexOf('|');
            t = text.substring(pipe+1);
        }
        t = t.trim();
        Compiler c = new Compiler((ExprChunk)currentChunk);
        int templateStart = tok.getStartIndex() + 1; // don't count '{' on left
        CompiledST sub = c.compile(t);
        sub.name = name;
        sub.formalArguments = args;
        if ( code.compiledSubtemplates==null ) {
            code.compiledSubtemplates = new ArrayList<CompiledST>();
        }
        code.compiledSubtemplates.add(sub);

        return name;
        */
//    }

    // CodeGenerator interface impl.

    public void emit(short opcode) {
        ensureCapacity();
        instrs[ip++] = (byte)opcode;
    }

    public void emit(short opcode, int arg) {
        ensureCapacity();
        instrs[ip++] = (byte)opcode;
        writeShort(instrs, ip, (short)arg);
        ip += 2;
    }

    public void emit(short opcode, String s) {
        int i = defineString(s);
        emit(opcode, i);
    }

	public void write(int addr, short value) {
		writeShort(instrs, addr, value);
	}

	public int address() { return ip; }

	public String compileAnonTemplate(TokenStream input,
							   List<Token> ids,
							   RecognizerSharedState state) {
		subtemplateCount++;
		String name = "_sub"+subtemplateCount;
		Compiler c = new Compiler();
		CompiledST sub = c.compile(input, state);
		sub.name = name;
		if ( ids!=null ) {
			sub.formalArguments = new LinkedHashMap<String,FormalArgument>();
			for (Token arg : ids) {
				String argName = arg.getText();
				sub.formalArguments.put(argName, new FormalArgument(argName));
			}
		}
		if ( code.compiledSubtemplates==null ) {
			code.compiledSubtemplates = new ArrayList<CompiledST>();
		}
		code.compiledSubtemplates.add(sub);
		return name;
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
