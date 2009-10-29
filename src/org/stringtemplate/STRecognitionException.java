package org.stringtemplate;

import org.antlr.runtime.RecognitionException;

public class STRecognitionException extends STException {
    Chunk chunk; // which chunk caused trouble?
    String msg;
    
    public STRecognitionException(Chunk chunk) {
        this.chunk = chunk;
    }
    public STRecognitionException(Chunk chunk, String msg, RecognitionException cause) {
        super(cause);
        this.chunk = chunk;
        this.msg = msg;
    }

}
