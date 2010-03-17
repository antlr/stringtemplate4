/*
 [The "BSD licence"]
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
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.ErrorManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestSubtemplates extends BaseTest {

    @Test public void testSimpleIteration() throws Exception {
        org.stringtemplate.v4.STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{<it>}>!");
        org.stringtemplate.v4.ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSimpleIterationWithArg() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n | <n>}>!");
        org.stringtemplate.v4.ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void test_it_NotDefinedWithArg() throws Exception {
        org.stringtemplate.v4.STGroup group = new org.stringtemplate.v4.STGroup();
        group.defineTemplate("test", "<names:{n | <it>}>!");
        org.stringtemplate.v4.ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void test_it_NotDefinedWithArgSingleValue() throws Exception {
        org.stringtemplate.v4.STGroup group = new org.stringtemplate.v4.STGroup();
        group.defineTemplate("test", "<names:{n | <it>}>!");
        org.stringtemplate.v4.ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        String expected = "!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNestedIterationWithArg() throws Exception {
        STGroup group = new org.stringtemplate.v4.STGroup();
        group.defineTemplate("test", "<users:{u | <u.id:{id | <id>=}><u.name>}>!");
        org.stringtemplate.v4.ST st = group.getInstanceOf("test");
        st.add("users", new TestCoreBasics.User(1, "parrt"));
        st.add("users", new TestCoreBasics.User(2, "tombu"));
        st.add("users", new TestCoreBasics.User(3, "sri"));
        String expected = "1=parrt2=tombu3=sri!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testParallelAttributeIteration() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        e.add("salaries", "big");
        e.add("salaries", "huge");
        String expecting = "Ter@1: big"+newline+"Tom@2: huge"+newline;
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationWithNullValue() throws Exception {
        ST e = new org.stringtemplate.v4.ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        e.add("phones", new ArrayList() {{add("1"); add(null); add("3");}});
        e.add("salaries", "big");
        e.add("salaries", "huge");
        e.add("salaries", "enormous");
        String expecting = "Ter@1: big"+newline+
                           "Tom@: huge"+newline+
                           "Sriram@3: enormous"+newline;
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationHasI() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<names,phones,salaries:{n,p,s | <i0>. <n>@<p>: <s>\n}>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        e.add("salaries", "big");
        e.add("salaries", "huge");
        String expecting =
            "0. Ter@1: big"+newline+
            "1. Tom@2: huge"+newline;
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationWithDifferentSizes() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        e.add("phones", "1");
        e.add("phones", "2");
        e.add("salaries", "big");
        String expecting = "Ter@1: big, Tom@2: , Sriram@: ";
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationWithSingletons() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("phones", "1");
        e.add("salaries", "big");
        String expecting = "Ter@1: big";
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo() throws Exception {
        String templates =
                "page(names,phones,salaries) ::= "+newline+
                "	<< <names,phones,salaries:{n,p,s | <value(n)>@<value(p)>: <value(s)>}; separator=\", \"> >>"+newline +
                "value(x=\"n/a\") ::= \"<x>\"" +newline;
        writeFile(tmpdir, "g.stg", templates);

        STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir+"/g.stg");
        org.stringtemplate.v4.ST p = group.getInstanceOf("page");
        p.add("names", "Ter");
        p.add("names", "Tom");
        p.add("names", "Sriram");
        p.add("phones", "1");
        p.add("phones", "2");
        p.add("salaries", "big");
        String expecting = " Ter@1: big, Tom@2: n/a, Sriram@n/a: n/a ";
        assertEquals(expecting, p.render());
    }

	@Test public void testBugFromANTLRv4() throws Exception {
		class OO { public LinkedHashMap<Integer,List<String>> m; }

		ErrorBuffer errors = new ErrorBuffer();
		ErrorManager.setErrorListener(errors);
		STGroup group = new STGroup();
		group.defineTemplate("test",
							 "<o.m.keys:{alt | alt <alt> via tokens <o.m.(alt); separator=\",\"><\\n>}>");
		ST st = group.getInstanceOf("test");
		OO o = new OO();
		o.m = new LinkedHashMap<Integer,List<String>>();
		o.m.put(1, new ArrayList<String>() {{add("foo");}});
		o.m.put(2, new ArrayList<String>() {{add("bar");}});
		st.add("o", o);
		String expected = "alt 1 via tokens foo\n" +
						  "alt 2 via tokens bar\n";
		String result = st.render();

		assertEquals(errors.errors.size(), 0); // ignores no such prop errors

		assertEquals(expected, result);
	}


}
