package org.stringtemplate;

public class ErrorTolerance {
	// bit set values telling ST what to care about
	public static final int DETECT_ADD_ATTR = 1;
	public static final int DETECT_UNKNOWN_PROPERTY = 2;
	public static final int DETECT_foo = 4;

	public static final int DEFAULT_TOLERANCE = DETECT_ADD_ATTR;

	public int detect = DEFAULT_TOLERANCE;

	public boolean detects(int x) { return (detect & x) != 0; }
	public void detect(int x) { detect |= x; }
	public void ignore(int x) { detect &= ~x; }
}
