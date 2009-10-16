package org.stringtemplate;

/** How to handle messages */
public interface STErrorListener {
    public void error(String msg, Throwable e);
    public void warning(String msg);
}
