package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;

public class TestWhitespace extends BaseTest {
    @Test public void testTrimmedSubtemplates() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n |    <n> }>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }
}
