package org.stringtemplate.test;

import org.junit.Test;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.Misc;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.HashMap;

public class TestDictionaries extends BaseTest {
    @Test public void testDict() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = 0;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictValuesAreTemplates() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] "+newline+
                "var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("w", "L");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = 0L;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictKeyLookupViaTemplate() throws Exception {
        // Make sure we try rendering stuff to string if not found as regular object
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] "+newline+
                "var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("w", "L");
        st.add("type", new ST("int"));
        st.add("name", "x");
        String expecting = "int x = 0L;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictKeyLookupAsNonToStringableObject() throws Exception {
        // Make sure we try rendering stuff to string if not found as regular object
        String templates =
                "group test;" +newline+
                "foo(m,k) ::= \"<m.(k)>\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("foo");
        Map<HashableUser,String> m = new HashMap<HashableUser,String>();
        m.put(new HashableUser(99,"parrt"), "first");
        m.put(new HashableUser(172036,"tombu"), "second");
        m.put(new HashableUser(391,"sriram"), "third");
        st.add("m", m);
        st.add("k", new HashableUser(172036,"tombu"));
        String expecting = "second";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictMissingDefaultValueIsEmpty() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("w", "L");
        st.add("type", "double"); // double not in typeInit map
        st.add("name", "x");
        String expecting = "double x = ;"; // weird, but tests default value is key
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictHiddenByFormalArg() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "var(typeInit,type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = ;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictEmptyValueAndAngleBracketStrings() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":, \"double\":<<0.0L>>] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "float");
        st.add("name", "x");
        String expecting = "float x = ;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictDefaultValue() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", default:\"null\"] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "UserRecord");
        st.add("name", "x");
        String expecting = "UserRecord x = null;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictEmptyDefaultValue() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", default:] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "UserRecord");
        st.add("name", "x");
        String expecting = "UserRecord x = ;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictDefaultValueIsKey() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", default:key] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "UserRecord");
        st.add("name", "x");
        String expecting = "UserRecord x = UserRecord;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    /**
     * Test that a map can have only the default entry.
     * <p>
     * Bug ref: JIRA bug ST-15 (Fixed)
     */
    @Test public void testDictDefaultStringAsKey() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"default\":\"foo\"] "+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("var");
        st.add("type", "default");
        st.add("name", "x");
        String expecting = "default x = foo;";
        String result = st.render();
        assertEquals(expecting, result);
    }
    
    /**
     * Test that a map can return a <b>string</b> with the word: default.
     * <p>
     * Bug ref: JIRA bug ST-15 (Fixed)
     */
    @Test public void testDictDefaultIsDefaultString() throws Exception {
        String templates =
                "group test;" +newline+
                "map ::= [default: \"default\"] "+newline+
                "t() ::= << <map.(\"1\")> >>"+newline                
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("t");
        String expecting = "default";
        String result = st.render();        
        assertEquals(expecting, result);
    }    

    @Test public void testDictViaEnclosingTemplates() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
// TODO                "intermediate(type,name) ::= \"<var(...)>\""+newline+
                "intermediate(type,name) ::= \"<var()>\""+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST st = group.getInstanceOf("intermediate");
        st.add("type", "int");
        st.add("name", "x");
        String expecting = "int x = 0;";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testDictViaEnclosingTemplates2() throws Exception {
        String templates =
                "group test;" +newline+
                "typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] "+newline+
                "intermediate(stuff) ::= \"<stuff>\""+newline+
                "var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""+newline
                ;
        Misc.writeFile(tmpdir, "test.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"test.stg");
        ST interm = group.getInstanceOf("intermediate");
        ST var = group.getInstanceOf("var");
        var.add("type", "int");
        var.add("name", "x");
        interm.add("stuff", var);
        String expecting = "int x = 0;";
        String result = interm.render();
        assertEquals(expecting, result);
    }    
}