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

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/** */
public class STGroupCompiletimeMessage extends STMessage {
	/** token inside group file */
	public Token token;
	public String srcName;

	public STGroupCompiletimeMessage(ErrorType error, String srcName, Token t, Throwable cause) {
		this(error, srcName, t, cause, null);
	}
	public STGroupCompiletimeMessage(ErrorType error, String srcName, Token t,
									 Throwable cause, Object arg)
	{
		this(error, srcName, t, cause, arg, null);
	}
	public STGroupCompiletimeMessage(ErrorType error, String srcName,
									 Token t, Throwable cause, Object arg, Object arg2)
	{
		super(error, null, cause, arg, arg2);
		this.token = t;
		this.srcName = srcName;
	}

	@Override
	public String toString() {
        RecognitionException re = (RecognitionException)cause;
		int line = 0;
		int charPos = -1;
		if ( token!=null ) {
			line = token.getLine();
			charPos = token.getCharPositionInLine();
		}
		else if ( re!=null ) {
			line = re.line;
			charPos = re.charPositionInLine;
		}
        String filepos = line+":"+charPos;
        if ( srcName!=null ) {
            return srcName+" "+filepos+": "+String.format(error.message, arg, arg2);
        }
        return filepos+": "+String.format(error.message, arg, arg2);
    }
}
