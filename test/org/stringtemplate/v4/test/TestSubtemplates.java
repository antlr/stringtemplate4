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

import org.junit.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestSubtemplates extends BaseTest {

    @Test public void testSimpleIteration()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n|<n>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMapIterationIsByKeys()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "emails", "<emails:{n|<n>}>!");
        ST st = group.getInstanceOf("test");
        Map<String,String> emails = new LinkedHashMap<String,String>();
        emails.put("parrt", "Ter");
        emails.put("tombu", "Tom");
        emails.put("dmose", "Dan");
        st.add("emails", emails);
        String expected = "parrttombudmose!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSimpleIterationWithArg()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n | <n>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNestedIterationWithArg()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "users", "<users:{u | <u.id:{id | <id>=}><u.name>}>!");
        ST st = group.getInstanceOf("test");
        st.add("users", new TestCoreBasics.User(1, "parrt"));
        st.add("users", new TestCoreBasics.User(2, "tombu"));
        st.add("users", new TestCoreBasics.User(3, "sri"));
        String expected = "1=parrt2=tombu3=sri!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplateAsDefaultArg()
    {
        String templates =
            "t(x,y={<x:{s|<s><s>}>}) ::= <<\n" +
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
            "y: aa";
        String result = b.render();
        assertEquals(expecting, result);
    }

    @Test public void testParallelAttributeIteration()
    {
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

    @Test public void testParallelAttributeIterationWithNullValue()
    {
        ST e = new ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        e.add("phones", new ArrayList<String>() {{add("1"); add(null); add("3");}});
        e.add("salaries", "big");
        e.add("salaries", "huge");
        e.add("salaries", "enormous");
        String expecting = "Ter@1: big"+newline+
                           "Tom@: huge"+newline+
                           "Sriram@3: enormous"+newline;
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationHasI()
    {
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

    @Test public void testParallelAttributeIterationWithDifferentSizes()
    {
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

    @Test public void testParallelAttributeIterationWithSingletons()
    {
        ST e = new ST(
                "<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("phones", "1");
        e.add("salaries", "big");
        String expecting = "Ter@1: big";
        assertEquals(expecting, e.render());
    }

    @Test public void testParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo()
    {
        String templates =
                "page(names,phones,salaries) ::= "+newline+
                "   << <names,phones,salaries:{n,p,s | <value(n)>@<value(p)>: <value(s)>}; separator=\", \"> >>"+newline +
                "value(x) ::= \"<if(!x)>n/a<else><x><endif>\"" +newline;
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

    @Test public void testEvalSTIteratingSubtemplateInSTFromAnotherGroup()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup innerGroup = new STGroup();
        innerGroup.setListener(errors);
        innerGroup.defineTemplate("test", "m", "<m:samegroup()>");
        innerGroup.defineTemplate("samegroup", "x", "hi ");
        ST st = innerGroup.getInstanceOf("test");
        st.add("m", new int[] {1,2,3});

        STGroup outerGroup = new STGroup();
        outerGroup.defineTemplate("errorMessage", "x", "<x>");
        ST outerST = outerGroup.getInstanceOf("errorMessage");
        outerST.add("x", st);

        String expected = "hi hi hi ";
        String result = outerST.render();

        assertEquals(errors.errors.size(), 0); // ignores no such prop errors

        assertEquals(expected, result);
    }

    @Test public void testEvalSTIteratingSubtemplateInSTFromAnotherGroupSingleValue()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup innerGroup = new STGroup();
        innerGroup.setListener(errors);
        innerGroup.defineTemplate("test", "m", "<m:samegroup()>");
        innerGroup.defineTemplate("samegroup", "x", "hi ");
        ST st = innerGroup.getInstanceOf("test");
        st.add("m", 10);

        STGroup outerGroup = new STGroup();
        outerGroup.defineTemplate("errorMessage", "x", "<x>");
        ST outerST = outerGroup.getInstanceOf("errorMessage");
        outerST.add("x", st);

        String expected = "hi ";
        String result = outerST.render();

        assertEquals(errors.errors.size(), 0); // ignores no such prop errors

        assertEquals(expected, result);
    }

    @Test public void testEvalSTFromAnotherGroup()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup innerGroup = new STGroup();
        innerGroup.setListener(errors);
        innerGroup.defineTemplate("bob", "inner");
        ST st = innerGroup.getInstanceOf("bob");

        STGroup outerGroup = new STGroup();
        outerGroup.setListener(errors);
        outerGroup.defineTemplate("errorMessage", "x", "<x>");
        outerGroup.defineTemplate("bob", "outer"); // should not be visible to test() in innerGroup
        ST outerST = outerGroup.getInstanceOf("errorMessage");
        outerST.add("x", st);

        String expected = "inner";
        String result = outerST.render();

        assertEquals(errors.errors.size(), 0); // ignores no such prop errors

        assertEquals(expected, result);
    }

}
