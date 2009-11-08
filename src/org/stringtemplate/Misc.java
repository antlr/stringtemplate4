package org.stringtemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Arrays;

public class Misc {
    public static final String newline = System.getProperty("line.separator");

    public static void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
            if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

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
}
