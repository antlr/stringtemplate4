package org.stringtemplate.misc;

import org.stringtemplate.Interpreter;
import org.stringtemplate.ST;

import java.util.*;

/** This class dumps out a hierarchy of templates in text form, indented
 *  to show the nested relationship.  Usage:
 *
 *     ST st = ...;
 *     STDump d = new STDump(st);
 *     System.out.println(d.toString());
 */
public class STDump {
    ST self;
    public STDump(ST self) { this.self = self; }

    public static String toString(ST self) {
        STDump d = new STDump(self);
        return d.toString();
    }

    public String toString() { return toString(0); }

    protected String toString(int n) {
        StringBuilder buf = new StringBuilder();
        buf.append(getTemplateDeclaratorString()+":");
        n++;
        if ( self.getAttributes()!=null ) {
            List<String> attrNames = new ArrayList<String>();
            attrNames.addAll(self.getAttributes().keySet());
            Collections.sort(attrNames);
            String longestName = (String)
                Collections.max(attrNames,
                                new Comparator<String>() {
                                    public int compare(String s1, String s2) {
                                        return s1.length() - s2.length();
                                    }
                                });
            int w = longestName.length();
            for (Iterator iter = attrNames.iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                buf.append(Misc.newline);
                indent(buf, n);
                buf.append(String.format("%-"+w+"s = ",name));
                Object value = self.getAttributes().get(name);
                buf.append( getValueDebugString(value, n) );
            }
        }
        buf.append(Misc.newline);
        n--;
        indent(buf, n);
        buf.append("]");
        return buf.toString();
    }

    protected String getValueDebugString(Object value, int n) {
        StringBuffer buf = new StringBuffer();
        value = Interpreter.convertAnythingIteratableToIterator(value);
        if ( value instanceof ST ) {
            STDump d = new STDump((ST)value);
            buf.append(d.toString(n));
        }
        else if ( value instanceof Iterator ) {
            Iterator it = (Iterator)value;
            int na=0;
            while ( it.hasNext() ) {
                String v = getValueDebugString(it.next(), n);
                if ( na>0 ) buf.append(", ");
                buf.append(v);
                na++;
            }
        }
        else {
            buf.append(value);
        }
        return buf.toString();
    }

    protected String getTemplateDeclaratorString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<");
        buf.append(self.getName());
        buf.append("(");
        if ( self.impl.formalArguments!=null ) {
            buf.append(self.impl.formalArguments.keySet());
        }
        buf.append(")@");
        buf.append(String.valueOf(hashCode()));
        buf.append(">");
        return buf.toString();
    }

    protected void indent(StringBuilder buf, int n) {
        for (int i=1; i<=n; i++) buf.append("   ");
    }
}
