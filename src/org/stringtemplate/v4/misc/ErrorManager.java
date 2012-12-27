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

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.InstanceScope;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;

public class ErrorManager {
    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
			@Override
            public void compileTimeError(STMessage msg) {
                System.err.println(msg);
            }

			@Override
            public void runTimeError(STMessage msg) {
                if ( msg.error != ErrorType.NO_SUCH_PROPERTY ) { // ignore these
                    System.err.println(msg);
                }
            }

			@Override
            public void IOError(STMessage msg) {
                System.err.println(msg);
            }

			@Override
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

	public final STErrorListener listener;

	public ErrorManager() { this(DEFAULT_ERROR_LISTENER); }
	public ErrorManager(STErrorListener listener) {
		this.listener = listener;
	}

	public void compileTimeError(ErrorType error, Token templateToken, Token t) {
		CharStream input = t.getInputStream();
		String srcName = null;
		if ( input!=null ) {
			srcName = input.getSourceName();
			if ( srcName!=null ) srcName = Misc.getFileName(srcName);
		}
		listener.compileTimeError(
            new STCompiletimeMessage(error,srcName,templateToken,t,null,t.getText())
        );
    }

	public void lexerError(String srcName, String msg, Token templateToken, RecognitionException e) {
		if ( srcName!=null ) srcName = Misc.getFileName(srcName);
        listener.compileTimeError(
            new STLexerMessage(srcName, msg, templateToken, e)
        );
    }

    public void compileTimeError(ErrorType error, Token templateToken, Token t, Object arg) {
        String srcName = t.getInputStream().getSourceName();
		if ( srcName!=null ) srcName = Misc.getFileName(srcName);
        listener.compileTimeError(
            new STCompiletimeMessage(error,srcName,templateToken,t,null,arg)
        );
    }

    public void compileTimeError(ErrorType error, Token templateToken, Token t, Object arg, Object arg2) {
        String srcName = t.getInputStream().getSourceName();
        if ( srcName!=null ) srcName = Misc.getFileName(srcName);
        listener.compileTimeError(
            new STCompiletimeMessage(error,srcName,templateToken,t,null,arg,arg2)
        );
    }

	public void groupSyntaxError(ErrorType error, String srcName, RecognitionException e, String msg) {
		Token t = e.token;
		listener.compileTimeError(
			new STGroupCompiletimeMessage(error,srcName,e.token,e,msg)
		);
	}

	public void groupLexerError(ErrorType error, String srcName, RecognitionException e, String msg) {
		listener.compileTimeError(
			new STGroupCompiletimeMessage(error,srcName,e.token,e,msg)
		);
	}

    public void runTimeError(Interpreter interp, InstanceScope scope, ErrorType error) {
        listener.runTimeError(new STRuntimeMessage(interp, error, scope != null ? scope.ip : 0, scope));
    }

    public void runTimeError(Interpreter interp, InstanceScope scope, ErrorType error, Object arg) {
        listener.runTimeError(new STRuntimeMessage(interp, error, scope != null ? scope.ip : 0, scope,arg));
    }

    public void runTimeError(Interpreter interp, InstanceScope scope, ErrorType error, Throwable e, Object arg) {
        listener.runTimeError(new STRuntimeMessage(interp, error, scope != null ? scope.ip : 0, scope,e,arg));
    }

	public void runTimeError(Interpreter interp, InstanceScope scope, ErrorType error, Object arg, Object arg2) {
		listener.runTimeError(new STRuntimeMessage(interp, error, scope != null ? scope.ip : 0, scope,null,arg,arg2));
	}

	public void runTimeError(Interpreter interp, InstanceScope scope, ErrorType error, Object arg, Object arg2, Object arg3) {
		listener.runTimeError(new STRuntimeMessage(interp, error, scope != null ? scope.ip : 0, scope,null,arg,arg2,arg3));
	}

    public void IOError(ST self, ErrorType error, Throwable e) {
        listener.IOError(new STMessage(error, self, e));
    }

    public void IOError(ST self, ErrorType error, Throwable e, Object arg) {
        listener.IOError(new STMessage(error, self, e, arg));
    }

    public void internalError(ST self, String msg, Throwable e) {
        listener.internalError(new STMessage(ErrorType.INTERNAL_ERROR, self, e, msg));
    }
}
