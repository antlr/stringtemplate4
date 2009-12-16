package org.stringtemplate;

import java.net.URLEncoder;
import java.util.*;

public class StringRenderer implements AttributeRenderer {
    // trim(s) and strlen(s) built-in funcs; these are format options
    public String toString(Object o, String formatString, Locale locale) {
        String s = (String)o;
        if ( formatString==null ) return s; 
        if ( formatString.equals("upper") ) return s.toUpperCase(locale);
        if ( formatString.equals("lower") ) return s.toLowerCase(locale);
        if ( formatString.equals("cap") ) {
            return Character.toUpperCase(s.charAt(0))+s.substring(1);
        }
        if ( formatString.equals("url-encode") ) {
            return URLEncoder.encode(s);
        }
        if ( formatString.equals("xml-encode") ) {
            return escapeHTML(s); // TODO: impl
        }
        return String.format(formatString, s);
    }

    public static String escapeHTML(String s) {
        if ( s==null ) {
            return null;
        }
        StringBuilder buf = new StringBuilder( s.length() );
        int len = s.length();
        for (int i=0; i<len; i++) {
            char c = s.charAt(i);
            switch ( c ) {
                case '&' :
                    buf.append("&amp;");
                    break;
                case '<' :
                    buf.append("&lt;");
                    break;
                case '>' :
                    buf.append("&gt;");
                    break;
                case '\r':
                case '\n':
                case '\t':
                    buf.append(c);
                    break;
                default:
                    boolean control = c < ' '; // 32
                    boolean aboveASCII = c > 126;
                    if ( control || aboveASCII ) buf.append("&#"+(int)c+";");
                    else buf.append(c);
            }
        }
        return buf.toString();
    }
}
