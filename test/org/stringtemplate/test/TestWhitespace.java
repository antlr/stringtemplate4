package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.AutoIndentWriter;

import java.io.StringWriter;

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
        ST t = new ST(
            "<users>\n" +
            "end\n");
        String expecting="end\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyLineWithIndent() throws Exception {
        ST t = new ST(
            "begin\n" +
            "    \n" +
            "end\n");
        String expecting="begin\n\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineByItselfGetsNoOutput() throws Exception {
        ST t = new ST(
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
        ST t = new ST(
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
        ST t = new ST(
            "begin\n"+
            "  <name>\n"+
            "	<users><users>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFExpr() throws Exception {
        ST t = new ST(
            "begin\n"+
            "<if(x)><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIndentedIFExpr() throws Exception {
        ST t = new ST(
            "begin\n"+
            "    <if(x)><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFElseExpr() throws Exception {
        ST t = new ST(
            "begin\n"+
            "<if(users)><else><endif>\n"+
            "end\n");
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFOnMultipleLines() throws Exception {
        ST t = new ST(
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
        ST t = new ST(
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

    @Test public void testLineBreak() throws Exception {
        ST st = new ST(
                "Foo <\\\\>"+newline+
                "  \t  bar" +newline
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo bar\n";     // expect \n in output
        assertEquals(expecting, result);
    }

    @Test public void testLineBreak2() throws Exception {
        ST st = new ST(
                "Foo <\\\\>       "+newline+
                "  \t  bar" +newline
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo bar\n";
        assertEquals(expecting, result);
    }

    @Test public void testLineBreakNoWhiteSpace() throws Exception {
        ST st = new ST(
                "Foo <\\\\>"+newline+
                "bar\n"
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo bar\n";
        assertEquals(expecting, result);
    }

    @Test public void testNewlineNormalizationInTemplateString() throws Exception {
        ST st = new ST(
                "Foo\r\n"+
                "Bar\n"
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo\nBar\n";     // expect \n in output
        assertEquals(expecting, result);
    }

    @Test public void testNewlineNormalizationInTemplateStringPC() throws Exception {
        ST st = new ST(
                "Foo\r\n"+
                "Bar\n"
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\r\n")); // force \r\n as newline
        String result = sw.toString();
        String expecting ="Foo\r\nBar\r\n";     // expect \r\n in output
        assertEquals(expecting, result);
    }

    @Test public void testNewlineNormalizationInAttribute() throws Exception {
        ST st = new ST(
                "Foo\r\n"+
                "<name>\n"
                );
        st.add("name", "a\nb\r\nc");
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo\na\nb\nc\n";     // expect \n in output
        assertEquals(expecting, result);
    }
}
