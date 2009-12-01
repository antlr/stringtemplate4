package org.stringtemplate.compiler;

import org.antlr.runtime.RecognitionException;
import org.stringtemplate.STException;

public class STRecognitionException extends STException {
    public STRecognitionException(String msg, RecognitionException cause) {
        super(msg,cause);
    }
}
