package org.stringtemplate;

public class STException extends RuntimeException { // no checking damnit!
    Exception cause;
    public STException() { ; }
    public STException(Exception cause) { this.cause = cause; }
}
