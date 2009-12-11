package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.StringReader;

public class TestRenderers extends BaseTest {
    @Test public void testRendererForGroup() throws Exception {
        String templates =
                "dateThing(created) ::= \"datetime: <created>\"\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
        String expecting = "datetime: 7/5/05 12:00 AM";
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
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
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
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
        String expecting = " datetime: 7/5/05 12:00 AM ";
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
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
        String expecting = " datetime: Tuesday, July 5, 2005 12:00:00 AM PDT ";
        String result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithPredefinedFormat3() throws Exception {
        String templates =
                "dateThing(created) ::= << date: <created; format=\"date:medium\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
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
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
        String expecting = " time: 12:00:00 AM ";
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

    @Test public void testLocaleWithNumberRenderer() throws Exception {
        String templates =
                "foo(x,y) ::= << <x; format=\"%,d\"> <y; format=\"%,2.3f\"> >>\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(Integer.class, new NumberRenderer());
        group.registerRenderer(Double.class, new NumberRenderer());
        ST st = group.getInstanceOf("foo");
        st.add("x", -2100);
        st.add("y", 3.14159);
        // Polish uses ' ' for ',' and ',' for '.'
        String expecting = " -2Ê100 3,142 ";
        String result = st.render(new Locale("pl"));
        assertEquals(expecting, result);
    }

    @Test public void testRendererWithFormatAndList() throws Exception {
        String template =
                "The names: <names; format=\"upper\">";
        STGroup group = new STGroup();
        group.registerRenderer(String.class, new StringRenderer());
        ST st = new ST(template);
        st.groupThatCreatedThisInstance = group;
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
        ST st = new ST(template);
        st.groupThatCreatedThisInstance = group;
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
        ST st = new ST(template);
        st.groupThatCreatedThisInstance = group;
        List names = new ArrayList();
        names.add("ter");
        names.add(null);
        names.add("sriram");
        st.add("names", names);
        String expecting = "The names: TER and N/A and SRIRAM";
        String result = st.render();
        assertEquals(expecting, result);
    }
    
}
