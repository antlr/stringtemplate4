package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

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
}
