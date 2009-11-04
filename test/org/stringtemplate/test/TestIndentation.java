package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.Misc;
import org.stringtemplate.STGroup;

public class TestIndentation extends BaseTest {
	@Test public void testSimpleIndentOfAttributeList()
			throws Exception
	{
		String templates =
				"group test;" +newline+
				"list(names) ::= <<" +
				"  <names; separator=\"\\n\">"+newline+
				">>"+newline;

		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		STErrorListener errors = new ErrorBuffer();
		group.setErrorListener(errors);
		ST t = group.getInstanceOf("list");
		t.add("names", "Terence");
		t.add("names", "Jim");
		t.add("names", "Sriram");
		String expecting =
				"  Terence"+newline+
				"  Jim"+newline+
				"  Sriram";
		assertEquals(expecting, t.render());
	}

	@Test public void testIndentOfMultilineAttributes()
			throws Exception
	{
		String templates =
				"group test;" +newline+
				"list(names) ::= <<" +
				"  <names; separator=\"\n\">"+newline+
				">>"+newline;
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		STErrorListener errors = new ErrorBuffer();
		group.setErrorListener(errors);
		ST t = group.getInstanceOf("list");
		t.add("names", "Terence\nis\na\nmaniac");
		t.add("names", "Jim");
		t.add("names", "Sriram\nis\ncool");
		String expecting =
				"  Terence"+newline+
				"  is"+newline+
				"  a"+newline+
				"  maniac"+newline+
				"  Jim"+newline+
				"  Sriram"+newline+
				"  is"+newline+
				"  cool";
		assertEquals(expecting, t.render());
	}

	@Test public void testIndentOfMultipleBlankLines()
			throws Exception
	{
		String templates =
				"group test;" +newline+
				"list(names) ::= <<" +
				"  <names>"+newline+
				">>"+newline;
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		STErrorListener errors = new ErrorBuffer();
		group.setErrorListener(errors);
		ST t = group.getInstanceOf("list");
		t.add("names", "Terence\n\nis a maniac");
		String expecting =
				"  Terence"+newline+
				""+newline+ // no indent on blank line
				"  is a maniac";
		assertEquals(expecting, t.render());
	}

	@Test public void testIndentBetweenLeftJustifiedLiterals()
			throws Exception
	{
		String templates =
				"group test;" +newline+
				"list(names) ::= <<" +
				"Before:"+newline +
				"  <names; separator=\"\\n\">"+newline+
				"after" +newline+
				">>"+newline;
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		STErrorListener errors = new ErrorBuffer();
		group.setErrorListener(errors);
		ST t = group.getInstanceOf("list");
		t.code.dump();
		t.add("names", "Terence");
		t.add("names", "Jim");
		t.add("names", "Sriram");
		String expecting =
				"Before:" +newline+
				"  Terence"+newline+
				"  Jim"+newline+
				"  Sriram"+newline+
				"after";
		assertEquals(expecting, t.render());
	}

	@Test public void testNestedIndent()
			throws Exception
	{
		String templates =
				"group test;" +newline+
				"method(name,stats) ::= <<" +
				"void <name>() {"+newline +
				"\t<stats; separator=\"\\n\">"+newline+
				"}" +newline+
				">>"+newline+
				"ifstat(expr,stats) ::= <<"+newline +
				"if (<expr>) {"+newline +
				"  <stats; separator=\"\\n\">"+newline +
				"}" +
				">>"+newline +
				"assign(lhs,expr) ::= <<<lhs>=<expr>;>>"+newline
				;
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		STErrorListener errors = new ErrorBuffer();
		group.setErrorListener(errors);

		ST t = group.getInstanceOf("method");
		t.add("name", "foo");
		ST s1 = group.getInstanceOf("assign");
		s1.add("lhs", "x");
		s1.add("expr", "0");
		ST s2 = group.getInstanceOf("ifstat");
		s2.add("expr", "x>0");
		ST s2a = group.getInstanceOf("assign");
		s2a.add("lhs", "y");
		s2a.add("expr", "x+y");
		ST s2b = group.getInstanceOf("assign");
		s2b.add("lhs", "z");
		s2b.add("expr", "4");
		s2.add("stats", s2a);
		s2.add("stats", s2b);
		t.add("stats", s1);
		t.add("stats", s2);
		String expecting =
				"void foo() {"+newline+
				"\tx=0;"+newline+
				"\tif (x>0) {"+newline+
				"\t  y=x+y;"+newline+
				"\t  z=4;"+newline+
				"\t}"+newline+
				"}";
		assertEquals(expecting, t.render());
	}	
}
