package org.stringtemplate;

public class STException extends RuntimeException { // no checking damnit!
    public STException() { ; }
    public STException(Exception cause) { super(cause); }
}
