package org.stringtemplate.v4.test;

import org.stringtemplate.v4.*;

public class Playground {
	public static void main(String[] args) {
		STGroup g = new STGroupFile("g.stg");
		ST t = g.getInstanceOf("u");
		if ( t!=null ) System.out.println(t.render());
	}
}
