package org.stringtemplate.compiler;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Token;

import java.util.List;

// TODO: maybe rename; more than code gen now; break into two interfaces?
public interface CodeGenerator {
    void emit(short opcode);
    void emit(short opcode, int arg);
    void emit(short opcode, int arg1, int arg2);
    void emit(short opcode, String s);
    void write(int addr, short value);
    /** Return address where next instruction will be written */
    int address();

    /** If we're compiling templates in subdir or group file under root,
     *  what's the templatePathPrefix to add?
     */
    String templateReferencePrefix();

    /** Compile a subtemplate. It's the '...' in this <names:{p | ...}> */
    String compileAnonTemplate(String enclosingTemplateName,
                               TokenStream input,
                               List<Token> ids,
                               RecognizerSharedState state);

    String compileRegion(String enclosingTemplateName,
                         String regionName,
                         TokenStream input,
                         RecognizerSharedState state);

    void defineBlankRegion(String fullyQualifiedName);
}
