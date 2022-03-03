package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.misc.Misc;

import static org.junit.Assert.assertEquals;

public class TestJoinPathSegments extends BaseTest {
    private final String ROOT_DIR = "rootDir";
    private final String TEMPLATE_GROUP_FILE = "templateDir/TestFile.stg";
    private final String EXPECTED = "rootDir/templateDir/TestFile.stg";

    @Test
    public void testBareRootDirAndBareGroupDir() {

        assertEquals(EXPECTED, Misc.joinPathSegments(ROOT_DIR, TEMPLATE_GROUP_FILE));
    }

    @Test
    public void testRootDirWithTrailingSlashAndBareGroupDir() {

        assertEquals(EXPECTED, Misc.joinPathSegments(ROOT_DIR + "/", TEMPLATE_GROUP_FILE));
    }

    @Test
    public void testBareRootDirAndGroupDirWithLeadingSlash() {
        assertEquals(EXPECTED, Misc.joinPathSegments(ROOT_DIR, "/" + TEMPLATE_GROUP_FILE));
    }

    @Test
    public void testRootDirWithTrailingSlashAndGroupDirWithLeadingSlash() {
        assertEquals(EXPECTED, Misc.joinPathSegments(ROOT_DIR + "/", "/" + TEMPLATE_GROUP_FILE));
    }
}
