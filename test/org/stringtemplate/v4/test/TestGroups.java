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

import org.junit.Test;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.ErrorBuffer;

import static org.junit.Assert.assertEquals;

public class TestGroups extends BaseTest {
    @Test public void testSimpleGroup() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = "foo";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupWithTwoTemplates() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir, "b.st", b);
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
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("subdir/b");
        String expected = "foobar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
        st2 = group.getInstanceOf("subdir/b"); // should work with / in front too
        expected = "bar";
        result = st2.render();
        assertEquals(expected, result);
    }

    @Test public void testAbsoluteTemplateRef() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a =
            "a(x) ::= << <subdir/b()> >>\n";
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= <<bar>>\n";
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupFileInDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<\n"+
            "foo\n"+
            ">>\n";
        writeFile(dir, "a.st", a);
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"duh\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("group/b");
        ST st3 = group.getInstanceOf("group/c");
        String expected = "foobarduh";
        String result = st1.render()+st2.render()+st3.render();
        assertEquals(expected, result);
    }

    @Test public void testSubSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir+"/sub1/sub2", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("sub1/sub2/b");
        String expected = "foobar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupFileInSubDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<\n"+
            "foo\n"+
            ">>\n";
        writeFile(dir, "a.st", a);
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

    @Test public void testRefToAnotherTemplateInSameGroup() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir, "a.st", a);
        writeFile(dir, "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRefToAnotherTemplateInSameSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a = "a() ::= << <subdir/b()> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir+"/subdir", "a.st", a);
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("subdir/a");
        st.impl.dump();
        String expected = " bar ";
        String result = st.render();
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
		String expected = "context [g] 1:1 attribute z isn't defined"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

	@Test public void testMissingNamedArg() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g() ::= \"<f(x={a},{b})>\"";
		   //012345678901234567

		writeFile(dir, "group.stg", groupFile);
		STGroupFile group = new STGroupFile(dir+"/group.stg");
		ErrorBuffer errors = new ErrorBuffer();
		group.setListener(errors);
		group.load();
		String expected = "group.stg 2:28: mismatched input '{' expecting ID"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

	@Test public void testNamedArgsNotAllowInIndirectInclude() throws Exception {
		String dir = getRandomDir();
		String groupFile =
			"f(x,y) ::= \"<x><y>\"\n" +
			"g(name) ::= \"<(name)(x={a},y={b})>\"";
		   //0123456789012345678901234567890
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

    // test fully-qualified template refs

    @Test public void testFullyQualifiedGetInstanceOf() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = "foo";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFullyQualifiedTemplateRef() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a = "a() ::= << <subdir/b()> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir+"/subdir", "a.st", a);
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("subdir/a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFullyQualifiedTemplateRef2() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        String a =
            "a(x) ::= << <group/b()> >>\n";
        writeFile(dir, "a.st", a);
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"<a()>\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("group/c"); // invokes /a
        String expected = " bar  bar ";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
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

}
