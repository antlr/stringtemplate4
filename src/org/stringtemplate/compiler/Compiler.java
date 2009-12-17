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
package org.stringtemplate.compiler;

import org.antlr.runtime.*;
import org.stringtemplate.*;
import org.stringtemplate.misc.ErrorManager;
import org.stringtemplate.misc.ErrorType;
import org.stringtemplate.misc.Interval;

import java.util.*;

/** A compiler for a single template */
public class Compiler {
    /** Given a template of length n, how much code will result?
     *  For now, let's assume n/5. Later, we can test in practice.
     */
    public static final double CODE_SIZE_FACTOR = 5.0;
    public static final int SUBTEMPLATE_INITIAL_CODE_SIZE = 15;
    public static final int INITIAL_STRING_TABLE_SIZE = 10;

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

    /** The compiled code implementation to fill in. */
    CompiledST code = new CompiledST();

    /** Track unique strings; copy into CompiledST's String[] after compilation */
    StringTable strings = new StringTable();

    /** Track instruction location within code.instrs array; this is
     *  next address to write to.  Byte-addressable memory.
     */
    int ip = 0;

    /** subdir context.  If we're compiling templates in subdir a/b/c, then
     *  /a/b/c is the path prefix to add to all ID refs; it fully qualifies them.
     *  It's like resolving x to this.x in Java for field x. 
     */
    String templatePathPrefix;

    /** If we're compiling a region or sub template, we need to know the
     *  enclosing template's name.  Region r in template t
     *  is formally called t.r.
     */
    String enclosingTemplateName;

    /** Name subtemplates /sub1, /sub2, ... */
    public static int subtemplateCount = 0; // public for testing access
    
    /** used to parse w/o compilation side-effects */
    public static final Compiler NOOP_GEN = new Compiler() {
    	public void emit(short opcode) {;}
    	public void emit(short opcode, int p, int q) {;}
    	public void emit(short opcode, int arg) {;}
    	public void emit(short opcode, int arg, int p, int q) {;}
    	public void emit(short opcode, String s) {;}
    	public void emit(short opcode, String s, int p, int q) {;}
        public void emit(short opcode, int arg1, int arg2, int p, int q) {;}
        public void insert(int addr, short opcode, String s) {;}
        public void write(int addr, short value) {;}
    	public int address() { return 0; }
        public String templateReferencePrefix() { return null; }
    	public String compileAnonTemplate(String enclosingTemplateName,
                                          TokenStream input,
                                          List<Token> ids,
                                          RecognizerSharedState state)
        {
    		Compiler c = new Compiler();
    		c.compile(input, state);
    		return null;
    	}
        public String compileRegion(String enclosingTemplateName,
                                    String regionName,
                                    TokenStream input,
                                    RecognizerSharedState state)
        {
        	Compiler c = new Compiler();
    		c.compile(input, state);
    		return null;
        }
        public void defineBlankRegion(String fullyQualifiedName) {;}
    };

    public Compiler() { this("/", "<unknown>"); }

    /** To compile a template, we need to know what directory level it's at
     *  (if any; most web apps do this but code gen apps don't) and what
     *  the enclosing template is (if any).
     */
    public Compiler(String templatePathPrefix, String enclosingTemplateName) {
        this.templatePathPrefix = templatePathPrefix;
        this.enclosingTemplateName = enclosingTemplateName;
    }

    public CompiledST compile(String template) {
        return compile(template, '<', '>');
    }

    /** Compile full template */
    public CompiledST compile(String template,
                              char delimiterStartChar,
                              char delimiterStopChar)
    {
        int initialSize = Math.max(5, (int)(template.length() / CODE_SIZE_FACTOR));
        code.instrs = new byte[initialSize];
        code.sourceMap = new Interval[initialSize];
        code.template = template;

        STLexer lexer =
            new STLexer(new ANTLRStringStream(template), delimiterStartChar, delimiterStopChar);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        STParser parser = new STParser(tokens, this, enclosingTemplateName);
        try {
            parser.templateAndEOF(); // parse, trigger compile actions
        }
        catch (RecognitionException re) { throwSTException(tokens, parser, re); }
        if ( strings!=null ) code.strings = strings.toArray();
        code.codeSize = ip;
        return code;
    }

    /** Compile subtemplate or region */
    protected CompiledST compile(TokenStream tokens,
                                 RecognizerSharedState state)
    {
        code.instrs = new byte[SUBTEMPLATE_INITIAL_CODE_SIZE];
        code.sourceMap = new Interval[SUBTEMPLATE_INITIAL_CODE_SIZE];
        STParser parser = new STParser(tokens, state, this, enclosingTemplateName);
        try {
            parser.template(); // parse, trigger compile actions
        }
        catch (RecognitionException re) { throwSTException(tokens, parser, re); }
        if ( strings!=null ) code.strings = strings.toArray();
        code.codeSize = ip;
        return code;
    }

