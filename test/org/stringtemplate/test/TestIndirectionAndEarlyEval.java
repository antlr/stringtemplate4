package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;

import java.util.Map;
import java.util.HashMap;

public class TestIndirectionAndEarlyEval extends BaseTest {
    @Test public void testEarlyEval() throws Exception {
        String template = "<(name)>";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectTemplateInclude() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "bar");
        String template = "<(name)()>";
        group.defineTemplate("test", template);
        ST st = group.getInstanceOf("test");
        st.add("name", "foo");
        String expected = "bar";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectTemplateIncludeViaTemplate() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "bar");
        group.defineTemplate("tname", "foo");
        String template = "<(tname())()>";
        group.defineTemplate("test", template);
        ST st = group.getInstanceOf("test");
        String expected = "bar";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectProp() throws Exception {
        String template = "<u.(propname)>: <u.name>";
        ST st = new ST(template);
        st.add("u", new TestCoreBasics.User(1, "parrt"));
        st.add("propname", "id");
        String expected = "1: parrt";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIndirectMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("a", "[<it>]");
        group.defineTemplate("test", "hi <names:(templateName)()>!");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        st.add("templateName", "a");
        String expected =
            "hi [Ter][Tom][Sumana]!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNonStringDictLookup() throws Exception {
        String template = "<m.(intkey)>";
        ST st = new ST(template);
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(36, "foo");
        st.add("m", m);
        st.add("intkey", 36);
        String expected = "foo";
        String result = st.render();
        assertEquals(expected, result);
    }
}
