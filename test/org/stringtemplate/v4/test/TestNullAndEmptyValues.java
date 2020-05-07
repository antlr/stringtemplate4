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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestNullAndEmptyValues extends BaseTest {
    public static class T {
        String template;
        Object x;
        String expecting;

        String result;

        public T(String template, Object x, String expecting) {
            this.template = template;
            this.x = x;
            this.expecting = expecting;
        }

        public T(T t) {
            this.template = t.template;
            this.x = t.x;
            this.expecting = t.expecting;
        }

        @Override
        public String toString() {
            String s = x.toString();
            if ( x.getClass().isArray() ) {
                s = Arrays.toString((Object[])x);
            }
            return "('"+template+"', "+s+", '"+expecting+"', '"+result+"')";
        }
    }

    final static Object UNDEF = "<undefined>";
    final static List<?> LIST0 = new ArrayList<Object>();

    final static T[] singleValuedTests = new T[] {
        new T("<x>", UNDEF, ""),
        new T("<x>", null, ""),
        new T("<x>", "", ""),
        new T("<x>", LIST0, ""),

        new T("<x:t()>", UNDEF, ""),
        new T("<x:t()>", null, ""),
        new T("<x:t()>", "", ""),
        new T("<x:t()>", LIST0, ""),

        new T("<x; null={y}>", UNDEF, "y"),
        new T("<x; null={y}>", null, "y"),
        new T("<x; null={y}>", "", ""),
        new T("<x; null={y}>", LIST0, ""),

        new T("<x:t(); null={y}>", UNDEF, "y"),
        new T("<x:t(); null={y}>", null, "y"),
        new T("<x:t(); null={y}>", "", ""),
        new T("<x:t(); null={y}>", LIST0, ""),

        new T("<if(x)>y<endif>", UNDEF, ""),
        new T("<if(x)>y<endif>", null, ""),
        new T("<if(x)>y<endif>", "", "y"),
        new T("<if(x)>y<endif>", LIST0, ""),

        new T("<if(x)>y<else>z<endif>", UNDEF, "z"),
        new T("<if(x)>y<else>z<endif>", null, "z"),
        new T("<if(x)>y<else>z<endif>", "", "y"),
        new T("<if(x)>y<else>z<endif>", LIST0, "z"),
    };

    final static String[] LISTa = {"a"};
    final static String[] LISTab = {"a", "b"};
    final static String[] LISTnull = {null};
    final static String[] LISTa_null = {"a",null};
    final static String[] LISTnull_b = {null,"b"};
    final static String[] LISTa_null_b = {"a",null,"b"};

    final static T[] multiValuedTests = new T[] {
        new T("<x>", LIST0,        ""),
        new T("<x>", LISTa,        "a"),
        new T("<x>", LISTab,       "ab"),
        new T("<x>", LISTnull,     ""),
        new T("<x>", LISTnull_b,   "b"),
        new T("<x>", LISTa_null,   "a"),
        new T("<x>", LISTa_null_b, "ab"),

        new T("<x; null={y}>", LIST0,        ""),
        new T("<x; null={y}>", LISTa,        "a"),
        new T("<x; null={y}>", LISTab,       "ab"),
        new T("<x; null={y}>", LISTnull,     "y"),
        new T("<x; null={y}>", LISTnull_b,   "yb"),
        new T("<x; null={y}>", LISTa_null,   "ay"),
        new T("<x; null={y}>", LISTa_null_b, "ayb"),

        new T("<x; separator={,}>", LIST0,        ""),
        new T("<x; separator={,}>", LISTa,        "a"),
        new T("<x; separator={,}>", LISTab,       "a,b"),
        new T("<x; separator={,}>", LISTnull,     ""),
        new T("<x; separator={,}>", LISTnull_b,   "b"),
        new T("<x; separator={,}>", LISTa_null,   "a"),
        new T("<x; separator={,}>", LISTa_null_b, "a,b"),

        new T("<x; null={y}, separator={,}>", LIST0,        ""),
        new T("<x; null={y}, separator={,}>", LISTa,        "a"),
        new T("<x; null={y}, separator={,}>", LISTab,       "a,b"),
        new T("<x; null={y}, separator={,}>", LISTnull,     "y"),
        new T("<x; null={y}, separator={,}>", LISTnull_b,   "y,b"),
        new T("<x; null={y}, separator={,}>", LISTa_null,   "a,y"),
        new T("<x; null={y}, separator={,}>", LISTa_null_b, "a,y,b"),

        new T("<if(x)>y<endif>", LIST0,        ""),
        new T("<if(x)>y<endif>", LISTa,        "y"),
        new T("<if(x)>y<endif>", LISTab,       "y"),
        new T("<if(x)>y<endif>", LISTnull,     "y"),
        new T("<if(x)>y<endif>", LISTnull_b,   "y"),
        new T("<if(x)>y<endif>", LISTa_null,   "y"),
        new T("<if(x)>y<endif>", LISTa_null_b, "y"),

        new T("<x:{it | <it>}>", LIST0,        ""),
        new T("<x:{it | <it>}>", LISTa,        "a"),
        new T("<x:{it | <it>}>", LISTab,       "ab"),
        new T("<x:{it | <it>}>", LISTnull,     ""),
        new T("<x:{it | <it>}>", LISTnull_b,   "b"),
        new T("<x:{it | <it>}>", LISTa_null,   "a"),
        new T("<x:{it | <it>}>", LISTa_null_b, "ab"),

        new T("<x:{it | <it>}; null={y}>", LIST0,        ""),
        new T("<x:{it | <it>}; null={y}>", LISTa,        "a"),
        new T("<x:{it | <it>}; null={y}>", LISTab,       "ab"),
        new T("<x:{it | <it>}; null={y}>", LISTnull,     "y"),
        new T("<x:{it | <it>}; null={y}>", LISTnull_b,   "yb"),
        new T("<x:{it | <it>}; null={y}>", LISTa_null,   "ay"),
        new T("<x:{it | <it>}; null={y}>", LISTa_null_b, "ayb"),

        new T("<x:{it | <i>.<it>}>", LIST0,        ""),
        new T("<x:{it | <i>.<it>}>", LISTa,        "1.a"),
        new T("<x:{it | <i>.<it>}>", LISTab,       "1.a2.b"),
        new T("<x:{it | <i>.<it>}>", LISTnull,     ""),
        new T("<x:{it | <i>.<it>}>", LISTnull_b,   "1.b"),
        new T("<x:{it | <i>.<it>}>", LISTa_null,   "1.a"),
        new T("<x:{it | <i>.<it>}>", LISTa_null_b, "1.a2.b"),

        new T("<x:{it | <i>.<it>}; null={y}>", LIST0,        ""),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTa,        "1.a"),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTab,       "1.a2.b"),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTnull,     "y"),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTnull_b,   "y1.b"),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTa_null,   "1.ay"),
        new T("<x:{it | <i>.<it>}; null={y}>", LISTa_null_b, "1.ay2.b"),

        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LIST0,        ""),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa,        "x"),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTab,       "xx"),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTnull,     "z"),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTnull_b,   "zx"),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa_null,   "xz"),
        new T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa_null_b, "xzx"),

        new T("<x:t():u(); null={y}>", LIST0,        ""),
        new T("<x:t():u(); null={y}>", LISTa,        "a"),
        new T("<x:t():u(); null={y}>", LISTab,       "ab"),
        new T("<x:t():u(); null={y}>", LISTnull,     "y"),
        new T("<x:t():u(); null={y}>", LISTnull_b,   "yb"),
        new T("<x:t():u(); null={y}>", LISTa_null,   "ay"),
        new T("<x:t():u(); null={y}>", LISTa_null_b, "ayb")
    };

    final static T[] listTests = new T[] {
        new T("<[]>", UNDEF, ""),
        new T("<[]; null={x}>", UNDEF, ""),
        new T("<[]:{it | x}>", UNDEF, ""),
        new T("<[[],[]]:{it| x}>", UNDEF, ""),
        new T("<[]:t()>", UNDEF, ""),
    };

    @Test public void testSingleValued() {
        List<T> failed = testMatrix(singleValuedTests);
        List<T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    @Test public void testMultiValued() {
        List<T> failed = testMatrix(multiValuedTests);
        List<T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    @Test public void testLists() {
        List<T> failed = testMatrix(listTests);
        List<T> expecting = Collections.emptyList();
        assertArrayEquals("failed tests "+failed,
                          expecting.toArray(), failed.toArray());
    }

    public List<T> testMatrix(T[] tests) {
        List<T> failed = new ArrayList<T>();
        for (T t : tests) {
            T test = new T(t); // dup since we might mod with result
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


    @Test public void testSeparatorWithNullFirstValue() {
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

    @Test public void testTemplateAppliedToNullIsEmpty() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:t()>");
        group.defineTemplate("t", "x", "<x>");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTemplateAppliedToMissingValueIsEmpty() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:t()>");
        group.defineTemplate("t", "x", "<x>");
        ST st = group.getInstanceOf("test");
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNull2ndValue() {
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

    @Test public void testSeparatorWithNullLastValue() {
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

    @Test public void testSeparatorWithTwoNullValuesInRow() {
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

    @Test public void testTwoNullValues() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; null=\"x\">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        st.add("name", null);
        String expected = "hi xx!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNullListItemNotCountedForIteratorIndex() {
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

    @Test public void testSizeZeroButNonNullListGetsNoOutput() {
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

    @Test public void testNullListGetsNoOutput() {
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

    @Test public void testEmptyListGetsNoOutput() {
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

    @Test public void testMissingDictionaryValue() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<m.foo>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<Object, Object>());
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testMissingDictionaryValue2() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<if(m.foo)>[<m.foo>]<endif>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<Object, Object>());
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testMissingDictionaryValue3() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "m", "<if(m.foo)>[<m.foo>]<endif>");
        ST t = group.getInstanceOf("test");
        t.add("m", new HashMap<String, Object>() {{put("foo",null);}});
        String expecting="";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void TestSeparatorEmittedForEmptyIteratorValue() {
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

    @Test public void TestSeparatorEmittedForEmptyIteratorValu3333e() {
        String dir = getRandomDir();
        String groupFile =
            "filter ::= [\"b\":, default: key]\n" +
            "t() ::= <%<[\"a\", \"b\", \"c\", \"b\"]:{it | <filter.(it)>}; separator=\",\">%>\n";
        writeFile(dir, "group.stg", groupFile);
        STGroupFile group = new STGroupFile(dir+"/group.stg");

        ST st = group.getInstanceOf("t");
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw));
        String result = sw.toString();
        String expecting = "a,,c,";
        assertEquals(expecting, result);
    }



    @Test public void TestSeparatorEmittedForEmptyIteratorValue2() {
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
