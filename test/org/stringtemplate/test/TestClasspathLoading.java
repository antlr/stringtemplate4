package org.stringtemplate.test;

import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupDir;
import org.stringtemplate.misc.Misc;

import static org.junit.Assert.assertEquals;

// THIS ONLY WORKS WHEN /tmp (or tmpdir) IS IN YOUR CLASSPATH

public class TestClasspathLoading extends BaseTest {
    //public static final String tmpdir = System.getProperty("java.io.tmpdir");
    public static final String tmpdir = "/tmp";
    public static final String newline = Misc.newline;
    public static final String dir = "yuck";

    @Before public void setup() {
        writeFile(tmpdir+"/"+dir, "a.st", "a(x) ::= <<a>>\n");
        writeFile(tmpdir+"/"+dir, "b.st", "b(x) ::= <<b>>\n");
        writeFile(tmpdir+"/"+dir, "c.st", "c(x) ::= <<c>>\n");
    }

    public static void main(String[] args) {
        //setup();
    }

    @Test
    public void test() {
        STGroup group = new STGroupDir(dir);
        ST st = group.getInstanceOf("a");
        String expected = "a";
        String result = st.render();
        assertEquals(expected, result);
    }
}
