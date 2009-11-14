package org.stringtemplate;

import java.util.*;

/** Works with Byte, Short, Integer, Long, and BigInteger as well as
 *  Float, Double, and BigDecimal.  You pass in a format string suitable
 *  for Formatter object:
 *
 *  http://java.sun.com/j2se/1.5.0/docs/api/java/util/Formatter.html
 *
 *  Can even do longs to date conversions.
 */
public class NumberRenderer implements AttributeRenderer {
    public String toString(Object o, String formatString, Locale locale) {
        // o will be instanceof Number
        if ( formatString==null ) return o.toString();
        Formatter f = new Formatter(locale);
        f.format(formatString, o);
        return f.toString();
    }
}
