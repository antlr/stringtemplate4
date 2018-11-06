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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.DateRenderer;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TestRenderers extends BaseTest {

	// Make sure to use the US Locale during the tests
	private Locale origLocale;

	@Before
	@Override
	public void setUp() {
		super.setUp();
		origLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
	}

	@After
	public void tearDown() {
		Locale.setDefault(origLocale);
	}

	@Test public void testRendererForGroup() throws Exception {
		String templates =
				"dateThing(created) ::= \"datetime: <created>\"\n";
		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/t.stg");
		group.registerRenderer(GregorianCalendar.class, new DateRenderer());
		ST st = group.getInstanceOf("dateThing");
		st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
		String expecting = "datetime: 7/5/05, 12:00 AM";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testRendererWithFormat() throws Exception {
        String templates =
                "dateThing(created) ::= << date: <created; format=\"yyyy.MM.dd\"> >>\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
        String expecting = " date: 2005.07.05 ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithPredefinedFormat() throws Exception {
        String templates =
                "dateThing(created) ::= << datetime: <created; format=\"short\"> >>\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
        String expecting = " datetime: 7/5/05, 12:00 AM ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithPredefinedFormat2() throws Exception {
        String templates =
                "dateThing(created) ::= << datetime: <created; format=\"full\"> >>\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        TimeZone origTimeZone = TimeZone.getDefault();
        try {
        	// set Timezone to "PDT"
        	TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        	st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
        	String expecting = " datetime: Tuesday, July 5, 2005 at 12:00:00 AM Pacific Daylight Time ";
        	String result = st.render();
        	assertEquals(expecting, result);
        } finally {
           	// Restore original Timezone
           	TimeZone.setDefault(origTimeZone);
        }
   }

    @Test public void testRendererWithPredefinedFormat3() throws Exception {
        String templates =
                "dateThing(created) ::= << date: <created; format=\"date:medium\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
        String expecting = " date: Jul 5, 2005 ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithPredefinedFormat4() throws Exception {
        String templates =
                "dateThing(created) ::= << time: <created; format=\"time:medium\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 7 - 1, 5));
        String expecting = " time: 12:00:00 AM ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testStringRendererWithFormat_cap() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"cap\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", "hi");
        String expecting = " Hi ";
        String result = st.render();
        assertEquals(expecting, result);
    }

	@Test public void testStringRendererWithTemplateInclude_cap() throws Exception {
		// must toString the t() ref before applying format
		String templates =
				"foo(x) ::= << <(t()); format=\"cap\"> >>\n" +
				"t() ::= <<ack>>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/t.stg");
		group.registerRenderer(String.class, new StringRenderer());
		ST st = group.getInstanceOf("foo");
		st.add("x", "hi");
		String expecting = " Ack ";
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testStringRendererWithSubtemplateInclude_cap() throws Exception {
		String templates =
				"foo(x) ::= << <({ack}); format=\"cap\"> >>\n" +
				"t() ::= <<ack>>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/t.stg");
		group.registerRenderer(String.class, new StringRenderer());
		ST st = group.getInstanceOf("foo");
		st.add("x", "hi");
		String expecting = " Ack ";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testStringRendererWithFormat_cap_emptyValue() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"cap\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", "");
        String expecting = " ";//FIXME: why not two spaces?
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testStringRendererWithFormat_url_encode() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"url-encode\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", "a b");
        String expecting = " a+b ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testStringRendererWithFormat_xml_encode() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"xml-encode\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", "a<b> &\t\b");
        String expecting = " a&lt;b&gt; &amp;\t&#8; ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testStringRendererWithFormat_xml_encode_null() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"xml-encode\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", null);
        String expecting = " ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testStringRendererWithPrintfFormat() throws Exception {
        String templates =
                "foo(x) ::= << <x; format=\"%6s\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", "hi");
        String expecting = "     hi ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testNumberRendererWithPrintfFormat() throws Exception {
        String templates =
                "foo(x,y) ::= << <x; format=\"%d\"> <y; format=\"%2.3f\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(Integer.class, new NumberRenderer());
        group.registerRenderer(Double.class, new NumberRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", -2100);
        st.add("y", 3.14159);
        String expecting = " -2100 3.142 ";
        String result = st.render();
        assertEquals(expecting, result);
    }

	@Test public void testInstanceofRenderer() throws Exception {
		String templates =
				"numberThing(x,y,z) ::= \"numbers: <x>, <y>; <z>\"\n";
		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/t.stg");
		group.registerRenderer(Number.class, new NumberRenderer());
		ST st = group.getInstanceOf("numberThing");
		st.add("x", -2100);
		st.add("y", 3.14159);
		st.add("z", "hi");
		String expecting = "numbers: -2100, 3.14159; hi";
		String result = st.render();
		assertEquals(expecting, result);
	}

    @Test public void testLocaleWithNumberRenderer() throws Exception {
        String templates =
                "foo(x,y) ::= <<\n" +
                "<x; format=\"%,d\"> <y; format=\"%,2.3f\">\n" +
                ">>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(Integer.class, new NumberRenderer());
        group.registerRenderer(Double.class, new NumberRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", -2100);
        st.add("y", 3.14159);
        // Polish uses ' ' (ASCII 160) for ',' and ',' for '.'
        String expecting = "-2\u00A0100 3,142";
        String result = st.render(new Locale("pl"));
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithFormatAndList() throws Exception {
        String template =
                "The names: <names; format=\"upper\">";
        STGroup group = new STGroup();
        group.registerRenderer(String.class, new StringRenderer());
        ST st = new ST(group, template);
        st.add("names", "ter");
        st.add("names", "tom");
        st.add("names", "sriram");
        String expecting = "The names: TERTOMSRIRAM";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithFormatAndSeparator() throws Exception {
        String template =
                "The names: <names; separator=\" and \", format=\"upper\">";
        STGroup group = new STGroup();
        group.registerRenderer(String.class, new StringRenderer());
        ST st = new ST(group, template);
        st.add("names", "ter");
        st.add("names", "tom");
        st.add("names", "sriram");
        String expecting = "The names: TER and TOM and SRIRAM";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithFormatAndSeparatorAndNull() throws Exception {
        String template =
                "The names: <names; separator=\" and \", null=\"n/a\", format=\"upper\">";
        STGroup group = new STGroup();
        group.registerRenderer(String.class, new StringRenderer());
        ST st = new ST(group, template);
        List<String> names = new ArrayList<String>();
        names.add("ter");
        names.add(null);
        names.add("sriram");
        st.add("names", names);
        String expecting = "The names: TER and N/A and SRIRAM";
        String result = st.render();
        assertEquals(expecting, result);
    }

	@Test public void testDateRendererWithLocale() {
		String input = "<date; format=\"dd 'de' MMMMM 'de' yyyy\">";
		STGroup group = new STGroup();
		group.registerRenderer(Calendar.class, new DateRenderer());
		ST st = new ST(group, input);

		Calendar cal = Calendar.getInstance();
		cal.set(2012, Calendar.JUNE, 12);
		st.add("date", cal);

		assertEquals("12 de junho de 2012", st.render(new Locale("pt")));
	}
}
