package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;
import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.DebugST;
import org.stringtemplate.misc.Misc;

import java.io.StringWriter;
import java.util.List;

public class TestDebugEvents extends BaseTest {
    @Test public void testString() throws Exception {
        String templates =
            "t() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.debug = true;
        DebugST st = (DebugST)group.getInstanceOf("t");
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[EvalExprEvent{self=t(), start=0, stop=2, expr=foo}," +
            " EvalTemplateEvent{self=t(), start=0, stop=2}]";
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testAttribute() throws Exception {
        String templates =
            "t(x) ::= << <x> >>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.debug = true;
        DebugST st = (DebugST)group.getInstanceOf("t");
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[EvalExprEvent{self=t(), start=0, stop=-1, expr=<x>}," +
            " EvalExprEvent{self=t(), start=0, stop=0, expr= }," +
            " EvalTemplateEvent{self=t(), start=0, stop=0}]";
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testTemplateCall() throws Exception {
        String templates =
            "t(x) ::= <<[<u()>]>>\n" +
            "u() ::= << <x> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.debug = true;
        DebugST st = (DebugST)group.getInstanceOf("t");
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[EvalExprEvent{self=t(), start=0, stop=0, expr=[}," +
            " EvalExprEvent{self=u(), start=1, stop=0, expr=<x>}," +
            " EvalExprEvent{self=u(), start=1, stop=1, expr= }," +
            " EvalTemplateEvent{self=u(), start=1, stop=1}," +
            " EvalExprEvent{self=t(), start=1, stop=1, expr=<u()>}," +
            " EvalExprEvent{self=t(), start=2, stop=2, expr=]}," +
            " EvalTemplateEvent{self=t(), start=0, stop=2}]";
        String result = events.toString();
        assertEquals(expected, result);
    }
}
