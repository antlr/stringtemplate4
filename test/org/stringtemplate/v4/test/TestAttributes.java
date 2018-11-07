package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.ST;

import static org.junit.Assert.assertTrue;

public class TestAttributes extends BaseTest {
	@Test
	public void testRedefOfKeyInClone() {
		ST a = new ST("x");
		// a has no formal args

		ST b = new ST(a);
		b.add("x", "foo");

		ST c = new ST(a);
		c.add("x", "bar");
		assertTrue(true); // should not get exception
	}

	// See https://github.com/antlr/stringtemplate4/issues/72 and
	// https://github.com/antlr/stringtemplate4/issues/98
	@Test
	public void testRedefOfKeyInCloneAfterAddingAttribute() {
		ST a = new ST("x");
		a.add("y", "eh?");  // This forces formal def of "y" attribute

		ST b = new ST(a);
		b.add("x", "foo");

		ST c = new ST(a);
		c.add("x", "bar");
		assertTrue(true); // should not get exception
	}
}
