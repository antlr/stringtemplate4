package org.stringtemplate.test;

import org.stringtemplate.STGroup;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.ST;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class TestNullAndEmptyValues extends BaseTest {
    @Test public void testSeparatorWithNullFirstValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null); // null is added to list, but ignored in iteration
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

    @Test public void testSizeZeroButNonNullListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        group.defineTemplate("test",
            "begin\n" +
            "<users:{name: <it>}; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        t.add("users", null);
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testNullListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        group.defineTemplate("test",
            "begin\n" +
            "<users:{name: <it>}; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        //t.setAttribute("users", new Duh());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyListGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        group.defineTemplate("test",
            "begin\n" +
            "<users:{name: <it>}; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        t.add("users", new ArrayList());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyListNoIteratorGetsNoOutput() throws Exception {
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        group.defineTemplate("test",
            "begin\n" +
            "<users; separator=\", \">\n" +
            "end\n");
        ST t = group.getInstanceOf("test");
        t.add("users", new ArrayList());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }


}
