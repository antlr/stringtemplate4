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

    @Test public void testEmptyLineWithIndent() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n" +
            "    \n" +
            "end\n");
        String expecting="begin\n\nend";
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
        String expecting="begin\nend";
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
        String expecting="begin\nend";
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
        String expecting="begin\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIndentedIFWithValueExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "    <if(x)>foo<endif>\n"+
            "end\n");
        t.add("x", "x");
        String expecting="begin\n    foo\nend";
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
        String expecting="begin\nend";
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
        String expecting="begin\nbar\nend";
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
        String expecting="begin\nbar\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFWithIndentOnMultipleLines() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "   <if(x)>\n" +
            "   foo\n" +
            "   <else>\n" +
            "   bar\n" +
            "   <endif>\n"+
            "end\n");
        String expecting="begin\n   bar\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFWithIndentAndExprOnMultipleLines() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "   <if(x)>\n" +
            "   <x>\n" +
            "   <else>\n" +
            "   <y>\n" +
            "   <endif>\n"+
            "end\n");
        t.add("y", "y");
        String expecting="begin\n   y\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testNestedIFWithIndentOnMultipleLines() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "   <if(x)>\n" +
            "      <if(y)>\n" +
            "      foo\n" +
            "      <endif>\n" +
            "   <else>\n" +
            "      <if(z)>\n" +
            "      foo\n" +
            "      <endif>\n" +
            "   <endif>\n"+
            "end\n");
        t.add("x", "x");
        t.add("y", "y");
        t.code.dump();
        String expecting="begin\n      foo\nend"; // double indent
        String result = t.render();
        assertEquals(expecting, result);
    }

}
