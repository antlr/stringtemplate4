package org.stringtemplate.misc;

import org.stringtemplate.compiler.CompiledST;
import org.stringtemplate.ST;
import org.stringtemplate.STWriter;
import org.stringtemplate.debug.DebugST;

import java.io.IOException;

/** A singleton no-op ST that renders to "" and doesn't set attributes etc...
 *  Used to prevent error propogation when we can't find a template.
 */
public class BlankST extends DebugST { // extend DebugST not just ST in case we're debugging
    public BlankST() { code = new CompiledST(); }
    public BlankST(String template) { this(); }

    public void add(String name, Object value) { }

    public void rawSetAttribute(String name, Object value) { }

    public Object getAttribute(String name) { return null; }

    public String getEnclosingInstanceStackString() { return null; }

    public int write(STWriter out) throws IOException { return 0; }

    public String render() { return ""; }
}
