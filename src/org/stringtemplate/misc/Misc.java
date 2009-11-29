package org.stringtemplate.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Arrays;

public class Misc {
    public static final String newline = System.getProperty("line.separator");

    // Seriously: why isn't this built in to java?
    public static String join(Iterator iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while ( iter.hasNext() ) {
            buf.append(iter.next());
            if ( iter.hasNext() ) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    public static String join(Object[] a, String separator, int start, int stop) {
        StringBuilder buf = new StringBuilder();
        for (int i = start; i < stop; i++) {
            if ( i>start ) buf.append(separator);
            buf.append(a[i].toString());
        }
        return buf.toString();
    }

    public static String strip(String s, int n) {
        return s.substring(n, s.length()-n);
    }

    public static String stripRight(String s, int n) {
        return s.substring(0, s.length()-n);
    }

    public static String trimOneStartingWS(String s) {
        // strip newline from front and back, but just one
        if ( s.startsWith("\r\n") ) s = s.substring(2);
        else if ( s.startsWith("\n") ) s = s.substring(1);
        /*
        if ( s.endsWith("\r\n") ) s = s.substring(0,s.length()-2);
        else if ( s.endsWith("\n") ) s = s.substring(0,s.length()-1);
         */
        return s;
    }

    public static String getFileNameNoSuffix(String f) {
        return f.substring(0,f.lastIndexOf('.'));
    }

    /*
    public static String trimRight(String s) {
        if ( s==null || s.length()==0 ) return s;
        int i = s.length()-1;
        while ( i>=0 && STLexer.isWS(s.charAt(i)) ) {
            i--;
        }
        return s.substring(0,i+1);
    }
    */

    public static String replaceEscapes(String s) {
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\t", "\\\\t");
        return s;
    }
}
