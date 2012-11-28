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
import org.stringtemplate.v4.compiler.GroupParser;

/** */
public class STLexerMessage extends STMessage {
	public String msg;
	/** overall token pulled from group file */
	public Token templateToken;
	public String srcName;

	public STLexerMessage(String srcName, String msg, Token templateToken, Throwable cause) {
		super(ErrorType.LEXER_ERROR, null, cause, null);
		this.msg = msg;
		this.templateToken = templateToken;
		this.srcName = srcName;
	}

	@Override
	public String toString() {
		RecognitionException re = (RecognitionException)cause;
		int line = re.line;
		int charPos = re.charPositionInLine;
		if ( templateToken!=null ) {
			int templateDelimiterSize = 1;
			if ( templateToken.getType()== GroupParser.BIGSTRING ) {
				templateDelimiterSize = 2;
			}
			line += templateToken.getLine() - 1;
			charPos += templateToken.getCharPositionInLine() + templateDelimiterSize;
		}
		String filepos = line+":"+charPos;
		if ( srcName!=null ) {
			return srcName+" "+filepos+": "+String.format(error.message, msg);
		}
		return filepos+": "+String.format(error.message, msg);
	}
}
