/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.debug.DebugST;
import org.stringtemplate.v4.debug.InterpEvent;
import org.stringtemplate.v4.misc.Misc;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDebugEvents extends BaseTest {
    @Test public void testString() throws Exception {
        String templates =
            "t() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        STGroup.debug = true;
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
        STGroup.debug = true;
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
        STGroup.debug = true;
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
