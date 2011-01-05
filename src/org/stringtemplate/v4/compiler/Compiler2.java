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

import org.stringtemplate.v4.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A compiler for a single template. */
public class Compiler2 {
	public static final String SUBTEMPLATE_PREFIX = "_sub";

    public static final int TEMPLATE_INITIAL_CODE_SIZE = 15;

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

	/** Name subtemplates _sub1, _sub2, ... */
	public static int subtemplateCount = 0;

    /** The compiled code implementation to fill in. */
    CompiledST impl = new CompiledST();

	/** Track unique strings; copy into CompiledST's String[] after compilation */
	StringTable stringtable = new StringTable();

	/** Track instruction location within code.instrs array; this is
	 *  next address to write to.  Byte-addressable memory.
	 */
	int ip = 0;

	/** If we're compiling a region or subtemplate, we need to know the
	 *  enclosing template's name.  Region r in template t
	 *  is formally called t.r.
	 */
	String enclosingTemplateName;


	public Compiler2() { this("<unknown>", null, '<', '>'); }

	/** To compile a template, we need to know what the
	 *  enclosing template is (if any) in case of regions.
	 */
	public Compiler2(String name,
					 String enclosingTemplateName,
					 char delimiterStartChar,
					 char delimiterStopChar)
	{
		impl.name = name;
		impl.enclosingTemplateName = enclosingTemplateName;
		this.delimiterStartChar = delimiterStartChar;
		this.delimiterStopChar = delimiterStopChar;
	}

	public CompiledST compile(String template) {
		return null;
	}

	/** Compile full template with respect to a list of formal args. */
	public CompiledST compile(List<FormalArgument> args, String template) {
		return null;
	}
	
}
