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

/** Generic StringTemplate output writer filter.
 *
 *  Literals and the elements of expressions are emitted via write().
 *  Separators are emitted via writeSeparator() because they must be
 *  handled specially when wrapping lines (we don't want to wrap
 *  in between an element and it's separator).
 */
public interface STWriter {
	public static final int NO_WRAP = -1;

    void pushIndentation(String indent);

    String popIndentation();

	void pushAnchorPoint();

	void popAnchorPoint();

	void setLineWidth(int lineWidth);

	/** Write the string and return how many actual chars were written.
	 *  With autoindentation and wrapping, more chars than length(str)
	 *  can be emitted.  No wrapping is done.
	 */
	int write(String str) throws IOException;

	/** Same as write, but wrap lines using the indicated string as the
	 *  wrap character (such as "\n").
	 */
	int write(String str, String wrap) throws IOException;

	/** Because we evaluate ST instance by invoking exec() again, we
     *  can't pass options in.  So the WRITE instruction of an applied
     *  template (such as when we wrap in between template applications
	 *  like <data:{v|[<v>]}; wrap>) we need to write the wrap string
	 *  before calling exec().  We expose just like for the separator.
     *  See Interpreter.writeObject where it checks for ST instance.
     *  If POJO, writePOJO passes wrap to STWriter's
     *
     *     write(String str, String wrap)
     *
     *  method.  Can't pass to exec(). 
	 */
	int writeWrap(String wrap) throws IOException;

	/** Write a separator.  Same as write() except that a \n cannot
	 *  be inserted before emitting a separator.
	 */
	int writeSeparator(String str) throws IOException;

    /** Return the absolute char index into the output of the char
     *  we're about to write.  Returns 0 if no char written yet.
     */
    int index();
}
