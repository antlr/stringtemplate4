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
package org.stringtemplate.v4;

import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.misc.BlankST;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/** An instance of the StringTemplate. It consists primarily of
 *  a reference to its implementation (shared among all instances)
 *  and a hash table of attributes.  Because of dynamic scoping,
 *  we also need a reference to any enclosing instance. For example,
 *  in a deeply nested template for an HTML page body, we could still reference
 *  the title attribute defined in the outermost page template.
 *
 *  To use templates, you create one (usually via STGroup) and then inject
 *  attributes using add(). To render its attacks, use render().
 */
public class ST {
	public static final String SUBTEMPLATE_PREFIX = "_sub";

    /** <@r()>, <@r>...<@end>, and @t.r() ::= "..." defined manually by coder */
    public static enum RegionType { IMPLICIT, EMBEDDED, EXPLICIT };

    public static final String UNKNOWN_NAME = "anonymous";
    public static final ST BLANK = new BlankST();

    /** The implementation for this template among all instances of same tmpelate . */
    public CompiledST impl;

    /** Map an attribute name to its value(s).
     *  rawSetAttribute makes a synchronized map so multiple threads can
     *  write to this table.
     */
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
	 *
	 *  Say we have a group, g1, with template t and import t and u templates from
	 *  another group, g2.  g1.getInstanceOf("u") finds u in g2 but remembers
	 *  that g1 created it.  If u includes t, it should create g1.t not g2.t.
	 *
	 *   g1 = {t(), u()}
	 *   |
	 *   v
	 *   g2 = {t()}
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
    public static final class AttributeList<T> extends ArrayList<T> {
        public AttributeList(int size) { super(size); }
        public AttributeList() { super(); }
    }

    public ST() {;}

    public ST(String template) {
        this(STGroup.defaultGroup, template);
    }

    /** Create ST using non-default delimiters; each one of these will live
     *  in it's own group since you're overriding a default; don't want to
     *  alter STGroup.defaultGroup.
     */
    public ST(String template, char delimiterStartChar, char delimiterStopChar) {
        this(new STGroup(delimiterStartChar, delimiterStopChar), template);
    }

    public ST(STGroup group, String template) {
        groupThatCreatedThisInstance = group;
        impl = groupThatCreatedThisInstance.compile(null, template);
        impl.name = UNKNOWN_NAME;
        impl.defineImplicitlyDefinedTemplates(groupThatCreatedThisInstance);
    }

	/** Clone a prototype template for application in MAP operations; copy all fields */
	public ST(ST proto) {
		this.impl = proto.impl;
		this.attributes = new HashMap<String,Object>(); // copy attributes
		this.attributes.putAll(proto.attributes);
		this.enclosingInstance = proto.enclosingInstance;
		this.groupThatCreatedThisInstance = proto.groupThatCreatedThisInstance;
		this.passThroughAttributes = proto.passThroughAttributes;
	}

    /** Inject an attribute (name/value pair). If there is already an
     *  attribute with that name, this method turns the attribute into an
     *  AttributeList with both the previous and the new attribute as elements.
     *  This method will never alter a List that you inject.  If you send
     *  in a List and then inject a single value element, add() copies
     *  original list and adds the new value.
     */
    public void add(String name, Object value) {
        if ( name==null ) return; // allow null value
        if ( name.indexOf('.')>=0 ) {
            throw new IllegalArgumentException("cannot have '.' in attribute names");
        }

        if ( value instanceof ST ) ((ST)value).enclosingInstance = this;

        Object curvalue = null;
        if ( attributes==null || !attributes.containsKey(name) ) { // new attribute
            checkAttributeExists(name);
            rawSetAttribute(name, value);
            return;
        }
        if ( attributes!=null ) curvalue = attributes.get(name);

        // attribute will be multi-valued for sure now
        // convert current attribute to list if not already
        // copy-on-write semantics; copy a list injected by user to add new value
        AttributeList<Object> multi = convertToAttributeList(curvalue);
        rawSetAttribute(name, multi); // replace with list

        // now, add incoming value to multi-valued attribute
        if ( value instanceof List ) {
            // flatten incoming list into existing list
            multi.addAll((List)value);
        }
        else if ( value!=null && value.getClass().isArray() ) {
            multi.addAll(Arrays.asList(value));
        }
        else {
            multi.add(value);
        }
    }

