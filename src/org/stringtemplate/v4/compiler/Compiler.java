/*
 [The "BSD license"]
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
package org.stringtemplate.v4.compiler;

import org.antlr.runtime.*;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A compiler for a single template */
public class Compiler {
	public static final String SUBTEMPLATE_PREFIX = "_sub";

    /** Given a template of length n, how much code will result?
     *  For now, let's assume n/5. Later, we can test in practice.
     */
    public static final double CODE_SIZE_FACTOR = 5.0;
    public static final int SUBTEMPLATE_INITIAL_CODE_SIZE = 15;

    /** The compiler needs to know how to delimit expressions.
     *  The STGroup normally passes in this information, but we
     *  can set some defaults.
     */
    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    public static final Map<String, Interpreter.Option> supportedOptions =
        new HashMap<String, Interpreter.Option>() {
            {
                put("anchor",       Interpreter.Option.ANCHOR);
                put("format",       Interpreter.Option.FORMAT);
                put("null",         Interpreter.Option.NULL);
                put("separator",    Interpreter.Option.SEPARATOR);
                put("wrap",         Interpreter.Option.WRAP);
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

    /** Name subtemplates /sub1, /sub2, ... */
    public static int subtemplateCount = 0; // public for testing access

    /** used to parse w/o compilation side-effects */
    public static final Compiler NOOP_GEN = new Compiler() {
        public void emit(short opcode) { }
        public void emit(short opcode, int p, int q) { }
        public void emit(short opcode, int arg) { }
        public void emit1(short opcode, int arg, int p, int q) { }
        public void emit1(short opcode, String s) { }
        public void emit1(short opcode, String s, int p, int q) { }
        public void emit(short opcode, int arg1, int arg2, int p, int q) { }
        public void insert(int addr, short opcode, String s) { }
        public void write(int addr, short value) { }
        public int address() { return 0; }
        public String compileAnonTemplate(String enclosingTemplateName,
                                          TokenStream input,
                                          List<Token> ids,
                                          RecognizerSharedState state)
        {
            return compileRegion(enclosingTemplateName, null, input, state);
        }
        public String compileRegion(String enclosingTemplateName,
                                    String regionName,
                                    TokenStream input,
                                    RecognizerSharedState state)
        {
			System.out.println("NOP GEN compile inline");
            Compiler c = new Compiler();
            c.compile(input, state);
            return null;
        }
        public void defineBlankRegion(String fullyQualifiedName) { }
    };

	/** The compiled code implementation to fill in. */
	CompiledST code = new CompiledST();

	/** If we're compiling a region or sub template, we need to know the
	 *  enclosing template's name.  Region r in template t
	 *  is formally called t.r.
	 */
	String enclosingTemplateName;

	public Compiler() { this("<unknown>", '<', '>'); }

    /** To compile a template, we need to know what
     *  the enclosing template is (if any).
     */
    public Compiler(String enclosingTemplateName,
                    char delimiterStartChar,
                    char delimiterStopChar)
    {
        this.enclosingTemplateName = enclosingTemplateName;
        this.delimiterStartChar = delimiterStartChar;
        this.delimiterStopChar = delimiterStopChar;
    }

    /** Compile full template */
	public CompiledST compile(String template) {
		return compile(null, template);
	}

	/** Compile full template with respect to a list of formal args. */
    public CompiledST compile(List<FormalArgument> args, String template) {
		//System.out.println("compile with args "+template);
		code.defineFormalArgs(args); // make sure args are defined prior to compilation

		int initialSize = Math.max(5, (int)(template.length() / CODE_SIZE_FACTOR));
        code.instrs = new byte[initialSize];
        code.sourceMap = new Interval[initialSize];
        code.template = template;

		ANTLRStringStream is = new ANTLRStringStream(template);
		STLexer lexer = new STLexer(is, delimiterStartChar, delimiterStopChar);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        STParser parser = new STParser(tokens, this, enclosingTemplateName);
        try {
            parser.templateAndEOF(); // parse, trigger compile actions
        }
        catch (RecognitionException re) { throwSTException(tokens, parser, re); }
        if ( code.stringtable!=null ) code.strings = code.stringtable.toArray();
        code.codeSize = code.ip;
        return code;
    }

    /** Compile subtemplate or region */
    protected CompiledST compile(TokenStream tokens,
                                 RecognizerSharedState state)
    {
//		tokens.LT(3);
//		System.out.println("compile inline in "+tokens.toString(tokens.index(), 10000));
        code.instrs = new byte[SUBTEMPLATE_INITIAL_CODE_SIZE];
        code.sourceMap = new Interval[SUBTEMPLATE_INITIAL_CODE_SIZE];
        STParser parser = new STParser(tokens, state, this, enclosingTemplateName);
        try {
            parser.template(); // parse, trigger compile actions
        }
        catch (RecognitionException re) { throwSTException(tokens, parser, re); }
        if ( code.stringtable!=null ) code.strings = code.stringtable.toArray();
        code.codeSize = code.ip;
        return code;
    }

    public String compileAnonTemplate(String enclosingTemplateName,
                                      TokenStream input,
                                      List<Token> argIDs,
                                      RecognizerSharedState state) {
//		input.LT(3);
//		System.out.println("compile anon in "+input.toString(input.index(), 10000));
        subtemplateCount++;
        String name = SUBTEMPLATE_PREFIX+subtemplateCount;
        TokenSource tokenSource = input.getTokenSource();
        STLexer lexer = null;
        int start=-1;
        if ( tokenSource instanceof STLexer ) {
            lexer = (STLexer) tokenSource;
            start = lexer.input.index();
        }
        Compiler c = new Compiler(enclosingTemplateName,
                                  delimiterStartChar, delimiterStopChar);
		// define {...} args and i, i0
		List<FormalArgument> args = null;
		if ( argIDs!=null ) {
			args = new ArrayList<FormalArgument>();
			for (Token arg : argIDs) {
				String argName = arg.getText();
				args.add(new FormalArgument(argName));
			}
		}
		c.code.defineFormalArgs(args);
		c.code.addArg(new FormalArgument("i"));
		c.code.addArg(new FormalArgument("i0"));

        CompiledST sub = c.compile(input, state);
        sub.name = name;
        sub.isAnonSubtemplate = true;
        if ( tokenSource instanceof STLexer ) {
            int stop = lexer.input.index();
            //System.out.println(start+".."+stop);
            sub.embeddedStart = start;
            sub.embeddedStop = stop-1;
            sub.template = lexer.input.substring(0, lexer.input.size()-1);
        }

		code.addImplicitlyDefinedTemplate(sub);
        return name;
    }

    public String compileRegion(String enclosingTemplateName,
                                String regionName,
                                TokenStream input,
                                RecognizerSharedState state)
    {
        Compiler c = new Compiler(enclosingTemplateName,
                                  delimiterStartChar, delimiterStopChar);
        CompiledST sub = c.compile(input, state);
        String fullName =
            STGroup.getMangledRegionName(enclosingTemplateName, regionName);
        sub.isRegion = true;
        sub.regionDefType = ST.RegionType.EMBEDDED;
        sub.name = fullName;
        code.addImplicitlyDefinedTemplate(sub);
        return fullName;
    }

    public void defineBlankRegion(String enclosingTemplateName, String name) {
        String mangled = STGroup.getMangledRegionName(enclosingTemplateName, name);
        CompiledST blank = new CompiledST();
        blank.isRegion = true;
        blank.regionDefType = ST.RegionType.IMPLICIT;
		blank.name = mangled;
        code.addImplicitlyDefinedTemplate(blank);
    }

    protected void throwSTException(TokenStream tokens, STParser parser, RecognitionException re) {
        String msg = parser.getErrorMessage(re, parser.getTokenNames());
        if ( re.token.getType() == STLexer.EOF_TYPE ) {
            throw new STException("premature EOF", re);
        }
        else if ( re instanceof NoViableAltException) {
            throw new STException(
                "'"+re.token.getText()+"' came as a complete surprise to me",re);
        }
        else if ( tokens.index() == 0 ) { // couldn't parse anything
            throw new STException(
                "this doesn't look like a template: \""+tokens+"\"", re);
        }
        else if ( tokens.LA(1) == STLexer.LDELIM ) { // couldn't parse expr
            throw new STException("doesn't look like an expression", re);
        }
        else {
            throw new STException(msg, re);
        }
    }

    public int defineString(String s) { return code.stringtable.add(s); }

    public void refAttr(CommonToken id) {
        String name = id.getText();
		if ( code.formalArguments!=null && code.formalArguments.get(name)!=null ) {
			FormalArgument arg = code.formalArguments.get(name);
			int index = arg.index;
            emit1(Bytecode.INSTR_LOAD_LOCAL, index, id.getStartIndex(), id.getStopIndex());
		}
        else {
			if ( Interpreter.predefinedAnonSubtemplateAttributes.contains(name) ) {
				ErrorManager.compileTimeError(ErrorType.NO_SUCH_ATTRIBUTE, id);
				emit(Bytecode.INSTR_NULL, id.getStartIndex(), id.getStopIndex());
			}
            else {
				emit1(Bytecode.INSTR_LOAD_ATTR, name, id.getStartIndex(), id.getStopIndex());
			}
        }
    }

    public void setOption(CommonToken id) {
        Interpreter.Option O = Compiler.supportedOptions.get(id.getText());
        if ( O==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_SUCH_OPTION, id);
            emit(Bytecode.INSTR_POP, id.getStartIndex(), id.getStopIndex());
            return;
        }
        emit1(Bytecode.INSTR_STORE_OPTION, O.ordinal(),
			  id.getStartIndex(), id.getStopIndex());
    }

    public void defaultOption(CommonToken id) {
        String v = Compiler.defaultOptionValues.get(id.getText());
        if ( v==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_DEFAULT_VALUE, id);
            emit(Bytecode.INSTR_POP, id.getStartIndex(), id.getStopIndex());
        }
        emit1(Bytecode.INSTR_LOAD_STR, v, id.getStartIndex(), id.getStopIndex());
    }

