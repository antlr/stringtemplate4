package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.*;

import static org.junit.Assert.assertEquals;

public class TestTemplateNames extends BaseTest {
	@Test public void testAbsoluteTemplateRefFromOutside() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir,           "a.st", "a(x) ::= << </subdir/b()> >>\n");
		writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
		STGroup group = new STGroupDir(dir);
		assertEquals(" bar ", group.getInstanceOf("a").render());
		assertEquals(" bar ", group.getInstanceOf("/a").render());
		assertEquals("bar", group.getInstanceOf("/subdir/b").render());
	}


	@Test public void testRelativeTemplateRefInExpr() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir,           "a.st", "a(x) ::= << <subdir/b()> >>\n");
		writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
		STGroup group = new STGroupDir(dir);
		assertEquals(" bar ", group.getInstanceOf("a").render());
	}

	@Test public void testAbsoluteTemplateRefInExpr() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir,           "a.st", "a(x) ::= << </subdir/b()> >>\n");
		writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
		STGroup group = new STGroupDir(dir);
		assertEquals(" bar ", group.getInstanceOf("a").render());
	}

	@Test public void testRefToAnotherTemplateInSameGroup() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a() ::= << <b()> >>\n");
		writeFile(dir, "b.st", "b() ::= <<bar>>\n");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		String expected = " bar ";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testRefToAnotherTemplateInSameSubdir() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir+"/subdir", "a.st", "a() ::= << <b()> >>\n");
		writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
		STGroup group = new STGroupDir(dir);
		group.getInstanceOf("/subdir/a").impl.dump();
		assertEquals(" bar ", group.getInstanceOf("/subdir/a").render());
	}

    @Test public void testFullyQualifiedGetInstanceOf() throws Exception {
        String dir = getRandomDir();
        writeFile(dir, "a.st", "a(x) ::= <<foo>>");
        STGroup group = new STGroupDir(dir);
		assertEquals("foo", group.getInstanceOf("a").render());
		assertEquals("foo", group.getInstanceOf("/a").render());
    }

    @Test public void testFullyQualifiedTemplateRef() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
		writeFile(dir+"/subdir", "a.st", "a() ::= << </subdir/b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
		assertEquals(" bar ", group.getInstanceOf("/subdir/a").render());
		assertEquals(" bar ", group.getInstanceOf("subdir/a").render());
    }

    @Test public void testFullyQualifiedTemplateRef2() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= << </group/b()> >>\n");
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"</a()>\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("/a");
        ST st2 = group.getInstanceOf("/group/c"); // invokes /a
        String expected = " bar  bar ";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

	@Test public void testRelativeInSubdir() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir,           "a.st", "a(x) ::= << </subdir/c()> >>\n");
		writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
		writeFile(dir+"/subdir", "c.st", "c() ::= << <b()> >>\n");
		STGroup group = new STGroupDir(dir);
		assertEquals("  bar  ", group.getInstanceOf("a").render());
	}

	// TODO: test <a/b()> is RELATIVE NOT ABSOLUTE
}
