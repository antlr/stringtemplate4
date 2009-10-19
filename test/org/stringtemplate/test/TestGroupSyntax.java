package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

public class TestGroupSyntax extends BaseTest {
    @Test public void testSimpleGroup() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t() ::= <<foo>>" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t() ::= <<" + Misc.newline+
            "foo" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testMultiTemplates() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "ta() ::= \"[<it>]\"" + Misc.newline +
            "duh() ::= <<hi there>>" + Misc.newline +
            "wow() ::= <<last>>" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "ta() ::= <<" +Misc.newline+
            "[<it>]" +Misc.newline+
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

    @Test public void testSingleTemplateWithArgs() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t(a,b) ::= \"[<a>]\"" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t(a,b) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testDefaultValues() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t(a={def1},b=\"def2\") ::= \"[<a>]\"" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t(a={def1},b=\"def2\") ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testDefaultValueTemplateWithArg() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t(a={x | 2*<x>}) ::= \"[<a>]\"" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t(a={x | 2*<x>}) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

    @Test public void testNestedDefaultValueTemplateWithEscapes() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t(a={x | \\< <x:{<it>\\}}>}) ::= \"[<a>]\"" + Misc.newline;

        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t(a={x | \\< <x:{<it>\\}}>}) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }
}
