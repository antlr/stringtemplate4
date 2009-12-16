package org.stringtemplate.misc;

import org.antlr.runtime.Token;
import org.antlr.runtime.RecognitionException;
import org.stringtemplate.misc.ErrorType;
import org.stringtemplate.misc.STMessage;

public class STSyntaxErrorMessage extends STMessage {
    String msg;
    
    public STSyntaxErrorMessage(ErrorType error, Token t) { this(error, t, null); }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause) {
        this(error, t, cause, null);
    }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause, String msg) {
        this(error, t, cause, msg, null);
    }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause, String msg, Object arg) {
        super(error, null, cause, arg);
        this.msg = msg;
    }

    public String toString() {
        RecognitionException re = (RecognitionException)cause;
        String hdr = re.line+":"+re.charPositionInLine;
        if ( arg==null ) return String.format(error.messageTemplate, hdr+": "+msg);
        return String.format(error.messageTemplate, arg+" "+hdr+": "+msg);
    }
}
