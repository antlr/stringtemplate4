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

    /** If we're compiling templates in subdir or group file under root,
     *  what's the prefix to add?
     */
    String templateReferencePrefix();
	String compileAnonTemplate(TokenStream input,
							   List<Token> ids,
							   RecognizerSharedState state);	
}
