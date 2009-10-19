package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

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

}
