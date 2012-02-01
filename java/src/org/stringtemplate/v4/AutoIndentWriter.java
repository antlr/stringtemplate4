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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/** Essentially a char filter that knows how to auto-indent output
 *  by maintaining a stack of indent levels.
 *
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
 *  Templates can mix \r\n and \n them but use \n for sure in options like
 *  wrap="\n". ST will generate the right thing. Override the default (locale)
 *  newline by passing in a string to the constructor.
 */
public class AutoIndentWriter implements STWriter {
	/** stack of indents; use List as it's much faster than Stack. Grows
	 *  from 0..n-1.
	 */
	public List<String> indents = new ArrayList<String>();

	/** Stack of integer anchors (char positions in line); avoid Integer
	 *  creation overhead.
	 */
	public int[] anchors = new int[10];
	public int anchors_sp = -1;

	/** \n or \r\n? */
	public String newline;

	public Writer out = null;
    public boolean atStartOfLine = true;

	/** Track char position in the line (later we can think about tabs).
	 *  Indexed from 0.  We want to keep charPosition <= lineWidth.
	 *  This is the position we are *about* to write not the position
	 *  last written to.
	 */
    public int charPosition = 0;

    /** The absolute char index into the output of the next char to be written. */
    public int charIndex = 0;

	public int lineWidth = NO_WRAP;

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

    public void pushIndentation(String indent) {
        indents.add(indent);
    }

    public String popIndentation() {
        return indents.remove(indents.size()-1);
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

    public int index() { return charIndex; }

	/** Write out a string literal or attribute expression or expression element.*/
	public int write(String str) throws IOException {
		int n = 0;
		int nll = newline.length();
		int sl = str.length();
		for (int i=0; i<sl; i++) {
			char c = str.charAt(i);
			// found \n or \r\n newline?
			if ( c=='\r' ) continue;
            if ( c=='\n' ) {
				atStartOfLine = true;
				charPosition = -nll; // set so the write below sets to 0
				out.write(newline);
				n += nll;
				charIndex += nll;
				charPosition += n; // wrote n more char
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
			charIndex++;
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
		int n = writeWrap(wrap);
		return n + write(str);
	}

	public int writeWrap(String wrap) throws IOException {
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
				if ( c=='\r' ) {
					continue;
				} else if ( c=='\n' ) {
					out.write(newline);
                    n += newline.length();
					charPosition = 0;
                    charIndex += newline.length();
                    n += indent();
					// continue writing any chars out
				}
				else {  // write A or B part
					n++;
					out.write(c);
					charPosition++;
                    charIndex++;
				}
			}
		}
		return n;
	}

	public int indent() throws IOException {
		int n = 0;
		for (String ind : indents) {
			if (ind != null) {
				n += ind.length();
				out.write(ind);
			}
		}

        // If current anchor is beyond current indent width, indent to anchor
        // *after* doing indents (might tabs in there or whatever)
        int indentWidth = n;
        if ( anchors_sp>=0 && anchors[anchors_sp]>indentWidth ) {
            int remainder = anchors[anchors_sp]-indentWidth;
            for (int i=1; i<=remainder; i++) out.write(' ');
            n += remainder;
        }

        charPosition += n;
        charIndex += n;
		return n;
	}
}
