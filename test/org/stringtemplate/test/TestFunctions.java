package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.Misc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.StringReader;

public class TestFunctions extends BaseTest {
    @Test public void testFirst() throws Exception {
        String template = "<first(names)>";
        ST st = new ST(template);
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
        ST st = new ST(template);
        List names = new ArrayList() {
            {add("Ter"); add("Tom");}
        };
        st.add("names", names);
        String expected = "2";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFirstOp() throws Exception {
        ST e = new ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testTruncOp() throws Exception {
        ST e = new ST(
                "<trunc(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Ter, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOp() throws Exception {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom, Sriram";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestOpEmptyList() throws Exception {
        ST e = new ST(
                "<rest(names); separator=\", \">"
            );
        e.add("names", new ArrayList());
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testReUseOfRestResult() throws Exception {
        String templates =
            "group test;" +newline+
            "a(names) ::= \"<b(rest(names))>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        ST e = group.getInstanceOf("a");
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

    @Test public void testCombinedOp() throws Exception {
        // replace first of yours with first of mine
        ST e = new ST(
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
        ST e = new ST(
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
            "group test;" +newline+
            "a(mine,yours) ::= \"<b([mine,yours])>\""+newline+
            "b(x) ::= \"<x>, <x>\""+newline
            ;
        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        ST e = group.getInstanceOf("a");
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
        ST e = new ST(
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
        ST e = new ST(
                "<first(rest(names))>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("names", "Sriram");
        String expecting = "Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testFirstWithOneAttributeOp() throws Exception {
        ST e = new ST(
                "<first(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithOneAttributeOp() throws Exception {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", "Ter");
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testLastWithLengthOneListAttributeOp() throws Exception {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new ArrayList() {{add("Ter");}});
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithOneAttributeOp() throws Exception {
        ST e = new ST(
                "<rest(names)>"
            );
        e.add("names", "Ter");
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRestWithLengthOneListAttributeOp() throws Exception {
        ST e = new ST(
                "<rest(names)>"
            );
        e.add("names", new ArrayList() {{add("Ter");}});
        String expecting = "";
        assertEquals(expecting, e.render());
    }

    @Test public void testRepeatedRestOp() throws Exception {
        ST e = new ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

    @Test public void testIncomingLists() throws Exception {
        ST e = new ST(
                "<rest(names)>, <rest(names)>" // gets 2nd element
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        String expecting = "Tom, Tom";
        assertEquals(expecting, e.render());
    }

	@Test public void testFirstWithCatAttribute() throws Exception {
		ST e = new ST(
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
		ST e = new ST(
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
		ST e = new ST(
				"<first(maps):{ m | <m> }>"
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

    @Test public void testTrim() throws Exception {
        ST e = new ST(
                "<last(names)>"
            );
        e.add("names", new ArrayList() {{add("Ter");}});
        String expecting = "Ter";
        assertEquals(expecting, e.render());
    }
}
