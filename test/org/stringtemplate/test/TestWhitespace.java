package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.STErrorListener;

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

    @Test public void testEmptyExprAsFirstLineGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "<users>\n" +
            "end\n");
        String expecting="end";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineByItselfGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<name>\n"+
            "<users>\n"+
            "<users>\n"+
            "end\n");
        String expecting="begin\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineWithIndentGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "  <name>\n"+
            "	<users>\n"+
            "	<users>\n"+
            "end\n");
        String expecting="begin\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }
}
