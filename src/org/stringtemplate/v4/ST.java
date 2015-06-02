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
import org.stringtemplate.v4.misc.Aggregate;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.MultiMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** An instance of the StringTemplate. It consists primarily of
 *  a {@linkplain ST#impl reference} to its implementation (shared among all
 *  instances) and a hash table of {@linkplain ST#locals attributes}.  Because
 *  of dynamic scoping, we also need a reference to any enclosing instance. For
 *  example, in a deeply nested template for an HTML page body, we could still
 *  reference the title attribute defined in the outermost page template.
 * <p>
 *  To use templates, you create one (usually via {@link STGroup}) and then inject
 *  attributes using {@link #add}. To render its attacks, use {@link ST#render()}.</p>
 * <p>
 *  TODO: {@link ST#locals} is not actually a hash table like the documentation
 *  says.</p>
 */
public class ST {
	public final static String VERSION = "4.0.8";

	/** {@code <@r()>}, {@code <@r>...<@end>}, and {@code @t.r() ::= "..."} defined manually by coder */
    public enum RegionType {
		/** {@code <@r()>} */
		IMPLICIT,
		/** {@code <@r>...<@end>} */
		EMBEDDED,
		/** {@code @t.r() ::= "..."} */
		EXPLICIT
	}

	/** Events during template hierarchy construction (not evaluation) */
	public static class DebugState {
		/** Record who made us? {@link ConstructionEvent} creates {@link Exception} to grab stack */
		public ConstructionEvent newSTEvent;

		/** Track construction-time add attribute "events"; used for ST user-level debugging */
		public MultiMap<String, AddAttributeEvent> addAttrEvents = new MultiMap<String, AddAttributeEvent>();
	}

    public static final String UNKNOWN_NAME = "anonymous";
	public static final Object EMPTY_ATTR = new Object();

	/** When there are no formal args for template t and you map t across
	 *  some values, t implicitly gets arg "it".  E.g., "<b>$it$</b>"
	 */
	public static final String IMPLICIT_ARG_NAME = "it";

    /** The implementation for this template among all instances of same template . */
    public CompiledST impl;

	/** Safe to simultaneously write via {@link #add}, which is synchronized.
	 *  Reading during exec is, however, NOT synchronized.  So, not thread safe
	 *  to add attributes while it is being evaluated.  Initialized to
	 *  {@link #EMPTY_ATTR} to distinguish {@code null} from empty.
	 */
	protected Object[] locals;

    /** Created as instance of which group? We need this to initialize interpreter
     *  via render.  So, we create st and then it needs to know which
     *  group created it for sake of polymorphism:
     *
	 *  <pre>
     *  st = skin1.getInstanceOf("searchbox");
     *  result = st.render(); // knows skin1 created it
	 *  </pre>
	 *
	 *  Say we have a group {@code g1} with template {@code t} that imports
	 *  templates {@code t} and {@code u} from another group {@code g2}.
	 *  {@code g1.getInstanceOf("u")} finds {@code u} in {@code g2} but remembers
	 *  that {@code g1} created it.  If {@code u} includes {@code t}, it should
	 *  create {@code g1.t} not {@code g2.t}.
	 *
	 *  <pre>
	 *   g1 = {t(), u()}
	 *   |
	 *   v
	 *   g2 = {t()}
	 *  </pre>
     */
    public STGroup groupThatCreatedThisInstance;

	/** If {@link STGroup#trackCreationEvents}, track creation and add
	 *  attribute events for each object. Create this object on first use.
	 */
	public DebugState debugState;

	/** Just an alias for {@link ArrayList}, but this way I can track whether a
     *  list is something ST created or it's an incoming list.
     */
    public static final class AttributeList extends ArrayList<Object> {
        public AttributeList(int size) { super(size); }
        public AttributeList() { super(); }
    }

	/** Used by group creation routine, not by users */
    protected ST() {
		if ( STGroup.trackCreationEvents ) {
			if ( debugState==null ) debugState = new ST.DebugState();
			debugState.newSTEvent = new ConstructionEvent();
		}
	}

	/** Used to make templates inline in code for simple things like SQL or log records.
	 *  No formal arguments are set and there is no enclosing instance.
	 */
    public ST(String template) {
        this(STGroup.defaultGroup, template);
    }

    /** Create ST using non-default delimiters; each one of these will live
     *  in it's own group since you're overriding a default; don't want to
     *  alter {@link STGroup#defaultGroup}.
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

	/** Clone a prototype template.
	 *  Copy all fields minus {@link #debugState}; don't delegate to {@link #ST()},
	 *  which creates {@link ConstructionEvent}.
	 */
	public ST(ST proto) {
		this.impl = proto.impl;
		if ( proto.locals!=null ) {
			//this.locals = Arrays.copyOf(proto.locals, proto.locals.length);
			this.locals = new Object[proto.locals.length];
			System.arraycopy(proto.locals, 0, this.locals, 0, proto.locals.length);
		}
		else if (impl.formalArguments != null && !impl.formalArguments.isEmpty()) {
			this.locals = new Object[impl.formalArguments.size()];
		}
		this.groupThatCreatedThisInstance = proto.groupThatCreatedThisInstance;
	}

	/** Inject an attribute (name/value pair). If there is already an attribute
	 *  with that name, this method turns the attribute into an
	 *  {@link AttributeList} with both the previous and the new attribute as
	 *  elements. This method will never alter a {@link List} that you inject.
	 *  If you send in a {@link List} and then inject a single value element,
	 *  {@code add} copies original list and adds the new value. The
	 *  attribute name cannot be null or contain '.'.
	 *  <p>
	 *  Return {@code this} so we can chain:</p>
	 *  <p>
	 *  {@code t.add("x", 1).add("y", "hi")}</p>
     */
    public synchronized ST add(String name, Object value) {
		if ( name==null ) {
			throw new NullPointerException("null attribute name");
		}
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
				//else locals = Arrays.copyOf(locals, impl.formalArguments.size());
				else {
					Object[] copy = new Object[impl.formalArguments.size()];
					System.arraycopy(locals, 0, copy, 0,
									 Math.min(locals.length, impl.formalArguments.size()));
					locals = copy;
				}
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
        AttributeList multi = convertToAttributeList(curvalue);
		locals[arg.index] = multi; // replace with list

        // now, add incoming value to multi-valued attribute
        if ( value instanceof List ) {
            // flatten incoming list into existing list
            multi.addAll((List<?>)value);
        }
        else if ( value!=null && value.getClass().isArray() ) {
			if (value instanceof Object[]) {
				multi.addAll(Arrays.asList((Object[])value));
			}
			else {
				multi.addAll(convertToAttributeList(value));
			}
        }
        else {
            multi.add(value);
        }
		return this;
    }

	/** Split {@code aggrName.{propName1,propName2}} into list
	 *  {@code [propName1, propName2]} and the {@code aggrName}. Spaces are
	 *  allowed around {@code ','}.
	 */
	public synchronized ST addAggr(String aggrSpec, Object... values) {
		int dot = aggrSpec.indexOf(".{");
		if ( values==null || values.length==0 ) {
			throw new IllegalArgumentException("missing values for aggregate attribute format: "+
											   aggrSpec);
		}
		int finalCurly = aggrSpec.indexOf('}');
		if ( dot<0 || finalCurly < 0 ) {
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

	/** Set {@code locals} attribute value when you only know the name, not the
	 *  index. This is ultimately invoked by calling {@code ST#add} from
	 *  outside so toss an exception to notify them.
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

	/** Find an attribute in this template only. */
	public Object getAttribute(String name) {
		FormalArgument localArg = null;
		if ( impl.formalArguments!=null ) localArg = impl.formalArguments.get(name);
		if ( localArg!=null ) {
			Object o = locals[localArg.index];
			if ( o==ST.EMPTY_ATTR ) o = null;
			return o;
		}
		return null;
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

    protected static AttributeList convertToAttributeList(Object curvalue) {
        AttributeList multi;
        if ( curvalue == null ) {
            multi = new AttributeList(); // make list to hold multiple values
            multi.add(curvalue);                 // add previous single-valued attribute
        }
        else if ( curvalue instanceof AttributeList ) { // already a list made by ST
            multi = (AttributeList)curvalue;
        }
        else if ( curvalue instanceof List) { // existing attribute is non-ST List
            // must copy to an ST-managed list before adding new attribute
            // (can't alter incoming attributes)
            List<?> listAttr = (List<?>)curvalue;
            multi = new AttributeList(listAttr.size());
            multi.addAll(listAttr);
        }
        else if ( curvalue instanceof Object[] ) { // copy array to list
            Object[] a = (Object[])curvalue;
            multi = new AttributeList(a.length);
            multi.addAll(Arrays.asList(a)); // asList doesn't copy as far as I can tell
        }
        else if ( curvalue.getClass().isArray() ) { // copy primitive array to list
			int length = Array.getLength(curvalue);
            multi = new AttributeList(length);
			for (int i = 0; i < length; i++) {
				multi.add(Array.get(curvalue, i));
			}
        }
        else {
            // curvalue nonlist and we want to add an attribute
            // must convert curvalue existing to list
            multi = new AttributeList(); // make list to hold multiple values
            multi.add(curvalue);                 // add previous single-valued attribute
        }
        return multi;
    }

    public String getName() { return impl.name; }

	public boolean isAnonSubtemplate() { return impl.isAnonSubtemplate; }

	public int write(STWriter out) throws IOException {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 impl.nativeGroup.errMgr,
											 false);
		InstanceScope scope = new InstanceScope(null, this);
		return interp.exec(out, scope);
    }

	public int write(STWriter out, Locale locale) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 locale,
											 impl.nativeGroup.errMgr,
											 false);
		InstanceScope scope = new InstanceScope(null, this);
		return interp.exec(out, scope);
	}

	public int write(STWriter out, STErrorListener listener) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 new ErrorManager(listener),
											 false);
		InstanceScope scope = new InstanceScope(null, this);
		return interp.exec(out, scope);
	}

	public int write(STWriter out, Locale locale, STErrorListener listener) {
		Interpreter interp = new Interpreter(groupThatCreatedThisInstance,
											 locale,
											 new ErrorManager(listener),
											 false);
		InstanceScope scope = new InstanceScope(null, this);
		return interp.exec(out, scope);
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
		InstanceScope scope = new InstanceScope(null, this);
		interp.exec(wr, scope); // render and track events
		List<InterpEvent> events = interp.getEvents();
		EvalTemplateEvent overallTemplateEval =
			(EvalTemplateEvent)events.get(events.size()-1);
		STViz viz = new STViz(errMgr, overallTemplateEval, out.toString(), interp,
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
		InstanceScope scope = new InstanceScope(null, this);
        interp.exec(wr, scope); // render and track events
        return interp.getEvents();
    }

	@Override
    public String toString() {
        if ( impl==null ) return "bad-template()";
		String name = impl.name+"()";
		if (this.impl.isRegion) {
			name = "@" + STGroup.getUnMangledTemplateName(name);
		}

        return name;
    }

	/**
	 * <pre>
	 * ST.format("name, phone | &lt;name&gt;:&lt;phone&gt;", n, p);
	 * ST.format("&lt;%1&gt;:&lt;%2&gt;", n, p);
	 * ST.format("&lt;name&gt;:&lt;phone&gt;", "name", x, "phone", y);
	 * </pre>
	 */
	public static String format(String template, Object... attributes) {
		return format(STWriter.NO_WRAP, template, attributes);
	}

	public static String format(int lineWidth, String template, Object... attributes) {
		template = template.replaceAll("%([0-9]+)", "arg$1");
		ST st = new ST(template);
		int i = 1;
		for (Object a : attributes) {
			st.add("arg"+i, a);
			i++;
		}
		return st.render(lineWidth);
	}
}
