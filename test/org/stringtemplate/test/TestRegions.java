package org.stringtemplate.test;

import org.junit.Test;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupDir;
import org.stringtemplate.STGroupFile;

import static org.junit.Assert.assertEquals;

public class TestRegions extends BaseTest {
    @Test public void testEmbeddedRegion() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= <<\n" +
            "[<@r>bar<@end>]\n" +
            ">>\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupFile(dir+"/group.stg");
        ST st = group.getInstanceOf("a");
        String expected = "[bar]"+newline;
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRegion() throws Exception {
        String dir = getRandomDir();
        String groupFile =
            "a() ::= <<\n" +
            "[<@r()>]\n" +
            ">>\n";
        writeFile(dir, "group.stg", groupFile);
        STGroup group = new STGroupFile(dir+"/group.stg");
        ST st = group.getInstanceOf("a");
        String expected = "[]"+newline;
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefineRegionInSubgroup() throws Exception {
        String dir = getRandomDir();
        String g1 = "a() ::= <<[<@r()>]>>\n";
        writeFile(dir, "g1.stg", g1);
        String g2 = "@a.r() ::= <<foo>>\n";
        writeFile(dir, "g2.stg", g2);

        STGroup group1 = new STGroupFile(dir+"/g1.stg");
        STGroup group2 = new STGroupFile(dir+"/g2.stg");
        group2.importTemplates(group1); // define r in g2
        ST st = group2.getInstanceOf("a");
        String expected = "[foo]";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefineRegionInSubgroupThatRefsSuper() throws Exception {
        String dir = getRandomDir();
        String g1 = "a() ::= <<[<@r>foo<@end>]>>\n";
        writeFile(dir, "g1.stg", g1);
        String g2 = "@a.r() ::= <<(<@super.r()>)>>\n";
        writeFile(dir, "g2.stg", g2);

        STGroup group1 = new STGroupFile(dir+"/g1.stg");
        STGroup group2 = new STGroupFile(dir+"/g2.stg");
        group2.importTemplates(group1); // define r in g2
        ST st = group2.getInstanceOf("a");
        String expected = "[(foo)]";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefineRegionInSubgroup2() throws Exception {
        String dir = getRandomDir();
        String g1 = "a() ::= <<[<@r()>]>>\n";
        writeFile(dir, "g1.stg", g1);
        String g2 = "@a.r() ::= <<foo>>>\n";
        writeFile(dir, "g2.stg", g2);

        STGroup group1 = new STGroupFile(dir+"/g1.stg");
        STGroup group2 = new STGroupFile(dir+"/g2.stg");
        group1.importTemplates(group2); // opposite of previous; g1 imports g2
        ST st = group1.getInstanceOf("a");
        String expected = "[]"; // @a.r implicitly defined in g1; can't see g2's
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefineRegionInSameGroup() throws Exception {
        String dir = getRandomDir();
        String g = "a() ::= <<[<@r()>]>>\n"+
                   "@a.r() ::= <<foo>>\n";
        writeFile(dir, "g.stg", g);

        STGroup group = new STGroupFile(dir+"/g.stg");
        ST st = group.getInstanceOf("a");
        String expected = "[foo]";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testCantDefineEmbeddedRegionAgain() throws Exception {
        String dir = getRandomDir();
        String g = "a() ::= <<[<@r>foo<@end>]>>\n"+
                   "@a.r() ::= <<bar>>\n"; // error; dup
        writeFile(dir, "g.stg", g);

        STGroup group = new STGroupFile(dir+"/g.stg");
        ErrorBuffer errors = new ErrorBuffer();
        group.setErrorListener(errors);
        group.load();
        String expected = "redefinition of /region__a__r";
        String result = errors.toString();
        assertEquals(expected, result);
    }

}
