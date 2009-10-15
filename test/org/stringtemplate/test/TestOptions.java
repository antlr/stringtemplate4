package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

import java.util.List;
import java.util.ArrayList;

public class TestOptions {
    @Test public void testSeparator() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testAttrSeparator() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator=sep>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ", ");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeSeparator() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "|");
        group.defineTemplate("test", "hi <name; separator=foo()>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ", ");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter|Tom|Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplateSeparator() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator={<sep> _}>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ",");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, _Tom, _Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNullFirstValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNull2ndValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator=\", \">!");
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
        group.defineTemplate("test", "hi <name; separator=\", \">!");
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
        group.defineTemplate("test", "hi <name; separator=\", \">!");
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

    @Test public void testSeparatorWithNullFirstValueAndNullOption() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; null=\"n/a\", separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi n/a, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNull2ndValueAndNullOption() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; null=\"n/a\", separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", "Sumana");
        String expected = "hi Ter, n/a, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNullValueAndNullOption() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<name; null=\"n/a\">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        String expected = "n/a!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMissingValueAndNullOption() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<name; null=\"n/a\">!");
        ST st = group.getInstanceOf("test");
        String expected = "!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMissingValuesInListAndNullOption() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <[x,y]; null=\"n/a\", separator=\", \">!");
        ST st = group.getInstanceOf("test");
        String expected = "hi !";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorDoesntApplyToNestedTemplate() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "<zippo>");
        group.defineTemplate("test", "hi <foo(); null=\"n/a\">!");
        ST st = group.getInstanceOf("test");
        st.code.dump();
        st.add("zippo", null);
        String expected = "hi !";
        String result = st.render();
        assertEquals(expected, result);
    }
}
