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

    @Test public void testSizeZeroOnLineWithIFExpr() throws Exception {
        STGroup group =
                new STGroup("test");
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n"+
            "<if(users)><endif>\n"+
            "end\n");
        String expecting="begin\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineWithIFElseExpr() throws Exception {
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

    @Test public void testSizeZeroOnLineWithIFOnMultipleLines() throws Exception {
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
        t.code.dump();
        String expecting="begin\nbar\nend";
        String result = t.render();
        assertEquals(expecting, result);
    }

    /*
    if we wrote n>0 char, emit \n
    if prev instr was NEWLINE also (no string and no <...> tag), emit \n
    if no string but was a <...> tag, don't emit \n
    <if(x)><else><endif>
    
    <if(x)>
    <x>
    <else>
    <y>
    <endif>

<if(x)>foo<endif>

<if(TREE_PARSER)>
import org.antlr.runtime.tree.*;
<endif>

<if(x)>
<if(y)>
    foo
<else>
    bar</n>
<endif>
<endif>
     */
}
