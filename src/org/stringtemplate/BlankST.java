package org.stringtemplate;

import java.io.IOException;

/** A singleton no-op ST that renders to "" and doesn't set attributes etc...
 *  Used to prevent error propogation when we can't find a template.
 */
public class BlankST extends ST {
    public BlankST() { code = new CompiledST(); }
    public BlankST(String template) { this(); }

    public void add(String name, Object value) { }

    public void setAttribute(String name, Object value) { }

    public Object getAttribute(String name) { return null; }

    public String getEnclosingInstanceStackString() { return null; }

    public int write(STWriter out) throws IOException { return Interpreter.MISSING; }

    public String render() { return ""; }
}
