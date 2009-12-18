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

import org.antlr.runtime.Token;
import org.antlr.runtime.RecognitionException;
import org.stringtemplate.misc.ErrorType;
import org.stringtemplate.misc.STMessage;

public class STSyntaxErrorMessage extends STMessage {
    String msg;
    
    public STSyntaxErrorMessage(ErrorType error, Token t) { this(error, t, null); }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause) {
        this(error, t, cause, null);
    }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause, String msg) {
        this(error, t, cause, msg, null);
    }
    public STSyntaxErrorMessage(ErrorType error, Token t, Throwable cause, String msg, Object arg) {
        super(error, null, cause, arg);
        this.msg = msg;
    }

    public String toString() {
        RecognitionException re = (RecognitionException)cause;
        String hdr = re.line+":"+re.charPositionInLine;
        if ( arg==null ) return String.format(error.messageTemplate, hdr+": "+msg);
        return String.format(error.messageTemplate, arg+" "+hdr+": "+msg);
    }
}
