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
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestFunctions extends BaseTest {
    @Test public void testFirst() throws Exception {
        String template = "<first(names)>";
        org.stringtemplate.v4.ST st = new ST(template);
        List names = new ArrayList() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "Ter";
        String result = st.render();
        assertEquals(expected, result);
    }

	@Test public void testLength() throws Exception {
		String template = "<length(names)>";
		org.stringtemplate.v4.ST st = new org.stringtemplate.v4.ST(template);
		List names = new ArrayList() {
			{add("Ter"); add("Tom");}
		};
		st.add("names", names);
		String expected = "2";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testLengthWithNullValues() throws Exception {
		String template = "<length(names)>";
		org.stringtemplate.v4.ST st = new org.stringtemplate.v4.ST(template);
		List names = new ArrayList() {
			{add("Ter"); add(null); add("Tom"); add(null); }
		};
		st.add("names", names);
		String expected = "4";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testFirstOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOp() throws Exception {
        ST e = new org.stringtemplate.v4.ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom, Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpEmptyList() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new ArrayList());
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testReUseOfRestResult() throws Exception {
        String templates =
            "a(names) ::= \"<b(rest(names))>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir+"/"+"t.stg");
        org.stringtemplate.v4.ST e = group.getInstanceOf("a");
        List names = new ArrayList();
        names.add("Ter");
        names.add("Tom");
        e.add("names", names);
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastOp() throws Exception {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Sriram";
        assertEquals(expecting, e.render());
    }

	@Test public void testStripOp() throws Exception {
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

	@Test public void testLengthStrip() throws Exception {
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

    @Test public void testCombinedOp() throws Exception {
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

    @Test public void testCatListAndSingleAttribute() throws Exception {
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

    @Test public void testReUseOfCat() throws Exception {
        String templates =
            "a(mine,yours) ::= \"<b([mine,yours])>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new org.stringtemplate.v4.STGroupFile(tmpdir+"/"+"t.stg");
        org.stringtemplate.v4.ST e = group.getInstanceOf("a");
        List mine = new ArrayList();
        mine.add("Ter");
        mine.add("Tom");
        e.add("mine", mine);
        List yours = new ArrayList();
        yours.add("Foo");
        e.add("yours", yours);
        String expecting = "TerTomFoo, TerTomFoo";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatListAndEmptyAttributes() throws Exception {
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

    @Test public void testNestedOp() throws Exception {
        ST e = new org.stringtemplate.v4.ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithOneAttributeOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithOneAttributeOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<last(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithLengthOneListAttributeOp() throws Exception {
        org.stringtemplate.v4.ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new ArrayList() {{add("Ter");}});
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithOneAttributeOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>"
            );
        e.add("names", "Ter");
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithLengthOneListAttributeOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>"
            );
        e.add("names", new ArrayList() {{add("Ter");}});
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOp() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testIncomingLists() throws Exception {
        ST e = new org.stringtemplate.v4.ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

	@Test public void testFirstWithCatAttribute() throws Exception {
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

	@Test public void testFirstWithListOfMaps() throws Exception {
		org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
				"<first(maps).Ter>"
			);
		final Map m1 = new HashMap();
		final Map m2 = new HashMap();
		m1.put("Ter", "x5707");
		e.add("maps", m1);
		m2.put("Tom", "x5332");
		e.add("maps", m2);
		String expecting = "x5707";
		assertEquals(expecting, e.render());

		List list = new ArrayList() {{add(m1); add(m2);}};
		e.add("maps", list);
		expecting = "x5707";
		assertEquals(expecting, e.render());
	}

	@Test public void testFirstWithListOfMaps2() throws Exception {
		org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
				"<first(maps):{ m | <m>!}>"
			);
		final Map m1 = new HashMap();
		final Map m2 = new HashMap();
		m1.put("Ter", "x5707");
		e.add("maps", m1);
		m2.put("Tom", "x5332");
		e.add("maps", m2);
		String expecting = "Ter!";
		assertEquals(expecting, e.render());
		List list = new ArrayList() {{add(m1); add(m2);}};
		e.add("maps", list);
		expecting = "Ter!";
		assertEquals(expecting, e.render());
	}

    @Test public void testTrim() throws Exception {
        ST e = new org.stringtemplate.v4.ST(
                "<trim(name)>"
            );
        e.add("name", " Ter  \n");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testStrlen() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<strlen(name)>"
            );
        e.add("name", "012345");
        String expecting = "6";
        assertEquals(expecting, e.render());
    }

    @Test public void testReverse() throws Exception {
        org.stringtemplate.v4.ST e = new org.stringtemplate.v4.ST(
                "<reverse(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Sriram, Tom, Ter";
        assertEquals(expecting, e.render());
    }

}
