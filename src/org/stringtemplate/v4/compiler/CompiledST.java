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
package org.stringtemplate.v4.compiler;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import java.io.*;
import java.util.*;

/** The result of compiling an {@link ST}.  Contains all the bytecode instructions,
 *  string table, bytecode address to source code map, and other bookkeeping
 *  info.  It's the implementation of an ST you might say.  All instances
 *  of the same template share a single implementation ({@link ST#impl} field).
 */
public class CompiledST implements Cloneable {
    public String name;

	/**
	 * Every template knows where it is relative to the group that loaded it.
	 * The prefix is the relative path from the root. {@code "/prefix/name"} is
	 * the fully qualified name of this template. All calls to
	 * {@link STGroup#getInstanceOf} calls must use fully qualified names. A
	 * {@code "/"} is added to the front if you don't specify one. Template
	 * references within template code, however, uses relative names, unless of
	 * course the name starts with {@code "/"}.
	 * <p>
	 * This has nothing to do with the outer filesystem path to the group dir or
	 * group file.</p>
	 * <p>
	 * We set this as we load/compile the template.</p>
	 * <p>
	 * Always ends with {@code "/"}.</p>
	 */
	public String prefix = "/";

    /** The original, immutable pattern (not really used again after
     *  initial "compilation"). Useful for debugging.  Even for
	 *  subtemplates, this is entire overall template.
     */
    public String template;

	/** The token that begins template definition; could be {@code <@r>} of region. */
	public Token templateDefStartToken;

	/** Overall token stream for template (debug only). */
	public TokenStream tokens;

	/** How do we interpret syntax of template? (debug only) */
	public CommonTree ast;

    public Map<String, FormalArgument> formalArguments;

	public boolean hasFormalArgs;

	public int numberOfArgsWithDefaultValues;

    /** A list of all regions and subtemplates. */
    public List<CompiledST> implicitlyDefinedTemplates;

	/**
	 * The group that physically defines this {@link ST} definition. We use it
	 * to initiate interpretation via {@link ST#toString}. From there, it
	 * becomes field {@link Interpreter#group} and is fixed until rendering
	 * completes.
	 */
    public STGroup nativeGroup = STGroup.defaultGroup;

    /** Does this template come from a {@code <@region>...<@end>} embedded in
     *  another template?
     */
    public boolean isRegion;

	/**
	 * If someone refs {@code <@r()>} in template t, an implicit
	 *
	 * <p>
	 * {@code @t.r() ::= ""}</p>
	 * <p>
	 * is defined, but you can overwrite this def by defining your own. We need
	 * to prevent more than one manual def though. Between this var and
	 * {@link #isRegion} we can determine these cases.</p>
	 */
    public ST.RegionType regionDefType;

    public boolean isAnonSubtemplate; // {...}

    public String[] strings;     // string operands of instructions
    public byte[] instrs;        // byte-addressable code memory.
    public int codeSize;
    public Interval[] sourceMap; // maps IP to range in template pattern

	public CompiledST() {
        instrs = new byte[Compiler.TEMPLATE_INITIAL_CODE_SIZE];
        sourceMap = new Interval[Compiler.TEMPLATE_INITIAL_CODE_SIZE];
		template = "";
	}

	/**
	 * Cloning the {@link CompiledST} for an {@link ST} instance allows
	 * {@link ST#add} to be called safely during interpretation for templates
	 * that do not contain formal arguments.
	 *
	 * @return A copy of the current {@link CompiledST} instance. The copy is a
	 * shallow copy, with the exception of the {@link #formalArguments} field
	 * which is also cloned.
	 *
	 * @exception CloneNotSupportedException If the current instance cannot be
	 * cloned.
	 */
	@Override
	public CompiledST clone() throws CloneNotSupportedException {
		CompiledST clone = (CompiledST)super.clone();
		if (formalArguments != null) {
			formalArguments = Collections.synchronizedMap(new LinkedHashMap<String,FormalArgument>(formalArguments));
		}

		return clone;
	}

