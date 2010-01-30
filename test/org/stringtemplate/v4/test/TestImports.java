/*
 [The "BSD licence"]
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
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

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

        org.stringtemplate.v4.STGroup group1 = new STGroupDir(dir1);
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
        st.impl.dump();
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

        org.stringtemplate.v4.STGroup group1 = new org.stringtemplate.v4.STGroupFile(dir+"/x/group.stg");
        STGroup group2 = new STGroupFile(dir+"/y/group.stg");
        group1.importTemplates(group2);
        org.stringtemplate.v4.ST st = group1.getInstanceOf("/b");
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

        org.stringtemplate.v4.STGroup group1 = new STGroupDir(dir+"/x");
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

        org.stringtemplate.v4.STGroup group1 = new org.stringtemplate.v4.STGroupDir(dir+"/x");
        org.stringtemplate.v4.STGroup group2 = new org.stringtemplate.v4.STGroupDir(dir+"/y");
        group1.importTemplates(group2);
        org.stringtemplate.v4.ST st = group1.getInstanceOf("/subdir/a");
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

        STGroup group1 = new org.stringtemplate.v4.STGroupDir(dir1);
        STGroup group2 = new org.stringtemplate.v4.STGroupDir(dir2);
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

        org.stringtemplate.v4.STGroup group1 = new STGroupDir(dir1);
        STGroup group2 = new STGroupDir(dir2);
        group2.importTemplates(group1);
        ST st = group2.getInstanceOf("a");
        String expected = " [dir1 a] ";
        String result = st.render();
        assertEquals(expected, result);
    }
}
