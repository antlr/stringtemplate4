package org.stringtemplate.test;

import org.stringtemplate.STErrorListener;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ErrorBuffer implements STErrorListener {
    StringBuffer errorOutput = new StringBuffer(500);
    int n = 0;
    public void error(String msg) { error(msg, null); }
    public void error(String msg, Throwable e) {
        n++;
        if ( n>1 ) {
            errorOutput.append('\n');
        }
        if ( e!=null ) {
            StringWriter duh = new StringWriter();
            e.printStackTrace(new PrintWriter(duh));
            errorOutput.append(msg+": "+duh.toString());
        }
        else {
            errorOutput.append(msg);
        }
    }
    public void warning(String msg) {
        n++;
        errorOutput.append(msg);
    }
    public boolean equals(Object o) {
        String me = toString();
        String them = o.toString();
        return me.equals(them);
    }
    public String toString() {
        return errorOutput.toString();
    }
}
