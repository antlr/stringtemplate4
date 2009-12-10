package org.stringtemplate;

import org.antlr.runtime.Token;

public class STCompiletimeMessage extends STMessage {
    Token token;
    String msg;
    
    public STCompiletimeMessage(ErrorType error, Token t) { this(error, t, null); }
    public STCompiletimeMessage(ErrorType error, Token t, Throwable cause) {
        this(error, t, cause, null);
    }
    public STCompiletimeMessage(ErrorType error, Token t, Throwable cause, String msg) {
        this(error, t, cause, msg, null);
    }
    public STCompiletimeMessage(ErrorType error, Token t, Throwable cause, String msg, Object arg) {
        super(error, null, cause, arg);
        this.token = t;
        this.msg = msg;
    }

    public String toString() {
        String hdr = token.getLine()+":"+token.getCharPositionInLine();
        if ( arg==null ) return String.format(error.messageTemplate, hdr+": "+msg);
        return String.format(error.messageTemplate, arg+" "+hdr+": "+msg);
    }
}
