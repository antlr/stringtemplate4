package org.stringtemplate.v4.test;

import org.stringtemplate.v4.*;

public class Playground {
	public static void main(String[] args) {
		ErrorBufferAllErrors errors = new ErrorBufferAllErrors();
		STGroup g = new STGroupFile("/tmp/g.stg");
		g.setListener(errors);
		ST t = g.getInstanceOf("u");
		if ( t!=null ) System.out.println(t.render());
		System.err.println("errors: "+errors);
	}
}