    public void func(CommonToken id) {
        Short funcBytecode = Compiler.funcs.get(id.getText());
        if ( funcBytecode==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_SUCH_FUNCTION, id);
            emit(Bytecode.INSTR_POP, id.getStartIndex(), id.getStopIndex());
        }
        else {
            emit(funcBytecode, id.getStartIndex(), id.getStopIndex());
        }
    }

    public void emit(short opcode) { emit(opcode,-1,-1); }

    public void emit(short opcode, int p, int q) {
        ensureCapacity(1);
        if ( !(p<0 || q<0) ) code.sourceMap[code.ip] = new Interval(p, q);
        code.instrs[code.ip++] = (byte)opcode;
    }

	public void emit1(short opcode, int arg) { emit1(opcode, arg, -1, -1); }

	public void emit1(short opcode, int arg, int p, int q) {
		emit(opcode, p, q);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES);
		writeShort(code.instrs, code.ip, (short)arg);
		code.ip += Bytecode.OPND_SIZE_IN_BYTES;
	}

	public void emit2(short opcode, int arg, int arg2, int p, int q) {
		emit(opcode, p, q);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES * 2);
		writeShort(code.instrs, code.ip, (short)arg);
		code.ip += Bytecode.OPND_SIZE_IN_BYTES;
		writeShort(code.instrs, code.ip, (short)arg2);
		code.ip += Bytecode.OPND_SIZE_IN_BYTES;
	}

	public void emit2(short opcode, String s, int arg2, int p, int q) {
		int i = defineString(s);
		emit2(opcode, i, arg2, p, q);
	}

    public void emit1(short opcode, String s) { emit1(opcode, s, -1, -1);}

	public void emit1(short opcode, String s, int p, int q) {
		int i = defineString(s);
		emit1(opcode, i, p, q);
	}

    public void insert(int addr, short opcode, String s) {
		//System.out.println("before insert of "+opcode+"("+s+"):"+ Arrays.toString(code.instrs));
        ensureCapacity(1+Bytecode.OPND_SIZE_IN_BYTES);
		int instrSize = 1 + Bytecode.OPND_SIZE_IN_BYTES;
		System.arraycopy(code.instrs, addr,
						 code.instrs, addr + instrSize,
						 code.ip-addr); // make room for opcode, opnd
        int save = code.ip;
        code.ip = addr;
        emit1(opcode, s);
        code.ip = save+instrSize;
		//System.out.println("after  insert of "+opcode+"("+s+"):"+ Arrays.toString(code.instrs));
		// adjust addresses for BR and BRF
		int a=addr+instrSize;
		while ( a < code.ip ) {
			byte op = code.instrs[a];
			Bytecode.Instruction I = Bytecode.instructions[op];
			if ( op == Bytecode.INSTR_BR || op == Bytecode.INSTR_BRF ) {
				int opnd = BytecodeDisassembler.getShort(code.instrs, a+1);
				writeShort(code.instrs, a+1, (short)(opnd+instrSize));
			}
			a += I.nopnds * Bytecode.OPND_SIZE_IN_BYTES + 1;
		}
		//System.out.println("after  insert of "+opcode+"("+s+"):"+ Arrays.toString(code.instrs));
    }

    public void write(int addr, short value) {
        writeShort(code.instrs, addr, value);
    }

    public int address() { return code.ip; }

    protected void ensureCapacity(int n) {
        if ( (code.ip+n) >= code.instrs.length ) { // ensure room for full instruction
            byte[] c = new byte[code.instrs.length*2];
            System.arraycopy(code.instrs, 0, c, 0, code.instrs.length);
            code.instrs = c;
            Interval[] sm = new Interval[code.sourceMap.length*2];
            System.arraycopy(code.sourceMap, 0, sm, 0, code.sourceMap.length);
            code.sourceMap = sm;
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
