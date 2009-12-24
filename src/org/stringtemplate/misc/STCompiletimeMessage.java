/*
 [The "BSD licence"]
 Copyright (c) 2009 Terence Parr
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
package org.stringtemplate.misc;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/** Used for semantic errors that occur at compile time not during
 *  interpretation.
 */
public class STCompiletimeMessage extends STMessage {
    Token token;
    String srcName;

    public STCompiletimeMessage(ErrorType error, String srcName, Token t) {
        this(error, srcName, t, null);
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token t, Throwable cause) {
        this(error, srcName, t, cause, null);
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token t,
                                Throwable cause, Object arg)
    {
        super(error, null, cause, arg);
        this.token = t;
        this.srcName = srcName;
    }
    public STCompiletimeMessage(ErrorType error, String srcName, Token t,
                                Throwable cause, Object arg, Object arg2)
    {
        super(error, null, cause, arg, arg2);
        this.token = t;
        this.srcName = srcName;
    }

    public String toString() {
        RecognitionException re = (RecognitionException)cause;
        String filepos = null;
        if ( re!=null ) {
            filepos = re.line+":"+re.charPositionInLine;
        }
        else if ( token!=null ) {
            filepos = token.getLine()+":"+token.getCharPositionInLine();
        }
        if ( srcName!=null ) {
            return srcName+" "+filepos+": "+String.format(error.message, arg, arg2);
        }
        return filepos+": "+String.format(error.message, arg, arg2);
    }
}