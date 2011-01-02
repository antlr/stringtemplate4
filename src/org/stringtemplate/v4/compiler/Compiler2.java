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

import org.antlr.runtime.CommonToken;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Interval;

import java.util.HashMap;
import java.util.Map;

/** A compiler for a single template. */
public class Compiler2 {
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

	/** Name subtemplates _sub1, _sub2, ... */
	public static int subtemplateCount = 0; // public for testing access

    /** The compiled code implementation to fill in. */
    CompiledST impl = new CompiledST();

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

	public int defineString(String s) { return impl.stringtable.add(s); }

	public void refAttr(CommonToken id) {
		String name = id.getText();
		if ( impl.formalArguments!=null && impl.formalArguments.get(name)!=null ) {
			FormalArgument arg = impl.formalArguments.get(name);
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
		if ( !(p<0 || q<0) ) impl.sourceMap[impl.ip] = new Interval(p, q);
		impl.instrs[impl.ip++] = (byte)opcode;
	}

	public void emit1(short opcode, int arg) { emit1(opcode, arg, -1, -1); }

	public void emit1(short opcode, int arg, int p, int q) {
		emit(opcode, p, q);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES);
		writeShort(impl.instrs, impl.ip, (short)arg);
		impl.ip += Bytecode.OPND_SIZE_IN_BYTES;
	}

	public void emit2(short opcode, int arg, int arg2, int p, int q) {
		emit(opcode, p, q);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES * 2);
		writeShort(impl.instrs, impl.ip, (short)arg);
		impl.ip += Bytecode.OPND_SIZE_IN_BYTES;
		writeShort(impl.instrs, impl.ip, (short)arg2);
		impl.ip += Bytecode.OPND_SIZE_IN_BYTES;
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
		//System.out.println("before insert of "+opcode+"("+s+"):"+ Arrays.toString(impl.instrs));
		ensureCapacity(1+Bytecode.OPND_SIZE_IN_BYTES);
		int instrSize = 1 + Bytecode.OPND_SIZE_IN_BYTES;
		System.arraycopy(impl.instrs, addr,
						 impl.instrs, addr + instrSize,
						 impl.ip-addr); // make room for opcode, opnd
		int save = impl.ip;
		impl.ip = addr;
		emit1(opcode, s);
		impl.ip = save+instrSize;
		//System.out.println("after  insert of "+opcode+"("+s+"):"+ Arrays.toString(impl.instrs));
		// adjust addresses for BR and BRF
		int a=addr+instrSize;
		while ( a < impl.ip ) {
			byte op = impl.instrs[a];
			Bytecode.Instruction I = Bytecode.instructions[op];
			if ( op == Bytecode.INSTR_BR || op == Bytecode.INSTR_BRF ) {
				int opnd = BytecodeDisassembler.getShort(impl.instrs, a+1);
				writeShort(impl.instrs, a+1, (short)(opnd+instrSize));
			}
			a += I.nopnds * Bytecode.OPND_SIZE_IN_BYTES + 1;
		}
		//System.out.println("after  insert of "+opcode+"("+s+"):"+ Arrays.toString(impl.instrs));
	}

	public void write(int addr, short value) {
		writeShort(impl.instrs, addr, value);
	}

	public int address() { return impl.ip; }

	protected void ensureCapacity(int n) {
		if ( (impl.ip+n) >= impl.instrs.length ) { // ensure room for full instruction
			byte[] c = new byte[impl.instrs.length*2];
			System.arraycopy(impl.instrs, 0, c, 0, impl.instrs.length);
			impl.instrs = c;
			Interval[] sm = new Interval[impl.sourceMap.length*2];
			System.arraycopy(impl.sourceMap, 0, sm, 0, impl.sourceMap.length);
			impl.sourceMap = sm;
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
