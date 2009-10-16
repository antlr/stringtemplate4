package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;

import java.util.List;
import java.util.ArrayList;

public class TestFunctions {
    @Test public void testFirst() throws Exception {
        String template = "<first(names)>";
        ST st = new ST(template);
        List names = new ArrayList() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testLength() throws Exception {
        String template = "<length(names)>";
        ST st = new ST(template);
        List names = new ArrayList() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "2";
        String result = st.render();
        assertEquals(expected, result);
    }
}
