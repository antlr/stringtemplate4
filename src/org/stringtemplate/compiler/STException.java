package org.stringtemplate.compiler;

public class STException extends RuntimeException { // no checking damnit!
    public STException() { ; }
	public STException(Exception cause) { super(cause); }
	public STException(String msg, Exception cause) { super(msg,cause); }
}
