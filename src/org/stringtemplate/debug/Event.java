package org.stringtemplate.debug;

public class Event {
    protected Throwable stack;
    public Event() { stack = new Throwable(); }
	public String getFileName() { return getSTEntryPoint().getFileName(); }
	public int getLine() { return getSTEntryPoint().getLineNumber(); }
	
	public StackTraceElement getSTEntryPoint() {
		StackTraceElement[] trace = stack.getStackTrace();
		for (StackTraceElement e : trace) {
			String name = e.toString();
			// TODO: remove special after testing
			if ( name.indexOf("main(")>0 ) return e;
			if ( !name.startsWith("org.stringtemplate") ) return e;
		}
		return trace[0];
	}
}
