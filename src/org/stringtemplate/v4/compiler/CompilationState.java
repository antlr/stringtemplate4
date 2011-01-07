package org.stringtemplate.v4.compiler;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Interval;

/** temp data used during construction and functions that fill it / use it.
 *  Result is impl CompiledST object.
 */
public class CompilationState {
	/** The compiled code implementation to fill in. */
	CompiledST impl = new CompiledST();

	/** Track unique strings; copy into CompiledST's String[] after compilation */
	StringTable stringtable = new StringTable();

	/** Track instruction location within code.instrs array; this is
	 *  next address to write to.  Byte-addressable memory.
	 */
	int ip = 0;

	TokenStream tokens;

	ErrorManager errMgr;

	public CompilationState(ErrorManager errMgr, String name, TokenStream tokens) {
		this.errMgr = errMgr;
		this.tokens = tokens;
		impl.name = name;
	}

	public int defineString(String s) { return stringtable.add(s); }

	public void refAttr(CommonTree id) {
		String name = id.getText();
		if ( impl.formalArguments!=null && impl.formalArguments.get(name)!=null ) {
			FormalArgument arg = impl.formalArguments.get(name);
			int index = arg.index;
			emit1(id, Bytecode.INSTR_LOAD_LOCAL, index);
		}
		else {
			if ( Interpreter.predefinedAnonSubtemplateAttributes.contains(name) ) {
				errMgr.compileTimeError(ErrorType.NO_SUCH_ATTRIBUTE, id.token);
				emit(id, Bytecode.INSTR_NULL);
			}
			else {
				emit1(id, Bytecode.INSTR_LOAD_ATTR, name);
			}
		}
	}

	public void setOption(CommonTree id) {
		Interpreter.Option O = Compiler.supportedOptions.get(id.getText());
		emit1(id, Bytecode.INSTR_STORE_OPTION, O.ordinal());
	}

	public void func(CommonTree id) {
		Short funcBytecode = Compiler.funcs.get(id.getText());
		if ( funcBytecode==null ) {
			errMgr.compileTimeError(ErrorType.NO_SUCH_FUNCTION, id.token);
			emit(id, Bytecode.INSTR_POP);
		}
		else {
			emit(id, funcBytecode);
		}
	}

	public void emit(short opcode) { emit(null,opcode); }

	public void emit(CommonTree opAST, short opcode) {
		ensureCapacity(1);
		if ( opAST!=null ) {
			int i = opAST.getTokenStartIndex();
			int j = opAST.getTokenStopIndex();
			int p = ((CommonToken)tokens.get(i)).getStartIndex();
			int q = ((CommonToken)tokens.get(j)).getStopIndex();
			if ( !(p<0 || q<0) ) impl.sourceMap[ip] = new Interval(p, q);
		}
		impl.instrs[ip++] = (byte)opcode;
	}

	public void emit1(CommonTree opAST, short opcode, int arg) {
		emit(opAST, opcode);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES);
		writeShort(impl.instrs, ip, (short)arg);
		ip += Bytecode.OPND_SIZE_IN_BYTES;
	}

	public void emit2(CommonTree opAST, short opcode, int arg, int arg2) {
		emit(opAST, opcode);
		ensureCapacity(Bytecode.OPND_SIZE_IN_BYTES * 2);
		writeShort(impl.instrs, ip, (short)arg);
		ip += Bytecode.OPND_SIZE_IN_BYTES;
		writeShort(impl.instrs, ip, (short)arg2);
		ip += Bytecode.OPND_SIZE_IN_BYTES;
	}

	public void emit2(CommonTree opAST, short opcode, String s, int arg2) {
		int i = defineString(s);
		emit2(opAST, opcode, i, arg2);
	}

	public void emit1(CommonTree opAST, short opcode, String s) {
		int i = defineString(s);
		emit1(opAST, opcode, i);
	}

	public void insert(int addr, short opcode, String s) {
		//System.out.println("before insert of "+opcode+"("+s+"):"+ Arrays.toString(impl.instrs));
		ensureCapacity(1+Bytecode.OPND_SIZE_IN_BYTES);
		int instrSize = 1 + Bytecode.OPND_SIZE_IN_BYTES;
		System.arraycopy(impl.instrs, addr,
						 impl.instrs, addr + instrSize,
						 ip-addr); // make room for opcode, opnd
		int save = ip;
		ip = addr;
		emit1(null,opcode, s);
		ip = save+instrSize;
		//System.out.println("after  insert of "+opcode+"("+s+"):"+ Arrays.toString(impl.instrs));
		// adjust addresses for BR and BRF
		int a=addr+instrSize;
		while ( a < ip ) {
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

	protected void ensureCapacity(int n) {
		if ( (ip+n) >= impl.instrs.length ) { // ensure room for full instruction
			byte[] c = new byte[impl.instrs.length*2];
			System.arraycopy(impl.instrs, 0, c, 0, impl.instrs.length);
			impl.instrs = c;
			Interval[] sm = new Interval[impl.sourceMap.length*2];
			System.arraycopy(impl.sourceMap, 0, sm, 0, impl.sourceMap.length);
			impl.sourceMap = sm;
		}
	}

	public void indent(String indent) {	emit1(null,Bytecode.INSTR_INDENT, indent); }

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}
}
