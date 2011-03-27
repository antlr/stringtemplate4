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

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;

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
            String longestName =
                Collections.max(attrNames,
                                new Comparator<String>() {
                                    public int compare(String s1, String s2) {
                                        return s1.length() - s2.length();
                                    }
                                });
            int w = longestName.length();
			for (Object attrName : attrNames) {
				String name = (String) attrName;
				buf.append(Misc.newline);
				indent(buf, n);
				buf.append(String.format("%-" + w + "s = ", name));
				Object value = self.getAttributes().get(name);
				buf.append(getValueDebugString(value, n));
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
        if ( self.impl.formalArguments!=null ) buf.append(self.impl.formalArguments.keySet());
        buf.append(")@");
        buf.append(String.valueOf(hashCode()));
        buf.append(">");
        return buf.toString();
    }

    protected void indent(StringBuilder buf, int n) {
        for (int i=1; i<=n; i++) buf.append("   ");
    }
}
