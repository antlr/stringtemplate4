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
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.*;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This class knows how to execute template bytecodes relative to a particular
 * {@link STGroup}. To execute the byte codes, we need an output stream and a
 * reference to an {@link ST} instance. That instance's {@link ST#impl} field
 * points at a {@link CompiledST}, which contains all of the byte codes and
 * other information relevant to execution.
 * <p>
 * This interpreter is a stack-based bytecode interpreter. All operands go onto
 * an operand stack.</p>
 * <p>
 * If {@link #debug} set, we track interpreter events. For now, I am only
 * tracking instance creation events. These are used by {@link STViz} to pair up
 * output chunks with the template expressions that generate them.</p>
 * <p>
 * We create a new interpreter for each invocation of
 * {@link ST#render}, {@link ST#inspect}, or {@link ST#getEvents}.</p>
 */
public class Interpreter {
	public enum Option { ANCHOR, FORMAT, NULL, SEPARATOR, WRAP }
	public static final int DEFAULT_OPERAND_STACK_SIZE = 100;

	public static final Set<String> predefinedAnonSubtemplateAttributes =
		new HashSet<String>() { { add("i"); add("i0"); } };

	/** Operand stack, grows upwards. */
	Object[] operands = new Object[DEFAULT_OPERAND_STACK_SIZE];
	/** Stack pointer register. */
	int sp = -1;
	/** The number of characters written on this template line so far. */
	int nwline = 0;

	/** Render template with respect to this group.
	 *
	 *  @see ST#groupThatCreatedThisInstance
	 *  @see CompiledST#nativeGroup
	 */
	STGroup group;

	/** For renderers, we have to pass in the locale. */
	Locale locale;

	ErrorManager errMgr;

	/**
	 * Dump bytecode instructions as they are executed. This field is mostly for
	 * StringTemplate development.
	 */
	public static boolean trace = false;

	/** If {@link #trace} is {@code true}, track trace here. */
	// TODO: track the pieces not a string and track what it contributes to output
	protected List<String> executeTrace;

	/** When {@code true}, track events inside templates and in {@link #events}. */
	public boolean debug = false;

	/**
	 * Track everything happening in interpreter across all templates if
	 * {@link #debug}. The last event in this field is the
	 * {@link EvalTemplateEvent} for the root template.
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

	/** Execute template {@code self} and return how many characters it wrote to {@code out}.
	 *
	 * @return the number of characters written to {@code out}
	 */
	public int exec(STWriter out, InstanceScope scope) {
		final ST self = scope.st;
		if ( trace ) System.out.println("exec("+self.getName()+")");
		try {
			setDefaultArguments(out, scope);
			return _exec(out, scope);
		}
		catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			errMgr.runTimeError(this, scope, ErrorType.INTERNAL_ERROR,
								"internal error: "+sw.toString());
			return 0;
		}
	}

	protected int _exec(STWriter out, InstanceScope scope) {
		final ST self = scope.st;
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
			if ( trace || debug ) trace(scope, ip);
			short opcode = code[ip];
			//count[opcode]++;
			scope.ip = ip;
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
						o = getAttribute(scope, name);
						if ( o==ST.EMPTY_ATTR ) o = null;
					}
					catch (STNoSuchAttributeException nsae) {
						errMgr.runTimeError(this, scope, ErrorType.NO_SUCH_ATTRIBUTE, name);
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
					operands[++sp] = getObjectProperty(out, scope, o, name);
					break;
				case Bytecode.INSTR_LOAD_PROP_IND :
					Object propName = operands[sp--];
					o = operands[sp];
					operands[sp] = getObjectProperty(out, scope, o, propName);
					break;
				case Bytecode.INSTR_NEW :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					// look up in original hierarchy not enclosing template (variable group)
					// see TestSubtemplates.testEvalSTFromAnotherGroup()
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, scope, name);
					// get n args and store into st's attr list
					storeArgs(scope, nargs, st);
					sp -= nargs;
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_NEW_IND:
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = (String)operands[sp-nargs];
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, scope, name);
					storeArgs(scope, nargs, st);
					sp -= nargs;
					sp--; // pop template name
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_NEW_BOX_ARGS :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					Map<String, Object> attrs = (ArgumentsMap)operands[sp--];
					// look up in original hierarchy not enclosing template (variable group)
					// see TestSubtemplates.testEvalSTFromAnotherGroup()
					st = self.groupThatCreatedThisInstance.getEmbeddedInstanceOf(this, scope, name);
					// get n args and store into st's attr list
					storeArgs(scope, attrs, st);
					operands[++sp] = st;
					break;
				case Bytecode.INSTR_SUPER_NEW :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					nargs = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					super_new(scope, name, nargs);
					break;
				case Bytecode.INSTR_SUPER_NEW_BOX_ARGS :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					attrs = (ArgumentsMap)operands[sp--];
					super_new(scope, name, attrs);
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
					attrs = (ArgumentsMap)operands[sp];
					attrs.put(name, o); // leave attrs on stack
					break;
				case Bytecode.INSTR_WRITE :
					o = operands[sp--];
					int n1 = writeObjectNoOptions(out, scope, o);
					n += n1;
					nwline += n1;
					break;
				case Bytecode.INSTR_WRITE_OPT :
					options = (Object[])operands[sp--]; // get options
					o = operands[sp--];                 // get option to write
					int n2 = writeObjectWithOptions(out, scope, o, options);
					n += n2;
					nwline += n2;
					break;
				case Bytecode.INSTR_MAP :
					st = (ST)operands[sp--]; // get prototype off stack
					o = operands[sp--];		 // get object to map prototype across
					map(scope,o,st);
					break;
				case Bytecode.INSTR_ROT_MAP :
					int nmaps = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					List<ST> templates = new ArrayList<ST>();
					for (int i=nmaps-1; i>=0; i--) templates.add((ST)operands[sp-i]);
					sp -= nmaps;
					o = operands[sp--];
					if ( o!=null ) rot_map(scope,o,templates);
					break;
				case Bytecode.INSTR_ZIP_MAP:
					st = (ST)operands[sp--];
					nmaps = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					List<Object> exprs = new ObjectList();
					for (int i=nmaps-1; i>=0; i--) exprs.add(operands[sp-i]);
					sp -= nmaps;
					operands[++sp] = zip_map(scope, exprs, st);
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
					operands[++sp] = new ArgumentsMap();
					break;
				case Bytecode.INSTR_PASSTHRU :
					nameIndex = getShort(code, ip);
					ip += Bytecode.OPND_SIZE_IN_BYTES;
					name = self.impl.strings[nameIndex];
					attrs = (ArgumentsMap)operands[sp];
					passthru(scope, name, attrs);
					break;
				case Bytecode.INSTR_LIST :
					operands[++sp] = new ObjectList();
					break;
				case Bytecode.INSTR_ADD :
					o = operands[sp--];             // pop value
					List<Object> list = (ObjectList)operands[sp]; // don't pop list
					addToList(scope, list, o);
					break;
				case Bytecode.INSTR_TOSTR :
					// replace with string value; early eval
					operands[sp] = toString(out, scope, operands[sp]);
					break;
				case Bytecode.INSTR_FIRST  :
					operands[sp] = first(scope, operands[sp]);
					break;
				case Bytecode.INSTR_LAST   :
					operands[sp] = last(scope, operands[sp]);
					break;
				case Bytecode.INSTR_REST   :
					operands[sp] = rest(scope, operands[sp]);
					break;
				case Bytecode.INSTR_TRUNC  :
					operands[sp] = trunc(scope, operands[sp]);
					break;
				case Bytecode.INSTR_STRIP  :
					operands[sp] = strip(scope, operands[sp]);
					break;
				case Bytecode.INSTR_TRIM   :
					o = operands[sp--];
					if ( o.getClass() == String.class ) {
						operands[++sp] = ((String)o).trim();
					}
					else {
						errMgr.runTimeError(this, scope, ErrorType.EXPECTING_STRING, "trim", o.getClass().getName());
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
						errMgr.runTimeError(this, scope, ErrorType.EXPECTING_STRING, "strlen", o.getClass().getName());
						operands[++sp] = 0;
					}
					break;
				case Bytecode.INSTR_REVERSE :
					operands[sp] = reverse(scope, operands[sp]);
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
					indent(out, scope, strIndex);
					break;
				case Bytecode.INSTR_DEDENT :
					out.popIndentation();
					break;
				case Bytecode.INSTR_NEWLINE :
					try {
						if ( (prevOpcode==0 && !self.isAnonSubtemplate() && !self.impl.isRegion) ||
							prevOpcode==Bytecode.INSTR_NEWLINE ||
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
					n1 = writeObjectNoOptions(out, scope, o);
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
			EvalTemplateEvent e = new EvalTemplateEvent(scope, start, stop);
			trackDebugEvent(scope, e);
		}
		return n;
	}

	void load_str(ST self, int ip) {
		int strIndex = getShort(self.impl.instrs, ip);
		ip += Bytecode.OPND_SIZE_IN_BYTES;
		operands[++sp] = self.impl.strings[strIndex];
	}

	// TODO: refactor to remove dup'd code
	void super_new(InstanceScope scope, String name, int nargs) {
		final ST self = scope.st;
		ST st = null;
		CompiledST imported = self.impl.nativeGroup.lookupImportedTemplate(name);
		if ( imported==null ) {
			errMgr.runTimeError(this, scope, ErrorType.NO_IMPORTED_TEMPLATE,
								name);
			st = self.groupThatCreatedThisInstance.createStringTemplateInternally(new CompiledST());
		}
		else {
			st = imported.nativeGroup.getEmbeddedInstanceOf(this, scope, name);
			st.groupThatCreatedThisInstance = group;
		}
		// get n args and store into st's attr list
		storeArgs(scope, nargs, st);
		sp -= nargs;
		operands[++sp] = st;
	}

	void super_new(InstanceScope scope, String name, Map<String,Object> attrs) {
		final ST self = scope.st;
		ST st = null;
		CompiledST imported = self.impl.nativeGroup.lookupImportedTemplate(name);
		if ( imported==null ) {
			errMgr.runTimeError(this, scope, ErrorType.NO_IMPORTED_TEMPLATE,
								name);
			st = self.groupThatCreatedThisInstance.createStringTemplateInternally(new CompiledST());
		}
		else {
			st = imported.nativeGroup.createStringTemplateInternally(imported);
			st.groupThatCreatedThisInstance = group;
		}

		// get n args and store into st's attr list
		storeArgs(scope, attrs, st);
		operands[++sp] = st;
	}

	void passthru(InstanceScope scope, String templateName, Map<String,Object> attrs) {
		CompiledST c = group.lookupTemplate(templateName);
		if ( c==null ) return; // will get error later
		if ( c.formalArguments==null ) return;
		for (FormalArgument arg : c.formalArguments.values()) {
			// if not already set by user, set to value from outer scope
			if ( !attrs.containsKey(arg.name) ) {
				//System.out.println("arg "+arg.name+" missing");
				try {
					Object o = getAttribute(scope, arg.name);
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
						errMgr.runTimeError(this, scope, ErrorType.NO_SUCH_ATTRIBUTE_PASS_THROUGH, arg.name);
						attrs.put(arg.name, null);
					}
				}
			}
		}
	}

	void storeArgs(InstanceScope scope, Map<String,Object> attrs, ST st) {
		boolean noSuchAttributeReported = false;
		if (attrs != null) {
			for (Map.Entry<String, Object> argument : attrs.entrySet()) {
				if (!st.impl.hasFormalArgs) {
					if (st.impl.formalArguments == null || !st.impl.formalArguments.containsKey(argument.getKey())) {
						try {
							// we clone the CompiledST to prevent modifying the original
							// formalArguments map during interpretation.
							st.impl = st.impl.clone();
							st.add(argument.getKey(), argument.getValue());
						} catch (CloneNotSupportedException ex) {
							noSuchAttributeReported = true;
							errMgr.runTimeError(this, scope,
												ErrorType.NO_SUCH_ATTRIBUTE,
												argument.getKey());
						}
					}
					else {
						st.rawSetAttribute(argument.getKey(), argument.getValue());
					}
				}
				else {
					// don't let it throw an exception in rawSetAttribute
					if ( st.impl.formalArguments==null || !st.impl.formalArguments.containsKey(argument.getKey()) ) {
						noSuchAttributeReported = true;
						errMgr.runTimeError(this, scope,
											ErrorType.NO_SUCH_ATTRIBUTE,
											argument.getKey());
						continue;
					}

					st.rawSetAttribute(argument.getKey(), argument.getValue());
				}
			}
		}

		if (st.impl.hasFormalArgs) {
			boolean argumentCountMismatch = false;
			Map<String, FormalArgument> formalArguments = st.impl.formalArguments;
			if (formalArguments == null) {
				formalArguments = Collections.emptyMap();
			}

			// first make sure that all non-default arguments are specified
			// ignore this check if a NO_SUCH_ATTRIBUTE error already occurred
			if (!noSuchAttributeReported) {
				for (Map.Entry<String, FormalArgument> formalArgument : formalArguments.entrySet()) {
					if (formalArgument.getValue().defaultValueToken != null || formalArgument.getValue().defaultValue != null) {
						// this argument has a default value, so it doesn't need to appear in attrs
						continue;
					}

					if (attrs == null || !attrs.containsKey(formalArgument.getKey())) {
						argumentCountMismatch = true;
						break;
					}
				}
			}

			// next make sure there aren't too many arguments. note that the names
			// of arguments are checked below as they are applied to the template
			// instance, so there's no need to do that here.
			if (attrs != null && attrs.size() > formalArguments.size()) {
				argumentCountMismatch = true;
			}

			if (argumentCountMismatch) {
				int nargs = attrs != null ? attrs.size() : 0;
				int nformalArgs = formalArguments.size();
				errMgr.runTimeError(this, scope,
									ErrorType.ARGUMENT_COUNT_MISMATCH,
									nargs,
									st.impl.name,
									nformalArgs);
			}
		}
	}

	void storeArgs(InstanceScope scope, int nargs, ST st) {
		if ( nargs>0 && !st.impl.hasFormalArgs && st.impl.formalArguments==null ) {
			st.add(ST.IMPLICIT_ARG_NAME, null); // pretend we have "it" arg
		}

		int nformalArgs = 0;
		if ( st.impl.formalArguments!=null ) nformalArgs = st.impl.formalArguments.size();
		int firstArg = sp-(nargs-1);
		int numToStore = Math.min(nargs, nformalArgs);
		if ( st.impl.isAnonSubtemplate ) nformalArgs -= predefinedAnonSubtemplateAttributes.size();

		if ( nargs < (nformalArgs-st.impl.numberOfArgsWithDefaultValues) ||
			 nargs > nformalArgs )
		{
			errMgr.runTimeError(this, scope,
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

	protected void indent(STWriter out, InstanceScope scope, int strIndex) {
		String indent = scope.st.impl.strings[strIndex];
		if ( debug ) {
			int start = out.index(); // track char we're about to write
			EvalExprEvent e = new IndentEvent(scope,
											  start, start + indent.length() - 1,
											  getExprStartChar(scope),
											  getExprStopChar(scope));
			trackDebugEvent(scope, e);
		}
		out.pushIndentation(indent);
	}

	/** Write out an expression result that doesn't use expression options.
	 *  E.g., {@code <name>}
	 */
	protected int writeObjectNoOptions(STWriter out, InstanceScope scope, Object o) {
		int start = out.index(); // track char we're about to write
		int n = writeObject(out, scope, o, null);
        if ( debug ) {
			EvalExprEvent e = new EvalExprEvent(scope,
												start, out.index() - 1,
												getExprStartChar(scope),
												getExprStopChar(scope));
			trackDebugEvent(scope, e);
        }
		return n;
	}

	/** Write out an expression result that uses expression options.
	 *  E.g., {@code <names; separator=", ">}
	 */
	protected int writeObjectWithOptions(STWriter out, InstanceScope scope, Object o,
										 Object[] options)
	{
		int start = out.index(); // track char we're about to write
		// precompute all option values (render all the way to strings)
		String[] optionStrings = null;
		if ( options!=null ) {
			optionStrings = new String[options.length];
			for (int i=0; i<Compiler.NUM_OPTIONS; i++) {
				optionStrings[i] = toString(out, scope, options[i]);
			}
		}
		if ( options!=null && options[Option.ANCHOR.ordinal()]!=null ) {
			out.pushAnchorPoint();
		}

		int n = writeObject(out, scope, o, optionStrings);

		if ( options!=null && options[Option.ANCHOR.ordinal()]!=null ) {
			out.popAnchorPoint();
		}
        if ( debug ) {
			EvalExprEvent e = new EvalExprEvent(scope,
												start, out.index() - 1,
												getExprStartChar(scope),
												getExprStopChar(scope));
			trackDebugEvent(scope, e);
        }
		return n;
	}

	/** Generic method to emit text for an object. It differentiates
	 *  between templates, iterable objects, and plain old Java objects (POJOs)
	 */
	protected int writeObject(STWriter out, InstanceScope scope, Object o, String[] options) {
		int n = 0;
		if ( o == null ) {
			if ( options!=null && options[Option.NULL.ordinal()]!=null ) {
				o = options[Option.NULL.ordinal()];
			}
			else return 0;
		}
		if ( o instanceof ST ) {
			scope = new InstanceScope(scope, (ST)o);
			if ( options!=null && options[Option.WRAP.ordinal()]!=null ) {
				// if we have a wrap string, then inform writer it
				// might need to wrap
				try {
					out.writeWrap(options[Option.WRAP.ordinal()]);
				}
				catch (IOException ioe) {
					errMgr.IOError(scope.st, ErrorType.WRITE_IO_ERROR, ioe);
				}
			}
			n = exec(out, scope);
		}
		else {
			o = convertAnythingIteratableToIterator(scope, o); // normalize
			try {
				if ( o instanceof Iterator) n = writeIterator(out, scope, o, options);
				else n = writePOJO(out, scope, o, options);
			}
			catch (IOException ioe) {
				errMgr.IOError(scope.st, ErrorType.WRITE_IO_ERROR, ioe, o);
			}
		}
		return n;
	}

	protected int writeIterator(STWriter out, InstanceScope scope, Object o, String[] options) throws IOException {
		if ( o==null ) return 0;
		int n = 0;
		Iterator<?> it = (Iterator<?>)o;
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
			int nw = writeObject(out, scope, iterValue, options);
			if ( nw > 0 ) seenAValue = true;
			n += nw;
		}
		return n;
	}

	protected int writePOJO(STWriter out, InstanceScope scope, Object o, String[] options) throws IOException {
		String formatString = null;
		if ( options!=null ) formatString = options[Option.FORMAT.ordinal()];
		// ask the native group defining the surrounding template for the renderer
		AttributeRenderer r = scope.st.impl.nativeGroup.getAttributeRenderer(o.getClass());
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

	protected int getExprStartChar(InstanceScope scope) {
		Interval templateLocation = scope.st.impl.sourceMap[scope.ip];
		if ( templateLocation!=null ) return templateLocation.a;
		return -1;
	}

	protected int getExprStopChar(InstanceScope scope) {
		Interval templateLocation = scope.st.impl.sourceMap[scope.ip];
		if ( templateLocation!=null ) return templateLocation.b;
		return -1;
	}

	protected void map(InstanceScope scope, Object attr, final ST st) {
		rot_map(scope, attr, new ArrayList<ST>() {{add(st);}});
	}

	/**
	 * Renders expressions of the form {@code <names:a()>} or
	 * {@code <names:a(),b()>}.
	 */
	protected void rot_map(InstanceScope scope, Object attr, List<ST> prototypes) {
		if ( attr==null ) {
			operands[++sp] = null;
			return;
		}
		attr = convertAnythingIteratableToIterator(scope, attr);
		if ( attr instanceof Iterator ) {
			List<ST> mapped = rot_map_iterator(scope, (Iterator) attr, prototypes);
			operands[++sp] = mapped;
		}
		else { // if only single value, just apply first template to sole value
			ST proto = prototypes.get(0);
			ST st = group.createStringTemplateInternally(proto);
			if ( st!=null ) {
				setFirstArgument(scope, st, attr);
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

	protected List<ST> rot_map_iterator(InstanceScope scope, Iterator<?> attr, List<ST> prototypes) {
		List<ST> mapped = new ArrayList<ST>();
		Iterator<?> iter = attr;
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
			setFirstArgument(scope, st, iterValue);
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

	/**
	 * Renders expressions of the form {@code <names,phones:{n,p | ...}>} or
	 * {@code <a,b:t()>}.
	 */
	// todo: i, i0 not set unless mentioned? map:{k,v | ..}?
	protected ST.AttributeList zip_map(InstanceScope scope, List<Object> exprs, ST prototype) {
		if ( exprs==null || prototype==null || exprs.size()==0 ) {
			return null; // do not apply if missing templates or empty values
		}
		// make everything iterable
		for (int i = 0; i < exprs.size(); i++) {
			Object attr = exprs.get(i);
			if ( attr!=null ) exprs.set(i, convertAnythingToIterator(scope, attr));
		}

		// ensure arguments line up
		int numExprs = exprs.size();
		CompiledST code = prototype.impl;
		Map<String, FormalArgument> formalArguments = code.formalArguments;
		if ( !code.hasFormalArgs || formalArguments==null ) {
			errMgr.runTimeError(this, scope, ErrorType.MISSING_FORMAL_ARGUMENTS);
			return null;
		}

		// todo: track formal args not names for efficient filling of locals
		String[] formalArgumentNames = formalArguments.keySet().toArray(new String[formalArguments.size()]);
		int nformalArgs = formalArgumentNames.length;
		if ( prototype.isAnonSubtemplate() ) nformalArgs -= predefinedAnonSubtemplateAttributes.size();
		if ( nformalArgs != numExprs ) {
			errMgr.runTimeError(this, scope,
									  ErrorType.MAP_ARGUMENT_COUNT_MISMATCH,
									  numExprs,
									  nformalArgs);
			// TODO just fill first n
			// truncate arg list to match smaller size
			int shorterSize = Math.min(formalArgumentNames.length, numExprs);
			numExprs = shorterSize;
			String[] newFormalArgumentNames = new String[shorterSize];
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
				Iterator<?> it = (Iterator<?>) exprs.get(a);
				if ( it!=null && it.hasNext() ) {
					String argName = formalArgumentNames[a];
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

	protected void setFirstArgument(InstanceScope scope, ST st, Object attr) {
		if ( !st.impl.hasFormalArgs ) {
			if ( st.impl.formalArguments==null ) {
				st.add(ST.IMPLICIT_ARG_NAME, attr);
				return;
			}
			// else fall thru to set locals[0]
		}
		if ( st.impl.formalArguments==null ) {
			errMgr.runTimeError(this, scope,
									  ErrorType.ARGUMENT_COUNT_MISMATCH,
									  1,
									  st.impl.name,
									  0);
			return;
		}
		st.locals[0] = attr;
	}

	protected void addToList(InstanceScope scope, List<Object> list, Object o) {
		o = convertAnythingIteratableToIterator(scope, o);
		if ( o instanceof Iterator ) {
			// copy of elements into our temp list
			Iterator<?> it = (Iterator<?>)o;
			while (it.hasNext()) list.add(it.next());
		}
		else {
			list.add(o);
		}
	}

	/**
	 * Return the first attribute if multi-valued, or the attribute itself if
	 * single-valued.
	 * <p>
	 * This method is used for rendering expressions of the form
	 * {@code <names:first()>}.</p>
	 */
	public Object first(InstanceScope scope, Object v) {
		if ( v==null ) return null;
		Object r = v;
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			Iterator<?> it = (Iterator<?>)v;
			if ( it.hasNext() ) {
				r = it.next();
			}
		}
		return r;
	}

	/**
	 * Return the last attribute if multi-valued, or the attribute itself if
	 * single-valued. Unless it's a {@link List} or array, this is pretty slow
	 * as it iterates until the last element.
	 * <p>
	 * This method is used for rendering expressions of the form
	 * {@code <names:last()>}.</p>
	 */
	public Object last(InstanceScope scope, Object v) {
		if ( v==null ) return null;
		if ( v instanceof List ) return ((List<?>)v).get(((List<?>)v).size()-1);
		else if ( v.getClass().isArray() ) {
			return Array.get(v, Array.getLength(v) - 1);
		}
		Object last = v;
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			Iterator<?> it = (Iterator<?>)v;
			while ( it.hasNext() ) {
				last = it.next();
			}
		}
		return last;
	}

	/**
	 * Return everything but the first attribute if multi-valued, or
	 * {@code null} if single-valued.
	 */
	public Object rest(InstanceScope scope, Object v) {
		if ( v == null ) return null;
		if ( v instanceof List ) { // optimize list case
			List<?> elems = (List<?>)v;
			if ( elems.size()<=1 ) return null;
			return elems.subList(1, elems.size());
		}
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			List<Object> a = new ArrayList<Object>();
			Iterator<?> it = (Iterator<?>)v;
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

	/** Return all but the last element. <code>trunc(<i>x</i>)==null</code> if <code><i>x</i></code> is single-valued. */
	public Object trunc(InstanceScope scope, Object v) {
		if ( v ==null ) return null;
		if ( v instanceof List ) { // optimize list case
			List<?> elems = (List<?>)v;
			if ( elems.size()<=1 ) return null;
			return elems.subList(0, elems.size()-1);
		}
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			List<Object> a = new ArrayList<Object>();
			Iterator<?> it = (Iterator<?>) v;
			while (it.hasNext()) {
				Object o = it.next();
				if ( it.hasNext() ) a.add(o); // only add if not last one
			}
			return a;
		}
		return null; // trunc(x)==null when x single-valued attribute
	}

	/** Return a new list without {@code null} values. */
	public Object strip(InstanceScope scope, Object v) {
		if ( v ==null ) return null;
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			List<Object> a = new ArrayList<Object>();
			Iterator<?> it = (Iterator<?>) v;
			while (it.hasNext()) {
				Object o = it.next();
				if ( o!=null ) a.add(o);
			}
			return a;
		}
		return v; // strip(x)==x when x single-valued attribute
	}

	/**
	 * Return a list with the same elements as {@code v} but in reverse order.
	 * <p>
	 * Note that {@code null} values are <i>not</i> stripped out; use
	 * {@code reverse(strip(v))} to do that.</p>
	 */
	public Object reverse(InstanceScope scope, Object v) {
		if ( v==null ) return null;
		v = convertAnythingIteratableToIterator(scope, v);
		if ( v instanceof Iterator ) {
			List<Object> a = new LinkedList<Object>();
			Iterator<?> it = (Iterator<?>)v;
			while (it.hasNext()) a.add(0, it.next());
			return a;
		}
		return v;
	}

	/**
	 * Return the length of a multi-valued attribute or 1 if it is a single
	 * attribute. If {@code v} is {@code null} return 0.
	 * <p>
	 * The implementation treats several common collections and arrays as
	 * special cases for speed.</p>
	 */
	public Object length(Object v) {
		if ( v == null) return 0;
		int i = 1;      // we have at least one of something. Iterator and arrays might be empty.
		if ( v instanceof Map ) i = ((Map<?, ?>)v).size();
		else if ( v instanceof Collection ) i = ((Collection<?>)v).size();
		else if ( v instanceof Object[] ) i = ((Object[])v).length;
		else if ( v.getClass().isArray() ) i = Array.getLength(v);
		else if ( v instanceof Iterable || v instanceof Iterator ) {
			Iterator<?> it = v instanceof Iterable ? ((Iterable<?>)v).iterator() : (Iterator<?>)v;
			i = 0;
			while ( it.hasNext() ) {
				it.next();
				i++;
			}
		}
		return i;
	}

	protected String toString(STWriter out, InstanceScope scope, Object value) {
		if ( value!=null ) {
			if ( value.getClass()==String.class ) return (String)value;
			// if not string already, must evaluate it
			StringWriter sw = new StringWriter();
			STWriter stw;
			try {
				Class<? extends STWriter> writerClass = out.getClass();
				Constructor<? extends STWriter> ctor = writerClass.getConstructor(Writer.class);
				stw = ctor.newInstance(sw);
			}
			catch (Exception e) {
				stw = new AutoIndentWriter(sw);
				errMgr.runTimeError(this, scope, ErrorType.WRITER_CTOR_ISSUE, out.getClass().getSimpleName());
			}

			if (debug && !scope.earlyEval) {
				scope = new InstanceScope(scope, scope.st);
				scope.earlyEval = true;
			}

			writeObjectNoOptions(stw, scope, value);

			return sw.toString();
		}
		return null;
	}

	public Object convertAnythingIteratableToIterator(InstanceScope scope, Object o) {
		Iterator<?> iter = null;
		if ( o == null ) return null;
		if ( o instanceof Iterable )      iter = ((Iterable<?>)o).iterator();
		else if ( o instanceof Object[] )  iter = Arrays.asList((Object[])o).iterator();
		else if ( o.getClass().isArray() ) iter = new ArrayIterator(o);
		else if ( o instanceof Map ) {
			if (scope.st.groupThatCreatedThisInstance.iterateAcrossValues) {
				iter = ((Map<?, ?>)o).values().iterator();
			}
			else {
				iter = ((Map<?, ?>)o).keySet().iterator();
			}
		}
		//// this is implied by the following line
		//else if ( o instanceof Iterator ) {
		//	iter = (Iterator<?>)o;
		//}
		if ( iter==null ) return o;
		return iter;
	}

	public Iterator<?> convertAnythingToIterator(InstanceScope scope, Object o) {
		o = convertAnythingIteratableToIterator(scope, o);
		if ( o instanceof Iterator ) return (Iterator<?>)o;
		List<Object> singleton = new ST.AttributeList(1);
		singleton.add(o);
		return singleton.iterator();
	}

	protected boolean testAttributeTrue(Object a) {
		if ( a==null ) return false;
		if ( a instanceof Boolean ) return (Boolean)a;
		if ( a instanceof Collection ) return ((Collection<?>)a).size()>0;
		if ( a instanceof Map ) return ((Map<?, ?>)a).size()>0;
		if ( a instanceof Iterable ) {
			return ((Iterable<?>)a).iterator().hasNext();
		}
		if ( a instanceof Iterator ) return ((Iterator<?>)a).hasNext();
		return true; // any other non-null object, return true--it's present
	}

	protected Object getObjectProperty(STWriter out, InstanceScope scope, Object o, Object property) {
		if ( o==null ) {
			errMgr.runTimeError(this, scope, ErrorType.NO_SUCH_PROPERTY,
									  "null." + property);
			return null;
		}

		try {
			final ST self = scope.st;
			ModelAdaptor adap = self.groupThatCreatedThisInstance.getModelAdaptor(o.getClass());
			return adap.getProperty(this, self, o, property, toString(out,scope,property));
		}
		catch (STNoSuchPropertyException e) {
			errMgr.runTimeError(this, scope, ErrorType.NO_SUCH_PROPERTY,
									  e, o.getClass().getName()+"."+property);
		}
		return null;
	}

	/**
	 * Find an attribute via dynamic scoping up enclosing scope chain. Only look
	 * for a dictionary definition if the attribute is not found, so attributes
	 * sent in to a template override dictionary names.
	 * <p>
	 * Return {@link ST#EMPTY_ATTR} if found definition but no value.</p>
	 */
	public Object getAttribute(InstanceScope scope, String name) {
		InstanceScope current = scope;
		while ( current!=null ) {
			ST p = current.st;
			FormalArgument localArg = null;
			if ( p.impl.formalArguments!=null ) localArg = p.impl.formalArguments.get(name);
			if ( localArg!=null ) {
				Object o = p.locals[localArg.index];
				return o;
			}
			current = current.parent; // look up enclosing scope chain
		}
		// got to root scope and no definition, try dictionaries in group and up
		final ST self = scope.st;
		STGroup g = self.impl.nativeGroup;
		Object o = getDictionary(g, name);
		if ( o!=null ) return o;

		// not found, report unknown attr
		throw new STNoSuchAttributeException(name, scope);
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

	/**
	 * Set any default argument values that were not set by the invoking
	 * template or by {@link ST#add} directly. Note that the default values may
	 * be templates.
	 * <p>
	 * The evaluation context is the {@code invokedST} template itself so
	 * template default arguments can see other arguments.</p>
	 */
	public void setDefaultArguments(STWriter out, InstanceScope scope) {
		final ST invokedST = scope.st;
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

					invokedST.rawSetAttribute(arg.name, toString(out, new InstanceScope(scope, invokedST), defaultArgST));
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

	/**
	 * If an instance of <i>x</i> is enclosed in a <i>y</i> which is in a
	 * <i>z</i>, return a {@code String} of these instance names in order from
	 * topmost to lowest; here that would be {@code [z y x]}.
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

	protected void trace(InstanceScope scope, int ip) {
		final ST self = scope.st;
		StringBuilder tr = new StringBuilder();
		BytecodeDisassembler dis = new BytecodeDisassembler(self.impl);
		StringBuilder buf = new StringBuilder();
		dis.disassembleInstruction(buf,ip);
		String name = self.impl.name+":";
		if ( Misc.referenceEquals(self.impl.name, ST.UNKNOWN_NAME) ) name = "";
		tr.append(String.format("%-40s",name+buf));
		tr.append("\tstack=[");
		for (int i = 0; i <= sp; i++) {
			Object o = operands[i];
			printForTrace(tr,scope,o);
		}
		tr.append(" ], calls=");
		tr.append(getEnclosingInstanceStackString(scope));
		tr.append(", sp="+sp+", nw="+ nwline);
		String s = tr.toString();
		if ( debug ) executeTrace.add(s);
		if ( trace ) System.out.println(s);
	}

	protected void printForTrace(StringBuilder tr, InstanceScope scope, Object o) {
		if ( o instanceof ST ) {
			if ( ((ST)o).impl ==null ) tr.append("bad-template()");
			else tr.append(" "+((ST)o).impl.name+"()");
			return;
		}
		o = convertAnythingIteratableToIterator(scope, o);
		if ( o instanceof Iterator ) {
			Iterator<?> it = (Iterator<?>)o;
			tr.append(" [");
			while ( it.hasNext() ) {
				Object iterValue = it.next();
				printForTrace(tr, scope, iterValue);
			}
			tr.append(" ]");
		}
		else {
			tr.append(" "+o);
		}
	}

	public List<InterpEvent> getEvents() { return events; }

	/**
	 * For every event, we track in overall {@link #events} list and in
	 * {@code self}'s {@link InstanceScope#events} list so that each template
	 * has a list of events used to create it. If {@code e} is an
	 * {@link EvalTemplateEvent}, store in parent's
	 * {@link InstanceScope#childEvalTemplateEvents} list for {@link STViz} tree
	 * view.
	 */
	protected void trackDebugEvent(InstanceScope scope, InterpEvent e) {
//		System.out.println(e);
		this.events.add(e);
		scope.events.add(e);
		if ( e instanceof EvalTemplateEvent ) {
			InstanceScope parent = scope.parent;
			if ( parent!=null ) {
				// System.out.println("add eval "+e.self.getName()+" to children of "+parent.getName());
				scope.parent.childEvalTemplateEvents.add((EvalTemplateEvent)e);
			}
		}
	}

	public List<String> getExecutionTrace() { return executeTrace; }

	public static int getShort(byte[] memory, int index) {
		int b1 = memory[index]&0xFF; // mask off sign-extended bits
		int b2 = memory[index+1]&0xFF;
		return b1<<(8*1) | b2;
	}

	protected static class ObjectList extends ArrayList<Object> {
	}

	protected static class ArgumentsMap extends HashMap<String, Object> {
	}

}

