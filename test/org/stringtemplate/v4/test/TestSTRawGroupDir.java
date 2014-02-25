package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSTRawGroupDir extends BaseTest {
	@Test public void testSimpleGroup() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "foo");
		STGroup group = new STRawGroupDir(dir, '$', '$');
		ST st = group.getInstanceOf("a");
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testSimpleGroup2() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "foo");
		writeFile(dir, "b.st", "$name$");
		STGroup group = new STRawGroupDir(dir, '$', '$');
		ST st = group.getInstanceOf("a");
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);

		ST b = group.getInstanceOf("b");
		b.add("name", "Bob");
		assertEquals("Bob", b.render());
	}

	@Test public void testSimpleGroupAngleBrackets() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "foo");
		writeFile(dir, "b.st", "<name>");
		STGroup group = new STRawGroupDir(dir);
		ST st = group.getInstanceOf("a");
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);

		ST b = group.getInstanceOf("b");
		b.add("name", "Bob");
		assertEquals("Bob", b.render());
	}

	@Test public void testSTRawGroupDir() {
		String dir = getRandomDir();
		writeFile(dir, "template.st", "$values:{foo|[$foo$]}$");
		STGroup group = new STRawGroupDir(dir, '$', '$');
		ST template = group.getInstanceOf("template");
		List<String> values = new ArrayList<String>();
		values.add("one");
		values.add("two");
		values.add("three");
		template.add("values", values);
		assertEquals("[one][two][three]", template.render());
	}

	@Test public void testMap() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "$names:bold()$");
		writeFile(dir, "bold.st", "<b>$it$</b>");
		STGroup group = new STRawGroupDir(dir, '$', '$');
		ST st = group.getInstanceOf("a");
		List<String> names = new ArrayList<String>();
		names.add("parrt");
		names.add("tombu");
		st.add("names", names);
//		String asmResult = st.impl.instrs();
//		System.out.println(asmResult);

//		st.inspect();
		String expected = "<b>parrt</b><b>tombu</b>";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testSuper() throws Exception {
		String dir1 = getRandomDir();
		String a = "dir1 a";
		String b = "dir1 b";
		writeFile(dir1, "a.st", a);
		writeFile(dir1, "b.st", b);
		String dir2 = getRandomDir();
		a = "[<super.a()>]";
		writeFile(dir2, "a.st", a);

		STGroup group1 = new STRawGroupDir(dir1);
		STGroup group2 = new STRawGroupDir(dir2);
		group2.importTemplates(group1);
		ST st = group2.getInstanceOf("a");
		String expected = "[dir1 a]";
		String result = st.render();
		assertEquals(expected, result);
	}

	/**
	 * This is a regression test for antlr/stringtemplate4#70. "Argument
	 * initialization for sub-template in template with STRawGroupDir doesn't
	 * recognize valid parameters"
	 * https://github.com/antlr/stringtemplate4/issues/70
	 */
	@Test
	public void testRawArgumentPassing() {
		String dir1 = getRandomDir();
		String mainRawTemplate = "Hello $name$" + newline +
			"Then do the footer:" + newline +
			"$footerRaw(lastLine=veryLastLineRaw())$" + newline;
		String footerRawTemplate =
			"Simple footer. And now a last line:" + newline +
			"$lastLine$";
		String veryLastLineTemplate =
			"That's the last line.";
		writeFile(dir1, "mainRaw.st", mainRawTemplate);
		writeFile(dir1, "footerRaw.st", footerRawTemplate);
		writeFile(dir1, "veryLastLineRaw.st", veryLastLineTemplate);

		STGroup group = new STRawGroupDir(dir1, '$', '$');
		ST st = group.getInstanceOf("mainRaw");
		assertNotNull(st);
		st.add("name", "John");
		String result = st.render();
		String expected =
			"Hello John" + newline +
			"Then do the footer:" + newline +
			"Simple footer. And now a last line:" + newline +
			"That's the last line." + newline;
		assertEquals(expected, result);
	}
}
