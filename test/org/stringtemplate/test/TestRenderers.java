package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;

public class TestRenderers extends BaseTest {
    /*
    public class DateRenderer implements AttributeRenderer {
        public String toString(Object o) {
            SimpleDateFormat f = new SimpleDateFormat ("yyyy.MM.dd");
            return f.format(((Calendar)o).getTime());
        }
        public String toString(Object o, String formatString) {
            return toString(o);
        }
    }

    public class DateRenderer2 implements AttributeRenderer {
        public String toString(Object o) {
            SimpleDateFormat f = new SimpleDateFormat ("MM/dd/yyyy");
            return f.format(((Calendar)o).getTime());
        }
        public String toString(Object o, String formatString) {
            return toString(o);
        }
    }

    public class DateRenderer3 implements AttributeRenderer {
        public String toString(Object o) {
            SimpleDateFormat f = new SimpleDateFormat ("MM/dd/yyyy");
            return f.format(((Calendar)o).getTime());
        }
        public String toString(Object o, String formatString) {
            SimpleDateFormat f = new SimpleDateFormat (formatString);
            return f.format(((Calendar)o).getTime());
        }
    }
    */

    public class StringRenderer implements AttributeRenderer {
        public String toString(Object o) {
            return (String)o;
        }
        public String toString(Object o, String formatString) {
            if ( formatString.equals("upper") ) {
                return ((String)o).toUpperCase();
            }
            return toString(o);
        }
    }

    @Test public void testRendererForGroup() throws Exception {
        String templates =
                "dateThing(created) ::= \"date: <created>\"\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/t.stg");
        group.registerRenderer(GregorianCalendar.class, new DateRenderer());
        ST st = group.getInstanceOf("dateThing");
        st.add("created", new GregorianCalendar(2005, 07-1, 05));
        String expecting = "date: 7/5/05 12:00 AM";
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
