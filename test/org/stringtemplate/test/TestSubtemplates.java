package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.ErrorManager;
import org.stringtemplate.STGroupFile;

import java.util.ArrayList;
import java.io.StringReader;

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
        String expecting = "0. Ter@1: big"+newline+"1. Tom@2: huge"+newline;
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

    @Test public void testParallelAttributeIterationWithMismatchArgListSizes() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);
        ST e = new ST(
                "<names,phones,salaries:{n,p | <n>@<p>}; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        e.add("salaries", "big");
        String expecting = "Ter@1, Tom@2";
        assertEquals(expecting, e.render());
        String errorExpecting = "template _sub13's actual and formal argument count does not match in context anonymous"+newline;
        assertEquals(errorExpecting, errors.toString());
    }

    @Test public void testParallelAttributeIterationWithMissingArgs() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);
        ST e = new ST(
                "<names,phones,salaries:{<n>@<p>}; separator=\", \">"
            );
        e.add("names", "Tom");
        e.add("phones", "2");
        e.add("salaries", "big");
        e.render(); // generate the error
        String errorExpecting = "missing argument definitions in context anonymous"+newline;
        assertEquals(errorExpecting, errors.toString());
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
        String expecting = "Ter@1: big, Tom@2: n/a, Sriram@n/a: n/a";
        assertEquals(expecting, p.render());
    }
}
