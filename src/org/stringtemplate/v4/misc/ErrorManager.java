/*
 [The "BSD license"]
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
package org.stringtemplate.v4.misc;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;

/** Track errors per thread; e.g., one server transaction's errors
 *  will go in one grouping since each has it's own thread.
 */
public class ErrorManager {

    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
            public void compileTimeError(STMessage msg) {
                System.err.println(msg);
            }

            public void runTimeError(STMessage msg) {
                if ( msg.error != ErrorType.NO_SUCH_PROPERTY ) { // ignore these
                    System.err.println(msg);
                }
            }

            public void IOError(STMessage msg) {
                System.err.println(msg);
            }

            public void internalError(STMessage msg) {
                System.err.println(msg);
                // throw new Error("internal error", msg.cause);
            }

            public void error(String s) { error(s, null); }
            public void error(String s, Throwable e) {
                System.err.println(s);
                if ( e!=null ) {
                    e.printStackTrace(System.err);
                }
            }
        };

    /** Gives us a new listener per thread.  If your server reuses threads,
     *  these thread locals might not go away.  Might need to manually reset.
     */
    static ThreadLocal<STErrorListener> listener = new ThreadLocal<STErrorListener>() {
        protected STErrorListener initialValue() { return DEFAULT_ERROR_LISTENER; }
    };

    public static void setErrorListener(STErrorListener listener) { ErrorManager.listener.set(listener); }

    public static void compileTimeError(ErrorType error, Token t) {
        String srcName = t.getInputStream().getSourceName();
        if ( srcName!=null ) srcName = Misc.getFileName(srcName);
        listener.get().compileTimeError(
            new STCompiletimeMessage(error,srcName,t,null,t.getText())
        );
    }

    public static void lexerError(ErrorType error, RecognitionException e, Object arg) {
        listener.get().compileTimeError(
            new STCompiletimeMessage(error,null,null,e,arg)
        );
    }

    public static void compileTimeError(ErrorType error, Token t, Object arg) {
        String srcName = t.getInputStream().getSourceName();
        srcName = Misc.getFileName(srcName);
        listener.get().compileTimeError(
            new STCompiletimeMessage(error,srcName,t,null,arg)
        );
    }

    public static void compileTimeError(ErrorType error, Token t, Object arg, Object arg2) {
        String srcName = t.getInputStream().getSourceName();
        if ( srcName!=null ) srcName = Misc.getFileName(srcName);
        listener.get().compileTimeError(
            new STCompiletimeMessage(error,srcName,t,null,arg,arg2)
        );
    }

    public static void syntaxError(ErrorType error, String srcName, RecognitionException e, String msg) {
        listener.get().compileTimeError(
            new STCompiletimeMessage(error,srcName,e.token,e,msg)
        );
    }

    public static void runTimeError(ST self, int ip, ErrorType error) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self));
    }

    public static void runTimeError(ST self, int ip, ErrorType error, Object arg) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self,arg));
    }

    public static void runTimeError(ST self, int ip, ErrorType error, Throwable e, Object arg) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self,e,arg));
    }

	public static void runTimeError(ST self, int ip, ErrorType error, Object arg, Object arg2) {
		listener.get().runTimeError(new STRuntimeMessage(error,ip,self,null,arg,arg2));
	}

	public static void runTimeError(ST self, int ip, ErrorType error, Object arg, Object arg2, Object arg3) {
		listener.get().runTimeError(new STRuntimeMessage(error,ip,self,null,arg,arg2,arg3));
	}

    public static void IOError(ST self, ErrorType error, Throwable e) {
        listener.get().IOError(new STMessage(error, self, e));
    }

    public static void IOError(ST self, ErrorType error, Throwable e, Object arg) {
        listener.get().IOError(new STMessage(error, self, e, arg));
    }

    public static void internalError(ST self, String msg, Throwable e) {
        listener.get().internalError(new STMessage(ErrorType.INTERNAL_ERROR, self, e, msg));
    }
}
