package org.stringtemplate;

/** Track errors per thread; e.g., one server transaction's errors
 *  will go in one grouping since each has it's own thread.
 *
 *  TODO: what happens if the thread is reused?  Will these listeners go away?
 */
public class ErrorManager {    
    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
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

    public static void setErrorListener(STErrorListener listener) { ErrorManager.listener.set(listener); }

    public static void error(String s) { listener.get().error(s, null); }

    public static void error(String s, Throwable e) { listener.get().error(s,e); }

    public static void warning(String s) { listener.get().warning(s); }
}
