package org.stringtemplate;

import org.antlr.runtime.RecognitionException;

public class STRecognitionException extends STException {
    Chunk chunk; // which chunk caused trouble?
    int error;   // which error occurred?

    public STRecognitionException(Chunk chunk) {
        this.chunk = chunk;
    }
    public STRecognitionException(Chunk chunk, RecognitionException cause) {
        super(cause);
        this.chunk = chunk;
    }

}
