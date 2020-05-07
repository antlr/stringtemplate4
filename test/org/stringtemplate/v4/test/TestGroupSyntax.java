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

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.Misc;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGroupSyntax extends BaseTest {
    @Test public void testSimpleGroup()
    {
        String templates =
            "t() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t() ::= <<" + Misc.newline+
            "foo" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testEscapedQuote()
    {
        // setTest(ranges) ::= "<ranges; separator=\"||\">"
        // has to unescape the strings.
        String templates =
            "setTest(ranges) ::= \"<ranges; separator=\\\"||\\\">\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "setTest(ranges) ::= <<"+Misc.newline+
            "<ranges; separator=\"||\">" +Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testMultiTemplates()
    {
        String templates =
            "ta(x) ::= \"[<x>]\"" + Misc.newline +
            "duh() ::= <<hi there>>" + Misc.newline +
            "wow() ::= <<last>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "ta(x) ::= <<" +Misc.newline+
            "[<x>]" +Misc.newline+
            ">>" +Misc.newline+
            "duh() ::= <<" +Misc.newline+
            "hi there" +Misc.newline+
            ">>" +Misc.newline+
            "wow() ::= <<" +Misc.newline+
            "last" +Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testSetDefaultDelimiters()
    {
        String templates =
            "delimiters \"<\", \">\"" + Misc.newline +
            "ta(x) ::= \"[<x>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("ta");
        st.add("x", "hi");
        String expected = "[hi]";
        String result = st.render();
        assertEquals(expected, result);

        assertEquals("[]", errors.errors.toString());
    }

    /**
     * This is a regression test for antlr/stringtemplate4#131.
     */
    @Test public void testSetDefaultDelimiters_STGroupString()
    {
        String templates =
            "delimiters \"<\", \">\"" + Misc.newline +
            "chapter(title) ::= <<" + Misc.newline +
            "chapter <title>" + Misc.newline +
            ">>" + Misc.newline;

        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroupString(templates);
        group.setListener(errors);
        ST st = group.getInstanceOf("chapter");
        st.add("title", "hi");
        String expected = "chapter hi";
        String result = st.render();
        assertEquals(expected, result);

        assertEquals("[]", errors.errors.toString());
    }

    @Test public void testSetNonDefaultDelimiters()
    {
        String templates =
            "delimiters \"%\", \"%\"" + Misc.newline +
            "ta(x) ::= \"[%x%]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("ta");
        st.add("x", "hi");
        String expected = "[hi]";
        String result = st.render();
        assertEquals(expected, result);
    }

    /**
     * This is a regression test for antlr/stringtemplate4#84.
     */
    @Test public void testSetUnsupportedDelimiters_At()
    {
        String templates =
            "delimiters \"@\", \"@\"" + Misc.newline +
            "ta(x) ::= \"[<x>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("ta");
        st.add("x", "hi");
        String expected = "[hi]";
        String result = st.render();
        assertEquals(expected, result);

        String expectedErrors = "[t.stg 1:11: unsupported delimiter character: @, "
            + "t.stg 1:16: unsupported delimiter character: @]";
        String resultErrors = errors.errors.toString();
        assertEquals(expectedErrors, resultErrors);
    }

    @Test public void testSingleTemplateWithArgs()
    {
        String templates =
            "t(a,b) ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a,b) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testDefaultValues()
    {
        String templates =
            "t(a={def1},b=\"def2\") ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a={def1},b=\"def2\") ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testDefaultValues2()
    {
        String templates =
            "t(x, y, a={def1}, b=\"def2\") ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(x,y,a={def1},b=\"def2\") ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testDefaultValueTemplateWithArg()
    {
        String templates =
            "t(a={x | 2*<x>}) ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a={x | 2*<x>}) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultValueBehaviorTrue()
    {
        String templates =
            "t(a=true) ::= <<\n" +
            "<a><if(a)>+<else>-<endif>\n" +
            ">>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
        ST st = group.getInstanceOf("t");
        String expected = "true+";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultValueBehaviorFalse()
    {
        String templates =
            "t(a=false) ::= <<\n" +
            "<a><if(a)>+<else>-<endif>\n" +
            ">>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
        ST st = group.getInstanceOf("t");
        String expected = "false-";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultValueBehaviorEmptyTemplate()
    {
        String templates =
            "t(a={}) ::= <<\n" +
            "<a><if(a)>+<else>-<endif>\n" +
            ">>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
        ST st = group.getInstanceOf("t");
        String expected = "+";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test
    public void testDefaultValueBehaviorEmptyList()
    {
        String templates =
            "t(a=[]) ::= <<\n" +
            "<a><if(a)>+<else>-<endif>\n" +
            ">>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
        ST st = group.getInstanceOf("t");
        String expected = "-";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNestedTemplateInGroupFile()
    {
        String templates =
            "t(a) ::= \"<a:{x | <x:{y | <y>}>}>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a) ::= <<" + newline +
            "<a:{x | <x:{y | <y>}>}>" + newline +
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testNestedDefaultValueTemplate()
    {
        String templates =
            "t(a={x | <x:{y|<y>}>}) ::= \"ick\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.load();
        String expected =
            "t(a={x | <x:{y|<y>}>}) ::= <<" + newline +
            "ick" + newline +
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testNestedDefaultValueTemplateWithEscapes()
    {
        String templates =
            "t(a={x | \\< <x:{y|<y>\\}}>}) ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a={x | \\< <x:{y|<y>\\}}>}) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testMessedUpTemplateDoesntCauseRuntimeError()
    {
        String templates =
            "main(p) ::= <<\n" +
            "<f(x=\"abc\")>\n" +
            ">>\n" +
            "\n" +
            "f() ::= <<\n" +
            "<x>\n" +
            ">>\n";
        writeFile(tmpdir, "t.stg", templates);

        STGroupFile group;
        ErrorBuffer errors = new ErrorBuffer();
        group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("main");
        st.render();

        String expected = "[context [/main] 1:1 attribute x isn't defined," +
                          " context [/main] 1:1 passed 1 arg(s) to template /f with 0 declared arg(s)," +
                          " context [/main /f] 1:1 attribute x isn't defined]";
        String result = errors.errors.toString();
        assertEquals(expected, result);
    }

    /**
     * This is a regression test for antlr/stringtemplate4#138.
     */
    @Test public void testIndentedComment()
    {
        String templates =
            "t() ::= <<" + Misc.newline +
            "  <! a comment !>" + Misc.newline +
            ">>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST template = group.getInstanceOf("t");

        assertEquals("[]", errors.errors.toString());
        assertNotNull(template);

        String expected = "";
        String result = template.render();
        assertEquals(expected, result);

        assertEquals("[]", errors.errors.toString());
    }
}
