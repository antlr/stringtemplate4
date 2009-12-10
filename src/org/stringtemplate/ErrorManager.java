package org.stringtemplate;

import org.antlr.tool.ToolMessage;
import org.antlr.runtime.RecognitionException;

/** Track errors per thread; e.g., one server transaction's errors
 *  will go in one grouping since each has it's own thread.
 *
 *  TODO: what happens if the thread is reused?  Will these listeners go away?
 */
public class ErrorManager {
    
    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
            public void compileTimeError(STMessage msg) {
            }

            public void runTimeError(STMessage msg) {
            }

            public void IOError(STMessage msg) {
                throw new Error("I/O error", msg.cause);
            }

            public void internalError(STMessage msg) {
                throw new Error("internal error", msg.cause);
            }

            // TODO: put in [root ... template] stack
            public void error(String s) { error(s, null); }
            public void error(String s, Throwable e) {
                System.err.println(s);
                if ( e!=null ) {
                    e.printStackTrace(System.err);
                }
            }
            public void warning(String s) {
                System.out.println(s);
            }
        };

    /** Gives us a new listener per thread */
    static ThreadLocal<STErrorListener> listener = new ThreadLocal<STErrorListener>() {
        protected STErrorListener initialValue() { return DEFAULT_ERROR_LISTENER; }
    };

    /** Backward compatibility for tombu, co-designer.  Don't check missing
     *  args against formal arg lists and don't require template headers in .st
     *  files.
     */
    public static boolean v3_mode = false;

    public static void setErrorListener(STErrorListener listener) { ErrorManager.listener.set(listener); }

    public static void compileTimeError(ErrorType error, Object arg) {
        listener.get().compileTimeError(new STMessage(error,null,null,arg));
    }

    public static void compileTimeError(ErrorType error, Object arg, Object arg2) {
        listener.get().compileTimeError(new STMessage(error,null,null,arg,arg2));
    }

    public static void syntaxError(ErrorType error, RecognitionException e, String msg) {
        listener.get().compileTimeError(new STCompiletimeMessage(error,e.token,e,msg));
    }

    public static void syntaxError(ErrorType error, RecognitionException e, String msg, Object arg) {
        listener.get().compileTimeError(new STCompiletimeMessage(error, e.token, e,msg,arg));
    }

    public static void runTimeError(ST self, int ip, ErrorType error) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self));
    }

    public static void runTimeError(ST self, int ip, ErrorType error, Object arg) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self,null,arg));
    }

    public static void runTimeError(ST self, int ip, ErrorType error, Throwable e, Object arg) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self,e,arg));
    }

    public static void runTimeError(ST self, int ip, ErrorType error, Object arg, Object arg2) {
        listener.get().runTimeError(new STRuntimeMessage(error,ip,self,null,arg,arg2));
    }

    public static void IOError(ST self, ErrorType error, Throwable e) {
        listener.get().IOError(new STMessage(error, self, e));
    }

    public static void IOError(ST self, ErrorType error, Throwable e, Object arg) {
        listener.get().IOError(new STMessage(error, self, e, arg));
    }

    public static void internalError(ST self, ErrorType error, Throwable e) {
        listener.get().internalError(new STMessage(error, self, e));
    }

    public static void internalError(ST self, ErrorType error, Throwable e, Object arg) {
        listener.get().internalError(new STMessage(error, self, e, arg));
    }

    public static void internalError(ST self, ErrorType error, Throwable e, Object arg, Object arg2) {
        listener.get().internalError(new STMessage(error, self, e, arg, arg2));
    }
}
