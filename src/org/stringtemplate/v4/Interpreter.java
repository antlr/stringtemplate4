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

import org.stringtemplate.v4.compiler.*;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.debug.*;
import org.stringtemplate.v4.misc.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

/** This class knows how to execute template bytecodes relative to a
 *  particular STGroup. To execute the byte codes, we need an output stream
 *  and a reference to an ST an instance. That instance's impl field points at
 *  a CompiledST, which contains all of the byte codes and other information
 *  relevant to execution.
 *
 *  This interpreter is a stack-based bytecode interpreter.  All operands
 *  go onto an operand stack.
 *
 *  If the group that we're executing relative to has debug set, we track
 *  interpreter events. For now, I am only tracking instance creation events.
 *  These are used by STViz to pair up output chunks with the template
 *  expressions that generate them.
 *
 *  We create a new interpreter for each ST.render(), DebugST.inspect, or
 *  DebugST.getEvents() invocation.
 */
public class Interpreter {
	public enum Option { ANCHOR, FORMAT, NULL, SEPARATOR, WRAP }
	public static final int DEFAULT_OPERAND_STACK_SIZE = 100;

	public static final Set<String> predefinedAnonSubtemplateAttributes =
		new HashSet<String>() { { add("i"); add("i0"); } };

	/** Operand stack, grows upwards */
	Object[] operands = new Object[DEFAULT_OPERAND_STACK_SIZE];
	int sp = -1;        // stack pointer register
	int current_ip = 0; // mirrors ip in exec(), but visible to all methods
	int nwline = 0;     // how many char written on this template LINE so far?

	/** Stack of enclosing instances (scopes).  Used for dynamic scoping
	 *  of attributes.
	 */
	public InstanceScope currentScope = null;

	/** Exec st with respect to this group. Once set in ST.toString(),
	 *  it should be fixed. ST has group also.
	 */
	STGroup group;

	/** For renderers, we have to pass in the locale */
	Locale locale;

	ErrorManager errMgr;

	/** Dump bytecode instructions as we execute them? mainly for parrt */
	public static boolean trace = false;

	/** If trace mode, track trace here */
	// TODO: track the pieces not a string and track what it contributes to output
	protected List<String> executeTrace;

	/** Track events inside templates and in this.events */
	public boolean debug = false;

	/** Track everything happening in interp if debug across all templates.
	 *  The last event in this field is the EvalTemplateEvent for the root
	 *  template.
	 */
	protected List<InterpEvent> events;

	public Interpreter(STGroup group, boolean debug) {
		this(group,Locale.getDefault(),group.errMgr, debug);
	}

	public Interpreter(STGroup group, Locale locale, boolean debug) {
		this(group, locale, group.errMgr, debug);
	}

	public Interpreter(STGroup group, ErrorManager errMgr, boolean debug) {
		this(group,Locale.getDefault(),errMgr, debug);
	}

	public Interpreter(STGroup group, Locale locale, ErrorManager errMgr, boolean debug) {
		this.group = group;
		this.locale = locale;
		this.errMgr = errMgr;
		this.debug = debug;
		if ( debug ) {
			events = new ArrayList<InterpEvent>();
			executeTrace = new ArrayList<String>();
		}
	}

//	public static int[] count = new int[Bytecode.MAX_BYTECODE+1];

//	public static void dumpOpcodeFreq() {
//		System.out.println("#### instr freq:");
//		for (int i=1; i<=Bytecode.MAX_BYTECODE; i++) {
//			System.out.println(count[i]+" "+Bytecode.instructions[i].name);
//		}
//	}

