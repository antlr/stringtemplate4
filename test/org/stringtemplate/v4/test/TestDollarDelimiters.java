/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.stringtemplate.v4.test;

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;

import static org.junit.Assert.assertEquals;

public class TestDollarDelimiters extends BaseTest {
    @Test public void testAttr()
    {
        String template = "hi $name$!";
        ST st = new org.stringtemplate.v4.ST(template, '$', '$');
        st.add("name", "Ter");
        String expected = "hi Ter!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testParallelMap()
    {
        STGroup group = new org.stringtemplate.v4.STGroup('$', '$');
        group.defineTemplate("test", "names,phones", "hi $names,phones:{n,p | $n$:$p$;}$");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        st.add("phones", "x5001");
        st.add("phones", "x5002");
        st.add("phones", "x5003");
        String expected =
            "hi Ter:x5001;Tom:x5002;Sumana:x5003;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRefToAnotherTemplateInSameGroup()
    {
        String dir = getRandomDir();
        String a = "a() ::= << <$b()$> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir, "a.st", a);
        writeFile(dir, "b.st", b);
        STGroup group = new org.stringtemplate.v4.STGroupDir(dir, '$', '$');
        org.stringtemplate.v4.ST st = group.getInstanceOf("a");
        String expected = " <bar> ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefaultArgument()
    {
        String templates =
                "method(name) ::= <<"+newline+
                "$stat(name)$" +newline+
                ">>"+newline+
                "stat(name,value=\"99\") ::= \"x=$value$; // $name$\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        org.stringtemplate.v4.STGroup group = new STGroupFile(tmpdir+"/group.stg", '$', '$');
        org.stringtemplate.v4.ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        String expecting = "x=99; // foo";
        String result = b.render();
        assertEquals(expecting, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#46.
     * https://github.com/antlr/stringtemplate4/issues/46
     */
    @Test public void testDelimitersClause()
    {
        String templates =
                "delimiters \"$\", \"$\""+newline+
                "method(name) ::= <<"+newline+
                "$stat(name)$" +newline+
                ">>"+newline+
                "stat(name,value=\"99\") ::= \"x=$value$; // $name$\""+newline
                ;
        writeFile(tmpdir, "group.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/group.stg");
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        String expecting = "x=99; // foo";
        String result = b.render();
        assertEquals(expecting, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#46.
     * https://github.com/antlr/stringtemplate4/issues/46
     */
    @Test public void testDelimitersClauseInGroupString()
    {
        String templates =
                "delimiters \"$\", \"$\""+newline+
                "method(name) ::= <<"+newline+
                "$stat(name)$" +newline+
                ">>"+newline+
                "stat(name,value=\"99\") ::= \"x=$value$; // $name$\""+newline
                ;
        STGroup group = new STGroupString(templates);
        ST b = group.getInstanceOf("method");
        b.add("name", "foo");
        String expecting = "x=99; // foo";
        String result = b.render();
        assertEquals(expecting, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#66.
     * https://github.com/antlr/stringtemplate4/issues/66
     */
    @Test
    public void testImportTemplatePreservesDelimiters() {
        String groupFile =
            "group GenerateHtml;" + newline +
            "import \"html.st\"" + newline +
            "entry() ::= <<" + newline +
            "$html()$" + newline +
            ">>" + newline;
        String htmlFile =
            "html() ::= <<" + newline +
            "<table style=\"stuff\">" + newline +
            ">>" + newline;

        String dir = getRandomDir();
        writeFile(dir, "GenerateHtml.stg", groupFile);
        writeFile(dir, "html.st", htmlFile);

        STGroup group = new STGroupFile(dir + "/GenerateHtml.stg", '$', '$');

        // test html template directly
        ST st = group.getInstanceOf("html");
        Assert.assertNotNull(st);
        String expected = "<table style=\"stuff\">";
        String result = st.render();
        assertEquals(expected, result);

        // test from entry template
        st = group.getInstanceOf("entry");
        Assert.assertNotNull(st);
        expected = "<table style=\"stuff\">";
        result = st.render();
        assertEquals(expected, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#66.
     * https://github.com/antlr/stringtemplate4/issues/66
     */
    @Test
    public void testImportGroupPreservesDelimiters() {
        String groupFile =
            "group GenerateHtml;" + newline +
            "import \"HtmlTemplates.stg\"" + newline +
            "entry() ::= <<" + newline +
            "$html()$" + newline +
            ">>" + newline;
        String htmlFile =
            "html() ::= <<" + newline +
            "<table style=\"stuff\">" + newline +
            ">>" + newline;

        String dir = getRandomDir();
        writeFile(dir, "GenerateHtml.stg", groupFile);
        writeFile(dir, "HtmlTemplates.stg", htmlFile);

        STGroup group = new STGroupFile(dir + "/GenerateHtml.stg", '$', '$');

        // test html template directly
        ST st = group.getInstanceOf("html");
        Assert.assertNotNull(st);
        String expected = "<table style=\"stuff\">";
        String result = st.render();
        assertEquals(expected, result);

        // test from entry template
        st = group.getInstanceOf("entry");
        Assert.assertNotNull(st);
        expected = "<table style=\"stuff\">";
        result = st.render();
        assertEquals(expected, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#66.
     * https://github.com/antlr/stringtemplate4/issues/66
     */
    @Test
    public void testDelimitersClauseOverridesConstructorDelimiters() {
        String groupFile =
            "group GenerateHtml;" + newline +
            "delimiters \"$\", \"$\"" + newline +
            "import \"html.st\"" + newline +
            "entry() ::= <<" + newline +
            "$html()$" + newline +
            ">>" + newline;
        String htmlFile =
            "html() ::= <<" + newline +
            "<table style=\"stuff\">" + newline +
            ">>" + newline;

        String dir = getRandomDir();
        writeFile(dir, "GenerateHtml.stg", groupFile);
        writeFile(dir, "html.st", htmlFile);

        STGroup group = new STGroupFile(dir + "/GenerateHtml.stg", '<', '>');

        // test html template directly
        ST st = group.getInstanceOf("html");
        Assert.assertNotNull(st);
        String expected = "<table style=\"stuff\">";
        String result = st.render();
        assertEquals(expected, result);

        // test from entry template
        st = group.getInstanceOf("entry");
        Assert.assertNotNull(st);
        expected = "<table style=\"stuff\">";
        result = st.render();
        assertEquals(expected, result);
    }

    /**
     * This is part of a regression test for antlr/stringtemplate4#66.
     * https://github.com/antlr/stringtemplate4/issues/66
     */
    @Test
    public void testDelimitersClauseOverridesInheritedDelimiters() {
        String groupFile =
            "group GenerateHtml;" + newline +
            "delimiters \"<\", \">\"" + newline +
            "import \"HtmlTemplates.stg\"" + newline +
            "entry() ::= <<" + newline +
            "<html()>" + newline +
            ">>" + newline;
        String htmlFile =
            "delimiters \"$\", \"$\"" + newline +
            "html() ::= <<" + newline +
            "<table style=\"stuff\">" + newline +
            ">>" + newline;

        String dir = getRandomDir();
        writeFile(dir, "GenerateHtml.stg", groupFile);
        writeFile(dir, "HtmlTemplates.stg", htmlFile);

        STGroup group = new STGroupFile(dir + "/GenerateHtml.stg");

        // test html template directly
        ST st = group.getInstanceOf("html");
        Assert.assertNotNull(st);
        String expected = "<table style=\"stuff\">";
        String result = st.render();
        assertEquals(expected, result);

        // test from entry template
        st = group.getInstanceOf("entry");
        Assert.assertNotNull(st);
        expected = "<table style=\"stuff\">";
        result = st.render();
        assertEquals(expected, result);
    }
}
