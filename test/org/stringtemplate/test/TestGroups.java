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
        String expected = "name must be of form /templatename: /sub/a";
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
            group.getInstanceOf("/group/b/b"); // can't have sub under group file
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "name must be of form /templatename: /group/b/b";
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

    @Test public void testAttemptToAccessSubDirWithWrongRootName() throws Exception {
        String dir = getRandomDir();
        String a =
            "a(x) ::= <<foo>>\n";
        writeFile(dir+"/subdir", "a.st", a);
        STGroup group = new STGroupDir(dir+"/subdir");
        String error = null;
        try {
            group.getInstanceOf("/x/b"); // name is subdir not x
        }
        catch (IllegalArgumentException iae) {
            error = iae.getMessage();
        }
        String expected = "no such subdirectory or group file: x";
        String result = error;
        assertEquals(expected, result);
    }

    @Test public void testRefToAnotherTemplateInSameGroup() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir, "a.st", a);
        writeFile(dir, "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRefToAnotherTemplateInSameSubdir() throws Exception {
        // /randomdir/a and /randomdir/subdir/b
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        String b = "b() ::= <<bar>>\n";
        writeFile(dir+"/subdir", "a.st", a);
        writeFile(dir+"/subdir", "b.st", b);
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("subdir/a");
        String expected = " bar ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplate() throws Exception {
        String dir1 = getRandomDir();
        String a = "a() ::= <<dir1 a>>\n";
        String b = "b() ::= <<dir1 b>>\n";
        writeFile(dir1, "a.st", a);
        writeFile(dir1, "b.st", b);
        String dir2 = getRandomDir();
        a = "a() ::= << <b()> >>\n";
        writeFile(dir2, "a.st", a);

        STGroup group1 = new STGroupDir(dir1);
        STGroup group2 = new STGroupDir(dir2);
        group2.importTemplates(group1);
        ST st = group2.getInstanceOf("b");
        String expected = "dir1 b";
        String result = st.render();
        assertEquals(expected, result);

        // do it again, but make a template ref imported template
        st = group2.getInstanceOf("a");
        expected = " dir1 b ";
        result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateInGroupFileFromDir() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        writeFile(dir, "a.st", a);

        String groupFile =
            "b() ::= \"group file b\"\n"+
            "c() ::= \"group file c\"\n";
        writeFile(dir, "group.stg", groupFile);

        STGroup group1 = new STGroupDir(dir);
        STGroup group2 = new STGroupFile(dir+"/group.stg");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("a");
        String expected = " group file b ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateInDirFromGroupFile() throws Exception {
        String dir = getRandomDir();
        String a = "a() ::= <<dir1 a>>\n";
        writeFile(dir, "a.st", a);

        String groupFile =
            "b() ::= \"<a()>\"\n";
        writeFile(dir, "group.stg", groupFile);

        STGroup group1 = new STGroupDir(dir);
        STGroup group2 = new STGroupFile(dir+"/group.stg");
        group2.importTemplates(group1);
        ST st = group2.getInstanceOf("b");
        String expected = "dir1 a";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateInGroupFileFromGroupFile() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= \"g1 a\"\n"+
            "b() ::= \"<c()>\"\n";
        writeFile(dir, "group1.stg", groupFile);

        groupFile =
            "b() ::= \"g2 b\"\n"+
            "c() ::= \"g2 c\"\n";
        writeFile(dir, "group2.stg", groupFile);

        STGroup group1 = new STGroupFile(dir+"/group1.stg");
        STGroup group2 = new STGroupFile(dir+"/group2.stg");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("b");
        String expected = "g2 c";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateFromSubdir() throws Exception {
        // /randomdir/x/subdir/a and /randomdir/y/subdir/b
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        String b = "b() ::= <<x/subdir/b>>\n";
        writeFile(dir, "x/subdir/a.st", a);
        writeFile(dir, "y/subdir/b.st", b);

        STGroup group1 = new STGroupDir(dir+"/x");
        STGroup group2 = new STGroupDir(dir+"/y");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("/subdir/a");
        String expected = " x/subdir/b ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateFromGroupFile() throws Exception {
        // /randomdir/x/subdir/a and /randomdir/y/subdir.stg which has a and b
        String dir = getRandomDir();
        String a = "a() ::= << <b()> >>\n"; // get b imported from subdir.stg
        writeFile(dir, "x/subdir/a.st", a);

        String groupFile =
            "a() ::= \"group file a\"\n"+
            "b() ::= \"group file b\"\n";
        writeFile(dir, "y/subdir.stg", groupFile);

        STGroup group1 = new STGroupDir(dir+"/x");
        STGroup group2 = new STGroupDir(dir+"/y");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("/subdir/a");
        String expected = " group file b ";
        String result = st.render();
        assertEquals(expected, result);
    }

}
