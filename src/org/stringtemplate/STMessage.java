package org.stringtemplate;

import org.antlr.runtime.Token;

import java.io.StringWriter;
import java.io.PrintWriter;

public class STMessage {
    /** if in debug mode, has create instance, add attr events and eval
     *  template events.
     */    
    public ST self;
    public ErrorType error;
    public Object arg;
    public Object arg2;
    public Token where;
    public Throwable cause;

    public STMessage(ErrorType error) {
        this.error = error;
    }
    public STMessage(ErrorType error, ST self) {
        this(error);
        this.self = self;
    }
    public STMessage(ErrorType error, ST self, Throwable cause) {
        this(error,self);
        this.cause = cause;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Token where) {
        this(error,self);
        this.cause = cause;
        this.where = where;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Object arg) {
        this(error,self,cause);
        this.arg = arg;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Token where, Object arg) {
        this(error,self,cause,where);
        this.arg = arg;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Object arg, Object arg2) {
        this(error,self,cause,arg);
        this.arg2 = arg2;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String msg = String.format(error.messageTemplate, arg, arg2);
        pw.print(msg);
        if ( cause!=null ) {
            cause.printStackTrace(pw);
            cause.printStackTrace();
        }
        return sw.toString();
    }
}
