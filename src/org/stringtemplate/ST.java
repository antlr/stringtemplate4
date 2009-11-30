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
package org.stringtemplate;

import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.AddAttributeEvent;
import org.stringtemplate.debug.ConstructionEvent;
import org.stringtemplate.debug.STDebugInfo;
import org.stringtemplate.misc.MultiMap;
import org.stringtemplate.gui.STViz;

import java.util.*;
import java.io.StringWriter;
import java.io.IOException;

public class ST {
	public static final String SUBTEMPLATE_PREFIX = "_sub";

    /** <@r()>, <@r>...<@end>, and @t.r() ::= "..." defined manually by coder */
    public static enum RegionType { IMPLICIT, EMBEDDED, EXPLICIT };    

    public static final String UNKNOWN_NAME = "unknown";
    public static final ST BLANK = new BlankST();
    
    /** The code to interpret; it pulls from attributes and this template's
     *  group of templates to evaluate to string.
     */
    public CompiledST code; // TODO: is this the right name?

    /** Map an attribute name to its value(s). */
    protected Map<String,Object> attributes;

    /** Enclosing instance if I'm embedded within another template.
     *  IF-subtemplates are considered embedded as well.
     */
    public ST enclosingInstance; // who's your daddy?

    /** Created as instance of which group? We need this to init interpreter
     *  via render.  So, we create st and then it needs to know which
     *  group created it for sake of polymorphism:
     *
     *  st = skin1.getInstanceOf("searchbox");
     *  result = st.render(); // knows skin1 created it
     */
    public STGroup groupThatCreatedThisInstance;

    /** Normally, formal parameters hide any attributes inherited from the
     *  enclosing template with the same name.  This is normally what you
     *  want, but makes it hard to invoke another template passing in all
     *  the data.  Use notation now: <otherTemplate(...)> to say "pass in
     *  all data".  Works great.  Can also say <otherTemplate(foo="xxx",...)>
     */
    protected boolean passThroughAttributes = false;    

	/** Just an alias for ArrayList, but this way I can track whether a
     *  list is something ST created or it's an incoming list.
     */
    public static final class AttributeList extends ArrayList {
        public AttributeList(int size) { super(size); }
        public AttributeList() { super(); }
    }
    
    public ST() {;}
    
    public ST(String template) {
        this(STGroup.defaultGroup, template);
    }

    public ST(STGroup nativeGroup, String template) {
        code = nativeGroup.defineTemplate(UNKNOWN_NAME, template);
        groupThatCreatedThisInstance = nativeGroup;
    }

    public void add(String name, Object value) {
        if ( name==null ) return; // allow null value
        if ( name.indexOf('.')>=0 ) {
            throw new IllegalArgumentException("cannot have '.' in attribute names");
        }

        if ( value instanceof ST ) ((ST)value).enclosingInstance = this;

		if ( groupThatCreatedThisInstance.debug ) {
            STDebugInfo info = groupThatCreatedThisInstance.debugInfoMap.get(this);
            if ( info!=null ) info.addAttrEvents.map(name, new AddAttributeEvent(name, value));
		}

        Object curvalue = null;
        if ( attributes==null || !attributes.containsKey(name) ) { // new attribute
            rawSetAttribute(name, value);
            return;
        }
        if ( attributes!=null ) curvalue = attributes.get(name);

        // attribute will be multi-valued for sure now
        // convert current attribute to list if not already
        // copy-on-write semantics; copy a list injected by user to add new value
        AttributeList multi = convertToAttributeList(curvalue);
        rawSetAttribute(name, multi); // replace with list

        // now, add incoming value to multi-valued attribute
        if ( value instanceof List ) {
            // flatten incoming list into existing list
            multi.addAll((List)value);
        }
        else if ( value!=null && value.getClass().isArray() ) {
            multi.addAll(Arrays.asList((Object[])value));
        }
        else {
            multi.add(value);
        }
    }

    protected void rawSetAttribute(String name, Object value) {
        if ( attributes==null ) attributes = new HashMap<String,Object>();
        attributes.put(name, value);
    }

