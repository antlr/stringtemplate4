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
package org.stringtemplate.v4;

import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.debug.AddAttributeEvent;
import org.stringtemplate.v4.debug.ConstructionEvent;
import org.stringtemplate.v4.debug.EvalTemplateEvent;
import org.stringtemplate.v4.debug.InterpEvent;
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.*;

import java.io.*;
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
	public final static String VERSION = "@version@";

	/** <@r()>, <@r>...<@end>, and @t.r() ::= "..." defined manually by coder */
    public static enum RegionType { IMPLICIT, EMBEDDED, EXPLICIT }

	/** Events during template hierarchy construction (not evaluation) */
	public static class DebugState {
		/** Record who made us? ConstructionEvent creates Exception to grab stack */
		public ConstructionEvent newSTEvent;

		/** Track construction-time add attribute "events"; used for ST user-level debugging */
		public MultiMap<String, AddAttributeEvent> addAttrEvents = new MultiMap<String, AddAttributeEvent>();
	}

	/** Track all events that happen while evaluating this template */
	public static class InterpDebugState {
		/* Includes the EvalTemplateEvent for this template.  This
		*  is a subset of Interpreter.events field. The final
		*  EvalTemplateEvent is stored in 3 places:
		*
		*  	1. In enclosingInstance's childTemplateEvents
		*  	2. In this event list
		*  	3. In the overall event list
		*
		*  The root ST has the final EvalTemplateEvent in its list.
		*
		*  All events get added to the enclosingInstance's event list.
		*/
		public List<InterpEvent> events = new ArrayList<InterpEvent>();

		/** All templates evaluated and embedded in this ST. Used
		 *  for tree view in STViz.
		 */
		public List<EvalTemplateEvent> childEvalTemplateEvents =
			new ArrayList<EvalTemplateEvent>();
	}

    public static final String UNKNOWN_NAME = "anonymous";
	public static final Object EMPTY_ATTR = new Object();

	/** Cache exception since this could happen a lot if people use "missing"
	 *  to mean boolean false.
	 */
	public static STNoSuchPropertyException cachedNoSuchPropException;

    /** The implementation for this template among all instances of same tmpelate . */
    public CompiledST impl;

	/** Safe to simultaneously write via add, which is synchronized.  Reading
	 *  during exec is, however, NOT synchronized.  So, not thread safe to
	 *  add attributes while it is being evaluated.  Initialized to EMPTY_ATTR
	 *  to distinguish null from empty.
	 */
	protected Object[] locals;

    /** Enclosing instance if I'm embedded within another template.
     *  IF-subtemplates are considered embedded as well. We look up
	 *  dynamically scoped attributes with this ptr.  Set only at
	 *  evaluation time.
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

	/** If Interpreter.trackCreationEvents, track creation, add-attr events
	 *  for each object. Create this object on first use.
	 */
	public DebugState debugState;

	/** Just an alias for ArrayList, but this way I can track whether a
     *  list is something ST created or it's an incoming list.
     */
    public static final class AttributeList<T> extends ArrayList<T> {
        public AttributeList(int size) { super(size); }
        public AttributeList() { super(); }
    }

	/** Used by group creation routine, not by users */
    public ST() {
		if ( STGroup.trackCreationEvents ) {
			if ( debugState==null ) debugState = new ST.DebugState();
			debugState.newSTEvent = new ConstructionEvent();
		}
	}

	/** Used to make templates inline in code for simple things like SQL or log records.
	 *  No formal args are set and there is no enclosing instance.
	 */
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
		this();
		groupThatCreatedThisInstance = group;
		impl = groupThatCreatedThisInstance.compile(group.getFileName(), null,
													null, template, null);
		impl.hasFormalArgs = false;
		impl.name = UNKNOWN_NAME;
		impl.defineImplicitlyDefinedTemplates(groupThatCreatedThisInstance);
    }

	/** Clone a prototype template for application in MAP operations;
	 *  copy all fields minus debugState; don't call this(), which creates
	 *  ctor event
	 */
	public ST(ST proto) {
		this.impl = proto.impl;
		if ( proto.locals!=null ) {
			this.locals = Arrays.copyOf(proto.locals, proto.locals.length);
		}
		//this.enclosingInstance = proto.enclosingInstance;
		this.groupThatCreatedThisInstance = proto.groupThatCreatedThisInstance;
	}

    /** Inject an attribute (name/value pair). If there is already an
     *  attribute with that name, this method turns the attribute into an
     *  AttributeList with both the previous and the new attribute as elements.
     *  This method will never alter a List that you inject.  If you send
     *  in a List and then inject a single value element, add() copies
     *  original list and adds the new value.
	 *
	 *  Return self so we can chain.  t.add("x", 1).add("y", "hi");
     */
    public synchronized ST add(String name, Object value) {
        if ( name==null ) return this; // allow null value but not name
        if ( name.indexOf('.')>=0 ) {
            throw new IllegalArgumentException("cannot have '.' in attribute names");
        }

		if ( STGroup.trackCreationEvents ) {
			if ( debugState==null ) debugState = new ST.DebugState();
			debugState.addAttrEvents.map(name, new AddAttributeEvent(name, value));
		}

		FormalArgument arg = null;
		if ( impl.hasFormalArgs ) {
			if ( impl.formalArguments!=null ) arg = impl.formalArguments.get(name);
			if ( arg==null ) {
				throw new IllegalArgumentException("no such attribute: "+name);
			}
		}
		else {
			// define and make room in locals (a hack to make new ST("simple template") work.)
			if ( impl.formalArguments!=null ) {
				arg = impl.formalArguments.get(name);
			}
			if ( arg==null ) { // not defined
				arg = new FormalArgument(name);
				impl.addArg(arg);
				if ( locals==null ) locals = new Object[1];
				else locals = Arrays.copyOf(locals, impl.formalArguments.size());
				locals[arg.index] = EMPTY_ATTR;
			}
		}

		Object curvalue = locals[arg.index];
        if ( curvalue==EMPTY_ATTR ) { // new attribute
			locals[arg.index] = value;
            return this;
        }

        // attribute will be multi-valued for sure now
        // convert current attribute to list if not already
        // copy-on-write semantics; copy a list injected by user to add new value
        AttributeList<Object> multi = convertToAttributeList(curvalue);
		locals[arg.index] = multi; // replace with list

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
		return this;
    }

	/** Split "aggrName.{propName1,propName2}" into list [propName1,propName2]
	 *  and the aggrName. Spaces are allowed around ','.
	 */
	public synchronized ST addAggr(String aggrSpec, Object... values) {
		int dot = aggrSpec.indexOf(".{");
		if ( values==null || values.length==0 ) {
			throw new IllegalArgumentException("missing values for aggregate attribute format: "+
											   aggrSpec);
		}
		int finalCurly = aggrSpec.indexOf('}');
		if ( dot<0 || finalCurly>=aggrSpec.length() ) {
			throw new IllegalArgumentException("invalid aggregate attribute format: "+
											   aggrSpec);
		}
		String aggrName = aggrSpec.substring(0, dot);
		String propString = aggrSpec.substring(dot+2, aggrSpec.length()-1);
		propString = propString.trim();
		String[] propNames = propString.split("\\ *,\\ *");
		if ( propNames==null || propNames.length==0 ) {
			throw new IllegalArgumentException("invalid aggregate attribute format: "+
											   aggrSpec);
		}
		if ( values.length != propNames.length ) {
			throw new IllegalArgumentException(
				"number of properties and values mismatch for aggregate attribute format: "+
				aggrSpec);
		}
		int i=0;
		Aggregate aggr = new Aggregate();
		for (String p : propNames) {
			Object v = values[i++];
			aggr.properties.put(p, v);
		}

		add(aggrName, aggr); // now add as usual
		return this;
	}

	/** Remove an attribute value entirely (can't remove attribute definitions). */
	public void remove(String name) {
		if ( impl.formalArguments==null ) {
			if ( impl.hasFormalArgs ) {
				throw new IllegalArgumentException("no such attribute: "+name);
			}
			return;
		}
		FormalArgument arg = impl.formalArguments.get(name);
		if ( arg==null ) {
			throw new IllegalArgumentException("no such attribute: "+name);
		}
		locals[arg.index] = EMPTY_ATTR; // reset value
	}

	/** Set this.locals attr value when you only know the name, not the index.
	 *  This is ultimately invoked by calling ST.add() from outside so toss
	 *  an exception to notify them.
	 */
    protected void rawSetAttribute(String name, Object value) {
		if ( impl.formalArguments==null ) {
			throw new IllegalArgumentException("no such attribute: "+name);
		}
		FormalArgument arg = impl.formalArguments.get(name);
		if ( arg==null ) {
			throw new IllegalArgumentException("no such attribute: "+name);
		}
		locals[arg.index] = value;
	}

    /** Find an attr via dynamic scoping up enclosing ST chain.
     *  If not found, look for a map.  So attributes sent in to a template
     *  override dictionary names.
     */
    public Object getAttribute(String name) {
        ST p = this;
        while ( p!=null ) {
			FormalArgument localArg = null;
			if ( p.impl.formalArguments!=null ) localArg = p.impl.formalArguments.get(name);
            if ( localArg!=null ) {
				Object o = p.locals[localArg.index];
				if ( o==ST.EMPTY_ATTR ) o = null;
				return o;
			}
            p = p.enclosingInstance;
        }
		// got to root template and no definition, try dictionaries in group
        if ( impl.nativeGroup.isDictionary(name) ) {
			return impl.nativeGroup.rawGetDictionary(name);
		}
		// not found, report unknown attr unless formal args unknown
		if ( cachedNoSuchPropException ==null ) {
			cachedNoSuchPropException = new STNoSuchPropertyException();
		}
		cachedNoSuchPropException.propertyName = name;
		throw cachedNoSuchPropException;
    }

    public Map<String, Object> getAttributes() {
		if ( impl.formalArguments==null ) return null;
		Map<String, Object> attributes = new HashMap<String, Object>();
		for (FormalArgument a : impl.formalArguments.values()) {
			Object o = locals[a.index];
			if ( o==ST.EMPTY_ATTR ) o = null;
			attributes.put(a.name, o);
		}
		return attributes;
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

	public boolean isAnonSubtemplate() { return impl.isAnonSubtemplate; }

	public int write(STWriter out) throws IOException {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 impl.nativeGroup.errMgr,
											 false);
		interp.setDefaultArguments(out, this);
		return interp.exec(out, this);
    }

	public int write(STWriter out, Locale locale) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 locale,
											 impl.nativeGroup.errMgr,
											 false);
		interp.setDefaultArguments(out, this);
		return interp.exec(out, this);
	}

	public int write(STWriter out, STErrorListener listener) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 new ErrorManager(listener),
											 false);
		interp.setDefaultArguments(out, this);
		return interp.exec(out, this);
	}

	public int write(STWriter out, Locale locale, STErrorListener listener) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 locale,
											 new ErrorManager(listener),
											 false);
		interp.setDefaultArguments(out, this);
		return interp.exec(out, this);
	}

	public int write(File outputFile, STErrorListener listener) throws IOException {
		return write(outputFile, listener, "UTF-8", Locale.getDefault(), STWriter.NO_WRAP);
	}

	public int write(File outputFile, STErrorListener listener, String encoding)
		throws IOException
	{
		return write(outputFile, listener, encoding, Locale.getDefault(), STWriter.NO_WRAP);
	}

	public int write(File outputFile, STErrorListener listener, String encoding, int lineWidth)
		throws IOException
	{
		return write(outputFile, listener, encoding, Locale.getDefault(), lineWidth);
	}

	public int write(File outputFile,
					 STErrorListener listener,
					 String encoding,
					 Locale locale,
					 int lineWidth)
		throws IOException
	{
		Writer bw = null;
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			bw = new BufferedWriter(osw);
			AutoIndentWriter w = new AutoIndentWriter(bw);
			w.setLineWidth(lineWidth);
			int n = write(w, locale, listener);
			bw.close();
			bw = null;
			return n;
		}
		finally {
			if (bw != null) bw.close();
		}
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

	// LAUNCH A WINDOW TO INSPECT TEMPLATE HIERARCHY

    public STViz inspect() { return inspect(Locale.getDefault()); }

    public STViz inspect(int lineWidth) {
		return inspect(impl.nativeGroup.errMgr, Locale.getDefault(), lineWidth);
	}

    public STViz inspect(Locale locale) {
		return inspect(impl.nativeGroup.errMgr, locale, STWriter.NO_WRAP);
	}

	public STViz inspect(ErrorManager errMgr, Locale locale, int lineWidth) {
		ErrorBuffer errors = new ErrorBuffer();
		impl.nativeGroup.setListener(errors);
		StringWriter out = new StringWriter();
		STWriter wr = new AutoIndentWriter(out);
		wr.setLineWidth(lineWidth);
		Interpreter interp =
			new Interpreter(groupThatCreatedThisInstance, locale, true);
		interp.exec(wr, this); // render and track events
		STViz viz = new STViz(errMgr, this, out.toString(), interp,
							  interp.getExecutionTrace(), errors.errors);
		viz.open();
		return viz;
	}

	// TESTING SUPPORT

	public List<InterpEvent> getEvents() { return getEvents(Locale.getDefault()); }

    public List<InterpEvent> getEvents(int lineWidth) { return getEvents(Locale.getDefault(), lineWidth); }

    public List<InterpEvent> getEvents(Locale locale) { return getEvents(locale, STWriter.NO_WRAP); }

    public List<InterpEvent> getEvents(Locale locale, int lineWidth) {
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out);
        wr.setLineWidth(lineWidth);
        Interpreter interp =
			new Interpreter(groupThatCreatedThisInstance, locale, true);
        interp.exec(wr, this); // render and track events
        return interp.getEvents();
    }

    public String toString() {
        if ( impl==null ) return "bad-template()";
        return impl.name+"()";
    }

	// ST.format("name, phone | <name>:<phone>", n, p);
	// ST.format("<%1>:<%2>", n, p);
	// ST.format("<name>:<phone>", "name", x, "phone", y);
	public static String format(String template, Object... attributes) {
		return format(STWriter.NO_WRAP, template, attributes);
	}

	public static String format(int lineWidth, String template, Object... attributes) {
		template = template.replaceAll("%([0-9]+)", "arg$1");
		System.out.println(template);

		ST st = new ST(template);
		int i = 1;
		for (Object a : attributes) {
			st.add("arg"+i, a);
			i++;
		}
		return st.render(lineWidth);
	}
}