    public String compileAnonTemplate(String enclosingTemplateName,
                                      TokenStream input,
                                      List<Token> argIDs,
                                      RecognizerSharedState state) {
        subtemplateCount++;
        String name = templatePathPrefix+ST.SUBTEMPLATE_PREFIX+subtemplateCount;
        TokenSource tokenSource = input.getTokenSource();
        STLexer lexer = null;
        int start=-1, stop=-1;
        if ( tokenSource instanceof STLexer ) {
            lexer = (STLexer) tokenSource;
            start = lexer.input.index();
        }
        Compiler c = new Compiler(templatePathPrefix, enclosingTemplateName);
        CompiledST sub = c.compile(input, state);
        sub.name = name;
        sub.isSubtemplate = true;
        if ( tokenSource instanceof STLexer ) {
            stop = lexer.input.index();
            //System.out.println(start+".."+stop);
            sub.embeddedStart = start;
            sub.embeddedStop = stop-1;
            sub.template = lexer.input.substring(0, lexer.input.size()-1);
        }
        code.addImplicitlyDefinedTemplate(sub);
        if ( argIDs!=null ) {
            sub.formalArguments = new LinkedHashMap<String,FormalArgument>();
            for (Token arg : argIDs) {
                String argName = arg.getText();
                sub.formalArguments.put(argName, new FormalArgument(argName));
            }
        }
        return name;
    }

    public String compileRegion(String enclosingTemplateName,
                                String regionName,
                                TokenStream input,
                                RecognizerSharedState state)
    {
        Compiler c = new Compiler(templatePathPrefix, enclosingTemplateName);
        CompiledST sub = c.compile(input, state);
        String fullName =
            templatePathPrefix+
            STGroup.getMangledRegionName(enclosingTemplateName, regionName);
        sub.isRegion = true;
        sub.regionDefType = ST.RegionType.EMBEDDED;
        sub.name = fullName;
        code.addImplicitlyDefinedTemplate(sub);
        return fullName;
    }

    public void defineBlankRegion(String enclosingTemplateName, String name) {
        String mangled = STGroup.getMangledRegionName(enclosingTemplateName, name);
        String fullName = prefixedName(mangled);
        CompiledST blank = new CompiledST();
        blank.isRegion = true;
        blank.regionDefType = ST.RegionType.IMPLICIT;
        blank.name = fullName;
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

    public int defineString(String s) { return strings.add(s); }
    
    public String prefixedName(String t) {
    	if ( t!=null && t.charAt(0)=='/' ) return templateReferencePrefix()+t.substring(1);
    	return templateReferencePrefix()+t;
    }

    public void refAttr(CommonToken id) {
        String name = id.getText();
        if ( Interpreter.predefinedAttributes.contains(name) ) {
            emit(Bytecode.INSTR_LOAD_LOCAL, name,
                     id.getStartIndex(), id.getStopIndex());
        }
        else {
            emit(Bytecode.INSTR_LOAD_ATTR, name,
                     id.getStartIndex(), id.getStopIndex());
        }
    }

    public void setOption(CommonToken id) {
        Interpreter.Option O = Compiler.supportedOptions.get(id.getText());
        if ( O==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_SUCH_OPTION, id);
	        emit(Bytecode.INSTR_POP,
	                 id.getStartIndex(), id.getStopIndex());
            return;
        }
        emit(Bytecode.INSTR_STORE_OPTION, O.ordinal(),
                 id.getStartIndex(), id.getStopIndex());
    }

    public void defaultOption(CommonToken id) {
        String v = Compiler.defaultOptionValues.get(id.getText());
        if ( v==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_DEFAULT_VALUE, id);
	        emit(Bytecode.INSTR_POP,
	                 id.getStartIndex(), id.getStopIndex());
        }
        emit(Bytecode.INSTR_LOAD_STR, v,
                 id.getStartIndex(), id.getStopIndex());
    }

    public void func(CommonToken id) {
        Short funcBytecode = Compiler.funcs.get(id.getText());
        if ( funcBytecode==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_SUCH_FUNCTION, id);
            emit(Bytecode.INSTR_POP,
                     id.getStartIndex(), id.getStopIndex());
        }
        else {
            emit(funcBytecode,
                     id.getStartIndex(), id.getStopIndex());
        }
    }

    public void emit(short opcode) { emit(opcode,-1,-1); }

    public void emit(short opcode, int p, int q) {
        ensureCapacity(1);
        if ( !(p<0 || q<0) ) code.sourceMap[ip] = new Interval(p, q);
        code.instrs[ip++] = (byte)opcode;
    }

    public void emit(short opcode, int arg) { emit(opcode,arg,-1,-1); }

    public void emit(short opcode, int arg, int p, int q) {
        emit(opcode, p, q);
        ensureCapacity(2);
        writeShort(code.instrs, ip, (short)arg);
        ip += 2;
    }

    public void emit(short opcode, int arg1, int arg2, int p, int q) {
        emit(opcode, arg1, p, q);
        ensureCapacity(2);
        writeShort(code.instrs, ip, (short)arg2);
        ip += 2;
    }

    public void emit(short opcode, String s) { emit(opcode,s,-1,-1);}

    public void insert(int addr, short opcode, String s) {
        ensureCapacity(3);
        System.arraycopy(code.instrs, addr, code.instrs, addr+3, 3); // make room for 3 bytes
        int save = ip;
        ip = addr;
        emit(opcode, s);
        ip = save;
    }

    public void emit(short opcode, String s, int p, int q) {
        int i = defineString(s);
        emit(opcode, i, p, q);
    }

    public void write(int addr, short value) {
        writeShort(code.instrs, addr, value);
    }

    public int address() { return ip; }

    public String templateReferencePrefix() { return templatePathPrefix; }

    protected void ensureCapacity(int n) {
        if ( (ip+n) >= code.instrs.length ) { // ensure room for full instruction
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