    protected void rawSetAttribute(String name, Object value) {
        if ( attributes==null ) {
            attributes = Collections.synchronizedMap(new HashMap<String,Object>());
        }
        attributes.put(name, value);
    }

    /** Cause an error if name is not defined.
     */
    protected void checkAttributeExists(String name) {
		if ( impl.formalArguments == FormalArgument.UNKNOWN ) return;
        if ( impl.formalArguments.get(name) == null ) {
            ErrorManager.runTimeError(this, -1, ErrorType.CANT_SET_ATTRIBUTE, name, getName());
        }
    }

    /** Find an attr with dynamic scoping up enclosing ST chain.
     *  If not found, look for a map.  So attributes sent in to a template
     *  override dictionary names.
     */
    public Object getAttribute(String name) {
        Object o = null;
        if ( attributes!=null ) o = attributes.get(name);
        if ( o!=null ) return o;

        if ( impl.formalArguments.get(name)!=null &&  // no local value && it's a formal arg
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
            if ( o!=null ) return o; // found it!
            p = p.enclosingInstance;
        }
        if ( impl.formalArguments.get(name)==null ) {
            // if not hidden by formal args, return any dictionary
            return impl.nativeGroup.rawGetDictionary(name);
        }
        return null;
    }

    public Map<String, Object> getAttributes() { return attributes; }

    /** Useful if you want to set all attributes at once, w/o using add() */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    protected static AttributeList<Object> convertToAttributeList(Object curvalue) {
        AttributeList<Object> multi;
        if ( curvalue == null ) {
            multi = new AttributeList<Object>(); // make list to hold multiple values
            multi.add(curvalue);                 // add previous single-valued attribute
        }
        else if ( curvalue.getClass() == AttributeList.class ) { // already a list made by ST
            multi = (AttributeList<Object>)curvalue;
        }
        else if ( curvalue instanceof List) { // existing attribute is non-ST List
            // must copy to an ST-managed list before adding new attribute
            // (can't alter incoming attributes)
            List listAttr = (List)curvalue;
            multi = new AttributeList<Object>(listAttr.size());
            multi.addAll(listAttr);
        }
        else if ( curvalue.getClass().isArray() ) { // copy array to list
            Object[] a = (Object[])curvalue;
            multi = new AttributeList<Object>(a.length);
            multi.addAll(Arrays.asList(a)); // asList doesn't copy as far as I can tell
        }
        else {
            // curvalue nonlist and we want to add an attribute
            // must convert curvalue existing to list
            multi = new AttributeList<Object>(); // make list to hold multiple values
            multi.add(curvalue);                 // add previous single-valued attribute
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
            if ( i>0 ) buf.append(" ");
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

    public String getName() { return impl.name; }

	public boolean isSubtemplate() { return impl.isSubtemplate; }

    public int write(STWriter out) throws IOException {
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance);
        interp.setDefaultArguments(this);
        return interp.exec(out, this);
    }

    public int write(STWriter out, Locale locale) {
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance, locale);
        interp.setDefaultArguments(this);
        return interp.exec(out, this);
    }

    public String render() { return render(Locale.getDefault()); }

    public String render(int lineWidth) { return render(Locale.getDefault(), lineWidth); }

    public String render(Locale locale) { return render(locale, STWriter.NO_WRAP); }

    public String render(Locale locale, int lineWidth) {
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out);
        wr.setLineWidth(lineWidth);
        write(wr, locale);
        return out.toString();
    }

    public String toString() {
        if ( impl==null ) return "bad-template()";
        return impl.name+"()";
    }

}
