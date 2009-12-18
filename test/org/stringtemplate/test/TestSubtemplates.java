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
package org.stringtemplate.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupFile;

import java.util.ArrayList;

public class TestSubtemplates extends BaseTest {

    @Test public void testSimpleIteration() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{<it>}>!");
        ST st = group.getInstanceOf("test");
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
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void test_it_NotDefinedWithArg() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n | <it>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void test_it_NotDefinedWithArgSingleValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n | <it>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        String expected = "!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNestedIterationWithArg() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<users:{u | <u.id:{id | <id>=}><u.name>}>!");
        ST st = group.getInstanceOf("test");
        st.add("users", new TestCoreBasics.User(1, "parrt"));
        st.add("users", new TestCoreBasics.User(2, "tombu"));
        st.add("users", new TestCoreBasics.User(3, "sri"));
        String expected = "1=parrt2=tombu3=sri!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testParallelAttributeIteration() throws Exception {
        ST e = new ST(
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
        ST e = new ST(
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
        ST e = new ST(
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
        ST e = new ST(
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
        ST e = new ST(
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

        STGroup group = new STGroupFile(tmpdir+"/g.stg");
        ST p = group.getInstanceOf("page");
        p.add("names", "Ter");
        p.add("names", "Tom");
        p.add("names", "Sriram");
        p.add("phones", "1");
        p.add("phones", "2");
        p.add("salaries", "big");
        String expecting = " Ter@1: big, Tom@2: n/a, Sriram@n/a: n/a ";
        assertEquals(expecting, p.render());
    }
}
