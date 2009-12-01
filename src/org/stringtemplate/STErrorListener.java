package org.stringtemplate;

/** How to handle messages */
public interface STErrorListener {
    public void compileTimeError(STMessage msg);
    public void runTimeError(STMessage msg);
    public void IOError(STMessage msg);
    public void internalError(STMessage msg);

//    public void warning(STMessage msg);
/*
    public void error(String msg, Throwable e);
    public void error(String msg);
    public void warning(String msg);
    */
}
