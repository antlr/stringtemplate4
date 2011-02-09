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
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestDictionaries extends BaseTest {
	@Test public void testDict() throws Exception {
		String templates =
				"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
				"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
				;
		writeFile(tmpdir, "test.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
		ST st = group.getInstanceOf("var");
		st.add("type", "int");
		st.add("name", "x");
		String expecting = "int x = 0;";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testDictValuesAreTemplates() throws Exception {
        String templates =
                "typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] "+newline+
                "var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
		st.impl.dump();
        st.add("w", "L");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = 0L;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictKeyLookupViaTemplate() throws Exception {
        // Make sure we try rendering stuff to string if not found as regular object
        String templates =
                "typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] "+newline+
                "var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("w", "L");
        st.add("type", new ST("int"));
        st.add("name", "x");
        String expecting = "int x = 0L;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictKeyLookupAsNonToStringableObject() throws Exception {
        // Make sure we try rendering stuff to string if not found as regular object
        String templates =
                "foo(m,k) ::= \"<m.(k)>\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("foo");
        Map<HashableUser,String> m = new HashMap<HashableUser,String>();
        m.put(new HashableUser(99,"parrt"), "first");
        m.put(new HashableUser(172036,"tombu"), "second");
        m.put(new HashableUser(391,"sriram"), "third");
        st.add("m", m);
        st.add("k", new HashableUser(172036,"tombu"));
        String expecting = "second";
        String result = st.render();
        assertEquals(expecting, result);
    }

	@Test public void testDictMissingDefaultValueIsEmpty() throws Exception {
		String templates =
				"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
				"var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
				;
		writeFile(tmpdir, "test.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
		ST st = group.getInstanceOf("var");
		st.add("w", "L");
		st.add("type", "double"); // double not in typeInit map
		st.add("name", "x");
		String expecting = "double x = ;";
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testDictMissingDefaultValueIsEmptyForNullKey() throws Exception {
		String templates =
				"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
				"var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
				;
		writeFile(tmpdir, "test.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
		ST st = group.getInstanceOf("var");
		st.add("w", "L");
		st.add("type", null); // double not in typeInit map
		st.add("name", "x");
		String expecting = " x = ;";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testDictHiddenByFormalArg() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "var(typeInit,type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = ;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictEmptyValueAndAngleBracketStrings() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", \"float\":, \"double\":<<0.0L>>] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "float");
        st.add("name", "x");
        String expecting = "float x = ;";
        String result = st.render();
        assertEquals(expecting, result);
    }

	@Test public void testDictDefaultValue() throws Exception {
		String templates =
				"typeInit ::= [\"int\":\"0\", default:\"null\"] "+newline+
				"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
				;
		writeFile(tmpdir, "test.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
		ST st = group.getInstanceOf("var");
		st.add("type", "UserRecord");
		st.add("name", "x");
		String expecting = "UserRecord x = null;";
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testDictNullKeyGetsDefaultValue() throws Exception {
		String templates =
				"typeInit ::= [\"int\":\"0\", default:\"null\"] "+newline+
				"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
				;
		writeFile(tmpdir, "test.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
		ST st = group.getInstanceOf("var");
		// missing or set to null: st.add("type", null);
		st.add("name", "x");
		String expecting = " x = null;";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testDictEmptyDefaultValue() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", default:] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        ErrorBuffer errors = new ErrorBuffer();
        STGroupFile group = new STGroupFile(tmpdir+"/"+"test.stg");
		group.setListener(errors);
        group.load();
        String expected = "[test.stg 1:33: missing value for key at ']']";
        String result = errors.errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testDictDefaultValueIsKey() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", default:key] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "UserRecord");
        st.add("name", "x");
        String expecting = "UserRecord x = UserRecord;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    /**
     * Test that a map can have only the default entry.
     */
    @Test public void testDictDefaultStringAsKey() throws Exception {
        String templates =
                "typeInit ::= [\"default\":\"foo\"] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "default");
        st.add("name", "x");
        String expecting = "default x = foo;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    /**
     * Test that a map can return a <b>string</b> with the word: default.
     */
    @Test public void testDictDefaultIsDefaultString() throws Exception {
        String templates =
                "map ::= [default: \"default\"] "+newline+
                "t() ::= << <map.(\"1\")> >>"+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("t");
        String expecting = " default ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictViaEnclosingTemplates() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "intermediate(type,name) ::= \"<var(type,name)>\""+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("intermediate");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = 0;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictViaEnclosingTemplates2() throws Exception {
        String templates =
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "intermediate(stuff) ::= \"<stuff>\""+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        writeFile(tmpdir, "test.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"test.stg");
        ST interm = group.getInstanceOf("intermediate");
        ST var = group.getInstanceOf("var");
        var.add("type", "int");
        var.add("name", "x");
        interm.add("stuff", var);
        String expecting = "int x = 0;";
        String result = interm.render();
        assertEquals(expecting, result);
    }

	@Test public void TestAccessDictionaryFromAnonymousTemplate() {
		String dir = tmpdir;
		String g =
			"a() ::= <<[<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>]>>\n" +
			"values ::= [\n" +
			"    \"a\":false,\n" +
			"    default:true\n" +
			"]\n";
		writeFile(dir, "g.stg", g);

		STGroup group = new STGroupFile(tmpdir+"/"+"g.stg");
		ST st = group.getInstanceOf("a");
		String expected = "[foo]";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void TestAccessDictionaryFromAnonymousTemplateInRegion() {
		String dir = tmpdir;
		String g =
			"a() ::= <<[<@r()>]>>\n" +
			"@a.r() ::= <<\n" +
			"<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>\n" +
			">>\n" +
			"values ::= [\n" +
			"    \"a\":false,\n" +
			"    default:true\n" +
			"]\n";
		writeFile(dir, "g.stg", g);

		STGroup group = new STGroupFile(tmpdir+"/"+"g.stg");
		ST st = group.getInstanceOf("a");
		String expected = "[foo]";
		String result = st.render();
		assertEquals(expected, result);
	}

}
