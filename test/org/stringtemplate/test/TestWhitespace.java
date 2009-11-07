package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.STErrorListener;

public class TestWhitespace extends BaseTest {
    @Test public void testTrimmedSubtemplates() throws Exception {
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

    @Test public void testTrimJustOneWSInSubtemplates() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n |  <n> }>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = " Ter  Tom  Sumana !";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimNewlineInSubtemplates() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n |\n" +
                                     "<n>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testLeaveNewlineOnEndInSubtemplates() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "<names:{n |\n" +
                                     "<n>\n" +
                                     "}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "Ter\nTom\nSumana\n!";
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
        String expecting="end\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyLineWithIndent() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n" +
            "    \n" +
            "end\n");
        String expecting="begin\n\nend\n";
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
        String expecting="begin\nend\n";
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
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineWithMultipleExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "  <name>\n"+
            "	<users><users>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<if(x)><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIndentedIFExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "    <if(x)><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFElseExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<if(users)><else><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFOnMultipleLines() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<if(users)>\n" +
            "foo\n" +
            "<else>\n" +
            "bar\n" +
            "<endif>\n"+
            "end\n");
        String expecting="begin\nbar\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testNestedIFOnMultipleLines() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<if(x)>\n" +
            "<if(y)>\n" +
            "foo\n" +
            "<else>\n" +
            "bar\n" +
            "<endif>\n"+
            "<endif>\n"+
            "end\n");
        t.add("x", "x");
        String expecting="begin\nbar\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }


}
