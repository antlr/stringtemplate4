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
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestFunctions extends BaseTest {
    @Test public void testFirst() {
        String template = "<first(names)>";
        org.stringtemplate.v4.ST st = new ST(template);
        List<String> names = new ArrayList<String>() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testLength() {
        String template = "<length(names)>";
        org.stringtemplate.v4.ST st = new org.stringtemplate.v4.ST(template);
        List<String> names = new ArrayList<String>() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "2";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testLengthWithNullValues() {
        String template = "<length(names)>";
        org.stringtemplate.v4.ST st = new org.stringtemplate.v4.ST(template);
        List<String> names = new ArrayList<String>() {
            {add("Ter"); add(null); add("Tom"); add(null); }
        };
        st.add("names", names);
        String expected = "4";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFirstOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstOpList() {
        ST e = new ST(
                "<first(names)>"
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstOpArray() {
        ST e = new ST(
                "<first(names)>"
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstOpPrimitiveArray() {
        ST e = new ST(
                "<first(names)>"
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "0";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOpList() {
        ST e = new ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Ter, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOpArray() {
        ST e = new ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Ter, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOpPrimitiveArray() {
        ST e = new ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "0, 1";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOp() {
        ST e = new org.stringtemplate.v4.ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom, Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpList() {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Tom, Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpArray() {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Tom, Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpPrimitiveArray() {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "1, 2";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpEmptyList() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new ArrayList<Object>());
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpEmptyArray() {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new String[0]);
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpEmptyPrimitiveArray() {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new int[0]);
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testReUseOfRestResult() {
        String templates =
            "a(names) ::= \"<b(rest(names))>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir+"/"+"t.stg");
        org.stringtemplate.v4.ST e = group.getInstanceOf("a");
        List<String> names = new ArrayList<String>();
        names.add("Ter");
        names.add("Tom");
        e.add("names", names);
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testReUseOfRestPrimitiveArrayResult() {
        String templates =
            "a(names) ::= \"<b(rest(names))>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST e = group.getInstanceOf("a");
        e.add("names", new int[] { 0, 1 });
        String expecting = "1, 1";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastOp() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastOpList() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastOpArray() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastOpPrimitiveArray() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "2";
        assertEquals(expecting, e.render());
    }

    @Test public void testStripOp() {
        ST e = new org.stringtemplate.v4.ST(
                "<strip(names); null=\"n/a\">"
            );
        e.add("names", null);
        e.add("names", "Tom");
        e.add("names", null);
        e.add("names", null);
        e.add("names", "Sriram");
        e.add("names", null);
        String expecting = "TomSriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testStripOpList() {
        ST e = new ST(
                "<strip(names); null=\"n/a\">"
            );
        e.add("names", Arrays.asList(null, "Tom", null, null, "Sriram", null));
        String expecting = "TomSriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testStripOpArray() {
        ST e = new ST(
                "<strip(names); null=\"n/a\">"
            );
        e.add("names", new String[] { null, "Tom", null, null, "Sriram", null });
        String expecting = "TomSriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testLengthStrip() {
        ST e = new org.stringtemplate.v4.ST(
                "<length(strip(names))>"
            );
        e.add("names", null);
        e.add("names", "Tom");
        e.add("names", null);
        e.add("names", null);
        e.add("names", "Sriram");
        e.add("names", null);
        String expecting = "2";
        assertEquals(expecting, e.render());
    }

    @Test public void testLengthStripList() {
        ST e = new ST(
                "<length(strip(names))>"
            );
        e.add("names", Arrays.asList(null, "Tom", null, null, "Sriram", null));
        String expecting = "2";
        assertEquals(expecting, e.render());
    }

    @Test public void testLengthStripArray() {
        ST e = new ST(
                "<length(strip(names))>"
            );
        e.add("names", new String[] { null, "Tom", null, null, "Sriram", null });
        String expecting = "2";
        assertEquals(expecting, e.render());
    }

    @Test public void testCombinedOp() {
        // replace first of yours with first of mine
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<[first(mine),rest(yours)]; separator=\", \">"
            );
        e.add("mine", "1");
        e.add("mine", "2");
        e.add("mine", "3");
        e.add("yours", "a");
        e.add("yours", "b");
        String expecting = "1, b";
        assertEquals(expecting, e.render());
    }

    @Test public void testCombinedOpList() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[first(mine),rest(yours)]; separator=\", \">"
            );
        e.add("mine", Arrays.asList("1", "2", "3"));
        e.add("yours", "a");
        e.add("yours", "b");
        String expecting = "1, b";
        assertEquals(expecting, e.render());
    }

    @Test public void testCombinedOpArray() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[first(mine),rest(yours)]; separator=\", \">"
            );
        e.add("mine", new String[] { "1", "2", "3" });
        e.add("yours", "a");
        e.add("yours", "b");
        String expecting = "1, b";
        assertEquals(expecting, e.render());
    }

    @Test public void testCombinedOpPrimitiveArray() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[first(mine),rest(yours)]; separator=\", \">"
            );
        e.add("mine", new int[] { 1, 2, 3 });
        e.add("yours", "a");
        e.add("yours", "b");
        String expecting = "1, b";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatListAndSingleAttribute() {
        // replace first of yours with first of mine
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<[mine,yours]; separator=\", \">"
            );
        e.add("mine", "1");
        e.add("mine", "2");
        e.add("mine", "3");
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatListAndSingleAttribute2() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[mine,yours]; separator=\", \">"
            );
        e.add("mine", Arrays.asList("1", "2", "3"));
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatArrayAndSingleAttribute() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[mine,yours]; separator=\", \">"
            );
        e.add("mine", new String[] { "1", "2", "3" });
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatPrimitiveArrayAndSingleAttribute() {
        // replace first of yours with first of mine
        ST e = new ST(
                "<[mine,yours]; separator=\", \">"
            );
        e.add("mine", new int[] { 1, 2, 3 });
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testReUseOfCat() {
        String templates =
            "a(mine,yours) ::= \"<b([mine,yours])>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir+"/"+"t.stg");
        org.stringtemplate.v4.ST e = group.getInstanceOf("a");
        List<String> mine = new ArrayList<String>();
        mine.add("Ter");
        mine.add("Tom");
        e.add("mine", mine);
        List<String> yours = new ArrayList<String>();
        yours.add("Foo");
        e.add("yours", yours);
        String expecting = "TerTomFoo, TerTomFoo";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatListAndEmptyAttributes() {
        // + is overloaded to be cat strings and cat lists so the
        // two operands (from left to right) determine which way it
        // goes.  In this case, x+mine is a list so everything from their
        // to the right becomes list cat.
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<[x,mine,y,yours,z]; separator=\", \">"
            );
        e.add("mine", "1");
        e.add("mine", "2");
        e.add("mine", "3");
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatListAndEmptyAttributes2() {
        // + is overloaded to be cat strings and cat lists so the
        // two operands (from left to right) determine which way it
        // goes.  In this case, x+mine is a list so everything from their
        // to the right becomes list cat.
        ST e = new ST(
                "<[x,mine,y,yours,z]; separator=\", \">"
            );
        e.add("mine", Arrays.asList("1", "2", "3"));
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatArrayAndEmptyAttributes2() {
        // + is overloaded to be cat strings and cat lists so the
        // two operands (from left to right) determine which way it
        // goes.  In this case, x+mine is a list so everything from their
        // to the right becomes list cat.
        ST e = new ST(
                "<[x,mine,y,yours,z]; separator=\", \">"
            );
        e.add("mine", new String[] { "1", "2", "3" });
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatPrimitiveArrayAndEmptyAttributes() {
        // + is overloaded to be cat strings and cat lists so the
        // two operands (from left to right) determine which way it
        // goes.  In this case, x+mine is a list so everything from their
        // to the right becomes list cat.
        ST e = new ST(
                "<[x,mine,y,yours,z]; separator=\", \">"
            );
        e.add("mine", new int[] { 1, 2, 3 });
        e.add("yours", "a");
        String expecting = "1, 2, 3, a";
        assertEquals(expecting, e.render());
    }

    @Test public void testNestedOp() {
        ST e = new org.stringtemplate.v4.ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testNestedOpList() {
        ST e = new ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testNestedOpArray() {
        ST e = new ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testNestedOpPrimitiveArray() {
        ST e = new ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "1";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithOneAttributeOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithOneAttributeOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<last(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithLengthOneListAttributeOp() {
        org.stringtemplate.v4.ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new ArrayList<String>() {{add("Ter");}});
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithLengthOneArrayAttributeOp() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new String[] { "Ter" });
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithLengthOnePrimitiveArrayAttributeOp() {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new int[] { 0 });
        String expecting = "0";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithOneAttributeOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>"
            );
        e.add("names", "Ter");
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithLengthOneListAttributeOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>"
            );
        e.add("names", new ArrayList<String>() {{add("Ter");}});
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithLengthOneArrayAttributeOp() {
        ST e = new ST(
                "<rest(names)>"
            );
        e.add("names", new String[] { "Ter" });
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithLengthOnePrimitiveArrayAttributeOp() {
        ST e = new ST(
                "<rest(names)>"
            );
        e.add("names", new int[] { 0 });
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOp() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOpList() {
        ST e = new ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", Arrays.asList("Ter", "Tom"));
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOpArray() {
        ST e = new ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", new String[] { "Ter", "Tom" });
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOpPrimitiveArray() {
        ST e = new ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", new int[] { 0, 1 });
        String expecting = "1, 1";
        assertEquals(expecting, e.render());
    }

    @Test public void testIncomingLists() {
        ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithCatAttribute() {
        ST e = new org.stringtemplate.v4.ST(
                "<first([names,phones])>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithListOfMaps() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(maps).Ter>"
            );
        final Map<String, String> m1 = new HashMap<String, String>();
        final Map<String, String> m2 = new HashMap<String, String>();
        m1.put("Ter", "x5707");
        e.add("maps", m1);
        m2.put("Tom", "x5332");
        e.add("maps", m2);
        String expecting = "x5707";
        assertEquals(expecting, e.render());

        List<Map<String, String>> list = new ArrayList<Map<String, String>>() {{add(m1); add(m2);}};
        e.add("maps", list);
        expecting = "x5707";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithListOfMaps2() {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(maps):{ m | <m>!}>"
            );
        final Map<String, String> m1 = new HashMap<String, String>();
        final Map<String, String> m2 = new HashMap<String, String>();
        m1.put("Ter", "x5707");
        e.add("maps", m1);
        m2.put("Tom", "x5332");
        e.add("maps", m2);
        String expecting = "Ter!";
        assertEquals(expecting, e.render());
        List<Map<String, String>> list = new ArrayList<Map<String, String>>() {{add(m1); add(m2);}};
        e.add("maps", list);
        expecting = "Ter!";
        assertEquals(expecting, e.render());
    }

    @Test public void testTrim() {
        ST e = new org.stringtemplate.v4.ST(
                "<trim(name)>"
            );
        e.add("name", " Ter  \n");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testStrlen()
    {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<strlen(name)>"
            );
        e.add("name", "012345");
        String expecting = "6";
        assertEquals(expecting, e.render());
    }

    @Test public void testReverse()
    {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<reverse(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Sriram, Tom, Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testReverseList()
    {
        ST e = new ST(
                "<reverse(names); separator=\", \">"
            );
        e.add("names", Arrays.asList("Ter", "Tom", "Sriram"));
        String expecting = "Sriram, Tom, Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testReverseArray()
    {
        ST e = new ST(
                "<reverse(names); separator=\", \">"
            );
        e.add("names", new String[] { "Ter", "Tom", "Sriram" });
        String expecting = "Sriram, Tom, Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testReversePrimitiveArray()
    {
        ST e = new ST(
                "<reverse(names); separator=\", \">"
            );
        e.add("names", new int[] { 0, 1, 2 });
        String expecting = "2, 1, 0";
        assertEquals(expecting, e.render());
    }

}