	/** Execute template self and return how many characters it wrote to out */
	public int exec(STWriter out, ST self) {
		pushScope(self);
		try {
			setDefaultArguments(out, self);
			return _exec(out, self);
		}
		catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			errMgr.runTimeError(this, self, current_ip, ErrorType.INTERNAL_ERROR,
								"internal error: "+sw.toString());
			return 0;
		}
		finally { popScope(); }
	}

	protected int _exec(STWriter out, ST self) {
		int start = out.index(); // track char we're about to write
		int prevOpcode = 0;
		int n = 0; // how many char we write out
		int nargs;
		int nameIndex;
		int addr;
		String name;
		Object o, left, right;
		ST st;
		Object[] options;
		byte[] code = self.impl.instrs;        // which code block are we executing
		int ip = 0;
		while ( ip < self.impl.codeSize ) {
			if ( trace || debug ) trace(self, ip);
			short opcode = code[ip];
			//count[opcode]++;
			current_ip = ip;
			ip++; //jump to next instruction or first byte of operand
			switch (opcode) {
				case Bytecode.INSTR_LOAD_STR :
					// just testing...
					load_str(self,ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					break;
				case Bytecode.INSTR_LOAD_ATTR :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					try {
						o = getAttribute(self, name);
						if ( o==ST.EMPTY_ATTR ) o = null;
					}
					catch (STNoSuchAttributeException nsae) {
						errMgr.runTimeError(this, self, current_ip, ErrorType.NO_SUCH_ATTRIBUTE, name);
						o = null;
					}
					operands[++sp] = o;
					break;
				case Bytecode.INSTR_LOAD_LOCAL:
					int valueIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = self.locals[valueIndex];
					if ( o==ST.EMPTY_ATTR ) o = null;
					operands[++sp] = o;
					break;
				case Bytecode.INSTR_LOAD_PROP :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = operands[sp--];
					name = self.impl.strings[nameIndex];
					operands[++sp] = getObjectProperty(out, self, o, name);
					break;
				case Bytecode.INSTR_LOAD_PROP_IND :
					Object propName = operands[sp--];
					o = operands[sp];
					operands[sp] = getObjectProperty(out, self, o, propName);
					break;
				case Bytecode.INSTR_NEW :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					// look up in original hierarchy not enclosing template (variable group)
					// see TestSubtemplates.testEvalSTFromAnotherGroup()
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, self, ip, name);
					// get n args and store into st's attr list
					storeArgs(self, nargs, st);
					sp -= nargs;
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_NEW_IND:
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = (String)operands[sp-nargs];
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, self, ip, name);
					storeArgs(self, nargs, st);
					sp -= nargs;
					sp--; // pop template name
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_NEW_BOX_ARGS :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					Map<String,Object> attrs = (Map<String,Object>)operands[sp--];
					// look up in original hierarchy not enclosing template (variable group)
					// see TestSubtemplates.testEvalSTFromAnotherGroup()
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, self, ip, name);
					// get n args and store into st's attr list
					storeArgs(self, attrs, st);
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_SUPER_NEW :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					super_new(self, name, nargs);
					break;
				case Bytecode.INSTR_SUPER_NEW_BOX_ARGS :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					attrs = (Map<String,Object>)operands[sp--];
					super_new(self, name, attrs);
					break;
				case Bytecode.INSTR_STORE_OPTION:
					int optionIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = operands[sp--];    // value to store
					options = (Object[])operands[sp]; // get options
					options[optionIndex] = o; // store value into options on stack
					break;
				case Bytecode.INSTR_STORE_ARG:
					nameIndex = getShort(code, ip);
					name = self.impl.strings[nameIndex];
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = operands[sp--];
					attrs = (Map<String,Object>)operands[sp];
					attrs.put(name, o); // leave attrs on stack
					break;
				case Bytecode.INSTR_WRITE :
					o = operands[sp--];
					int n1 = writeObjectNoOptions(out, self, o);
					n += n1;
					nwline += n1;
					break;
				case Bytecode.INSTR_WRITE_OPT :
					options = (Object[])operands[sp--]; // get options
					o = operands[sp--];                 // get option to write
					int n2 = writeObjectWithOptions(out, self, o, options);
					n += n2;
					nwline += n2;
					break;
				case Bytecode.INSTR_MAP :
					st = (ST)operands[sp--]; // get prototype off stack
					o = operands[sp--];		 // get object to map prototype across
					map(self,o,st);
					break;
				case Bytecode.INSTR_ROT_MAP :
					int nmaps = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					List<ST> templates = new ArrayList<ST>();
					for (int i=nmaps-1; i>=0; i--) templates.add((ST)operands[sp-i]);
					sp -= nmaps;
					o = operands[sp--];
					if ( o!=null ) rot_map(self,o,templates);
					break;
				case Bytecode.INSTR_ZIP_MAP:
					st = (ST)operands[sp--];
					nmaps = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					List<Object> exprs = new ArrayList<Object>();
					for (int i=nmaps-1; i>=0; i--) exprs.add(operands[sp-i]);
					sp -= nmaps;
					operands[++sp] = zip_map(self, exprs, st);
					break;
				case Bytecode.INSTR_BR :
					ip = getShort(code, ip);
					break;
				case Bytecode.INSTR_BRF :
					addr = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = operands[sp--]; // <if(expr)>...<endif>
					if ( !testAttributeTrue(o) ) ip = addr; // jump
					break;
				case Bytecode.INSTR_OPTIONS :
					operands[++sp] = new Object[Compiler.NUM_OPTIONS];
					break;
				case Bytecode.INSTR_ARGS:
					operands[++sp] = new HashMap<String,Object>();
					break;
				case Bytecode.INSTR_PASSTHRU :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					attrs = (Map<String,Object>)operands[sp];
					passthru(self, name, attrs);
					break;
				case Bytecode.INSTR_LIST :
					operands[++sp] = new ArrayList<Object>();
					break;
				case Bytecode.INSTR_ADD :
					o = operands[sp--];             // pop value
					List<Object> list = (List<Object>)operands[sp]; // don't pop list
					addToList(list, o);
					break;
				case Bytecode.INSTR_TOSTR :
					// replace with string value; early eval
					operands[sp] = toString(out, self, operands[sp]);
					break;
				case Bytecode.INSTR_FIRST  :
					operands[sp] = first(operands[sp]);
					break;
				case Bytecode.INSTR_LAST   :
					operands[sp] = last(operands[sp]);
					break;
				case Bytecode.INSTR_REST   :
					operands[sp] = rest(operands[sp]);
					break;
				case Bytecode.INSTR_TRUNC  :
					operands[sp] = trunc(operands[sp]);
					break;
				case Bytecode.INSTR_STRIP  :
					operands[sp] = strip(operands[sp]);
					break;
				case Bytecode.INSTR_TRIM   :
					o = operands[sp--];
					if ( o.getClass() == String.class ) {
						operands[++sp] = ((String)o).trim();
					}
					else {
						errMgr.runTimeError(this, self, current_ip, ErrorType.EXPECTING_STRING, "trim", o.getClass().getName());
						operands[++sp] = o;
					}
					break;
				case Bytecode.INSTR_LENGTH :
					operands[sp] = length(operands[sp]);
					break;
				case Bytecode.INSTR_STRLEN :
					o = operands[sp--];
					if ( o.getClass() == String.class ) {
						operands[++sp] = ((String)o).length();
					}
					else {
						errMgr.runTimeError(this, self, current_ip, ErrorType.EXPECTING_STRING, "strlen", o.getClass().getName());
						operands[++sp] = 0;
					}
					break;
				case Bytecode.INSTR_REVERSE :
					operands[sp] = reverse(operands[sp]);
					break;
				case Bytecode.INSTR_NOT :
					operands[sp] = !testAttributeTrue(operands[sp]);
					break;
				case Bytecode.INSTR_OR :
					right = operands[sp--];
					left = operands[sp--];
					operands[++sp] = testAttributeTrue(left) || testAttributeTrue(right);
					break;
				case Bytecode.INSTR_AND :
					right = operands[sp--];
					left = operands[sp--];
					operands[++sp] = testAttributeTrue(left) && testAttributeTrue(right);
					break;
				case Bytecode.INSTR_INDENT :
					int strIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					indent(out, self, strIndex);
					break;
				case Bytecode.INSTR_DEDENT :
					out.popIndentation();
					break;
				case Bytecode.INSTR_NEWLINE :
					try {
						if ( prevOpcode==Bytecode.INSTR_NEWLINE ||
							prevOpcode==Bytecode.INSTR_INDENT ||
							nwline>0 )
						{
							out.write(Misc.newline);
						}
						nwline = 0;
					}
					catch (IOException ioe) {
						errMgr.IOError(self, ErrorType.WRITE_IO_ERROR, ioe);
					}
					break;
				case Bytecode.INSTR_NOOP :
					break;
				case Bytecode.INSTR_POP :
					sp--; // throw away top of stack
					break;
				case Bytecode.INSTR_NULL :
					operands[++sp] = null;
					break;
				case Bytecode.INSTR_TRUE :
					operands[++sp] = true;
					break;
				case Bytecode.INSTR_FALSE :
					operands[++sp] = false;
					break;
				case Bytecode.INSTR_WRITE_STR :
					strIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					o = self.impl.strings[strIndex];
					n1 = writeObjectNoOptions(out, self, o);
					n += n1;
					nwline += n1;
					break;
				// TODO: generate this optimization
//				case Bytecode.INSTR_WRITE_LOCAL:
//					valueIndex = getShort(code, ip);
//					ip += Bytecode.OPND_SIZE_IN_BYTES;
//					o = self.locals[valueIndex];
//					if ( o==ST.EMPTY_ATTR ) o = null;
//					n1 = writeObjectNoOptions(out, self, o);
//					n += n1;
//					nwline += n1;
//					break;
				default :
					errMgr.internalError(self, "invalid bytecode @ "+(ip-1)+": "+opcode, null);
					self.impl.dump();
			}
			prevOpcode = opcode;
		}
		if ( debug ) {
			int stop = out.index() - 1;
			EvalTemplateEvent e = new EvalTemplateEvent(currentScope, start, stop);
			trackDebugEvent(self, e);
		}
		return n;
	}

	void load_str(ST self, int ip) {
		int strIndex = getShort(self.impl.instrs, ip);
		ip += Bytecode.OPND_SIZE_IN_BYTES;
		operands[++sp] = self.impl.strings[strIndex];
	}

	// TODO: refactor to remove dup'd code
	void super_new(ST self, String name, int nargs) {
		ST st = null;
		CompiledST imported = self.impl.nativeGroup.lookupImportedTemplate(name);
		if ( imported==null ) {
			errMgr.runTimeError(this, self, current_ip, ErrorType.NO_IMPORTED_TEMPLATE,
								name);
			st = self.groupThatCreatedThisInstance.createStringTemplateInternally(new CompiledST());
		}
		else {
			st = imported.nativeGroup.getEmbeddedInstanceOf(this, self, current_ip, name);
			st.groupThatCreatedThisInstance = group;
		}
		// get n args and store into st's attr list
		storeArgs(self, nargs, st);
		sp -= nargs;
		operands[++sp] = st;
	}

	void super_new(ST self, String name, Map<String,Object> attrs) {
		ST st = null;
		CompiledST imported = self.impl.nativeGroup.lookupImportedTemplate(name);
		if ( imported==null ) {
			errMgr.runTimeError(this, self, current_ip, ErrorType.NO_IMPORTED_TEMPLATE,
								name);
			st = self.groupThatCreatedThisInstance.createStringTemplateInternally(new CompiledST());
		}
		else {
			st = imported.nativeGroup.createStringTemplateInternally(imported);
			st.groupThatCreatedThisInstance = group;
		}

		// get n args and store into st's attr list
		storeArgs(self, attrs, st);
		operands[++sp] = st;
	}

	void passthru(ST self, String templateName, Map<String,Object> attrs) {
		CompiledST c = group.lookupTemplate(templateName);
		if ( c==null ) return; // will get error later
		if ( c.formalArguments==null ) return;
		for (FormalArgument arg : c.formalArguments.values()) {
			// if not already set by user, set to value from outer scope
			if ( !attrs.containsKey(arg.name) ) {
				//System.out.println("arg "+arg.name+" missing");
				try {
					Object o = getAttribute(self, arg.name);
					// If the attribute exists but there is no value and
					// the formal argument has no default value, make it null.
					if ( o==ST.EMPTY_ATTR && arg.defaultValueToken==null ) {
						attrs.put(arg.name, null);
					}
					// Else, the attribute has an existing value, set arg.
					else if ( o!=ST.EMPTY_ATTR ) {
						attrs.put(arg.name, o);
					}
				}
				catch (STNoSuchAttributeException nsae) {
					// if no such attribute exists for arg.name, set parameter
					// if no default value
					if ( arg.defaultValueToken==null ) {
						attrs.put(arg.name, null);
					}
				}
			}
		}
	}

	void storeArgs(ST self, Map<String,Object> attrs, ST st) {
		int nformalArgs = 0;
		if ( st.impl.formalArguments!=null ) nformalArgs = st.impl.formalArguments.size();
		int nargs = 0;
		if ( attrs!=null ) nargs = attrs.size();

		if ( nargs < (nformalArgs-st.impl.numberOfArgsWithDefaultValues) ||
			 nargs > nformalArgs )
		{
			errMgr.runTimeError(this, self,
								current_ip,
								ErrorType.ARGUMENT_COUNT_MISMATCH,
								nargs,
								st.impl.name,
								nformalArgs);
		}

		for (String argName : attrs.keySet()) {
			// don't let it throw an exception in rawSetAttribute
			if ( st.impl.formalArguments==null || !st.impl.formalArguments.containsKey(argName) ) {
				errMgr.runTimeError(this, self,
									current_ip,
									ErrorType.NO_SUCH_ATTRIBUTE,
									argName);
				continue;
			}
			Object o = attrs.get(argName);
			st.rawSetAttribute(argName, o);
		}
	}

	void storeArgs(ST self, int nargs, ST st) {
		int nformalArgs = 0;
		if ( st.impl.formalArguments!=null ) nformalArgs = st.impl.formalArguments.size();
		int firstArg = sp-(nargs-1);
		int numToStore = Math.min(nargs, nformalArgs);
		if ( st.impl.isAnonSubtemplate ) nformalArgs -= predefinedAnonSubtemplateAttributes.size();

		if ( nargs < (nformalArgs-st.impl.numberOfArgsWithDefaultValues) ||
			 nargs > nformalArgs )
		{
			errMgr.runTimeError(this, self,
								current_ip,
								ErrorType.ARGUMENT_COUNT_MISMATCH,
								nargs,
								st.impl.name,
								nformalArgs);
		}

		if ( st.impl.formalArguments==null ) return;

		Iterator<String> argNames = st.impl.formalArguments.keySet().iterator();
		for (int i=0; i<numToStore; i++) {
			Object o = operands[firstArg+i];    // value to store
			String argName = argNames.next();
			st.rawSetAttribute(argName, o);
		}
	}

	protected void indent(STWriter out, ST self, int strIndex) {
		String indent = self.impl.strings[strIndex];
		if ( debug ) {
			int start = out.index(); // track char we're about to write
			EvalExprEvent e = new IndentEvent(currentScope,
											  start, start + indent.length() - 1,
											  getExprStartChar(self),
											  getExprStopChar(self));
			trackDebugEvent(self, e);
		}
		out.pushIndentation(indent);
	}

	/** Write out an expression result that doesn't use expression options.
	 *  E.g., <name>
	 */
	protected int writeObjectNoOptions(STWriter out, ST self, Object o) {
		int start = out.index(); // track char we're about to write
		int n = writeObject(out, self, o, null);
        if ( debug ) {
			EvalExprEvent e = new EvalExprEvent(currentScope,
												start, out.index() - 1,
												getExprStartChar(self),
												getExprStopChar(self));
			trackDebugEvent(self, e);
        }
		return n;
	}

	/** Write out an expression result that uses expression options.
	 *  E.g., <names; separator=", ">
	 */
	protected int writeObjectWithOptions(STWriter out, ST self, Object o,
										 Object[] options)
	{
		int start = out.index(); // track char we're about to write
		// precompute all option values (render all the way to strings)
		String[] optionStrings = null;
		if ( options!=null ) {
			optionStrings = new String[options.length];
			for (int i=0; i<Compiler.NUM_OPTIONS; i++) {
				optionStrings[i] = toString(out, self, options[i]);
			}
		}
		if ( options!=null && options[Option.ANCHOR.ordinal()]!=null ) {
			out.pushAnchorPoint();
		}

		int n = writeObject(out, self, o, optionStrings);

		if ( options!=null && options[Option.ANCHOR.ordinal()]!=null ) {
			out.popAnchorPoint();
		}
        if ( debug ) {
			EvalExprEvent e = new EvalExprEvent(currentScope,
												start, out.index() - 1,
												getExprStartChar(self),
												getExprStopChar(self));
			trackDebugEvent(self, e);
        }
		return n;
	}

	/** Generic method to emit text for an object. It differentiates
	 *  between templates, iterable objects, and plain old Java objects (POJOs)
	 */
	protected int writeObject(STWriter out, ST self, Object o, String[] options) {
		int n = 0;
		if ( o == null ) {
			if ( options!=null && options[Option.NULL.ordinal()]!=null ) {
				o = options[Option.NULL.ordinal()];
			}
			else return 0;
		}
		if ( o instanceof ST ) {
			ST st = (ST)o;
			if ( options!=null && options[Option.WRAP.ordinal()]!=null ) {
				// if we have a wrap string, then inform writer it
				// might need to wrap
				try {
					out.writeWrap(options[Option.WRAP.ordinal()]);
				}
				catch (IOException ioe) {
					errMgr.IOError(self, ErrorType.WRITE_IO_ERROR, ioe);
				}
			}
			n = exec(out, st);
		}
		else {
			o = convertAnythingIteratableToIterator(o); // normalize
			try {
				if ( o instanceof Iterator) n = writeIterator(out, self, o, options);
				else n = writePOJO(out, o, options);
			}
			catch (IOException ioe) {
				errMgr.IOError(self, ErrorType.WRITE_IO_ERROR, ioe, o);
			}
		}
		return n;
	}

	protected int writeIterator(STWriter out, ST self, Object o, String[] options) throws IOException {
		if ( o==null ) return 0;
		int n = 0;
		Iterator it = (Iterator)o;
		String separator = null;
		if ( options!=null ) separator = options[Option.SEPARATOR.ordinal()];
		boolean seenAValue = false;
		while ( it.hasNext() ) {
			Object iterValue = it.next();
			// Emit separator if we're beyond first value
			boolean needSeparator = seenAValue &&
				separator!=null &&            // we have a separator and
				(iterValue!=null ||           // either we have a value
					options[Option.NULL.ordinal()]!=null); // or no value but null option
			if ( needSeparator ) n += out.writeSeparator(separator);
			int nw = writeObject(out, self, iterValue, options);
			if ( nw > 0 ) seenAValue = true;
			n += nw;
		}
		return n;
	}

	protected int writePOJO(STWriter out, Object o, String[] options) throws IOException {
		String formatString = null;
		if ( options!=null ) formatString = options[Option.FORMAT.ordinal()];
		// ask the native group defining the surrounding template for the renderer
		AttributeRenderer r = currentScope.st.impl.nativeGroup.getAttributeRenderer(o.getClass());
		String v;
		if ( r!=null ) v = r.toString(o, formatString, locale);
		else v = o.toString();
		int n;
		if ( options!=null && options[Option.WRAP.ordinal()]!=null ) {
			n = out.write(v, options[Option.WRAP.ordinal()]);
		}
		else {
			n = out.write(v);
		}
		return n;
	}

	protected int getExprStartChar(ST self) {
		Interval templateLocation = self.impl.sourceMap[current_ip];
		if ( templateLocation!=null ) return templateLocation.a;
		return -1;
	}

	protected int getExprStopChar(ST self) {
		Interval templateLocation = self.impl.sourceMap[current_ip];
		if ( templateLocation!=null ) return templateLocation.b;
		return -1;
	}

	protected void map(ST self, Object attr, final ST st) {
		rot_map(self, attr, new ArrayList<ST>() {{add(st);}});
	}

	// <names:a()> or <names:a(),b()>
	protected void rot_map(ST self, Object attr, List<ST> prototypes) {
		if ( attr==null ) {
			operands[++sp] = null;
			return;
		}
		attr = convertAnythingIteratableToIterator(attr);
		if ( attr instanceof Iterator ) {
			List<ST> mapped = rot_map_iterator(self, (Iterator) attr, prototypes);
			operands[++sp] = mapped;
		}
		else { // if only single value, just apply first template to sole value
			ST proto = prototypes.get(0);
			ST st = group.createStringTemplateInternally(proto);
			if ( st!=null ) {
				setFirstArgument(self, st, attr);
				if ( st.impl.isAnonSubtemplate ) {
					st.rawSetAttribute("i0", 0);
					st.rawSetAttribute("i", 1);
				}
				operands[++sp] = st;
			}
			else {
				operands[++sp] = null;
			}
		}
	}

	protected List<ST> rot_map_iterator(ST self, Iterator attr, List<ST> prototypes) {
		List<ST> mapped = new ArrayList<ST>();
		Iterator iter = (Iterator)attr;
		int i0 = 0;
		int i = 1;
		int ti = 0;
		while ( iter.hasNext() ) {
			Object iterValue = iter.next();
			if ( iterValue == null ) { mapped.add(null); continue; }
			int templateIndex = ti % prototypes.size(); // rotate through
			ti++;
			ST proto = prototypes.get(templateIndex);
			ST st = group.createStringTemplateInternally(proto);
			setFirstArgument(self, st, iterValue);
			if ( st.impl.isAnonSubtemplate ) {
				st.rawSetAttribute("i0", i0);
				st.rawSetAttribute("i", i);
			}
			mapped.add(st);
			i0++;
			i++;
		}
		return mapped;
	}

	// <names,phones:{n,p | ...}> or <a,b:t()>
	// todo: i, i0 not set unless mentioned? map:{k,v | ..}?
	protected ST.AttributeList zip_map(ST self, List<Object> exprs, ST prototype) {
		if ( exprs==null || prototype==null || exprs.size()==0 ) {
			return null; // do not apply if missing templates or empty values
		}
		// make everything iterable
		for (int i = 0; i < exprs.size(); i++) {
			Object attr = exprs.get(i);
			if ( attr!=null ) exprs.set(i, convertAnythingToIterator(attr));
		}

		// ensure arguments line up
		int numExprs = exprs.size();
		CompiledST code = prototype.impl;
		Map formalArguments = code.formalArguments;
		if ( !code.hasFormalArgs || formalArguments==null ) {
			errMgr.runTimeError(this, self, current_ip, ErrorType.MISSING_FORMAL_ARGUMENTS);
			return null;
		}

		// todo: track formal args not names for efficient filling of locals
		Object[] formalArgumentNames = formalArguments.keySet().toArray();
		int nformalArgs = formalArgumentNames.length;
		if ( prototype.isAnonSubtemplate() ) nformalArgs -= predefinedAnonSubtemplateAttributes.size();
		if ( nformalArgs != numExprs ) {
			errMgr.runTimeError(this, self,
									  current_ip,
									  ErrorType.MAP_ARGUMENT_COUNT_MISMATCH,
									  numExprs,
									  nformalArgs);
			// TODO just fill first n
			// truncate arg list to match smaller size
			int shorterSize = Math.min(formalArgumentNames.length, numExprs);
			numExprs = shorterSize;
			Object[] newFormalArgumentNames = new Object[shorterSize];
			System.arraycopy(formalArgumentNames, 0,
							 newFormalArgumentNames, 0,
							 shorterSize);
			formalArgumentNames = newFormalArgumentNames;
		}

		// keep walking while at least one attribute has values

		ST.AttributeList results = new ST.AttributeList();
		int i = 0; // iteration number from 0
		while ( true ) {
			// get a value for each attribute in list; put into ST instance
			int numEmpty = 0;
			ST embedded = group.createStringTemplateInternally(prototype);
			embedded.rawSetAttribute("i0", i);
			embedded.rawSetAttribute("i", i+1);
			for (int a = 0; a < numExprs; a++) {
				Iterator it = (Iterator) exprs.get(a);
				if ( it!=null && it.hasNext() ) {
					String argName = (String)formalArgumentNames[a];
					Object iteratedValue = it.next();
					embedded.rawSetAttribute(argName, iteratedValue);
				}
				else {
					numEmpty++;
				}
			}
			if ( numEmpty==numExprs ) break;
			results.add(embedded);
			i++;
		}
		return results;
	}

	protected void setFirstArgument(ST self, ST st, Object attr) {
		if ( st.impl.formalArguments==null ) {
			errMgr.runTimeError(this, self,
									  current_ip,
									  ErrorType.ARGUMENT_COUNT_MISMATCH,
									  1,
									  st.impl.name,
									  0);
			return;
		}
		st.locals[0] = attr;
	}

	protected void addToList(List<Object> list, Object o) {
		o = convertAnythingIteratableToIterator(o);
		if ( o instanceof Iterator ) {
			// copy of elements into our temp list
			Iterator it = (Iterator)o;
			while (it.hasNext()) list.add(it.next());
		}
		else {
			list.add(o);
		}
	}

	/** Return the first attribute if multiple valued or the attribute
	 *  itself if single-valued.  Used in <names:first()>
	 */
	public Object first(Object v) {
		if ( v==null ) return null;
		Object r = v;
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			Iterator it = (Iterator)v;
			if ( it.hasNext() ) {
				r = it.next();
			}
		}
		return r;
	}

	/** Return the last attribute if multiple valued or the attribute
	 *  itself if single-valued. Unless it's a list or array, this is pretty
	 *  slow as it iterates until the last element.
	 */
	public Object last(Object v) {
		if ( v==null ) return null;
		if ( v instanceof List ) return ((List)v).get(((List)v).size()-1);
		else if ( v.getClass().isArray() ) {
			Object[] elems = (Object[])v;
			return elems[elems.length-1];
		}
		Object last = v;
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			Iterator it = (Iterator)v;
			while ( it.hasNext() ) {
				last = it.next();
			}
		}
		return last;
	}

	/** Return everything but the first attribute if multiple valued
	 *  or null if single-valued.
	 */
	public Object rest(Object v) {
		if ( v == null ) return null;
		if ( v instanceof List ) { // optimize list case
			List elems = (List)v;
			if ( elems.size()<=1 ) return null;
			return elems.subList(1, elems.size());
		}
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			List a = new ArrayList();
			Iterator it = (Iterator)v;
			if ( !it.hasNext() ) return null; // if not even one value return null
			it.next(); // ignore first value
			while (it.hasNext()) {
				Object o = it.next();
				a.add(o);
			}
			return a;
		}
		return null;  // rest of single-valued attribute is null
	}

	/** Return all but the last element.  trunc(x)=null if x is single-valued. */
	public Object trunc(Object v) {
		if ( v ==null ) return null;
		if ( v instanceof List ) { // optimize list case
			List elems = (List)v;
			if ( elems.size()<=1 ) return null;
			return elems.subList(0, elems.size()-1);
		}
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			List a = new ArrayList();
			Iterator it = (Iterator) v;
			while (it.hasNext()) {
				Object o = it.next();
				if ( it.hasNext() ) a.add(o); // only add if not last one
			}
			return a;
		}
		return null; // trunc(x)==null when x single-valued attribute
	}

	/** Return a new list w/o null values. */
	public Object strip(Object v) {
		if ( v ==null ) return null;
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			List a = new ArrayList();
			Iterator it = (Iterator) v;
			while (it.hasNext()) {
				Object o = it.next();
				if ( o!=null ) a.add(o);
			}
			return a;
		}
		return v; // strip(x)==x when x single-valued attribute
	}

	/** Return a list with the same elements as v but in reverse order. null
	 *  values are NOT stripped out. use reverse(strip(v)) to do that.
	 */
	public Object reverse(Object v) {
		if ( v==null ) return null;
		v = convertAnythingIteratableToIterator(v);
		if ( v instanceof Iterator ) {
			List a = new LinkedList();
			Iterator it = (Iterator)v;
			while (it.hasNext()) a.add(0, it.next());
			return a;
		}
		return v;
	}

	/** Return the length of a mult-valued attribute or 1 if it is a
	 *  single attribute. If attribute is null return 0.
	 *  Special case several common collections and primitive arrays for
	 *  speed. This method by Kay Roepke from v3.
	 */
	public Object length(Object v) {
		if ( v == null) return 0;
		int i = 1;      // we have at least one of something. Iterator and arrays might be empty.
		if ( v instanceof Map ) i = ((Map)v).size();
		else if ( v instanceof Collection ) i = ((Collection)v).size();
		else if ( v instanceof Object[] ) i = ((Object[])v).length;
		else if ( v instanceof int[] ) i = ((int[])v).length;
		else if ( v instanceof long[] ) i = ((long[])v).length;
		else if ( v instanceof float[] ) i = ((float[])v).length;
		else if ( v instanceof double[] ) i = ((double[])v).length;
		else if ( v instanceof Iterator) {
			Iterator it = (Iterator)v;
			i = 0;
			while ( it.hasNext() ) {
				it.next();
				i++;
			}
		}
		return i;
	}

	protected String toString(STWriter out, ST self, Object value) {
		if ( value!=null ) {
			if ( value.getClass()==String.class ) return (String)value;
			// if not string already, must evaluate it
			StringWriter sw = new StringWriter();
			STWriter stw = null;
			try {
				Class writerClass = out.getClass();
				Constructor ctor =
					writerClass.getConstructor(new Class[] {Writer.class});
				stw = (STWriter)ctor.newInstance(sw);
			}
			catch (Exception e) {
				stw = new AutoIndentWriter(sw);
				errMgr.runTimeError(this, self, current_ip, ErrorType.WRITER_CTOR_ISSUE, out.getClass().getSimpleName());
			}
			writeObjectNoOptions(stw, self, value);

			return sw.toString();
		}
		return null;
	}

	public Object convertAnythingIteratableToIterator(Object o) {
		Iterator iter = null;
		if ( o == null ) return null;
		if ( o instanceof Collection )      iter = ((Collection)o).iterator();
		else if ( o.getClass().isArray() ) iter = new ArrayIterator(o);
		else if ( currentScope.st.groupThatCreatedThisInstance.iterateAcrossValues &&
				  o instanceof Map )
		{
			iter = ((Map)o).values().iterator();
		}
		else if ( o instanceof Map )        iter = ((Map)o).keySet().iterator();
		else if ( o instanceof Iterator )  iter = (Iterator)o;
		if ( iter==null ) return o;
		return iter;
	}

	public Iterator convertAnythingToIterator(Object o) {
		o = convertAnythingIteratableToIterator(o);
		if ( o instanceof Iterator ) return (Iterator)o;
		List singleton = new ST.AttributeList(1);
		singleton.add(o);
		return singleton.iterator();
	}

	protected boolean testAttributeTrue(Object a) {
		if ( a==null ) return false;
		if ( a instanceof Boolean ) return (Boolean)a;
		if ( a instanceof Collection ) return ((Collection)a).size()>0;
		if ( a instanceof Map ) return ((Map)a).size()>0;
		if ( a instanceof Iterator ) return ((Iterator)a).hasNext();
		return true; // any other non-null object, return true--it's present
	}

	protected Object getObjectProperty(STWriter out, ST self, Object o, Object property) {
		if ( o==null ) {
			errMgr.runTimeError(this, self, current_ip, ErrorType.NO_SUCH_PROPERTY,
									  "null attribute");
			return null;
		}

		try {
			ModelAdaptor adap = self.groupThatCreatedThisInstance.getModelAdaptor(o.getClass());
			return adap.getProperty(this, self, o, property, toString(out,self,property));
		}
		catch (STNoSuchPropertyException e) {
			errMgr.runTimeError(this, self, current_ip, ErrorType.NO_SUCH_PROPERTY,
									  e, o.getClass().getName()+"."+property);
		}
		return null;
	}

	/** Find an attr via dynamic scoping up enclosing scope chain.
	 *  If not found, look for a map.  So attributes sent in to a template
	 *  override dictionary names.
	 *
	 *  return EMPTY_ATTR if found def but no value
	 */
	public Object getAttribute(ST self, String name) {
		InstanceScope scope = currentScope;
		while ( scope!=null ) {
			ST p = scope.st;
			FormalArgument localArg = null;
			if ( p.impl.formalArguments!=null ) localArg = p.impl.formalArguments.get(name);
			if ( localArg!=null ) {
				Object o = p.locals[localArg.index];
				return o;
			}
			scope = scope.parent; // look up enclosing scope chain
		}
		// got to root scope and no definition, try dictionaries in group and up
		STGroup g = self.impl.nativeGroup;
		Object o = getDictionary(g, name);
		if ( o!=null ) return o;

		// not found, report unknown attr
		if ( ST.cachedNoSuchAttrException ==null ) {
			ST.cachedNoSuchAttrException = new STNoSuchAttributeException();
		}
		ST.cachedNoSuchAttrException.name = name;
		ST.cachedNoSuchAttrException.scope = currentScope;
		throw ST.cachedNoSuchAttrException;
	}

	public Object getDictionary(STGroup g, String name) {
		if ( g.isDictionary(name) ) {
			return g.rawGetDictionary(name);
		}
		if ( g.imports!=null ) {
			for (STGroup sup : g.imports) {
				Object o = getDictionary(sup, name);
				if ( o!=null ) return o;
			}
		}
		return null;
	}

	/** Set any default argument values that were not set by the
	 *  invoking template or by setAttribute directly.  Note
	 *  that the default values may be templates.
	 *
	 *  The evaluation context is the invokedST template itself so
	 *  template default args can see other args.
	 */
	public void setDefaultArguments(STWriter out, ST invokedST) {
		if ( invokedST.impl.formalArguments==null ||
			 invokedST.impl.numberOfArgsWithDefaultValues==0 ) {
			return;
		}
		for (FormalArgument arg : invokedST.impl.formalArguments.values()) {
			// if no value for attribute and default arg, inject default arg into self
			if ( invokedST.locals[arg.index]!=ST.EMPTY_ATTR || arg.defaultValueToken==null ) {
				continue;
			}
			//System.out.println("setting def arg "+arg.name+" to "+arg.defaultValueToken);
			if ( arg.defaultValueToken.getType()==GroupParser.ANONYMOUS_TEMPLATE ) {
				CompiledST code = arg.compiledDefaultValue;
				if (code == null) {
					code = new CompiledST();
				}
				ST defaultArgST = group.createStringTemplateInternally(code);
				defaultArgST.groupThatCreatedThisInstance = group;
				// If default arg is template with single expression
				// wrapped in parens, x={<(...)>}, then eval to string
				// rather than setting x to the template for later
				// eval.
				String defArgTemplate = arg.defaultValueToken.getText();
				if ( defArgTemplate.startsWith("{"+group.delimiterStartChar+"(") &&
					defArgTemplate.endsWith(")"+group.delimiterStopChar+"}") ) {

					invokedST.rawSetAttribute(arg.name, toString(out, invokedST, defaultArgST));
				}
				else {
					invokedST.rawSetAttribute(arg.name, defaultArgST);
				}
			}
			else {
				invokedST.rawSetAttribute(arg.name, arg.defaultValue);
			}
		}
	}

	private void popScope() {
		current_ip = currentScope.ret_ip;
		currentScope = currentScope.parent; // pop
	}

	private void pushScope(ST self) {
		currentScope = new InstanceScope(currentScope, self); // push
		if ( debug ) {
			currentScope.events = new ArrayList<InterpEvent>();
			currentScope.childEvalTemplateEvents = new ArrayList<EvalTemplateEvent>();
		}
		currentScope.ret_ip = current_ip;
	}

	/** If an instance of x is enclosed in a y which is in a z, return
	 *  a String of these instance names in order from topmost to lowest;
	 *  here that would be "[z y x]".
	 */
	public static String getEnclosingInstanceStackString(InstanceScope scope) {
		List<ST> templates = getEnclosingInstanceStack(scope, true);
		StringBuilder buf = new StringBuilder();
		int i = 0;
		for (ST st : templates) {
			if ( i>0 ) buf.append(" ");
			buf.append(st.getName());
			i++;
		}
		return buf.toString();
	}

	public static List<ST> getEnclosingInstanceStack(InstanceScope scope, boolean topdown) {
		List<ST> stack = new LinkedList<ST>();
		InstanceScope p = scope;
		while ( p!=null ) {
			if ( topdown ) stack.add(0,p.st);
			else stack.add(p.st);
			p = p.parent;
		}
		return stack;
	}

	public static List<InstanceScope> getScopeStack(InstanceScope scope, boolean topdown) {
		List<InstanceScope> stack = new LinkedList<InstanceScope>();
		InstanceScope p = scope;
		while ( p!=null ) {
			if ( topdown ) stack.add(0,p);
			else stack.add(p);
			p = p.parent;
		}
		return stack;
	}

	public static List<EvalTemplateEvent> getEvalTemplateEventStack(InstanceScope scope, boolean topdown) {
		List<EvalTemplateEvent> stack = new LinkedList<EvalTemplateEvent>();
		InstanceScope p = scope;
		while ( p!=null ) {
			EvalTemplateEvent eval = (EvalTemplateEvent)p.events.get(p.events.size()-1);
			if ( topdown ) stack.add(0,eval);
			else stack.add(eval);
			p = p.parent;
		}
		return stack;
	}

	protected void trace(ST self, int ip) {
		StringBuilder tr = new StringBuilder();
		BytecodeDisassembler dis = new BytecodeDisassembler(self.impl);
		StringBuilder buf = new StringBuilder();
		dis.disassembleInstruction(buf,ip);
		String name = self.impl.name+":";
		if ( self.impl.name==ST.UNKNOWN_NAME ) name = "";
		tr.append(String.format("%-40s",name+buf));
		tr.append("\tstack=[");
		for (int i = 0; i <= sp; i++) {
			Object o = operands[i];
			printForTrace(tr,o);
		}
		tr.append(" ], calls=");
		tr.append(getEnclosingInstanceStackString(currentScope));
		tr.append(", sp="+sp+", nw="+ nwline);
		String s = tr.toString();
		if ( debug ) executeTrace.add(s);
		if ( trace ) System.out.println(s);
	}

	protected void printForTrace(StringBuilder tr, Object o) {
		if ( o instanceof ST ) {
			if ( ((ST)o).impl ==null ) tr.append("bad-template()");
			else tr.append(" "+((ST)o).impl.name+"()");
			return;
		}
		o = convertAnythingIteratableToIterator(o);
		if ( o instanceof Iterator ) {
			Iterator it = (Iterator)o;
			tr.append(" [");
			while ( it.hasNext() ) {
				Object iterValue = it.next();
				printForTrace(tr, iterValue);
			}
			tr.append(" ]");
		}
		else {
			tr.append(" "+o);
		}
	}

	public List<InterpEvent> getEvents() { return events; }

	/** For every event, we track in overall list and in self's
	 *  event list so that each template has a list of events used to
	 *  create it.  If EvalTemplateEvent, store in parent's
	 *  childEvalTemplateEvents list for STViz tree view.
	 */
	protected void trackDebugEvent(ST self, InterpEvent e) {
//		System.out.println(e);
		this.events.add(e);
		currentScope.events.add(e);
		if ( e instanceof EvalTemplateEvent ) {
			InstanceScope parent = currentScope.parent;
			if ( parent!=null ) {
				// System.out.println("add eval "+e.self.getName()+" to children of "+parent.getName());
				currentScope.parent.childEvalTemplateEvents.add((EvalTemplateEvent)e);
			}
		}
	}

	public List<String> getExecutionTrace() { return executeTrace; }

	public static int getShort(byte[] memory, int index) {
		int b1 = memory[index]&0xFF; // mask off sign-extended bits
		int b2 = memory[index+1]&0xFF;
		return b1<<(8*1) | b2;
	}

}

