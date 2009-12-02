package org.stringtemplate.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupFile;
import org.stringtemplate.ST;
import org.stringtemplate.ErrorManager;
import org.stringtemplate.compiler.*;
import org.stringtemplate.misc.Misc;

public class TestRuntimeErrors extends BaseTest {
    @Before
    public void setUp() { org.stringtemplate.compiler.Compiler.subtemplateCount = 0; }

    public static class UserHiddenName {
        protected String name;
        public UserHiddenName(String name) { this.name = name; }
        protected String getName() { return name; }
    }

    public static class UserHiddenNameField {
        protected String name;
        public UserHiddenNameField(String name) { this.name = name; }
    }

    @Test public void testMissingEmbeddedTemplate() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t() ::= \"<foo()>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "no such template: foo in context t"+newline;
		String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testMissingSuperTemplate() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t() ::= \"<super.t()>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String templates2 =
            "u() ::= \"blech\"" + Misc.newline;

        writeFile(tmpdir, "t2.stg", templates2);
        STGroup group2 = new STGroupFile(tmpdir+"/"+"t2.stg");
        group.importTemplates(group2);
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "no such template: super.t in context t"+newline;
		String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testNoPropertyNotError() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t(u) ::= \"<u.x>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.add("u", new User(32, "parrt"));
        st.render();
        String expected = "";
		String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testHiddenPropertyNotError() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t(u) ::= \"<u.name>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.add("u", new UserHiddenName("parrt"));
        st.render();
        String expected = "";
		String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testHiddenFieldNotError() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t(u) ::= \"<u.name>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.add("u", new UserHiddenNameField("parrt"));
        st.render();
        String expected = "";
		String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testSoleArg() throws Exception {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);

        String templates =
            "t() ::= \"<u({9})>\"\n"+
            "u(x,y) ::= \"<x>\"\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "expecting single arg in template reference u() (not 2) in context t"+newline;
		String result = errors.toString();
        assertEquals(expected, result);
    }
}
