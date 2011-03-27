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
package org.stringtemplate.v4;

import java.net.URLEncoder;
import java.util.Locale;

/** This render knows to perform a few operations on String objects:
 *  upper, lower, cap, url-encode, xml-encode.
 */
public class StringRenderer implements AttributeRenderer {
    // trim(s) and strlen(s) built-in funcs; these are format options
    public String toString(Object o, String formatString, Locale locale) {
        String s = (String)o;
        if ( formatString==null ) return s;
        if ( formatString.equals("upper") ) return s.toUpperCase(locale);
        if ( formatString.equals("lower") ) return s.toLowerCase(locale);
        if ( formatString.equals("cap") ) {
            return (s.length() > 0) ? Character.toUpperCase(s.charAt(0))+s.substring(1) : s;
        }
        if ( formatString.equals("url-encode") ) {
            return URLEncoder.encode(s);
        }
        if ( formatString.equals("xml-encode") ) {
            return escapeHTML(s);
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
                    if ( control || aboveASCII ) {
                        buf.append("&#");
                        buf.append((int)c);
                        buf.append(";");
                    }
                    else buf.append(c);
            }
        }
        return buf.toString();
    }
}
