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

import org.junit.*;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.ErrorBuffer;

import static org.junit.Assert.assertEquals;

public class TestOptions extends BaseTest {
    @Test public void testSeparator() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithSpaces() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; separator= \", \">!");
        ST st = group.getInstanceOf("test");
        System.out.println(st.impl.ast.toStringTree());
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testAttrSeparator() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name,sep", "hi <name; separator=sep>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ", ");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeSeparator() {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "|");
        group.defineTemplate("test", "name,sep", "hi <name; separator=foo()>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ", ");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter|Tom|Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplateSeparator() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name,sep", "hi <name; separator={<sep> _}>!");
        ST st = group.getInstanceOf("test");
        st.add("sep", ",");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi Ter, _Tom, _Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNullFirstValueAndNullOption() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; null=\"n/a\", separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "hi n/a, Tom, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSeparatorWithNull2ndValueAndNullOption() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "hi <name; null=\"n/a\", separator=\", \">!");
        ST st = group.getInstanceOf("test");
        st.impl.dump();
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", "Sumana");
        String expected = "hi Ter, n/a, Sumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNullValueAndNullOption() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name; null=\"n/a\">");
        ST st = group.getInstanceOf("test");
        st.add("name", null);
        String expected = "n/a";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testListApplyWithNullValueAndNullOption() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:{n | <n>}; null=\"n/a\">");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", "Sumana");
        String expected = "Tern/aSumana";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDoubleListApplyWithNullValueAndNullOption() {
        // first apply sends [ST, null, ST] to second apply, which puts [] around
        // the value.  This verifies that null not blank comes out of first apply
        // since we don't get [null].
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name:{n | <n>}:{n | [<n>]}; null=\"n/a\">");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", null);
        st.add("name", "Sumana");
        String expected = "[Ter]n/a[Sumana]";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMissingValueAndNullOption() {
        STGroup group = new STGroup();
        group.defineTemplate("test", "name", "<name; null=\"n/a\">");
        ST st = group.getInstanceOf("test");
        String expected = "n/a";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testOptionDoesntApplyToNestedTemplate() {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "<zippo>");
        group.defineTemplate("test", "zippo", "<foo(); null=\"n/a\">");
        ST st = group.getInstanceOf("test");
        st.add("zippo", null);
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIllegalOption()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroup();
        group.setListener(errors);
        group.defineTemplate("test", "name", "<name; bad=\"ugly\">");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
        expected = "[test 1:7: no such option: bad]";
        assertEquals(expected, errors.errors.toString());
    }
}
