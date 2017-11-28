/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.v4.test;

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGroups extends BaseTest {
	@Test public void testSimpleGroup() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<foo>>");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEscapeOneRightAngle() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= << > >>");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		st.add("x", "parrt");
		String expected = " > ";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEscapeJavaRightShift() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= << \\>> >>");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		st.add("x", "parrt");
		String expected = " >> ";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEscapeJavaRightShift2() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= << >\\> >>");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		st.add("x", "parrt");
		String expected = " >> ";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEscapeJavaRightShiftAtRightEdge() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<\\>>>"); // <<\>>>
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		st.add("x", "parrt");
		String expected = "\\>";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEscapeJavaRightShiftAtRightEdge2() throws Exception {
		String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<>\\>>>");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("a");
		st.add("x", "parrt");
		String expected = ">>";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testSimpleGroupFromString() throws Exception {
		String g =
			"a(x) ::= <<foo>>\n"+
			"b() ::= <<bar>>\n";
		STGroup group = new STGroupString(g);
		ST st = group.getInstanceOf("a");
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testGroupWithTwoTemplates() throws Exception {
        String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<foo>>");
		writeFile(dir, "b.st", "b() ::= \"bar\"");
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("b");
        String expected = "foobar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir,           "a.st", "a(x) ::= <<foo>>");
		writeFile(dir+"/subdir", "b.st", "b() ::= \"bar\"");
        STGroup group = new STGroupDir(dir);
		assertEquals("foo", group.getInstanceOf("a").render());
		assertEquals("bar", group.getInstanceOf("/subdir/b").render());
		assertEquals("bar", group.getInstanceOf("subdir/b").render());
    }

	@Test public void testSubdirWithSubtemplate() throws Exception {
		// /randomdir/a and /randomdir/subdir/b
		String dir = getRandomDir();
		writeFile(dir+"/subdir", "a.st", "a(x) ::= \"<x:{y|<y>}>\"");
		STGroup group = new STGroupDir(dir);
		ST st = group.getInstanceOf("/subdir/a");
		st.add("x", new String[] {"a", "b"});
		assertEquals("ab", st.render());
	}

    @Test public void testGroupFileInDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<foo>>");
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"duh\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
		assertEquals("foo", group.getInstanceOf("a").render());
		assertEquals("bar", group.getInstanceOf("/group/b").render());
		assertEquals("duh", group.getInstanceOf("/group/c").render());
    }

	@Test public void testSubSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
		writeFile(dir,              "a.st", "a(x) ::= <<foo>>");
		writeFile(dir+"/sub1/sub2", "b.st", "b() ::= \"bar\"");
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("/sub1/sub2/b");
        String expected = "foobar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupFileInSubDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
		writeFile(dir, "a.st", "a(x) ::= <<foo>>");
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"duh\"\n";
        writeFile(dir, "subdir/group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("subdir/group/b");
        ST st3 = group.getInstanceOf("subdir/group/c");
        String expected = "foobarduh";
        String result = st1.render()+st2.render()+st3.render();
        assertEquals(expected, result);
    }

    @Test public void testDupDef() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "b() ::= \"bar\"\n"+
            "b() ::= \"duh\"\n";
        writeFile(dir, "group.stg", groupFile);
		STErrorListener errors = new ErrorBuffer();
        STGroupFile group = new STGroupFile(dir+"/group.stg");
		group.setListener(errors);
        group.load();
		String expected = "group.stg 2:0: redefinition of template b"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
    }

	@Test public void testAlias() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"a() ::= \"bar\"\n"+
			"b ::= a\n";
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ST st = group.getInstanceOf("b");
		String expected = "bar";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testAliasWithArgs() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"a(x,y) ::= \"<x><y>\"\n"+
			"b ::= a\n";
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ST st = group.getInstanceOf("b");
		st.add("x", 1);
		st.add("y", 2);
		String expected = "12";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testSimpleDefaultArg() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        String b = "b(x=\"foo\") ::= \"<x>\"\n";
        writeFile(dir, "a.st", a);
        writeFile(dir, "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = " foo ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefaultArgument() throws Exception {
        String templates =
                "method(name) ::= <<"+newline+
                "<stat(name)>" +newline+
                ">>"+newline+
                "stat(name,value=\"99\") ::= \"x=<value>; // <name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        String expecting = "x=99; // foo";
        String result = b.render();
        assertEquals(expecting, result);
    }

	@Test public void testBooleanDefaultArguments() throws Exception {
		String templates =
				"method(name) ::= <<"+newline+
				"<stat(name)>" +newline+
				">>"+newline+
				"stat(name,x=true,y=false) ::= \"<name>; <x> <y>\""+newline
				;
		writeFile(tmpdir, "group.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/group.stg");
		ST b = group.getInstanceOf("method");
		b.add("name", "foo");
		String expecting = "foo; true false";
		String result = b.render();
		assertEquals(expecting, result);
	}

	@Test public void testDefaultArgument2() throws Exception {
		String templates =
				"stat(name,value=\"99\") ::= \"x=<value>; // <name>\""+newline
				;
		writeFile(tmpdir, "group.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/group.stg");
		ST b = group.getInstanceOf("stat");
		b.add("name", "foo");
		String expecting = "x=99; // foo";
		String result = b.render();
		assertEquals(expecting, result);
	}

	@Test public void testSubtemplateAsDefaultArgSeesOtherArgs() throws Exception {
		String templates =
			"t(x,y={<x:{s|<s><z>}>},z=\"foo\") ::= <<\n" +
			"x: <x>\n" +
			"y: <y>\n" +
			">>"+newline
			;
		writeFile(tmpdir, "group.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/group.stg");
		ST b = group.getInstanceOf("t");
		b.add("x", "a");
		String expecting =
			"x: a" +newline+
			"y: afoo";
		String result = b.render();
		assertEquals(expecting, result);
	}

	@Test public void testEarlyEvalOfDefaultArgs() throws Exception {
		String templates =
			"s(x,y={<(x)>}) ::= \"<x><y>\"\n"; // should see x in def arg
		STGroup group = new STGroupString(templates);
		ST b = group.getInstanceOf("s");
		b.add("x", "a");
		String expecting = "aa";
		String result = b.render();
		assertEquals(expecting, result);
	}

	@Test public void testDefaultArgumentAsSimpleTemplate() throws Exception {
		String templates =
				"stat(name,value={99}) ::= \"x=<value>; // <name>\""+newline
				;
		writeFile(tmpdir, "group.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/group.stg");
		ST b = group.getInstanceOf("stat");
		b.add("name", "foo");
		String expecting = "x=99; // foo";
		String result = b.render();
		assertEquals(expecting, result);
	}

    @Test public void testDefaultArgumentManuallySet() throws Exception {
        class Field {
            public String name = "parrt";
            public int n = 0;
			@Override
            public String toString() {
                return "Field";
            }
        }
		// set arg f manually for stat(f=f)
        String templates =
                "method(fields) ::= <<"+newline+
                "<fields:{f | <stat(f)>}>" +newline+
                ">>"+newline+
                "stat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST m = group.getInstanceOf("method");
        m.add("fields", new Field());
        String expecting = "x=parrt; // parrt";
        String result = m.render();
        assertEquals(expecting, result);
    }

    @Test public void testDefaultArgumentSeesVarFromDynamicScoping() throws Exception {
        class Field {
            public String name = "parrt";
            public int n = 0;
			@Override
            public String toString() {
                return "Field";
            }
        }
        String templates =
                "method(fields) ::= <<"+newline+
                "<fields:{f | <stat()>}>" +newline+
                ">>"+newline+
                "stat(value={<f.name>}) ::= \"x=<value>; // <f.name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST m = group.getInstanceOf("method");
        m.add("fields", new Field());
        String expecting = "x=parrt; // parrt";
        String result = m.render();
        assertEquals(expecting, result);
    }

    @Test public void testDefaultArgumentImplicitlySet2() throws Exception {
        class Field {
            public String name = "parrt";
            public int n = 0;
			@Override
            public String toString() {
                return "Field";
            }
        }
		// f of stat is implicit first arg
        String templates =
                "method(fields) ::= <<"+newline+
                "<fields:{f | <f:stat()>}>" +newline+
                ">>"+newline+
                "stat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST m = group.getInstanceOf("method");
        m.add("fields", new Field());
        String expecting = "x=parrt; // parrt";
        String result = m.render();
        assertEquals(expecting, result);
    }

    @Test public void testDefaultArgumentAsTemplate() throws Exception {
        String templates =
                "method(name,size) ::= <<"+newline+
                "<stat(name)>" +newline+
                ">>"+newline+
                "stat(name,value={<name>}) ::= \"x=<value>; // <name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        b.add("size", "2");
        String expecting = "x=foo; // foo";
        String result = b.render();
        //System.err.println("result='"+result+"'");
        assertEquals(expecting, result);
    }

    @Test public void testDefaultArgumentAsTemplate2() throws Exception {
        String templates =
                "method(name,size) ::= <<"+newline+
                "<stat(name)>" +newline+
                ">>"+newline+
                "stat(name,value={ [<name>] }) ::= \"x=<value>; // <name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        b.add("size", "2");
        String expecting = "x=[foo] ; // foo"; // won't see ' ' after '=' since it's an indent not simple string
        String result = b.render();
        //System.err.println("result='"+result+"'");
        assertEquals(expecting, result);
    }

    @Test public void testDoNotUseDefaultArgument() throws Exception {
        String templates =
                "method(name) ::= <<"+newline+
                "<stat(name,\"34\")>" +newline+
                ">>"+newline+
                "stat(name,value=\"99\") ::= \"x=<value>; // <name>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        String expecting = "x=34; // foo";
        String result = b.render();
        assertEquals(expecting, result);
    }

    @Test public void testDefaultArgumentInParensToEvalEarly() throws Exception {
        class Counter {
            int n = 0;
			@Override
            public String toString() {
                return String.valueOf(n++);
            }
        }
        String templates =
                "A(x) ::= \"<B()>\""+newline+
                "B(y={<(x)>}) ::= \"<y> <x> <x> <y>\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST a = group.getInstanceOf("A");
        a.add("x", new Counter());
        String expecting = "0 1 2 0"; // trace must be false to get these numbers
        String result = a.render();
        //System.err.println("result='"+result+"'");
        assertEquals(expecting, result);
    }

	@Test public void testTrueFalseArgs() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(true,{a})>\"";
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ST st = group.getInstanceOf("g");
		String expected = "truea";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testNamedArgsInOrder() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(x={a},y={b})>\"";
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ST st = group.getInstanceOf("g");
		String expected = "ab";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testNamedArgsOutOfOrder() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(y={b},x={a})>\"";
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ST st = group.getInstanceOf("g");
		String expected = "ab";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testUnknownNamedArg() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(x={a},z={b})>\"";
		   //012345678901234567

		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ErrorBuffer errors = new ErrorBuffer();
		group.setListener(errors);
		ST st = group.getInstanceOf("g");
		st.render();
		String expected = "context [/g] 1:1 attribute z isn't defined"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

	@Test public void testMissingNamedArg() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(x={a},{b})>\"";
		   //01234567890123456789

		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ErrorBuffer errors = new ErrorBuffer();
		group.setListener(errors);
		group.load();
		String expected = "group.stg 2:18: mismatched input '{' expecting ELLIPSIS"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

	@Test public void testNamedArgsNotAllowInIndirectInclude() throws Exception {
		String dir = getRandomDir();
		String groupFile =
		    "f(x,y) ::= \"<x><y>\"\n" +
		   //01234567890 1234567 8 9
		    "g(name) ::= \"<(name)(x={a},y={b})>\"";
		   //012345678901 2345678901234567890123 4
		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ErrorBuffer errors = new ErrorBuffer();
		group.setListener(errors);
		group.load();
		String expected = "group.stg 2:22: '=' came as a complete surprise to me"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testCantSeeGroupDirIfGroupFileOfSameName() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= <<dir1 a>>\n";
        writeFile(dir, "group/a.st", a); // can't see this file

        String groupFile =
            "b() ::= \"group file b\"\n";
        writeFile(dir, "group.stg", groupFile);

        STGroup group1 = new STGroupDir(dir);
        ST st = group1.getInstanceOf("group/a"); // can't see
        assertEquals(null, st);
    }

	@Test public void testUnloadingSimpleGroup() throws Exception {
		String dir = getRandomDir();
		String a =
			"a(x) ::= <<foo>>\n";
		String b =
			"b() ::= <<bar>>\n";
		writeFile(dir, "a.st", a);
		writeFile(dir, "b.st", b);
		STGroup group = new STGroupDir(dir);
		group.load(); // force load
		ST st = group.getInstanceOf("a");
		int originalHashCode = System.identityHashCode(st);
		group.unload(); // blast cache
		st = group.getInstanceOf("a");
		int newHashCode = System.identityHashCode(st);
		assertEquals(originalHashCode==newHashCode, false); // diff objects
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);
		st = group.getInstanceOf("b");
		expected = "bar";
		result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testUnloadingGroupFile() throws Exception {
		String dir = getRandomDir();
		String a =
			"a(x) ::= <<foo>>\n" +
			"b() ::= <<bar>>\n";
		writeFile(dir, "a.stg", a);
		STGroup group = new STGroupFile(dir+"/a.stg");
		group.load(); // force load
		ST st = group.getInstanceOf("a");
		int originalHashCode = System.identityHashCode(st);
		group.unload(); // blast cache
		st = group.getInstanceOf("a");
		int newHashCode = System.identityHashCode(st);
		assertEquals(originalHashCode==newHashCode, false); // diff objects
		String expected = "foo";
		String result = st.render();
		assertEquals(expected, result);
		st = group.getInstanceOf("b");
		expected = "bar";
		result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testGroupFileImport() throws Exception {
        // /randomdir/group1.stg (a template) and /randomdir/group2.stg with b.
    	// group1 imports group2, a includes b
        String dir = getRandomDir();
        String groupFile1 =
            "import \"group2.stg\"\n"+
            "a(x) ::= <<\n"+
            "foo<b()>\n"+
            ">>\n";
        writeFile(dir, "group1.stg", groupFile1);
        String groupFile2 =
            "b() ::= \"bar\"\n";
        writeFile(dir, "group2.stg", groupFile2);
        STGroup group1 = new STGroupFile(dir+"/group1.stg");

        // Is the imported template b found?
        ST stb = group1.getInstanceOf("b");
        assertEquals("bar", stb.render());

        // Is the include of b() resolved?
        ST sta = group1.getInstanceOf("a");
        assertEquals("foobar", sta.render());

        // Are the correct "ThatCreatedThisInstance" groups assigned
        assertEquals("group1",sta.groupThatCreatedThisInstance.getName());
        assertEquals("group1",stb.groupThatCreatedThisInstance.getName());

        // Are the correct (native) groups assigned for the templates
        assertEquals("group1",sta.impl.nativeGroup.getName());
        assertEquals("group2",stb.impl.nativeGroup.getName());
    }

	@Test
	public void testGetTemplateNames() throws Exception {
		String templates =
			"t() ::= \"foo\"\n" +
			"main() ::= \"<t()>\"";
		writeFile(tmpdir, "t.stg", templates);

		STGroup group = new STGroupFile(tmpdir + "/t.stg");
		// try to get an undefined template.
		// This will add an entry to the "templates" field in STGroup, however
		// this should not be returned.
		group.lookupTemplate("t2");

		Set<String> names = group.getTemplateNames();

		// Should only contain "t" and "main" (not "t2")
		assertEquals(2, names.size());
		assertTrue(names.contains("/t"));
		assertTrue(names.contains("/main"));
	}

	@Test
	public void testUnloadWithImports() throws Exception {
		writeFile(tmpdir, "t.stg",
				"import \"g1.stg\"\n\nmain() ::= <<\nv1-<f()>\n>>");
		writeFile(tmpdir, "g1.stg", "f() ::= \"g1\"");
		writeFile(tmpdir, "g2.stg", "f() ::= \"g2\"\nf2() ::= \"f2\"\n");
		STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir + "/t.stg");
		ST st = group.getInstanceOf("main");
		Assert.assertEquals("v1-g1", st.render());

		// Change the text of group t, including the imports.
		writeFile(tmpdir, "t.stg",
				"import \"g2.stg\"\n\nmain() ::= <<\nv2-<f()>;<f2()>\n>>");
		group.unload();
		st = group.getInstanceOf("main");
		Assert.assertEquals("v2-g2;f2", st.render());
	}

	@Test public void testLineBreakInGroup() throws Exception {
		String templates =
			"t() ::= <<"+newline+
				"Foo <\\\\>"+newline+
				"  \t  bar"+newline+
				">>"+newline;
		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		Assert.assertNotNull(st);
		String expecting ="Foo bar";
		Assert.assertEquals(expecting, st.render());
	}

	@Test public void testLineBreakInGroup2() throws Exception {
		String templates =
			"t() ::= <<"+newline+
				"Foo <\\\\>       "+newline+
				"  \t  bar"+newline+
				">>"+newline;
		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		Assert.assertNotNull(st);
		String expecting ="Foo bar";
		Assert.assertEquals(expecting, st.render());
	}

	@Test public void testLineBreakMissingTrailingNewline() throws Exception {
		writeFile(tmpdir, "t.stg", "a(x) ::= <<<\\\\>\r\n>>"); // that is <<<\\>>> not an escaped >>
		ErrorBuffer errors = new ErrorBuffer();
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		group.setListener(errors);
		ST st = group.getInstanceOf("a");
		assertEquals("t.stg 1:15: Missing newline after newline escape <\\\\>" + newline, errors.toString());
		st.add("x", "parrt");
		String expected = "";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testLineBreakWithScarfedTrailingNewline() throws Exception {
		writeFile(tmpdir, "t.stg", "a(x) ::= <<<\\\\>\r\n>>"); // \r\n removed as trailing whitespace
		ErrorBuffer errors = new ErrorBuffer();
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		group.setListener(errors);
		ST st = group.getInstanceOf("a");
		assertEquals("t.stg 1:15: Missing newline after newline escape <\\\\>" + newline, errors.toString());
		st.add("x", "parrt");
		String expected = "";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testNullURL() {
		String err = null;
		try {
			new STGroupFile((URL) null, "UTF-8", '<', '>');
		}
		catch (IllegalArgumentException e) {
			err =  e.getMessage();
		}
		assertEquals(err, "URL to group file cannot be null");
	}

  public void doMultipleThreadInvoke(Callable<Object> task) throws InterruptedException, ExecutionException {
    ExecutorService pool = Executors.newFixedThreadPool(20);
    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(100);
    for (int i = 0; i < 100; i++) {
      tasks.add(task);
    }

    List<Future<Object>> futures = pool.invokeAll(tasks);
    pool.shutdown();

    for (Future<Object> future : futures) {
      future.get();
    }
  }

  public void testGroupString(STGroup group) throws Exception {

    assertTrue(group.isDefined("stat"));

    ST b = group.getInstanceOf("stat");
    b.add("name", "foo");
    String expecting = "x=99; // foo";
    String result = b.render();
    assertEquals(expecting, result);
  }


  @Test public void testGroupStringMultipleThreads() throws Exception {
    String templates =
        "stat(name,value={99}) ::= \"x=<value>; // <name>\"" + newline;
    final STGroup group = new STGroupString(templates);

    doMultipleThreadInvoke(new Callable<Object>() {
      public Object call() throws Exception {
        testGroupString(group);
        return null;
      }
    });
  }

  public void testGroupFile(STGroup group) throws Exception {
    assertTrue(group.isDefined("a"));
    assertEquals("foo", group.getInstanceOf("a").render());
  }

  @Test public void testGroupFileMultipleThreads() throws Exception {
    // /randomdir/a and /randomdir/group.stg with b and c templates
    String dir = getRandomDir();
    writeFile(dir, "a.stg", "a(x) ::= <<foo>>");

    final STGroup group = new STGroupFile(dir + "/a.stg");

     doMultipleThreadInvoke(new Callable<Object>() {
      public Object call() throws Exception {
        testGroupFile(group);
        return null;
      }
    });
  }
}
