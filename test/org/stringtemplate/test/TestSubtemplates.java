package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

public class TestSubtemplates {
    @Test public void testNullAttr() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{<it>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "hi TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }
}
