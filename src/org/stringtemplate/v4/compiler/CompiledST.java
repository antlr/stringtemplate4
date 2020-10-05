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

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.Interval;
import org.stringtemplate.v4.misc.Misc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/** The result of compiling an {@link ST}.  Contains all the bytecode instructions,
 *  string table, bytecode address to source code map, and other bookkeeping
 *  info.  It's the implementation of an ST you might say.  All instances
 *  of the same template share a single implementation ({@link ST#impl} field).
 */
public class CompiledST implements Cloneable {
    /**
     * @deprecated since 4.3; use {@link #getName()} or {@link #setName(String)} instead
     */
    @Deprecated
    public String name;

    /**
     * @deprecated since 4.3; use {@link #getPrefix()} or {@link #setPrefix(String)} instead
     */
    @Deprecated
    public String prefix = "/";

    /**
     * @deprecated since 4.3; use {@link #getTemplate()} or {@link #setTemplate(String)} instead
     */
    @Deprecated
    public String template;

    /**
     * @deprecated since 4.3; use {@link #getTemplateDefStartToken()} or {@link #setTemplateDefStartToken(Token)} instead
     */
    @Deprecated
    public Token templateDefStartToken;

    /**
     * @deprecated since 4.3; use {@link #getTokens()} or {@link #setTokens(TokenStream)} instead
     */
    @Deprecated
    public TokenStream tokens;

    /**
     * @deprecated since 4.3; use {@link #getAST()} or {@link #setAST(CommonTree)} instead
     */
    @Deprecated
    public CommonTree ast;

    /**
     * @deprecated since 4.3; use {@link #getFormalArguments()} instead
     */
    @Deprecated
    public Map<String, FormalArgument> formalArguments;

    /**
     * @deprecated since 4.3; use {@link #hasFormalArgs()} or {@link #setHasFormalArgs(boolean)} instead
     */
    @Deprecated
    public boolean hasFormalArgs;

    /**
     * @deprecated since 4.3; use {@link #getNumberOfArgsWithDefaultValues()} instead
     */
    @Deprecated
    public int numberOfArgsWithDefaultValues;

    /**
     * A list of all regions and subtemplates.
     *
     * @deprecated since 4.3; for internal use only
     */
    public List<CompiledST> implicitlyDefinedTemplates;

    /**
     * @deprecated since 4.3; use {@link #getNativeGroup()} or {@link #setNativeGroup(STGroup)} instead
     */
    @Deprecated
    public STGroup nativeGroup = STGroup.defaultGroup;

    /**
     * @deprecated since 4.3; use {@link #isRegion()} or {@link #setRegion(boolean)} instead
     */
    @Deprecated
    public boolean isRegion;

    /**
     * @deprecated since 4.3; use {@link #getRegionDefType()} or {@link #setRegionDefType(ST.RegionType)} instead
     */
    @Deprecated
    public ST.RegionType regionDefType;

    /**
     * @deprecated since 4.3; use {@link #isAnonymousSubtemplate()} or {@link #setAnonymousSubtemplate(boolean)} instead
     */
    @Deprecated
    public boolean isAnonSubtemplate; // {...}

    /**
     * @deprecated since 4.3; use {@link #getStrings()} or {@link #setStrings(String[])} instead
     */
    @Deprecated
    public String[] strings; // string operands of instructions

    /**
     * @deprecated since 4.3; use {@link #getInstructions()} or {@link #setInstructions(byte[])} instead
     */
    @Deprecated
    public byte[] instrs;        // byte-addressable code memory.

    /**
     * @deprecated since 4.3; use {@link #getCodeSize()} or {@link #setCodeSize(int)} instead
     */
    @Deprecated
    public int codeSize;

    /**
     * @deprecated since 4.3; use {@link #getSourceMap()} or {@link #setSourceMap(Interval[])} instead
     */
    @Deprecated
    public Interval[] sourceMap; // maps IP to range in template pattern

