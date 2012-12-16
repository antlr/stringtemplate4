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

import static org.junit.Assert.*;

public class TestIndirectionAndEarlyEval extends BaseTest {
    @Test public void testEarlyEval() throws Exception {
        String template = "<(name)>";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
    }

	@Test public void testIndirectTemplateInclude() throws Exception {
		STGroup group = new STGroup();
		group.defineTemplate("foo", "bar");
		String template = "<(name)()>";
		group.defineTemplate("test", "name", template);
		ST st = group.getInstanceOf("test");
		st.add("name", "foo");
		String expected = "bar";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testIndirectTemplateIncludeWithArgs() throws Exception {
		STGroup group = new STGroup();
		group.defineTemplate("foo", "x,y", "<x><y>");
		String template = "<(name)({1},{2})>";
		group.defineTemplate("test", "name", template);
		ST st = group.getInstanceOf("test");
		st.add("name", "foo");
		String expected = "12";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test
	public void testIndirectCallWithPassThru() throws Exception {
		// pass-through for dynamic template invocation is not supported by the
		// bytecode representation
		writeFile(tmpdir, "t.stg",
			"t1(x) ::= \"<x>\"\n" +
			"main(x=\"hello\",t=\"t1\") ::= <<\n" +
			"<(t)(...)>\n" +
			">>");
		STGroup group = new STGroupFile(tmpdir + "/t.stg");
		ErrorBuffer errors = new ErrorBuffer();
		group.setListener(errors);
		ST st = group.getInstanceOf("main");
		assertEquals("t.stg 2:34: mismatched input '...' expecting RPAREN" + newline, errors.toString());
		assertNull(st);
	}

    @Test public void testIndirectTemplateIncludeViaTemplate() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "bar");
        group.defineTemplate("tname", "foo");
        String template = "<(tname())()>";
        group.defineTemplate("test", "name", template);
        ST st = group.getInstanceOf("test");
        String expected = "bar";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectProp() throws Exception {
        String template = "<u.(propname)>: <u.name>";
        ST st = new ST(template);
        st.add("u", new TestCoreBasics.User(1, "parrt"));
        st.add("propname", "id");
        String expected = "1: parrt";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("a", "x", "[<x>]");
        group.defineTemplate("test", "names,templateName", "hi <names:(templateName)()>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        st.add("templateName", "a");
        String expected =
            "hi [Ter][Tom][Sumana]!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNonStringDictLookup() throws Exception {
        String template = "<m.(intkey)>";
        ST st = new ST(template);
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(36, "foo");
        st.add("m", m);
        st.add("intkey", 36);
        String expected = "foo";
        String result = st.render();
        assertEquals(expected, result);
    }
}
