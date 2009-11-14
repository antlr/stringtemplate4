package org.stringtemplate;

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
            return s; // TODO: impl
        }
        if ( formatString.equals("xml-encode") ) {
            return s; // TODO: impl
        }
        return String.format(formatString, s);
    }
}
