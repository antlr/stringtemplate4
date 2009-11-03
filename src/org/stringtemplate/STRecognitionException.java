package org.stringtemplate;

import org.antlr.runtime.RecognitionException;

public class STRecognitionException extends STException {
    public STRecognitionException(String msg, RecognitionException cause) {
        super(msg,cause);
    }
}