    /** Find an attr with dynamic scoping up enclosing ST chain.
     *  If not found, look for a map.  So attributes sent in to a template
     *  override dictionary names.
     */
    public Object getAttribute(String name) {
        Object o = null;
        if ( attributes!=null ) o = attributes.get(name);
        if ( o!=null ) return o;
        
        if ( code.formalArguments!=null &&
             code.formalArguments.get(name)!=null &&  // no local value && it's a formal arg
             !passThroughAttributes )                 // but no ... in arg list
        {
            // if you've defined attribute as formal arg for this
            // template and it has no value, do not look up the
            // enclosing dynamic scopes.
            return null;
        }

        ST p = this.enclosingInstance;
        while ( p!=null ) {
            if ( p.attributes!=null ) o = p.attributes.get(name);
            if ( o!=null ) return o;
            p = p.enclosingInstance;
        }
        if ( code.formalArguments==null || code.formalArguments.get(name)==null ) {
            // if not hidden by formal args, return any dictionary
            return code.nativeGroup.dictionaries.get(name);
        }
        return null;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    protected static AttributeList convertToAttributeList(Object curvalue) {
        AttributeList multi;
        if ( curvalue == null ) {
            multi = new AttributeList(); // make list to hold multiple values
            multi.add(curvalue);         // add previous single-valued attribute
        }
        else if ( curvalue.getClass() == AttributeList.class ) { // already a list made by ST
            multi = (AttributeList)curvalue;
        }
        else if ( curvalue instanceof List) { // existing attribute is non-ST List
            // must copy to an ST-managed list before adding new attribute
            // (can't alter incoming attributes)
            List listAttr = (List)curvalue;
            multi = new AttributeList(listAttr.size());
            multi.addAll(listAttr);
        }
        else if ( curvalue.getClass().isArray() ) { // copy array to list
            Object[] a = (Object[])curvalue;
            multi = new AttributeList(a.length);
            multi.addAll(Arrays.asList(a)); // asList doesn't copy as far as I can tell
        }
        else {
            // curvalue nonlist and we want to add an attribute
            // must convert curvalue existing to list
            multi = new AttributeList(); // make list to hold multiple values
            multi.add(curvalue);         // add previous single-valued attribute
        }
        return multi;
    }

    /** If an instance of x is enclosed in a y which is in a z, return
     *  a String of these instance names in order from topmost to lowest;
     *  here that would be "[z y x]".
     */
    public String getEnclosingInstanceStackString() {
        List<ST> templates = getEnclosingInstanceStack(true);
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (ST st : templates) {
            if ( i>0 ) buf.append(", ");
            buf.append(st.getName());
            i++;
        }
        return buf.toString();
    }

    public List<ST> getEnclosingInstanceStack(boolean topdown) {
        List<ST> stack = new LinkedList<ST>();
        ST p = this;
        while ( p!=null ) {
            if ( topdown ) stack.add(0,p);
            else stack.add(p);
            p = p.enclosingInstance;
        }
        return stack;
    }

    public STDebugInfo getDebugInfo() {
        return groupThatCreatedThisInstance.getDebugInfo(this);
    }

    public String getName() { return code.name; }

	public boolean isSubtemplate() { return code.isSubtemplate(); }

    public int write(STWriter out) throws IOException {
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance, out);
        return interp.exec(this);
    }

    public int write(STWriter out, Locale locale) throws IOException {
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance, out, locale);
        return interp.exec(this);
    }

    public String render() { return render(Locale.getDefault()); }

    public String render(int lineWidth) { return render(Locale.getDefault(), lineWidth); }

    public String render(Locale locale) { return render(locale, STWriter.NO_WRAP); }

    public String render(Locale locale, int lineWidth) {
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out);
        wr.setLineWidth(lineWidth);
        try {
            write(wr, locale);
        }
        catch (IOException io) {
            System.err.println("Got IOException writing to writer");
        }
        return out.toString();
    }

    public String inspect() { return inspect(Locale.getDefault()); }

    public String inspect(int lineWidth) { return inspect(Locale.getDefault(), lineWidth); }

    public String inspect(Locale locale) { return inspect(locale, STWriter.NO_WRAP); }

    public String inspect(Locale locale, int lineWidth) {
        String s = render(locale, lineWidth);
        new STViz(this);
        return s;
    }

    public String toString() {
        return code.name+"()";
    }
}
