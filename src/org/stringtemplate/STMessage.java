package org.stringtemplate;

import java.io.StringWriter;
import java.io.PrintWriter;

public class STMessage {
    /** if in debug mode, has create instance, add attr events and eval
     *  template events.
     */    
    ST self;
    ErrorType error;
    public Object arg;
    public Object arg2;
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
    public STMessage(ErrorType error, ST self, Throwable cause, Object arg) {
        this(error,self,cause);
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
        if ( self!=null ) {
            pw.print(" in context ");
            pw.print(self.getEnclosingInstanceStackString());
        }
        pw.println();
        if ( cause!=null ) {
            cause.printStackTrace(pw);
            cause.printStackTrace();
        }
        return sw.toString();
    }
}
