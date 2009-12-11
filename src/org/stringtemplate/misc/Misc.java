package org.stringtemplate.misc;

import java.io.*;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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

    public static String trimOneStartingNewline(String s) {
        // strip newline from front but just one
        if ( s.startsWith("\r\n") ) s = s.substring(2);
        else if ( s.startsWith("\n") ) s = s.substring(1);
        return s;
    }

    public static String getFileNameNoSuffix(String f) {
        return f.substring(0,f.lastIndexOf('.'));
    }

    public static String readLines(String file) throws IOException {
        Reader r = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(r);
        StringBuilder buf = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            buf.append(line);
            buf.append('\n');
            line = br.readLine();
        }
        br.close();
        return buf.toString();
    }

    public static String replaceEscapes(String s) {
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\t", "\\\\t");
        return s;
    }

    /** Given index into string, compute the line and char position in line */
    public static Coordinate getLineCharPosition(String s, int index) {
        int line = 1;
        int charPos = 0;
        int p = 0;
        while ( p < index ) { // don't care about s[index] itself; count before
            if ( s.charAt(p)=='\n' ) { line++; charPos=0; }
            else charPos++;
            p++;
        }
        
        return new Coordinate(line,charPos);
    }

    public static Object accessField(Field f, Object o, Object value) throws IllegalAccessException {
        try {
            // make sure it's accessible (stupid java)
            f.setAccessible(true);
        }
        catch (SecurityException se) {
            ; // oh well; security won't let us
        }
        value = f.get(o);
        return value;
    }

    public static Object invokeMethod(Method m, Object o, Object value) throws IllegalAccessException, InvocationTargetException {
        try {
            // make sure it's accessible (stupid java)
            m.setAccessible(true);
        }
        catch (SecurityException se) {
            ; // oh well; security won't let us
        }
        value = m.invoke(o,(Object[])null);
        return value;
    }

    public static Method getMethod(Class c, String methodName) {
        Method m;
        try {
            m = c.getMethod(methodName, (Class[])null);
        }
        catch (NoSuchMethodException nsme) {
            m = null;
        }
        return m;
    }
}
