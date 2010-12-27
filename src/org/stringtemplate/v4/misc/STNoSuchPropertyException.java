package org.stringtemplate.v4.misc;

import org.stringtemplate.v4.compiler.STException;

public class STNoSuchPropertyException extends STException {
	public String propertyName;
	public STNoSuchPropertyException() {}
	public STNoSuchPropertyException(Exception e, String propertyName) {
		super(null, e);
		this.propertyName = propertyName;
	}
}
