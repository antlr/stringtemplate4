package org.stringtemplate;

public interface CodeGenerator {
	void emit(short opcode);
	void emit(short opcode, int arg);
	void emit(short opcode, String s);
	void write(int addr, short value);
	/** Return address where next instruction will be written */
	int address();
}
