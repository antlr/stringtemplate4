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

import org.antlr.runtime.Token;
import org.stringtemplate.v4.compiler.GroupParser;

/** Used for semantic errors that occur at compile time not during
 *  interpretation. For ST parsing ONLY not group parsing.
 */
public class STCompiletimeMessage extends STMessage {
	/** overall token pulled from group file */
	public Token templateToken;
	/** token inside template */
    public Token token;
    public String srcName;

    public STCompiletimeMessage(ErrorType error, String srcName, Token templateToken, Token t) {
        this(error, srcName, templateToken, t, null);
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token templateToken, Token t, Throwable cause) {
        this(error, srcName, templateToken, t, cause, null);
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token templateToken, Token t,
                                Throwable cause, Object arg)
    {
		this(error, srcName, templateToken, t, cause, arg, null);
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token templateToken,
								Token t, Throwable cause, Object arg, Object arg2)
    {
		super(error, null, cause, arg, arg2);
		this.templateToken = templateToken;
		this.token = t;
		this.srcName = srcName;
    }

	@Override
    public String toString() {
		int line = 0;
		int charPos = -1;
		if ( token!=null ) {
			line = token.getLine();
			charPos = token.getCharPositionInLine();
			// check the input streams - if different then token is embedded in templateToken and we need to adjust the offset
			if ( templateToken!=null && !templateToken.getInputStream().equals(token.getInputStream()) ) {
				int templateDelimiterSize = 1;
				if ( templateToken.getType()== GroupParser.BIGSTRING || templateToken.getType()== GroupParser.BIGSTRING_NO_NL ) {
					templateDelimiterSize = 2;
				}
				line += templateToken.getLine() - 1;
				charPos += templateToken.getCharPositionInLine() + templateDelimiterSize;
			}
		}
        String filepos = line+":"+charPos;
        if ( srcName!=null ) {
            return srcName+" "+filepos+": "+String.format(error.message, arg, arg2);
        }
        return filepos+": "+String.format(error.message, arg, arg2);
    }
}
