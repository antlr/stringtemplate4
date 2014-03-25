/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.v4.misc;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Iterator;

public class Misc {
    public static final String newline = System.getProperty("line.separator");

	/** Makes it clear when a comparison is intended as reference equality.
	 */
	public static boolean referenceEquals(Object x, Object y) {
		return x == y;
	}

    // Seriously: why isn't this built in to java?
    public static String join(Iterator<?> iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while ( iter.hasNext() ) {
            buf.append(iter.next());
            if ( iter.hasNext() ) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

//    public static String join(Object[] a, String separator, int start, int stop) {
//        StringBuilder buf = new StringBuilder();
//        for (int i = start; i < stop; i++) {
//            if ( i>start ) buf.append(separator);
//            buf.append(a[i].toString());
//        }
//        return buf.toString();
//    }

    public static String strip(String s, int n) {
        return s.substring(n, s.length()-n);
    }

//    public static String stripRight(String s, int n) {
//        return s.substring(0, s.length()-n);
//    }

    /** Strip a single newline character from the front of {@code s}. */
    public static String trimOneStartingNewline(String s) {
        if ( s.startsWith("\r\n") ) s = s.substring(2);
        else if ( s.startsWith("\n") ) s = s.substring(1);
        return s;
    }

    /** Strip a single newline character from the end of {@code s}. */
    public static String trimOneTrailingNewline(String s) {
        if ( s.endsWith("\r\n") ) s = s.substring(0, s.length()-2);
        else if ( s.endsWith("\n") ) s = s.substring(0, s.length()-1);
        return s;
    }

	/** Given, say, {@code file:/tmp/test.jar!/org/foo/templates/main.stg}
	 *  convert to {@code file:/tmp/test.jar!/org/foo/templates}
	 */
	public static String stripLastPathElement(String f) {
		int slash = f.lastIndexOf('/');
		if ( slash<0 ) return f;
		return f.substring(0, slash);
	}

    public static String getFileNameNoSuffix(String f) {
		if (f==null) return null;
		f = getFileName(f);
        return f.substring(0,f.lastIndexOf('.'));
    }

    public static String getFileName(String fullFileName) {
		if (fullFileName==null) return null;
        File f = new File(fullFileName); // strip to simple name
        return f.getName();
    }

	public static String getParent(String name) {
		//System.out.println("getParent("+name+")="+p);
		if (name==null) return null;
		int lastSlash=name.lastIndexOf('/');
		if (lastSlash>0) return name.substring(0, lastSlash);
		if (lastSlash==0) return "/";
		//System.out.println("getParent("+name+")="+p);
		return "";
	}

	public static String getPrefix(String name) {
		if (name==null) return "/";
		String parent = getParent(name);
		String prefix = parent;
		if ( !parent.endsWith("/") ) prefix += '/';
		return prefix;
	}

    public static String replaceEscapes(String s) {
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\t", "\\\\t");
        return s;
    }

	/** Replace &gt;\&gt; with &gt;&gt; in s. Replace \&gt;&gt; unless prefix of \&gt;&gt;&gt; with &gt;&gt;.
	 *  Do NOT replace if it's &lt;\\&gt;
	 */
	public static String replaceEscapedRightAngle(String s) {
		StringBuilder buf = new StringBuilder();
		int i = 0;
		while ( i<s.length() ) {
			char c = s.charAt(i);
			if ( c=='<' && s.substring(i).startsWith("<\\\\>") ) {
				buf.append("<\\\\>");
				i += "<\\\\>".length();
				continue;
			}
			if ( c=='>' && s.substring(i).startsWith(">\\>") ) {
				buf.append(">>");
				i += ">\\>".length();
				continue;
			}
			if ( c=='\\' && s.substring(i).startsWith("\\>>") &&
				!s.substring(i).startsWith("\\>>>") )
			{
				buf.append(">>");
				i += "\\>>".length();
				continue;
			}
			buf.append(c);
			i++;
		}
		return buf.toString();
	}

	public static boolean urlExists(URL url) {
		try {
			URLConnection connection = url.openConnection();
			if (connection instanceof JarURLConnection) {
				JarURLConnection jarURLConnection = (JarURLConnection)connection;
				URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { jarURLConnection.getJarFileURL() });
				try {
					return urlClassLoader.findResource(jarURLConnection.getEntryName()) != null;
				}
				finally {
					if (urlClassLoader instanceof Closeable) {
						((Closeable)urlClassLoader).close();
					}
				}
			}

			InputStream is = null;
			try {
				is = url.openStream();
			}
			finally {
				if (is != null) {
					is.close();
				}
			}

			return is != null;
		}
		catch (IOException ioe) {
			return false;
		}
	}

	/**
	 * Given {@code index} into string {@code s}, compute the line and char
	 * position in line.
	 */
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
}
