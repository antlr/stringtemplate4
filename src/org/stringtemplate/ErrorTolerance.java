package org.stringtemplate;

public class ErrorTolerance {
	// bit set values telling ST what to emit errors about
    public static final int DETECT_UNKNOWN_ATTRIBUTE = 1;
	public static final int DETECT_UNKNOWN_PROPERTY = 2;
    public static final int DETECT_UNKNOWN_TEMPLATE = 4;
    public static final int DETECT_MALFORMED_TEMPLATE_NAME = 8;

	public static final int DEFAULT_TOLERANCE =
        DETECT_UNKNOWN_TEMPLATE |
        DETECT_MALFORMED_TEMPLATE_NAME;

    /** Gives us a new listener per thread */
    static ThreadLocal<Integer> detect = new ThreadLocal<Integer>() {
        protected Integer initialValue() { return DEFAULT_TOLERANCE; }
    };

	public boolean detects(int x) { return (detect.get() & x) != 0; }
	public void detect(int x) { detect.set( detect.get() | x ); }
	public void ignore(int x) { detect.set( detect.get() & ~x ); }
}
