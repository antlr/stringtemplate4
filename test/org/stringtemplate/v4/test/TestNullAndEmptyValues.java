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
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.test.classT.T;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestNullAndEmptyValues extends classT {


    @Test public void testSingleValued() throws Exception {
        List<classT.T> failed = testMatrix(singleValuedTests);
        List<classT.T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    @Test public void testMultiValued() throws Exception {
        List<classT.T> failed = testMatrix(multiValuedTests);
        List<classT.T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    @Test public void testLists() throws Exception {
        List<classT.T> failed = testMatrix(listTests);
        List<classT.T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    public List<classT.T> testMatrix(classT.T[] tests) throws Exception {
        List<classT.T> failed = new ArrayList<classT.T>();
        for (classT.T t : tests) {
            classT.T test = new classT.T(t); // dup since we might mod with result
            STGroup group = new STGroup();
//          System.out.println("running "+test);
            group.defineTemplate("t", "x", "<x>");
            group.defineTemplate("u", "x", "<x>");
            group.defineTemplate("test", "x", test.template);
            ST st = group.getInstanceOf("test");
            if ( test.x!=UNDEF ) {
                st.add("x", test.x);
            }
            String result = st.render();
            if ( !result.equals(test.expecting) ) {
                test.result = result;
                failed.add(test);
            }
        }
        return failed;
    }


    @Test public void testSeparatorWithNullFirstValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null); // null is added to list, but ignored in iteration
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTemplateAppliedToNullIsEmpty() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:t()>");
        group.defineTemplate("t", "x", "<x>");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTemplateAppliedToMissingValueIsEmpty() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:t()>");
        group.defineTemplate("t", "x", "<x>");
        ST st = group.getInstanceOf("test");
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNull2ndValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", "Sumana");
        String expected = "hi Ter, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNullLastValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", null);
        String expected = "hi Ter, Tom!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithTwoNullValuesInRow() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", null);
        st.add("name", null);
        st.add("name", "Sri");
        String expected = "hi Ter, Tom, Sri!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTwoNullValues() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; null=\"x\">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        st.add("name", null);
        String expected = "hi xx!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNullListItemNotCountedForIteratorIndex() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:{n | <i>:<n>}>");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", null);
        st.add("name", "Jesse");
        String expected = "1:Ter2:Jesse";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSizeZeroButNonNullListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "users",
            "begin\n" +
            "<users>\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        t.add("users", null);
        String expecting="begin"+newline+"end";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testNullListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "users",
            "begin\n" +
            "<users:{u | name: <u>}; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        String expecting="begin"+newline+"end";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "users",
            "begin\n" +
            "<users:{u | name: <u>}; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        t.add("users", new ArrayList<Object>());
        String expecting="begin"+newline+"end";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testMissingDictionaryValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<m.foo>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<Object, Object>());
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testMissingDictionaryValue2() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<if(m.foo)>[<m.foo>]<endif>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<Object, Object>());
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testMissingDictionaryValue3() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<if(m.foo)>[<m.foo>]<endif>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<String, Object>() {{put("foo",null);}});
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void TestSeparatorEmittedForEmptyIteratorValue()
        throws IOException
    {
        ST st = new ST(
            "<values:{v|<if(v)>x<endif>}; separator=\" \">"
        );
        st.add("values", new boolean[] {true, false, true});
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw));
        String result = sw.toString();
        String expecting = "x  x";
        assertEquals(expecting, result);
    }

    @Test public void TestSeparatorEmittedForEmptyIteratorValu3333e()
        throws IOException
    {
        String dir = BaseTest.getRandomDir();
        String groupFile =
            "filter ::= [\"b\":, default: key]\n" +
            "t() ::= <%<[\"a\", \"b\", \"c\", \"b\"]:{it | <filter.(it)>}; separator=\",\">%>\n";
        BaseTest.writeFile(dir, "group.stg", groupFile);
        STGroupFile group = new STGroupFile(dir+"/group.stg");

        ST st = group.getInstanceOf("t");
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw));
        String result = sw.toString();
        String expecting = "a,,c,";
        assertEquals(expecting, result);
    }



    @Test public void TestSeparatorEmittedForEmptyIteratorValue2()
        throws IOException
    {
        ST st = new ST(
            "<values; separator=\" \">"
        );
        st.add("values", new String[]{"x", "", "y"});
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw));
        String result = sw.toString();
        String expecting = "x  y";
        assertEquals(expecting, result);
    }
}
