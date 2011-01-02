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

import org.antlr.runtime.*;
import org.stringtemplate.v4.compiler.*;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.debug.DebugST;
import org.stringtemplate.v4.misc.*;

import java.net.URL;
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

    /** Load files using what encoding? */
    public String encoding;

    /** Every group can import templates/dictionaries from other groups.
     *  The list must be synchronized (see importTemplates).
     */
    protected List<STGroup> imports;

    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    /** Maps template name to StringTemplate object. synchronized. */
    protected Map<String, CompiledST> templates =
		Collections.synchronizedMap(new LinkedHashMap<String, CompiledST>());

    /** Maps dict names to HashMap objects.  This is the list of dictionaries
     *  defined by the user like typeInitMap ::= ["int":"0"]
     */
    protected Map<String, Map<String,Object>> dictionaries =
        Collections.synchronizedMap(new HashMap<String, Map<String,Object>>());

    /** A dictionary that allows people to register a renderer for
     *  a particular kind of object for any template evaluated relative to this
     *  group.  For example, a date should be formatted differently depending
     *  on the locale.  You can set Date.class to an object whose
     *  toString(Object) method properly formats a Date attribute
     *  according to locale.  Or you can have a different renderer object
     *  for each locale.
     *
	 *  Order of addition is recorded and matters.  If more than one
	 *  renderer works for an object, the first registered has priority.
	 *
	 *  Renderer associated with type t works for object o if
	 *
	 * 		t.isAssignableFrom(o.getClass()) // would assignment t = o work?
	 *
	 *  So it works if o is subclass or implements t.
	 *
     *  This structure is synchronized.
     */
    protected Map<Class, AttributeRenderer> renderers;

    /** A dictionary that allows people to register a model adaptor for
     *  a particular kind of object (subclass or implementation). Applies
	 *  for any template evaluated relative to this group.
	 *
	 *  ST initializes with model adaptors that know how to pull
	 *  properties out of Objects, Maps, and STs.
	 */
	protected Map<Class, ModelAdaptor> adaptors =
		Collections.synchronizedMap(
			new LinkedHashMap<Class, ModelAdaptor>() {{
				put(Object.class, new ObjectModelAdaptor());
				put(ST.class, new STModelAdaptor());
				put(Map.class, new MapModelAdaptor());
			}}
		);

	/** Cache exact attribute type to adaptor object */
	protected Map<Class, ModelAdaptor> typeToAdaptorCache =
		Collections.synchronizedMap(new LinkedHashMap<Class, ModelAdaptor>());

	public static STGroup defaultGroup = new STGroup();

    /** Used to indicate that the template doesn't exist.
     *  Prevents duplicate group file loads and unnecessary file checks.
     */
    protected static final CompiledST NOT_FOUND_ST = new CompiledST();

    public boolean debug = false;

    public STGroup() { ; }

    public STGroup(char delimiterStartChar, char delimiterStopChar) {
        this.delimiterStartChar = delimiterStartChar;
        this.delimiterStopChar = delimiterStopChar;
    }

    /** The primary means of getting an instance of a template from this
     *  group. Names must be absolute, fully-qualified names like a/b
     */
    public ST getInstanceOf(String name) {
		if ( name==null ) return null;
        //System.out.println("getInstanceOf("+name+")");
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
            ST instanceST = createStringTemplate();
            instanceST.groupThatCreatedThisInstance = this;
            instanceST.impl = c;
			if ( instanceST.impl.formalArguments!=null ) {
				instanceST.locals = new Object[instanceST.impl.formalArguments.size()];
				Arrays.fill(instanceST.locals, ST.EMPTY_ATTR);
			}
            return instanceST;
        }
        return null;
    }

    protected ST getEmbeddedInstanceOf(ST enclosingInstance, int ip, String name) {
        ST st = getInstanceOf(name);
        if ( st==null ) {
            ErrorManager.runTimeError(enclosingInstance, ip, ErrorType.NO_SUCH_TEMPLATE,
                                      name);
			st = createStringTemplate();
			st.impl = new CompiledST();
			return st;
        }
        st.enclosingInstance = enclosingInstance;
        return st;
    }

	/** Create singleton template for use with dictionary values */
	public ST createSingleton(Token templateToken) {
		String template;
		if ( templateToken.getType()==GroupParser.BIGSTRING ) {
			template = Misc.strip(templateToken.getText(),2);
		}
		else {
			template = Misc.strip(templateToken.getText(),1);
		}
		ST st = createStringTemplate();
		st.groupThatCreatedThisInstance = this;
		st.impl = compile(null, null, template);
		st.impl.hasFormalArgs = false;
		st.impl.name = ST.UNKNOWN_NAME;
		st.impl.defineImplicitlyDefinedTemplates(this);
		return st;
	}

    /** Is this template defined in this group or from this group below?
     *  Names must be absolute, fully-qualified names like /a/b
     */
    public boolean isDefined(String name) {
        return lookupTemplate(name)!=null;
    }

	/** Look up a fully-qualified name */
    protected CompiledST lookupTemplate(String name) {
        CompiledST code = templates.get(name);
        if ( code==NOT_FOUND_ST ) return null;
        // try to load from disk and look up again
        if ( code==null ) code = load(name);
        if ( code==null ) code = lookupImportedTemplate(name);
        if ( code==null ) {
            templates.put(name, NOT_FOUND_ST);
        }
        return code;
    }

	/** "unload" all templates and dictionaries but leave renderers, adaptors,
	 *  and import relationships.  This essentially forces next getInstanceOf
	 *  to reload templates.
	 */
	public synchronized void unload() {
		templates.clear();
		dictionaries.clear();
	}

    /** Load st from disk if dir or load whole group file if .stg file (then
     *  return just one template). name is fully-qualified.
     */
    protected CompiledST load(String name) { return null; }

    /** Force a load if it makes sense for the group */
    public void load() { ; }

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
	public boolean isDictionary(String name) { return dictionaries.get(name)!=null; }

    // for testing
    public CompiledST defineTemplate(String templateName, String template) {
		return defineTemplate(templateName, new CommonToken(GroupParser.ID,templateName),
			null,
			template);
	}

    // for testing
	public CompiledST defineTemplate(String name, String argsS, String template) {
		String[] args = argsS.split(",");
		List<FormalArgument> a = new ArrayList<FormalArgument>();
		for (int i = 0; i  < args.length; i ++) {
			a.add(new FormalArgument(args[i]));
		}
		return defineTemplate(name, new CommonToken(GroupParser.ID, name),
			a, template);
	}

	public CompiledST defineTemplate(String templateName, Token nameT,
                                     List<FormalArgument> args,
									 String template)
    {
		if ( templateName==null || templateName.length()==0 ) {
			throw new IllegalArgumentException("empty template name");
		}
		if ( templateName.indexOf('.')>=0 ) {
			throw new IllegalArgumentException("cannot have '.' in template names");
		}
        template = Misc.trimOneStartingNewline(template);
        template = Misc.trimOneTrailingNewline(template);
		// compile, passing in templateName as enclosing name for any embedded regions
        CompiledST code = compile(templateName, args, template);
        code.name = templateName;
        rawDefineTemplate(templateName, code, nameT);
		code.defineArgDefaultValueTemplates(this);
        code.defineImplicitlyDefinedTemplates(this); // define any anonymous subtemplates

        return code;
    }

    /** Make name and alias for target.  Replace any previous def of name */
    public CompiledST defineTemplateAlias(Token aliasT, Token targetT) {
        String alias = aliasT.getText();
        String target = targetT.getText();
        CompiledST targetCode = templates.get(target);
        if ( targetCode==null ){
            ErrorManager.compileTimeError(ErrorType.ALIAS_TARGET_UNDEFINED, aliasT, alias, target);
            return null;
        }
        templates.put(alias, targetCode);
        return targetCode;
    }

    public CompiledST defineRegion(String enclosingTemplateName,
                                   Token regionT,
								   String template)
    {
        String name = regionT.getText();
        CompiledST code = compile(enclosingTemplateName, null, template);
        String mangled = getMangledRegionName(enclosingTemplateName, name);

        if ( lookupTemplate(mangled)==null ) {
            ErrorManager.compileTimeError(ErrorType.NO_SUCH_REGION, regionT,
                                          enclosingTemplateName, name);
            return new CompiledST();
        }
        code.name = mangled;
        code.isRegion = true;
        code.regionDefType = ST.RegionType.EXPLICIT;

        rawDefineTemplate(mangled, code, regionT);
        return code;
    }

    public void defineTemplateOrRegion(
		String templateName,
		String regionSurroundingTemplateName,
        Token templateToken, String template,
        Token nameToken,
        List<FormalArgument> args)
    {
        int n = 1; // num char to strip from left, right of template def token text "" <<>>
        if ( templateToken.getType()==GroupLexer.BIGSTRING ) n=2;
        try {
            if ( regionSurroundingTemplateName!=null ) {
                defineRegion(regionSurroundingTemplateName, nameToken, template);
            }
            else {
                defineTemplate(templateName, nameToken, args, template);
            }
		}
		catch (STException e) {
			RecognitionException re = (RecognitionException)e.getCause();
			re.charPositionInLine =
				re.charPositionInLine+templateToken.getCharPositionInLine()+n;
			re.line = re.line + templateToken.getLine() - 1;
			ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR,
				Misc.getFileName(templateToken.getInputStream().getSourceName()),
				re, e.getMessage());
		}
	}

    public void rawDefineTemplate(String name, CompiledST code, Token defT) {
        CompiledST prev = templates.get(name);
        if ( prev!=null ) {
            if ( !prev.isRegion ) {
                ErrorManager.compileTimeError(ErrorType.TEMPLATE_REDEFINITION, defT);
                return;
            }
            if ( prev.isRegion && prev.regionDefType== ST.RegionType.EMBEDDED ) {
                ErrorManager.compileTimeError(ErrorType.EMBEDDED_REGION_REDEFINITION,
                                              defT,
                                              getUnMangledTemplateName(name));
                return;
            }
            else if ( prev.isRegion && prev.regionDefType== ST.RegionType.EXPLICIT ) {
                ErrorManager.compileTimeError(ErrorType.REGION_REDEFINITION,
                                              defT,
                                              getUnMangledTemplateName(name));
                return;
            }
        }
        templates.put(name, code);
    }

    public void undefineTemplate(String name) {
        templates.remove(name);
    }

	/** Compile a template */
    public CompiledST compile(String enclosingTemplateName,
							  List<FormalArgument> args,
                              String template)
    {
		//System.out.println("STGroup.compile: "+enclosingTemplateName);
        Compiler c = new Compiler(enclosingTemplateName,
                                  delimiterStartChar, delimiterStopChar);
        CompiledST code = c.compile(args, template);
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

    /** Return "t.foo" from "region__t__foo" */
    public static String getUnMangledTemplateName(String mangledName) {
        String t = mangledName.substring("region__".length(),
                                         mangledName.lastIndexOf("__"));
        String r = mangledName.substring(mangledName.lastIndexOf("__")+2,
                                         mangledName.length());
        return t+'.'+r;
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

	/** Load group dir or file (if .stg suffix) and then import templates. Don't hold
	 *  an independent ref to the "supergroup".
	 *
	 *  Override this if you want to look for groups elsewhere (database maybe?)
	 *
	 *  importTemplates("org.foo.proj.G.stg") will try to find file org/foo/proj/G.stg
	 *  relative to current dir or in CLASSPATH. The name is not relative to this group.
	 *  Can use "/a/b/c/myfile.stg" also or "/a/b/c/mydir".
	 *
	 *  Pass token so you can give good error if you want.
	 */
	public void importTemplates(Token fileNameToken) {
		String fileName = fileNameToken.getText();
		// do nothing upon syntax error
		if ( fileName==null || fileName.equals("<missing STRING>") ) return;
		fileName = Misc.strip(fileName, 1);
		STGroup g = null;
		if ( fileName.endsWith(".stg") ) {
			g = new STGroupFile(fileName, delimiterStartChar, delimiterStopChar);
		}
		else {
			g = new STGroupDir(fileName, delimiterStartChar, delimiterStopChar);
		}
		importTemplates(g);
	}

	/** Load a group file with full path fileName; it's relative to root by prefix. */
    public void loadGroupFile(String prefix, String fileName) {
        //System.out.println("load group file prefix="+prefix+", fileName="+fileName);
        GroupParser parser = null;
        try {
            URL f = new URL(fileName);
            ANTLRInputStream fs = new ANTLRInputStream(f.openStream(), encoding);
            GroupLexer lexer = new GroupLexer(fs);
            fs.name = fileName;
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new GroupParser(tokens);
            parser.group(this, prefix);
        }
        catch (Exception e) {
            ErrorManager.IOError(null, ErrorType.CANT_LOAD_GROUP_FILE, e, fileName);
        }
    }

	/** Add an adaptor for a kind of object so ST knows how to pull properties
	 *  from them. Add adaptors in increasing order of specificity.  ST adds Object,
	 *  Map, and ST model adaptors for you first. Adaptors you add have
	 *  priority over default adaptors.
	 *
	 *  If an adaptor for type T already exists, it is replaced by the adaptor arg.
	 *
	 *  This must invalidate cache entries, so set your adaptors up before
	 *  render()ing your templates for efficiency.
	 */
	public void registerModelAdaptor(Class attributeType, ModelAdaptor adaptor) {
		adaptors.put(attributeType, adaptor);
		invalidateModelAdaptorCache(attributeType);
	}

	/** remove at least all types in cache that are subclasses or implement attributeType */
	public void invalidateModelAdaptorCache(Class attributeType) {
		typeToAdaptorCache.clear(); // be safe, not clever; wack all values
		/*
		List<Class> kill = new ArrayList<Class>();
		for (Class t : typeToAdaptorCache.keySet()) {
			if ( attributeType.isAssignableFrom(t) ) {
				System.out.println(attributeType.getName() + " = " + t.getName());
				kill.add(t);
			}
		}
		for (Class t : kill) typeToAdaptorCache.remove(t);
		*/
	}

	public ModelAdaptor getModelAdaptor(Class attributeType) {
		ModelAdaptor a = typeToAdaptorCache.get(attributeType);
		if ( a!=null ) return a;

		// Else, we must find adaptor that fits;
		// find last fit (most specific)
		for (Class t : adaptors.keySet()) {
			// t works for attributeType if attributeType subclasses t or implements
			if ( t.isAssignableFrom(attributeType) ) {
				//System.out.println(t.getName()+" = "+attributeType.getName());
				a = adaptors.get(t);
			}
		}
		typeToAdaptorCache.put(attributeType, a); // cache it for next time
		return a;
	}

    /** Register a renderer for all objects of a particular "kind" for all
     *  templates evaluated relative to this group.  Use r to render if
	 *  object in question is instanceof(attributeType).
     */
    public void registerRenderer(Class attributeType, AttributeRenderer r) {
		// TODO: invalidate cache
        if ( renderers ==null ) {
            renderers =
				Collections.synchronizedMap(new LinkedHashMap<Class, AttributeRenderer>());
        }
        renderers.put(attributeType, r);
	}

	public AttributeRenderer getAttributeRenderer(Class attributeType) {
		if ( renderers==null ) return null;
		// TODO: cache this lookup
		for (Class t : renderers.keySet()) {
			// t works for attributeType if attributeType subclasses t or implements
			if ( t.isAssignableFrom(attributeType) ) return renderers.get(t);
		}
		return null;
	}

    /** StringTemplate object factory; each group can have its own. */
	public ST createStringTemplate() {
		// TODO: try making a mem pool?
		if ( debug ) {
			return new DebugST();
		}
		return new ST();
	}

	public ST createStringTemplate(ST proto) {
		if ( debug ) {
			return new DebugST(proto);
		}
		return new ST(proto);
	}

    public String getName() { return "<no name>;"; }

    public String toString() { return getName(); }

    public String show() {
        StringBuilder buf = new StringBuilder();
        if ( imports!=null ) buf.append(" : "+imports);
        for (String name : templates.keySet()) {
			CompiledST c = templates.get(name);
			if ( c.isAnonSubtemplate || c==NOT_FOUND_ST ) continue;
            int slash = name.lastIndexOf('/');
            name = name.substring(slash+1, name.length());
            buf.append(name);
            buf.append('(');
            if ( c.formalArguments!=null ) buf.append( Misc.join(c.formalArguments.values().iterator(), ",") );
            buf.append(')');
            buf.append(" ::= <<"+Misc.newline);
            buf.append(c.template+ Misc.newline);
            buf.append(">>"+Misc.newline);
        }
        return buf.toString();
    }
}
