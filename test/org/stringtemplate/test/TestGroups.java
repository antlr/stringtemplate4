package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupDir;
import org.stringtemplate.ST;
import org.stringtemplate.STGroupFile;

public class TestGroups extends BaseTest {
    @Test public void testSimpleGroup() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = "foo"+newline;
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupWithTwoTemplates() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir, "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("b");
        String expected = "foo"+newline+"bar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("subdir/b");
        String expected = "foo"+newline+"bar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupFileInDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<\n"+
            "foo\n"+
            ">>\n";
        writeFile(dir, "a.st", a);
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"duh\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("group/b");
        ST st3 = group.getInstanceOf("group/c");
        String expected = "foo"+newline+"barduh";
        String result = st1.render()+st2.render()+st3.render();
        assertEquals(expected, result);
    }

    @Test public void testSubSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        writeFile(dir, "a.st", a);
        String b =
            "b() ::= \"bar\""+newline;
        writeFile(dir+"/sub1/sub2", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("sub1/sub2/b");
        String expected = "foo"+newline+"bar";
        String result = st1.render()+st2.render();
        assertEquals(expected, result);
    }

    @Test public void testGroupFileInSubDir() throws Exception {
        // /randomdir/a and /randomdir/group.stg with b and c templates
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<\n"+
            "foo\n"+
            ">>\n";
        writeFile(dir, "a.st", a);
        String groupFile =
            "b() ::= \"bar\"\n"+
            "c() ::= \"duh\"\n";
        writeFile(dir, "subdir/group.stg", groupFile);
        STGroup group = new STGroupDir(dir);
        ST st1 = group.getInstanceOf("a");
        ST st2 = group.getInstanceOf("subdir/group/b");
        ST st3 = group.getInstanceOf("subdir/group/c");
        String expected = "foo"+newline+"barduh";
        String result = st1.render()+st2.render()+st3.render();
        assertEquals(expected, result);
    }

    @Test public void testAttemptToAccessTemplateUnderGroupFile() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= \"bar\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupFile(dir+"/group.stg");
        String error = null;
        try {
            group.getInstanceOf("sub/b"); // can't have sub under group file
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "can't use relative template name sub/b";
        String result = error;
        assertEquals(expected, result);
    }

    @Test public void testAttemptToUseWrongGroupFileNameFromRoot() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= \"bar\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupFile(dir+"/group.stg");
        String error = null;
        try {
            group.getInstanceOf("/sub/a"); // can't have sub under group file
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "name must be of form /group/templatename: /sub/a";
        String result = error;
        assertEquals(expected, result);
    }

    @Test public void testAttemptToGoTooDeepUsingGroupFileNameFromRoot() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= \"bar\"\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupFile(dir+"/group.stg");
        String error = null;
        try {
            group.getInstanceOf("/gropu/b/b"); // can't have sub under group file
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "name must be of form /group/templatename: /gropu/b/b";
        String result = error;
        assertEquals(expected, result);
    }

    @Test public void testAttemptToAccessDirWithSameNameAsTemplate() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<foo>>\n";
        writeFile(dir, "a.st", a);
        STGroup group = new STGroupDir(dir);
        String error = null;
        try {
            group.getInstanceOf("a/b"); // 'a' is a template 
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "a is a template not a dir or group file";
        String result = error;
        assertEquals(expected, result);
    }
}
