package org.stringtemplate.test;

import org.junit.Test;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupDir;
import org.stringtemplate.STGroupFile;

import static org.junit.Assert.assertEquals;

public class TestImports extends BaseTest {
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
        writeFile(dir, "x/a.st", a);

        String groupFile =
            "b() ::= \"group file b\"\n"+
            "c() ::= \"group file c\"\n";
        writeFile(dir, "y/group.stg", groupFile);

        STGroup group1 = new STGroupDir(dir+"/x");
        STGroup group2 = new STGroupFile(dir+"/y/group.stg");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("/a");
        st.code.dump();
        String expected = " group file b ";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testImportTemplateInGroupFileFromGroupFile() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= \"g1 a\"\n"+
            "b() ::= \"<c()>\"\n";
        writeFile(dir, "x/group.stg", groupFile);

        groupFile =
            "b() ::= \"g2 b\"\n"+
            "c() ::= \"g2 c\"\n";
        writeFile(dir, "y/group.stg", groupFile);

        STGroup group1 = new STGroupFile(dir+"/x/group.stg");
        STGroup group2 = new STGroupFile(dir+"/y/group.stg");
        group1.importTemplates(group2);
        ST st = group1.getInstanceOf("/b");
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

    @Test public void testPolymorphicTemplateReference() throws Exception {
        String dir1 = getRandomDir();
        String b = "b() ::= <<dir1 b>>\n";
        writeFile(dir1, "b.st", b);
        String dir2 = getRandomDir();
        String a = "a() ::= << <b()> >>\n";
        b = "b() ::= <<dir2 b>>\n";
        writeFile(dir2, "a.st", a);
        writeFile(dir2, "b.st", b);

        STGroup group1 = new STGroupDir(dir1);
        STGroup group2 = new STGroupDir(dir2);
        group1.importTemplates(group2);

        // normal lookup; a created from dir2 calls dir2.b
        ST st = group2.getInstanceOf("a");
        String expected = " dir2 b ";
        String result = st.render();
        assertEquals(expected, result);

        // polymorphic lookup; a created from dir1 calls dir2.a which calls dir1.b
        st = group1.getInstanceOf("a");
        expected = " dir1 b ";
        result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSuper() throws Exception {
        String dir1 = getRandomDir();
        String a = "a() ::= <<dir1 a>>\n";
        String b = "b() ::= <<dir1 b>>\n";
        writeFile(dir1, "a.st", a);
        writeFile(dir1, "b.st", b);
        String dir2 = getRandomDir();
        a = "a() ::= << [<super.a()>] >>\n";
        writeFile(dir2, "a.st", a);

        STGroup group1 = new STGroupDir(dir1);
        STGroup group2 = new STGroupDir(dir2);
        group2.importTemplates(group1);
        ST st = group2.getInstanceOf("a");
        String expected = " [dir1 a] ";
        String result = st.render();
        assertEquals(expected, result);
    }
}