    public void addImplicitlyDefinedTemplate(CompiledST sub) {
		sub.prefix = this.prefix;
		if ( sub.name.charAt(0)!='/' ) sub.name = sub.prefix+sub.name;
        if ( implicitlyDefinedTemplates == null ) {
            implicitlyDefinedTemplates = new ArrayList<CompiledST>();
        }
        implicitlyDefinedTemplates.add(sub);
    }

	public void defineArgDefaultValueTemplates(STGroup group) {
		if ( formalArguments==null ) return;
		for (String a : formalArguments.keySet()) {
			FormalArgument fa = formalArguments.get(a);
			if ( fa.defaultValueToken!=null ) {
				numberOfArgsWithDefaultValues++;
				switch (fa.defaultValueToken.getType()) {
				case GroupParser.ANONYMOUS_TEMPLATE:
					String argSTname = fa.name + "_default_value";
					Compiler c2 = new Compiler(group);
					String defArgTemplate =
						Misc.strip(fa.defaultValueToken.getText(), 1);
					fa.compiledDefaultValue =
						c2.compile(group.getFileName(), argSTname, null,
								   defArgTemplate, fa.defaultValueToken);
					fa.compiledDefaultValue.name = argSTname;
					fa.compiledDefaultValue.defineImplicitlyDefinedTemplates(group);
					break;

				case GroupParser.STRING:
					fa.defaultValue = Misc.strip(fa.defaultValueToken.getText(), 1);
					break;

				case GroupParser.LBRACK:
					fa.defaultValue = Collections.emptyList();
					break;

				case GroupParser.TRUE:
				case GroupParser.FALSE:
					fa.defaultValue = fa.defaultValueToken.getType()==GroupParser.TRUE;
					break;

				default:
					throw new UnsupportedOperationException("Unexpected default value token type.");
				}
			}
		}
	}

	public void defineFormalArgs(List<FormalArgument> args) {
		hasFormalArgs = true; // even if no args; it's formally defined
		if ( args == null ) formalArguments = null;
		else for (FormalArgument a : args) addArg(a);
	}

	/** Used by {@link ST#add} to add args one by one without turning on full formal args definition signal. */
	public void addArg(FormalArgument a) {
		if ( formalArguments==null ) {
			formalArguments = Collections.synchronizedMap(new LinkedHashMap<String,FormalArgument>());
		}
		a.index = formalArguments.size();
		formalArguments.put(a.name, a);
	}

	public void defineImplicitlyDefinedTemplates(STGroup group) {
		if ( implicitlyDefinedTemplates !=null ) {
            for (CompiledST sub : implicitlyDefinedTemplates) {
                group.rawDefineTemplate(sub.name, sub, sub.templateDefStartToken);
                sub.defineImplicitlyDefinedTemplates(group);
            }
        }
    }

	public String getTemplateSource() {
		Interval r = getTemplateRange();
		return template.substring(r.a, r.b+1);
	}

	public Interval getTemplateRange() {
		if ( isAnonSubtemplate ) {
			int start = Integer.MAX_VALUE;
			int stop = Integer.MIN_VALUE;
			for (Interval interval : sourceMap) {
				if (interval == null) {
					continue;
				}

				start = Math.min(start, interval.a);
				stop = Math.max(stop, interval.b);
			}

			if (start <= stop + 1) {
				return new Interval(start, stop);
			}
		}
		return new Interval(0, template.length()-1);
	}

    public String instrs() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
        return dis.instrs();
    }

    public void dump() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
		System.out.println(name+":");
        System.out.println(dis.disassemble());
        System.out.println("Strings:");
        System.out.println(dis.strings());
        System.out.println("Bytecode to template map:");
        System.out.println(dis.sourceMap());
    }

    public String disasm() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(dis.disassemble());
        pw.println("Strings:");
        pw.println(dis.strings());
        pw.println("Bytecode to template map:");
        pw.println(dis.sourceMap());
        pw.close();
        return sw.toString();
    }
}
