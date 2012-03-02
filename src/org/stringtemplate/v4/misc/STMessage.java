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
import org.stringtemplate.v4.ST;

import java.io.PrintWriter;
import java.io.StringWriter;

/** Upon error, ST creates an {@link STMessage} or subclass instance and notifies
 *  the listener.  This root class is used for IO and internal errors.
 *
 *  @see STRuntimeMessage
 *  @see STCompiletimeMessage
 */
public class STMessage {
    /** if in debug mode, has created instance, add attr events and eval
     *  template events.
     */
    public ST self;
    public ErrorType error;
    public Object arg;
	public Object arg2;
	public Object arg3;
    public Throwable cause;

    public STMessage(ErrorType error) {
        this.error = error;
    }
    public STMessage(ErrorType error, ST self) {
        this(error);
        this.self = self;
    }
    public STMessage(ErrorType error, ST self, Throwable cause) {
        this(error,self);
        this.cause = cause;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Object arg) {
        this(error,self,cause);
        this.arg = arg;
    }
    public STMessage(ErrorType error, ST self, Throwable cause, Token where, Object arg) {
        this(error,self,cause,where);
        this.arg = arg;
    }
	public STMessage(ErrorType error, ST self, Throwable cause, Object arg, Object arg2) {
		this(error,self,cause,arg);
		this.arg2 = arg2;
	}
	public STMessage(ErrorType error, ST self, Throwable cause, Object arg, Object arg2, Object arg3) {
		this(error,self,cause,arg,arg2);
		this.arg3 = arg3;
	}

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String msg = String.format(error.message, arg, arg2, arg3);
        pw.print(msg);
        if ( cause!=null ) {
            pw.print("\nCaused by: ");
            cause.printStackTrace(pw);
        }
        return sw.toString();
    }
}
