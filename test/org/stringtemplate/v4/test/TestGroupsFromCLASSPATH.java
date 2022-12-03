package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TestGroupsFromCLASSPATH extends BaseTest {
    @Test
    public void testLoadTemplateFileFromDir() {
        STGroupDir stg = new STGroupDir("org/antlr/templates/dir1");
        ST st = stg.getInstanceOf("sample");
        String result = st.render();
        String expecting = "a test";
        assertEquals(expecting, result);
    }

    @Test
    public void testLoadTemplateFileInSubdir() {
        STGroupDir stg = new STGroupDir("org/antlr/templates");
        ST st = stg.getInstanceOf("dir1/sample");
        String result = st.render();
        String expecting = "a test";
        assertEquals(expecting, result);
    }

    /**
     * $ test/resources $ jar cvf test.jar jarbase
     * added manifest
     * adding: jarbase/(in = 0) (out= 0)(stored 0%)
     * adding: jarbase/dir1/(in = 0) (out= 0)(stored 0%)
     * adding: jarbase/dir1/sample.st(in = 24) (out= 26)(deflated -8%)
     * adding: jarbase/testgroupfile.stg(in = 18) (out= 20)(deflated -11%)
     *
     * I set it up so the jar is in the CLASSPATH and URL of group file should be this
     * at runtime:
     *
     *  jar:file:/Users/parrt/antlr/code/stringtemplate4/test/test.jar!/jarbase/testgroupfile.stg
     */
    @Test
    public void testLoadTemplateGroupFileFromJar() {
        // load jarbase/testgroupfile.stg from test.jar that is in CLASSPATH
        STGroupFile stg = new STGroupFile("jarbase/testgroupfile.stg");
        ST st = stg.getInstanceOf("t");
        String result = st.render();
        String expecting = "foo";
        assertEquals(expecting, result);
    }

    /** URL of jarbase/dir1 should be
     *
     *    jar:file:/Users/parrt/antlr/code/stringtemplate4/test/test.jar!/jarbase/dir1
     */
    @Test
    public void testLoadTemplateGroupDirFromJar() {
        // load jarbase/testgroupfile.stg from test.jar that is in CLASSPATH
        STGroupDir stg = new STGroupDir("jarbase/dir1");
        ST st = stg.getInstanceOf("sample");
        String result = st.render();
        String expecting = "a test";
        assertEquals(expecting, result);
    }

    @Test
    public void testLoadTemplateGroupDirViaURL() {
        // Compute URL of dir inside test.jar that is in CLASSPATH
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("jarbase/dir1");
        STGroupDir stg = new STGroupDir(url);
        ST st = stg.getInstanceOf("sample");
        String result = st.render();
        String expecting = "a test";
        assertEquals(expecting, result);
    }

    @Test
    public void testLoadTemplateGroupDirViaURLWithTrailingSlash() {
        // Compute URL of dir inside test.jar that is in CLASSPATH
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("jarbase/dir1/"); // <----- extra slash
        STGroupDir stg = new STGroupDir(url);
        ST st = stg.getInstanceOf("sample");
        String result = st.render();
        String expecting = "a test";
        assertEquals(expecting, result);
    }

    @Test
    public void testLoadTemplateGroupFileFromCLASSPATH() {
        STGroupFile stg = new STGroupFile("org/antlr/templates/testgroupfile.stg");
        ST st = stg.getInstanceOf("t");
        String result = st.render();
        String expecting = "foo";
        assertEquals(expecting, result);
    }
}
