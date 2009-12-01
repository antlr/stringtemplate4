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

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;
import org.stringtemplate.misc.Misc;
import org.stringtemplate.debug.DebugST;
import org.stringtemplate.compiler.*;
import org.stringtemplate.compiler.Compiler;

import java.util.*;

/** A directory or directory tree of .st template files and/or group files.
 *  Individual template files contain formal template definitions. In a sense,
 *  it's like a single group file broken into multiple files, one for each template.
 *  ST v3 had just the pure template inside, not the template name and header.
 *  Name inside must match filename (minus suffix).
 */
public class STGroup {
    /** When we use key as a value in a dictionary, this is how we signify. */
    public static final String DICT_KEY = "key";
    public static final String DEFAULT_KEY = "default";

    public String fullyQualifiedRootDirName;

    /** Load files using what encoding? */
    public String encoding;

    /** Every group can import templates/dictionaries from other groups */
    protected List<STGroup> imports;

    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    /** Maps template name to StringTemplate object */
    protected LinkedHashMap<String, CompiledST> templates =
        new LinkedHashMap<String,CompiledST>();

    /** Maps dict names to HashMap objects.  This is the list of dictionaries
     *  defined by the user like typeInitMap ::= ["int":"0"]
     */
    protected Map<String, Map<String,Object>> dictionaries =
        new HashMap<String, Map<String,Object>>();

    /** A dictionary that allows people to register a renderer for
     *  a particular kind of object for any template evaluated relative to this
     *  group.  For example, a date should be formatted differently depending
     *  on the locale.  You can set Date.class to an object whose
     *  toString(Object) method properly formats a Date attribute
     *  according to locale.  Or you can have a different renderer object
     *  for each locale.
     */
    protected Map<Class,AttributeRenderer> renderers;

    protected boolean alreadyLoaded = false;
    
    /** Where to report errors.  All string templates in this group
     *  use this error handler by default.
     */
    //public STErrorListener listener = DEFAULT_ERROR_LISTENER;
	
	public static ErrorTolerance DEFAULT_ERROR_TOLERANCE = new ErrorTolerance();
	public ErrorTolerance tolerance = DEFAULT_ERROR_TOLERANCE;

	public static STGroup defaultGroup = new STGroup();

    public boolean debug = false;    

    public STGroup() { ; }

