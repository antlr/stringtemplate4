package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.*;

import static org.junit.Assert.assertEquals;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.Misc;

public class TestTemplateNames extends BaseTest {
    @Test public void testAbsoluteTemplateRefFromOutside() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir,           "a.st", "a(x) ::= << </subdir/b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        assertEquals(" bar ", group.getInstanceOf("a").render());
        assertEquals(" bar ", group.getInstanceOf("/a").render());
        assertEquals("bar", group.getInstanceOf("/subdir/b").render());
    }


    @Test public void testRelativeTemplateRefInExpr() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir,           "a.st", "a(x) ::= << <subdir/b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        assertEquals(" bar ", group.getInstanceOf("a").render());
    }

    @Test public void testAbsoluteTemplateRefInExpr() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir,           "a.st", "a(x) ::= << </subdir/b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        assertEquals(" bar ", group.getInstanceOf("a").render());
    }

    @Test public void testRefToAnotherTemplateInSameGroup() {
        String dir = getRandomDir();
        writeFile(dir, "a.st", "a() ::= << <b()> >>\n");
        writeFile(dir, "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRefToAnotherTemplateInSameSubdir() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir+"/subdir", "a.st", "a() ::= << <b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        group.getInstanceOf("/subdir/a").impl.dump();
        assertEquals(" bar ", group.getInstanceOf("/subdir/a").render());
    }

    @Test public void testFullyQualifiedGetInstanceOf() {
        String dir = getRandomDir();
        writeFile(dir, "a.st", "a(x) ::= <<foo>>");
        STGroup group = new STGroupDir(dir);
        assertEquals("foo", group.getInstanceOf("a").render());
        assertEquals("foo", group.getInstanceOf("/a").render());
    }

    @Test public void testFullyQualifiedTemplateRef() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir+"/subdir", "a.st", "a() ::= << </subdir/b()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        STGroup group = new STGroupDir(dir);
        assertEquals(" bar ", group.getInstanceOf("/subdir/a").render());
        assertEquals(" bar ", group.getInstanceOf("subdir/a").render());
    }

    @Test public void testFullyQualifiedTemplateRef2() {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        writeFile(dir, "a.st", "a(x) ::= << </group/b()> >>\n");
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"</a()>\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("/a");
        ST st2 = group.getInstanceOf("/group/c"); // invokes /a
        String expected = " bar  bar ";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testRelativeInSubdir() {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        writeFile(dir,           "a.st", "a(x) ::= << </subdir/c()> >>\n");
        writeFile(dir+"/subdir", "b.st", "b() ::= <<bar>>\n");
        writeFile(dir+"/subdir", "c.st", "c() ::= << <b()> >>\n");
        STGroup group = new STGroupDir(dir);
        assertEquals("  bar  ", group.getInstanceOf("a").render());
    }

    /**
     * This is a regression test for antlr/stringtemplate4#94.
     */
    @Test public void testIdWithHyphens()
    {
        String templates =
            "template-a(x-1) ::= \"[<x-1>]\"" + Misc.newline +
            "template-b(x-2) ::= <<" + Misc.newline +
            "<template-a(x-2)>" + Misc.newline +
            ">>" + Misc.newline +
            "t-entry(x-3) ::= <<[<template-b(x-3)>]>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST template = group.getInstanceOf("t-entry");
        template.add("x-3", "x");
        String expected = "[[x]]";
        String result = template.render();
        assertEquals(expected, result);

        assertEquals("[]", errors.errors.toString());
    }

    // TODO: test <a/b()> is RELATIVE NOT ABSOLUTE
}
