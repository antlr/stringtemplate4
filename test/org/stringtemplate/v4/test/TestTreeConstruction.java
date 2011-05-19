package org.stringtemplate.v4.test;

import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.Tree;

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class TestTreeConstruction extends gUnitBase {
	@Before public void setup() {
	    lexerClassName = "org.stringtemplate.v4.compiler.STLexer";
	    parserClassName = "org.stringtemplate.v4.compiler.STParser";
	}
	@Test public void test_template1() throws Exception {
		// gunit test on line 16
		RuleReturnScope rstruct = (RuleReturnScope)execParser("template", "<[]>", 16);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(EXPR [)";
		assertEquals("testing rule template", expecting, actual);
	}

	@Test public void test_template2() throws Exception {
		// gunit test on line 17
		RuleReturnScope rstruct = (RuleReturnScope)execParser("template", "<[a,b]>", 17);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(EXPR ([ a b))";
		assertEquals("testing rule template", expecting, actual);
	}
}
