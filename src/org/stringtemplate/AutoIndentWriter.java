/*
 [The "BSD licence"]
 Copyright (c) 2003-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/** Essentially a char filter that knows how to auto-indent output
 *  by maintaining a stack of indent levels.  I set a flag upon newline
 *  and then next nonwhitespace char resets flag and spits out indention.
 *  The indent stack is a stack of strings so we can repeat original indent
 *  not just the same number of columns (don't have to worry about tabs vs
 *  spaces then).
 *
 *  Anchors are char positions (tabs won't work) that indicate where all
 *  future wraps should justify to.  The wrap position is actually the
 *  larger of either the last anchor or the indentation level.
 *
 *  This is a filter on a Writer.
 *
 *  \n is the proper way to say newline for options and templates.
 *  Templates can mix them but use \n for sure and options like
 *  wrap="\n". ST will generate the right thing. Override the default (locale)
 *  newline by passing in a string to the constructor.
 */
public class AutoIndentWriter implements STWriter {
	/** stack of indents; use List as it's much faster than Stack. Grows
	 *  from 0..n-1.  List<String>
	 */
	protected List indents = new ArrayList();

	/** Stack of integer anchors (char positions in line); avoid Integer
	 *  creation overhead.
	 */
	protected int[] anchors = new int[10];
	protected int anchors_sp = -1;

	/** \n or \r\n? */
	protected String newline;

	protected Writer out = null;
    protected boolean atStartOfLine = true;

	/** Track char position in the line (later we can think about tabs).
	 *  Indexed from 0.  We want to keep charPosition <= lineWidth.
	 *  This is the position we are *about* to write not the position
	 *  last written to.
	 */
	protected int charPosition = 0;
	protected int lineWidth = NO_WRAP;

	protected int charPositionOfStartOfExpr = 0;

	public AutoIndentWriter(Writer out, String newline) {
		this.out = out;
		indents.add(null); // s oftart with no indent
		this.newline = newline;
	}

	public AutoIndentWriter(Writer out) {
		this(out, System.getProperty("line.separator"));
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/** Push even blank (null) indents as they are like scopes; must
     *  be able to pop them back off stack.
     *
     *  To deal with combined anchors and indentation, force indents to
     *  include any current anchor point.  If current anchor is beyond
     *  current indent width, add the difference to the indent to be added.
     *
     *  This prevents a check later to deal with anchors when starting new line.
     */
    public void pushIndentation(String indent) {
        int lastAnchor = 0;
        int indentWidth = getIndentationWidth();
        // If current anchor is beyond current indent width, add in difference
        if ( anchors_sp>=0 && anchors[anchors_sp]>indentWidth ) {
            lastAnchor = anchors[anchors_sp];
            StringBuffer buf = getIndentString(lastAnchor-indentWidth);
            if ( indent!=null ) buf.append(indent); // don't add if null
            indents.add(buf.toString());
            return;
        }
        indents.add(indent);        
    }

    public String popIndentation() {
        return (String)indents.remove(indents.size()-1);
    }

	public void pushAnchorPoint() {
		if ( (anchors_sp +1)>=anchors.length ) {
			int[] a = new int[anchors.length*2];
			System.arraycopy(anchors, 0, a, 0, anchors.length-1);
			anchors = a;
		}
		anchors_sp++;
		anchors[anchors_sp] = charPosition;
	}

	public void popAnchorPoint() {
		anchors_sp--;
	}

	public int getIndentationWidth() {
		int n = 0;
        for (int i=0; i<indents.size(); i++) {
            String ind = (String)indents.get(i);
            if ( ind!=null ) {
                n+=ind.length();
			}
        }
		return n;
	}

	/** Write out a string literal or attribute expression or expression element.*/
	public int write(String str) throws IOException {
		int n = 0;
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			// found \n or \r\n newline?
			if ( c=='\r' || c=='\n' ) {
				atStartOfLine = true;
				charPosition = -1; // set so the write below sets to 0
				n += newline.length();
				out.write(newline);
				charPosition += n; // wrote n more char 
				// skip an extra char upon \r\n
				if ( (c=='\r' && (i+1)<str.length() && str.charAt(i+1)=='\n') ) {
					i++; // loop iteration i++ takes care of skipping 2nd char
				}
				continue;
			}
			// normal character
			// check to see if we are at the start of a line; need indent if so
			if ( atStartOfLine ) {
                n+=indent();
                atStartOfLine = false;
			}
			n++;
			out.write(c);
			charPosition++;
		}
		return n;
	}

    public int writeSeparator(String str) throws IOException {
		return write(str);
	}

	/** Write out a string literal or attribute expression or expression element.
	 *
	 *  If doing line wrap, then check wrap before emitting this str.  If
	 *  at or beyond desired line width then emit a \n and any indentation
	 *  before spitting out this str.
	 */
	public int write(String str, String wrap) throws IOException {
		int n = writeWrapSeparator(wrap);
		return n + write(str);
	}

	public int writeWrapSeparator(String wrap) throws IOException {
		int n = 0;
		// if want wrap and not already at start of line (last char was \n)
		// and we have hit or exceeded the threshold
		if ( lineWidth!=NO_WRAP && wrap!=null && !atStartOfLine &&
			 charPosition >= lineWidth )
		{
			// ok to wrap
			// Walk wrap string and look for A\nB.  Spit out A\n
			// then spit indent or anchor, whichever is larger
			// then spit out B.
			for (int i=0; i<wrap.length(); i++) {
				char c = wrap.charAt(i);
				if ( c=='\n' ) {
					n++;
					out.write(c);
					charPosition = 0;
                    n+=indent();
					// continue writing any chars out
				}
				else {  // write A or B part
					n++;
					out.write(c);
					charPosition++;
				}
			}
		}
		return n;
	}

	public int indent() throws IOException {
		int n = 0;
		for (int i=0; i<indents.size(); i++) {
			String ind = (String)indents.get(i);
			if ( ind!=null ) {
				n+=ind.length();
				out.write(ind);
			}
		}
		charPosition += n;
		return n;
	}

	public int indent(int spaces) throws IOException {
		for (int i=1; i<=spaces; i++) {
			out.write(' ');
		}
		charPosition += spaces;
		return spaces;
	}

    protected StringBuffer getIndentString(int spaces) {
        StringBuffer buf = new StringBuffer();
        for (int i=1; i<=spaces; i++) {
            buf.append(' ');
        }
        return buf;
    }
}
