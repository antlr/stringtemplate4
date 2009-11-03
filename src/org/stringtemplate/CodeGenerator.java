package org.stringtemplate;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Token;

import java.util.List;

public interface CodeGenerator {
	void emit(short opcode);
	void emit(short opcode, int arg);
	void emit(short opcode, String s);
	void write(int addr, short value);
	/** Return address where next instruction will be written */
	int address();
	String compileAnonTemplate(TokenStream input,
							   List<Token> ids,
							   RecognizerSharedState state);	
}
