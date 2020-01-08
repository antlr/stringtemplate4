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
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.debug.InterpEvent;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDebugEvents extends BaseTest {
    @Test public void testString()
    {
        String templates =
            "t() ::= <<foo>>" + newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[EvalExprEvent{self=/t(), expr='foo', exprStartChar=0, exprStopChar=2, start=0, stop=2}," +
            " EvalTemplateEvent{self=/t(), start=0, stop=2}]";
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testAttribute()
    {
        String templates =
            "t(x) ::= << <x> >>" + newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[IndentEvent{self=/t(), expr=' ', exprStartChar=0, exprStopChar=0, start=0, stop=0}," +
            " EvalExprEvent{self=/t(), expr='<x>', exprStartChar=1, exprStopChar=3, start=0, stop=-1}," +
            " EvalExprEvent{self=/t(), expr=' ', exprStartChar=4, exprStopChar=4, start=0, stop=0}," +
            " EvalTemplateEvent{self=/t(), start=0, stop=0}]";
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test public void testTemplateCall()
    {
        String templates =
            "t(x) ::= <<[<u()>]>>\n" +
            "u() ::= << <x> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        group.getInstanceOf("u").impl.dump();
        List<InterpEvent> events = st.getEvents();
        String expected =
            "[EvalExprEvent{self=/t(), expr='[', exprStartChar=0, exprStopChar=0, start=0, stop=0}," +
            " IndentEvent{self=/u(), expr=' ', exprStartChar=0, exprStopChar=0, start=1, stop=1}," +
            " EvalExprEvent{self=/u(), expr='<x>', exprStartChar=1, exprStopChar=3, start=1, stop=0}," +
            " EvalExprEvent{self=/u(), expr=' ', exprStartChar=4, exprStopChar=4, start=1, stop=1}," +
            " EvalTemplateEvent{self=/u(), start=1, stop=1}," +
            " EvalExprEvent{self=/t(), expr='<u()>'," +
            " exprStartChar=1, exprStopChar=5, start=1, stop=1}," +
            " EvalExprEvent{self=/t(), expr=']'," +
            " exprStartChar=6, exprStopChar=6, start=2, stop=2}," +
            " EvalTemplateEvent{self=/t(), start=0, stop=2}]";
        String result = events.toString();
        assertEquals(expected, result);
    }

    @Test
    public void testEvalExprEventForSpecialCharacter()
    {
        String templates = "t() ::= <<[<\\n>]>>\n";
        //                            012 345
        // Rendering t() emits: "[\n]"  or  "[\r\n]" (depends on line.separator)
        //                       01 2        01 2 3
        STGroupString g = new STGroupString(templates);
        ST st = g.getInstanceOf("t");
        st.impl.dump();
        List<InterpEvent> events = st.getEvents();
        int n = newline.length();
        String expected =
            "[EvalExprEvent{self=/t(), expr='[', exprStartChar=0, exprStopChar=0, start=0, stop=0}, " +
            "EvalExprEvent{self=/t(), expr='\\n', exprStartChar=2, exprStopChar=3, start=1, stop="+n+"}, " +
            "EvalExprEvent{self=/t(), expr=']', exprStartChar=5, exprStopChar=5, start="+(n+1)+", stop="+(n+1)+"}, " +
            "EvalTemplateEvent{self=/t(), start=0, stop="+(n+1)+"}]";
        String result = events.toString();
        assertEquals(expected, result);
    }
}