    public CompiledST() {
        instrs = new byte[Compiler.TEMPLATE_INITIAL_CODE_SIZE];
        sourceMap = new Interval[Compiler.TEMPLATE_INITIAL_CODE_SIZE];
        template = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * The original, immutable pattern (not really used again after
     * initial "compilation"). Useful for debugging.  Even for
     * subtemplates, this is entire overall template.
     */
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * The token that begins template definition; could be {@code <@r>} of region.
     */
    public Token getTemplateDefStartToken() {
        return templateDefStartToken;
    }

    public void setTemplateDefStartToken(Token templateDefStartToken) {
        this.templateDefStartToken = templateDefStartToken;
    }

    /**
     * Overall token stream for template (debug only).
     */
    public TokenStream getTokens() {
        return tokens;
    }

    public void setTokens(TokenStream tokens) {
        this.tokens = tokens;
    }

    /**
     * How do we interpret syntax of template? (debug only)
     */
    public CommonTree getAST() {
        return ast;
    }

    public void setAST(CommonTree ast) {
        this.ast = ast;
    }

    public Map<String, FormalArgument> getFormalArguments() {
        return formalArguments;
    }

    public boolean hasFormalArgs() {
        return hasFormalArgs;
    }

    public void setHasFormalArgs(boolean hasFormalArgs) {
        this.hasFormalArgs = hasFormalArgs;
    }

    public int getNumberOfArgsWithDefaultValues() {
        return numberOfArgsWithDefaultValues;
    }

    /**
     * The group that physically defines this {@link ST} definition. We use it
     * to initiate interpretation via {@link ST#toString}. From there, it
     * becomes field {@link Interpreter#group} and is fixed until rendering
     * completes.
     */
    public STGroup getNativeGroup() {
        return nativeGroup;
    }

    public void setNativeGroup(STGroup nativeGroup) {
        this.nativeGroup = nativeGroup;
    }

    /** Does this template come from a {@code <@region>...<@end>} embedded in
     *  another template?
     */
    public boolean isRegion() {
        return isRegion;
    }

    public void setRegion(boolean region) {
        isRegion = region;
    }

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
    public ST.RegionType getRegionDefType() {
        return regionDefType;
    }

    public void setRegionDefType(ST.RegionType regionDefType) {
        this.regionDefType = regionDefType;
    }

    public boolean isAnonymousSubtemplate() {
        return isAnonSubtemplate;
    }

    public void setAnonymousSubtemplate(boolean anonSubtemplate) {
        isAnonSubtemplate = anonSubtemplate;
    }

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public byte[] getInstructions() {
        return instrs;
    }

    public void setInstructions(byte[] instrs) {
        this.instrs = instrs;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    public Interval[] getSourceMap() {
        return sourceMap;
    }

    public void setSourceMap(Interval[] sourceMap) {
        this.sourceMap = sourceMap;
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
            if (fa.getDefaultValueToken() != null ) {
                numberOfArgsWithDefaultValues++;
                switch (fa.getDefaultValueToken().getType()) {
                case GroupParser.ANONYMOUS_TEMPLATE:
                    String argSTname = fa.getName() + "_default_value";
                    Compiler c2 = new Compiler(group);
                    String defArgTemplate =
                        Misc.strip(fa.getDefaultValueToken().getText(), 1);
                    fa.setCompiledDefaultValue(c2.compile(group.getFileName(), argSTname, null,
                                                          defArgTemplate, fa.getDefaultValueToken()));
                    fa.getCompiledDefaultValue().name = argSTname;
                    fa.getCompiledDefaultValue().defineImplicitlyDefinedTemplates(group);
                    break;

                case GroupParser.STRING:
                    fa.setDefaultValue(Misc.strip(fa.getDefaultValueToken().getText(), 1));
                    break;

                case GroupParser.LBRACK:
                    fa.setDefaultValue(Collections.emptyList());
                    break;

                case GroupParser.TRUE:
                case GroupParser.FALSE:
                    fa.setDefaultValue(fa.getDefaultValueToken().getType() == GroupParser.TRUE);
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
        else if (formalArguments.containsKey(a.getName())) {
            throw new IllegalArgumentException(String.format("Formal argument %s already exists.", a.getName()));
        }

        a.setIndex(formalArguments.size());
        formalArguments.put(a.getName(), a);
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
        return template.substring(r.getStart(), r.getEnd() + 1);
    }

    public Interval getTemplateRange() {
        if ( isAnonSubtemplate ) {
            int start = Integer.MAX_VALUE;
            int stop = Integer.MIN_VALUE;
            for (Interval interval : sourceMap) {
                if (interval == null) {
                    continue;
                }

                start = Math.min(start, interval.getStart());
                stop = Math.max(stop, interval.getEnd());
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
