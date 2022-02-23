package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TestGroupDirPaths extends BaseTest {
    private final String GROUP_DIR = "test-templates";
    private final String TEMPLATE = "TestGroupDirPathsGroupFile/TestGroupDirPathsTemplate";
    private final String EXPECTED = "TestGroupDirPathsOutput";

    @Test
    public void testBareRootDirAndBareGroupDir() throws MalformedURLException {
        STGroupDir stGroupDir = new STGroupDir(GROUP_DIR);

        stGroupDir.root = ensureNoEndingSlash(stGroupDir.root);

        ST st = stGroupDir.getInstanceOf(TEMPLATE);

        assertEquals(EXPECTED, st.render());
    }

    @Test
    public void testRootDirWithTrailingSlashAndBareGroupDir() throws MalformedURLException {
        STGroupDir stGroupDir = new STGroupDir(GROUP_DIR);

        stGroupDir.root = ensureEndingSlash(stGroupDir.root);

        ST st = stGroupDir.getInstanceOf(TEMPLATE);

        assertEquals(EXPECTED, st.render());
    }

    @Test
    public void testBareRootDirAndGroupDirWithLeadingSlash() throws MalformedURLException {
        STGroupDir stGroupDir = new STGroupDir(GROUP_DIR);

        stGroupDir.root = ensureNoEndingSlash(stGroupDir.root);

        ST st = stGroupDir.getInstanceOf("/" + TEMPLATE);

        assertEquals(EXPECTED, st.render());
    }

    @Test
    public void testRootDirWithTrailingSlashAndGroupDirWithLeadingSlash() throws MalformedURLException {
        STGroupDir stGroupDir = new STGroupDir(GROUP_DIR);

        stGroupDir.root = ensureEndingSlash(stGroupDir.root);

        ST st = stGroupDir.getInstanceOf("/" + TEMPLATE);

        assertEquals(EXPECTED, st.render());
    }

    private static URL ensureEndingSlash(URL input) throws MalformedURLException {
        String inputString = input.toString();
        return new URL(inputString.endsWith("/") ? inputString : inputString + "/");
    }

    private static URL ensureNoEndingSlash(URL input) throws MalformedURLException {
        String inputString = input.toString();
        return new URL(inputString.endsWith("/") ? inputString.replaceFirst("/+$", "") : inputString);
    }
}
