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

import java.util.*;

/** A directory of .st template files and/or group files.  I think of a
 *  group of templates as a node in the ST tree.  Individual template files
 *  contain formal template definitions. In a sense, it's like a single group
 *  file broken into multiple files, one for each template.
 *  ST v3 had just the pure template inside, not the
 *  template name and header.  Name inside must match filename (minus suffix).
 *
 *  Most people will use STTree. STGroup is just a node in a tree.  A node
 *  is either a directory of .st files or a group file.
 */
public class STGroup {
    /** When we use key as a value in a dictionary, this is how we signify. */
    public static final String DICT_KEY = "key";
    public static final String DEFAULT_KEY = "default";

    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
            public void error(String s) { error(s, null); }
            public void error(String s, Throwable e) {
                System.err.println(s);
                if ( e!=null ) {
                    e.printStackTrace(System.err);
                }
            }
            public void warning(String s) {
                System.out.println(s);
            }
        };

    /** The topmost group of templates in the template tree.
     *  Point to yourself if group is root; but parent will be null.
     */
    public STGroup root;

    public STGroupDir parent; // Are we a subdir or group file in dir?

    public String fullyQualifiedRootDirName; // if we're root

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

    protected boolean alreadyLoaded = false;
    
    /** Where to report errors.  All string templates in this group
     *  use this error handler by default.
     */
    public STErrorListener listener = DEFAULT_ERROR_LISTENER;
	
	public static ErrorTolerance DEFAULT_ERROR_TOLERANCE = new ErrorTolerance();
	public ErrorTolerance tolerance = DEFAULT_ERROR_TOLERANCE;

	public static STGroup defaultGroup = new STGroup();

    public STGroup() { ; }

    // TODO: for dirs, should this load everything in dir and below?
    public void load() { } // nothing to do unless it's a group file
    
    /** The primary means of getting an instance of a template from this
     *  group.
     */
    public ST getInstanceOf(String name) {
        //System.out.println("getInstanceOf("+name+") resolves to "+ getAbsoluteTemplatePath()+"/"+name);
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
            ST instanceST = createStringTemplate();
            //instanceST.group = this;  leave it as nativeGroup
            instanceST.name = name;
            instanceST.code = c;
            return instanceST;
        }
        return null;
    }

    public ST getEmbeddedInstanceOf(ST enclosingInstance, String name) {
        ST st = getInstanceOf(name);
        if ( st==null ) {
            System.err.println("no such template: "+name);
            return ST.BLANK;
        }
        st.enclosingInstance = enclosingInstance;
        return st;
    }

    public CompiledST lookupTemplate(String name) { return templates.get(name); }

    protected CompiledST lookupImportedTemplate(String name) {
        System.out.println("look for "+name+" in "+imports);
        if ( this!=root ) { // look for absolute template name from root
            return root.lookupImportedTemplate(getAbsoluteTemplateName(name));
        }
        // if we're at the root, look for name in imports
        if ( imports==null ) return null;
        for (STGroup g : imports) {
            CompiledST code = g.lookupTemplate(name);
            if ( code!=null ) return code;
        }
        return null;
    }

    // TODO: send in start/stop char or line/col so errors can be relative
    public CompiledST defineTemplate(String name, String template) {
        return defineTemplate(name, (LinkedHashMap<String,FormalArgument>)null, template);
    }

    public CompiledST defineTemplate(String name,
                                     List<String> args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate(name, margs, template);
    }

    public CompiledST defineTemplate(String name,
                                     String[] args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate(name, margs, template);
    }

	// can't trap recog errors here; don't know where in file template is defined
    public CompiledST defineTemplate(String name,
                                     LinkedHashMap<String,FormalArgument> args,
                                     String template)
    {
        if ( name!=null && (name.length()==0 || name.indexOf('.')>=0) ) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        Compiler c = new Compiler();
		CompiledST code = c.compile(template);
        code.name = name;
        code.formalArguments = args;
        code.nativeGroup = this;
        templates.put(name, code);
        if ( args!=null ) { // compile any default args
            for (String a : args.keySet()) {
                FormalArgument fa = args.get(a);
                if ( fa.defaultValue!=null ) {
                    Compiler c2 = new Compiler();
                    fa.compiledDefaultValue = c2.compile(template);
                }
            }
        }
        // define any anonymous subtemplates
        defineAnonSubtemplates(code);

        return code;
    }

	public void defineAnonSubtemplates(CompiledST code) {
        if ( code.compiledSubtemplates!=null ) {
            for (CompiledST sub : code.compiledSubtemplates) {
                templates.put(sub.name, sub);
                defineAnonSubtemplates(sub);
            }
        }
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
        if ( imports==null ) imports = new ArrayList<STGroup>();
        imports.add(g);
    }

    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        ST st = new ST();
        return st;
    }

    public String getName() { return "<no name>;"; }

    /** Get string that would navigate from root group down to this group.
     *  If we're root, return "/"
     *  If we're one level down, return "/subdir"
     *  If we're two levels down, return "/subdir/subsubdir"
     */
    public String getAbsoluteTemplatePath() {
        /*
        System.out.print("getTemplatePathFromRoot root="+
                         (root!=null?root.getName():null)+" this="+this.getName());
         */
        List<String> elems = new LinkedList<String>();
        STGroup p = this;
        while ( p!=root ) {
            elems.add(0, p.getName());
            p = p.parent;
        }
        String s = "/" + Misc.join(elems.iterator(), "/");
        //System.out.println("; template path="+s);
        return s;
    }

    public String getAbsoluteTemplateName(String name) {
        String p = getAbsoluteTemplatePath();
        if ( p.equals("/") ) return "/"+name;
        return p+"/"+name;
    }

    public String toString() {
       // return show();
        return getName();
    }

    public String show() {
        StringBuilder buf = new StringBuilder();
        //if ( supergroup!=null ) buf.append(" : "+supergroup);
        for (String name : templates.keySet()) {
			if ( name.startsWith("_") ) continue;
            CompiledST c = templates.get(name);
            buf.append(name);
            buf.append('(');
            if ( c.formalArguments!=null ) {
                buf.append( Misc.join(c.formalArguments.values().iterator(), ",") );
            }
            buf.append(')');
            buf.append(" ::= <<"+Misc.newline);
            buf.append(c.template+Misc.newline);
            buf.append(">>"+Misc.newline);
        }
        return buf.toString();
    }

    @Override
    public int hashCode() { return getName().hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof STGroup ) return getName().equals(obj);
        return false;
    }

    public void setErrorListener(STErrorListener listener) {
        this.listener = listener;
    }

	public void setErrorTolerance(ErrorTolerance errors) { this.tolerance = errors; }
	public boolean detects(int x) { return tolerance.detects(x); }
	public void detect(int x) { tolerance.detect(x); }
	public void ignore(int x) { tolerance.ignore(x); }
}