    /** The primary means of getting an instance of a template from this
     *  group. Must be absolute name like /a/b
     */
    public ST getInstanceOf(String name) {
        if ( name.charAt(0)!='/' ) name = "/"+name;
        //System.out.println("getInstanceOf("+name+")");
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
            ST instanceST = createStringTemplate();
            instanceST.groupThatCreatedThisInstance = this;
            instanceST.code = c;
            return instanceST;
        }
        return null;
    }

    public ST getEmbeddedInstanceOf(ST enclosingInstance, String name) {
        ST st = getInstanceOf(name);
        if ( st==null ) {
            ErrorManager.error("no such template: "+name);
            return ST.BLANK;
        }
        st.enclosingInstance = enclosingInstance;
        return st;
    }

    public CompiledST lookupTemplate(String name) {
        if ( !alreadyLoaded ) load();
        CompiledST code = templates.get(name);
        if ( code==null ) code = lookupImportedTemplate(name);
        return code;
    }

    protected CompiledST lookupImportedTemplate(String name) {
        //System.out.println("look for "+name+" in "+imports);
        if ( imports==null ) return null;
        for (STGroup g : imports) {
            CompiledST code = g.lookupTemplate(name);
            if ( code!=null ) return code;
        }
        return null;
    }

    public CompiledST rawGetTemplate(String name) { return templates.get(name); }
    public Map<String,Object> rawGetDictionary(String name) { return dictionaries.get(name); }

    // TODO: send in start/stop char or line/col so errors can be relative
    public CompiledST defineTemplate(String name, String template) {
        return defineTemplate("/", name, null, template);
    }

    public CompiledST defineTemplate(String name,
                                     List<String> args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate("/", name, margs, template);
    }

    public CompiledST defineTemplate(String name,
                                     String[] args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate("/", name, margs, template);
    }

	// can't trap recog errors here; don't know where in file template is defined
    public CompiledST defineTemplate(String prefix,
                                     String name,
                                     LinkedHashMap<String,FormalArgument> args,
                                     String template)
    {
        if ( name!=null && (name.length()==0 || name.indexOf('.')>=0) ) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        CompiledST code = compile(prefix, name, template);
        code.name = name;
        code.formalArguments = args;
        rawDefineTemplate(prefix+name, code);
        if ( args!=null ) { // compile any default args
            for (String a : args.keySet()) {
                FormalArgument fa = args.get(a);
                if ( fa.defaultValueToken !=null ) {
                    Compiler c2 = new Compiler(prefix, name);
                    fa.compiledDefaultValue = c2.compile(template);
                }
            }
        }
        // define any anonymous subtemplates
        defineImplicitlyDefinedTemplates(code);

        return code;
    }

    public CompiledST defineRegion(String prefix,
                                   String enclosingTemplateName,
                                   String name,
                                   String template)
    {
        CompiledST code = compile(prefix, enclosingTemplateName, template);
        code.name = prefix+getMangledRegionName(enclosingTemplateName, name);
        code.isRegion = true;
        code.regionDefType = ST.RegionType.EXPLICIT;
        rawDefineTemplate(code.name, code);
        return code;
    }

	protected void defineImplicitlyDefinedTemplates(CompiledST code) {
        if ( code.implicitlyDefinedTemplates !=null ) {
            for (CompiledST sub : code.implicitlyDefinedTemplates) {
                rawDefineTemplate(sub.name, sub);
                defineImplicitlyDefinedTemplates(sub);
            }
        }
    }

    protected void rawDefineTemplate(String name, CompiledST code) {
        CompiledST prev = templates.get(name);
        if ( prev!=null ) {
            if ( !prev.isRegion ) {
                ErrorManager.error("redefinition of "+name);
                return;
            }
            if ( prev.isRegion && prev.regionDefType==ST.RegionType.EMBEDDED ) {
                ErrorManager.error("can't redefine embedded region "+name);
                return;
            }
            else if ( prev.isRegion && prev.regionDefType==ST.RegionType.EXPLICIT ) {
                ErrorManager.error("can't redefine region in same group: "+name);
                return;
            }
        }
        templates.put(name, code);
    }

    protected CompiledST compile(String prefix, String enclosingTemplateName, String template) {
        Compiler c = new org.stringtemplate.compiler.Compiler(prefix, enclosingTemplateName);
        CompiledST code = c.compile(template);
        code.nativeGroup = this;
        code.template = template;
        return code;
    }

    /** The "foo" of t() ::= "<@foo()>" is mangled to "region#t#foo" */
    public static String getMangledRegionName(String enclosingTemplateName,
                                              String name)
    {
        return "region__"+enclosingTemplateName+"__"+name;
    }

    /** Return "t" from "region__t__foo" */
    public static String getUnMangledTemplateName(String mangledName)
    {
        return mangledName.substring("region__".length(),
            mangledName.lastIndexOf("__"));
    }

    /** Define a map for this group; not thread safe...do not keep adding
     *  these while you reference them.
     */
    public void defineDictionary(String name, Map<String,Object> mapping) {
        dictionaries.put(name, mapping);
    }

    /** Make this group import templates/dictionaries from g. */
    public void importTemplates(STGroup g) {
        if ( g==null ) return;
        if ( imports==null ) imports = Collections.synchronizedList(new ArrayList<STGroup>());
        imports.add(g);
    }

    public void load() { ; }

    // TODO: make this happen in background then flip ptr to new list of templates/dictionaries?
    public void loadGroupFile(String prefix, String fileName) {
        String absoluteFileName = fullyQualifiedRootDirName+"/"+fileName;
        //System.out.println("load group file "+absoluteFileName);
        try {
            ANTLRFileStream fs = new ANTLRFileStream(absoluteFileName, encoding);
            GroupLexer lexer = new GroupLexer(fs);
            UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group(this, prefix);
        }
        catch (Exception e) {
            ErrorManager.error("can't load group file: "+absoluteFileName, e);
        }
    }

    /** Register a renderer for all objects of a particular type for all
     *  templates evaluated relative to this group.
     */
    public void registerRenderer(Class attributeType, AttributeRenderer r) {
        if ( renderers ==null ) {
            renderers = Collections.synchronizedMap(new HashMap<Class,AttributeRenderer>());
        }
        renderers.put(attributeType, r);
    }

    public AttributeRenderer getAttributeRenderer(Class attributeType) {
        if ( renderers==null ) return null;
        return renderers.get(attributeType);
    }

    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        // TODO: try making a mem pool
        if ( debug ) {
            return new DebugST();
        }
        return new ST();
    }

    public String getName() { return "<no name>;"; }

    public LinkedHashMap<String, CompiledST> getTemplates() {
        return templates;
    }

    public String toString() { return getName(); }

    public String show() {
        if ( !alreadyLoaded ) load();
        StringBuilder buf = new StringBuilder();
        if ( imports!=null ) buf.append(" : "+imports);
        for (String name : templates.keySet()) {
			CompiledST c = templates.get(name);
			if ( c.isSubtemplate() ) continue;
            int slash = name.lastIndexOf('/');
            name = name.substring(slash+1, name.length());
            buf.append(name);
            buf.append('(');
            if ( c.formalArguments!=null ) {
                buf.append( Misc.join(c.formalArguments.values().iterator(), ",") );
            }
            buf.append(')');
            buf.append(" ::= <<"+Misc.newline);
            buf.append(c.template+ Misc.newline);
            buf.append(">>"+Misc.newline);
        }
        return buf.toString();
    }

    public void setDebug(boolean b) { debug = b; }

	public void setErrorTolerance(ErrorTolerance errors) { this.tolerance = errors; }
	public boolean detects(int x) { return tolerance.detects(x); }
	public void detect(int x) { tolerance.detect(x); }
	public void ignore(int x) { tolerance.ignore(x); }
}
