/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.v4.test;

import org.junit.*;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class TestWhitespace extends BaseTest {
    @Test public void testTrimmedSubtemplates()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n | <n>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimmedNewlinesBeforeAfterInTemplate()
    {
        String templates =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        STGroupString group = new STGroupString(templates);
        ST st = group.getInstanceOf("a");
        String expected = "foo";
        String result = st.render();
        assertEquals(expected, result);
    }

    /**
     * This is a regression test for antlr/stringtemplate4#93.
     */
    @Test public void testNoTrimmedNewlinesBeforeAfterInCodedTemplate()
    {
        ST st = new ST(newline + "foo" + newline);
        String expected = newline + "foo" + newline;
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDontTrimJustSpaceBeforeAfterInTemplate()
    {
        String templates =
            "a(x) ::= << foo >>\n";
        STGroupString group = new STGroupString(templates);
        ST st = group.getInstanceOf("a");
        String expected = " foo ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimmedSubtemplatesNoArgs()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "[<foo({ foo })>]");
        group.defineTemplate("foo", "x", "<x>");
        ST st = group.getInstanceOf("test");
        String expected = "[ foo ]";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimmedSubtemplatesArgs()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{x|  foo }>");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = " foo  foo  foo ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimJustOneWSInSubtemplates()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n |  <n> }>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = " Ter  Tom  Sumana !";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrimNewlineInSubtemplates()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n |\n" +
                                     "<n>}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testLeaveNewlineOnEndInSubtemplates()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "<names:{n |\n" +
                                     "<n>\n" +
                                     "}>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "Ter"+newline+"Tom"+newline+"Sumana"+newline+"!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Ignore("will revisit the behavior of indented expressions spanning multiple lines for a future release")
    @Test public void testTabBeforeEndInSubtemplates()
    {
        STGroup group = new STGroup();
        group.defineTemplate("test", "names", "  <names:{n |\n" +
                                     "    <n>\n" +
                                     "  }>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        String expected = "    Ter"+newline+"    Tom"+newline+"    Sumana"+newline+"!";
        String result = st.render();
        st.impl.dump();
        assertEquals(expected, result);
    }

    @Test public void testEmptyExprAsFirstLineGetsNoOutput()
    {
        ST t = new ST(
            "<users>\n" +
            "end\n");
        String expecting="end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyLineWithIndent()
    {
        ST t = new ST(
            "begin\n" +
            "    \n" +
            "end\n");
        String expecting="begin"+newline+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEmptyLine()
    {
        ST t = new ST(
            "begin\n" +
            "\n" +
            "end\n");
        String expecting="begin"+newline+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineByItselfGetsNoOutput()
    {
        ST t = new ST(
            "begin\n"+
            "<name>\n"+
            "<users>\n"+
            "<users>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineWithIndentGetsNoOutput()
    {
        ST t = new ST(
            "begin\n"+
            "  <name>\n"+
            "   <users>\n"+
            "   <users>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testSizeZeroOnLineWithMultipleExpr()
    {
        ST t = new ST(
            "begin\n"+
            "  <name>\n"+
            "   <users><users>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFExpr()
    {
        ST t = new ST(
            "begin\n"+
            "<if(x)><endif>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIndentedIFExpr()
    {
        ST t = new ST(
            "begin\n"+
            "    <if(x)><endif>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFElseExprOnSingleLine()
    {
        ST t = new ST(
            "begin\n"+
            "<if(users)><else><endif>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testIFOnMultipleLines()
    {
        ST t = new ST(
            "begin\n"+
            "<if(users)>\n" +
            "foo\n" +
            "<else>\n" +
            "bar\n" +
            "<endif>\n"+
            "end\n");
        String expecting="begin"+newline+"bar"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testEndifNotOnLineAlone()
    {
        ST t = new ST(
            "begin\n"+
            "  <if(users)>\n" +
            "  foo\n" +
            "  <else>\n" +
            "  bar\n" +
            "  <endif>end\n");
        String expecting="begin"+newline+"  bar"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testElseIFOnMultipleLines()
    {
        ST t = new ST(
            "begin\n"+
            "<if(a)>\n" +
            "foo\n" +
            "<elseif(b)>\n" +
            "bar\n" +
            "<endif>\n"+
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testElseIFOnMultipleLines2()
    {
        ST t = new ST(
            "begin\n"+
            "<if(a)>\n" +
            "foo\n" +
            "<elseif(b)>\n" +
            "bar\n" +
            "<endif>\n"+
            "end\n");
        t.add("b", true);
        String expecting="begin"+newline+"bar"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testElseIFOnMultipleLines3()
    {
        ST t = new ST(
            "begin\n"+
            "  <if(a)>\n" +
            "  foo\n" +
            "  <elseif(b)>\n" +
            "  bar\n" +
            "  <endif>\n"+
            "end\n");
        t.add("a", true);
        String expecting="begin"+newline+"  foo"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testNestedIFOnMultipleLines()
    {
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
        String expecting="begin"+newline+"bar"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testLineBreak()
    {
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

    @Test public void testLineBreak2()
    {
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

    @Test public void testLineBreakNoWhiteSpace()
    {
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

    @Test public void testNewlineNormalizationInTemplateString()
    {
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

    @Test public void testNewlineNormalizationInTemplateStringPC()
    {
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

    @Test public void testNewlineNormalizationInAttribute()
    {
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

    @Test public void testCommentOnlyLineGivesNoOutput()
    {
        ST t = new org.stringtemplate.v4.ST(
            "begin\n" +
            "<! ignore !>\n" +
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

    @Test public void testCommentOnlyLineGivesNoOutput2()
    {
        ST t = new org.stringtemplate.v4.ST(
            "begin\n" +
            "    <! ignore !>\n" +
            "end\n");
        String expecting="begin"+newline+"end"+newline;
        String result = t.render();
        assertEquals(expecting, result);
    }

 }
