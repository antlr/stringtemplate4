package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

import java.io.StringWriter;
import java.util.List;

public class TestDebugEvents extends BaseTest {
    @Test public void testString() throws Exception {
        String templates =
            "t() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.code.dump();
        StringWriter sw = new StringWriter();
        Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
        interp.setDebug(true);
        interp.exec(st);
        String expected = "";
        List<Interpreter.DebugEvent> events = interp.getEvents();
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testAttribute() throws Exception {
        String templates =
            "t(x) ::= << <x> >>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.code.dump();
        st.add("x", "foo");
        StringWriter sw = new StringWriter();
        Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
        interp.setDebug(true);
        interp.exec(st);
        String expected = "";
        List<Interpreter.DebugEvent> events = interp.getEvents();
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testTemplateCall() throws Exception {
        String templates =
            "t(x) ::= <<[<u()>]>>\n" +
            "u() ::= << <x> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.code.dump();
        st.add("x", "foo");
        StringWriter sw = new StringWriter();
        Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
        interp.setDebug(true);
        interp.exec(st);
        String expected = "";
        List<Interpreter.DebugEvent> events = interp.getEvents();
        String result = events.toString();
        assertEquals(expected, result);
    }
}
