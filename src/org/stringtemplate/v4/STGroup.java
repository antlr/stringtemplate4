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

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.compiler.GroupLexer;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.compiler.STException;
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.Aggregate;
import org.stringtemplate.v4.misc.AggregateModelAdaptor;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.MapModelAdaptor;
import org.stringtemplate.v4.misc.Misc;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STModelAdaptor;
import org.stringtemplate.v4.misc.TypeRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A directory or directory tree of {@code .st} template files and/or group files.
 *  Individual template files contain formal template definitions. In a sense,
 *  it's like a single group file broken into multiple files, one for each template.
 *  ST v3 had just the pure template inside, not the template name and header.
 *  Name inside must match filename (minus suffix).
 */
public class STGroup {
	public static final String GROUP_FILE_EXTENSION;
	public static final String TEMPLATE_FILE_EXTENSION;
	static {
		GROUP_FILE_EXTENSION = ".stg";
		TEMPLATE_FILE_EXTENSION = ".st";
	}

    /** When we use key as a value in a dictionary, this is how we signify. */
    public static final String DICT_KEY = "key";
    public static final String DEFAULT_KEY = "default";

    /** The encoding to use for loading files. Defaults to UTF-8. */
    public String encoding = "UTF-8";

    /** Every group can import templates/dictionaries from other groups.
     *  The list must be synchronized (see {@link STGroup#importTemplates}).
     */
    protected final List<STGroup> imports = Collections.synchronizedList(new ArrayList<STGroup>());

    protected final List<STGroup> importsToClearOnUnload = Collections.synchronizedList(new ArrayList<STGroup>());

    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    /** Maps template name to {@link CompiledST} object. This map is synchronized. */
    protected Map<String, CompiledST> templates =
		Collections.synchronizedMap(new LinkedHashMap<String, CompiledST>());

    /** Maps dictionary names to {@link Map} objects representing the dictionaries
     *  defined by the user like {@code typeInitMap ::= ["int":"0"]}.
     */
    protected Map<String, Map<String,Object>> dictionaries =
        Collections.synchronizedMap(new HashMap<String, Map<String,Object>>());

    /** A dictionary that allows people to register a renderer for
     *  a particular kind of object for any template evaluated relative to this
     *  group.  For example, a date should be formatted differently depending
     *  on the locale.  You can set {@code Date.class} to an object whose
     *  {@code toString(Object)} method properly formats a {@link Date} attribute
     *  according to locale.  Or you can have a different renderer object
     *  for each locale.
     *  <p>
	 *  Order of addition is recorded and matters.  If more than one
	 *  renderer works for an object, the first registered has priority.</p>
	 *  <p>
	 *  Renderer associated with type {@code t} works for object {@code o} if</p>
	 *  <pre>
	 *  t.isAssignableFrom(o.getClass()) // would assignment t = o work?
	 *  </pre>
	 *  So it works if {@code o} is subclass or implements {@code t}.
	 *  <p>
     *  This structure is synchronized.</p>
     */
    protected Map<Class<?>, AttributeRenderer> renderers;

    /** A dictionary that allows people to register a model adaptor for
     *  a particular kind of object (subclass or implementation). Applies
	 *  for any template evaluated relative to this group.
	 * <p>
	 *  ST initializes with model adaptors that know how to pull
	 *  properties out of {@link Object}s, {@link Map}s, and {@link ST}s.</p>
	 * <p>
	 *  The last one you register gets priority; do least to most specific.</p>
	 */
	protected final Map<Class<?>, ModelAdaptor> adaptors;
	{
		TypeRegistry<ModelAdaptor> registry = new TypeRegistry<ModelAdaptor>();
		registry.put(Object.class, new ObjectModelAdaptor());
		registry.put(ST.class, new STModelAdaptor());
		registry.put(Map.class, new MapModelAdaptor());
		registry.put(Aggregate.class, new AggregateModelAdaptor());
		adaptors = Collections.synchronizedMap(registry);
	}

    /** Used to indicate that the template doesn't exist.
     *  Prevents duplicate group file loads and unnecessary file checks.
     */
    protected static final CompiledST NOT_FOUND_ST = new CompiledST();

	public static final ErrorManager DEFAULT_ERR_MGR = new ErrorManager();

	/** Watch loading of groups and templates. */
	public static boolean verbose = false;

	/**
	 * For debugging with {@link STViz}. Records where in code an {@link ST} was
	 * created and where code added attributes.
	 */
	public static boolean trackCreationEvents = false;

	/** v3 compatibility; used to iterate across {@link Map#values()} instead of
	 *  v4's default {@link Map#keySet()}.
	 *  But to convert ANTLR templates, it's too hard to find without
	 *  static typing in templates.
	 */
	public boolean iterateAcrossValues = false;

	public static STGroup defaultGroup = new STGroup();

	/** The {@link ErrorManager} for entire group; all compilations and executions.
	 *  This gets copied to parsers, walkers, and interpreters.
	 */
	public ErrorManager errMgr = STGroup.DEFAULT_ERR_MGR;

    public STGroup() { }

    public STGroup(char delimiterStartChar, char delimiterStopChar) {
        this.delimiterStartChar = delimiterStartChar;
        this.delimiterStopChar = delimiterStopChar;
    }

    /** The primary means of getting an instance of a template from this
     *  group. Names must be absolute, fully-qualified names like {@code /a/b}.
     */
    public ST getInstanceOf(String name) {
		if ( name==null ) return null;
		if ( verbose ) System.out.println(getName()+".getInstanceOf("+name+")");
		if ( name.charAt(0)!='/' ) name = "/"+name;
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
			return createStringTemplate(c);
        }
        return null;
    }

    protected ST getEmbeddedInstanceOf(Interpreter interp,
									   InstanceScope scope,
									   String name)
	{
		String fullyQualifiedName = name;
		if ( name.charAt(0)!='/' ) {
			fullyQualifiedName = scope.st.impl.prefix + name;
		}
		if ( verbose ) System.out.println("getEmbeddedInstanceOf(" + fullyQualifiedName +")");
        ST st = getInstanceOf(fullyQualifiedName);
		if ( st==null ) {
			errMgr.runTimeError(interp, scope,
								ErrorType.NO_SUCH_TEMPLATE,
								fullyQualifiedName);
			return createStringTemplateInternally(new CompiledST());
		}
		// this is only called internally. wack any debug ST create events
		if ( trackCreationEvents ) {
			st.debugState.newSTEvent = null; // toss it out
		}
        return st;
    }

	/** Create singleton template for use with dictionary values. */
	public ST createSingleton(Token templateToken) {
		String template;
		if ( templateToken.getType()==GroupParser.BIGSTRING || templateToken.getType()==GroupParser.BIGSTRING_NO_NL ) {
			template = Misc.strip(templateToken.getText(),2);
		}
		else {
			template = Misc.strip(templateToken.getText(),1);
		}
		CompiledST impl = compile(getFileName(), null, null, template, templateToken);
		ST st = createStringTemplateInternally(impl);
		st.groupThatCreatedThisInstance = this;
		st.impl.hasFormalArgs = false;
		st.impl.name = ST.UNKNOWN_NAME;
		st.impl.defineImplicitlyDefinedTemplates(this);
		return st;
	}

    /** Is this template defined in this group or from this group below?
     *  Names must be absolute, fully-qualified names like {@code /a/b}.
     */
    public boolean isDefined(String name) {
        return lookupTemplate(name)!=null;
    }

	/** Look up a fully-qualified name. */
    public CompiledST lookupTemplate(String name) {
		if ( name.charAt(0)!='/' ) name = "/"+name;
		if ( verbose ) System.out.println(getName()+".lookupTemplate("+name+")");
        CompiledST code = rawGetTemplate(name);
        if ( code==NOT_FOUND_ST ) {
			if ( verbose ) System.out.println(name+" previously seen as not found");
			return null;
		}
        // try to load from disk and look up again
        if ( code==null ) code = load(name);
        if ( code==null ) code = lookupImportedTemplate(name);
        if ( code==null ) {
			if ( verbose ) System.out.println(name+" recorded not found");
            templates.put(name, NOT_FOUND_ST);
        }
		if ( verbose ) if ( code!=null ) System.out.println(getName()+".lookupTemplate("+name+") found");
        return code;
    }

	/**
	 * Unload all templates, dictionaries and import relationships, but leave
	 * renderers and adaptors. This essentially forces the next call to
	 * {@link #getInstanceOf} to reload templates. Call {@code unload()} on each
	 * group in the {@link #imports} list, and remove all elements in
	 * {@link #importsToClearOnUnload} from {@link #imports}.
	 */
	public synchronized void unload() {
		templates.clear();
		dictionaries.clear();
		for (STGroup imp : imports) {
			imp.unload();
		}
		for (STGroup imp : importsToClearOnUnload) {
			imports.remove(imp);
		}
		importsToClearOnUnload.clear();
	}

    /** Load st from disk if directory or load whole group file if .stg file (then
     *  return just one template). {@code name} is fully-qualified.
     */
    protected CompiledST load(String name) { return null; }

    /** Force a load if it makes sense for the group. */
    public void load() { }

    protected CompiledST lookupImportedTemplate(String name) {
        if ( imports.size()==0 ) return null;
        for (STGroup g : imports) {
			if ( verbose ) System.out.println("checking "+g.getName()+" for imported "+name);
            CompiledST code = g.lookupTemplate(name);
			if ( code!=null ) {
				if ( verbose ) System.out.println(g.getName()+".lookupImportedTemplate("+name+") found");
				return code;
			}
        }
		if ( verbose ) System.out.println(name+" not found in "+getName()+" imports");
        return null;
    }

	public CompiledST rawGetTemplate(String name) { return templates.get(name); }
	public Map<String,Object> rawGetDictionary(String name) { return dictionaries.get(name); }
	public boolean isDictionary(String name) { return dictionaries.get(name)!=null; }

	/** for testing */
	public CompiledST defineTemplate(String templateName, String template) {
		if ( templateName.charAt(0)!='/' ) templateName = "/"+templateName;
		try {
			CompiledST impl =
				defineTemplate(templateName,
							   new CommonToken(GroupParser.ID, templateName),
							   null, template, null);
			return impl;
		}
		catch (STException se) {
			// we have reported the error; the exception just blasts us
			// out of parsing this template
		}
		return null;
	}

	/** for testing */
	public CompiledST defineTemplate(String name, String argsS, String template) {
		if ( name.charAt(0)!='/' ) name = "/"+name;
		String[] args = argsS.split(",");
		List<FormalArgument> a = new ArrayList<FormalArgument>();
		for (String arg : args) {
			a.add(new FormalArgument(arg));
		}
		return defineTemplate(name, new CommonToken(GroupParser.ID, name),
							  a, template, null);
	}

	public CompiledST defineTemplate(String fullyQualifiedTemplateName,
									 Token nameT,
                                     List<FormalArgument> args,
									 String template,
									 Token templateToken)
    {
		if ( verbose ) System.out.println("defineTemplate("+fullyQualifiedTemplateName+")");
		if ( fullyQualifiedTemplateName==null || fullyQualifiedTemplateName.length()==0 ) {
			throw new IllegalArgumentException("empty template name");
		}
		if ( fullyQualifiedTemplateName.indexOf('.')>=0 ) {
			throw new IllegalArgumentException("cannot have '.' in template names");
		}
        template = Misc.trimOneStartingNewline(template);
        template = Misc.trimOneTrailingNewline(template);
		// compile, passing in templateName as enclosing name for any embedded regions
        CompiledST code = compile(getFileName(), fullyQualifiedTemplateName, args, template, templateToken);
        code.name = fullyQualifiedTemplateName;
        rawDefineTemplate(fullyQualifiedTemplateName, code, nameT);
		code.defineArgDefaultValueTemplates(this);
        code.defineImplicitlyDefinedTemplates(this); // define any anonymous subtemplates

        return code;
    }

    /** Make name and alias for target.  Replace any previous definition of name. */
    public CompiledST defineTemplateAlias(Token aliasT, Token targetT) {
        String alias = aliasT.getText();
        String target = targetT.getText();
        CompiledST targetCode = rawGetTemplate("/"+target);
        if ( targetCode==null ){
            errMgr.compileTimeError(ErrorType.ALIAS_TARGET_UNDEFINED, null, aliasT, alias, target);
            return null;
        }
        rawDefineTemplate("/" + alias, targetCode, aliasT);
        return targetCode;
    }

    public CompiledST defineRegion(String enclosingTemplateName,
                                   Token regionT,
								   String template,
								   Token templateToken)
    {
        String name = regionT.getText();
		template = Misc.trimOneStartingNewline(template);
		template = Misc.trimOneTrailingNewline(template);
        CompiledST code = compile(getFileName(), enclosingTemplateName, null, template, templateToken);
        String mangled = getMangledRegionName(enclosingTemplateName, name);

        if ( lookupTemplate(mangled)==null ) {
            errMgr.compileTimeError(ErrorType.NO_SUCH_REGION, templateToken, regionT,
                                          enclosingTemplateName, name);
            return new CompiledST();
        }
        code.name = mangled;
        code.isRegion = true;
        code.regionDefType = ST.RegionType.EXPLICIT;
		code.templateDefStartToken = regionT;

        rawDefineTemplate(mangled, code, regionT);
		code.defineArgDefaultValueTemplates(this);
		code.defineImplicitlyDefinedTemplates(this);

        return code;
    }

    public void defineTemplateOrRegion(
		String fullyQualifiedTemplateName,
		String regionSurroundingTemplateName,
        Token templateToken,
		String template,
        Token nameToken,
        List<FormalArgument> args)
    {
        try {
            if ( regionSurroundingTemplateName!=null ) {
                defineRegion(regionSurroundingTemplateName, nameToken, template, templateToken);
            }
            else {
                defineTemplate(fullyQualifiedTemplateName, nameToken, args, template, templateToken);
            }
		}
		catch (STException e) {
			// after getting syntax error in a template, we emit msg
			// and throw exception to blast all the way out to here.
		}
	}

	public void rawDefineTemplate(String name, CompiledST code, Token defT) {
		CompiledST prev = rawGetTemplate(name);
		if ( prev!=null ) {
			if ( !prev.isRegion ) {
				errMgr.compileTimeError(ErrorType.TEMPLATE_REDEFINITION, null, defT);
				return;
			}
			else {
				if ( code.regionDefType!=ST.RegionType.IMPLICIT &&
					 prev.regionDefType==ST.RegionType.EMBEDDED )
				{
					errMgr.compileTimeError(ErrorType.EMBEDDED_REGION_REDEFINITION,
											null,
											defT,
											getUnMangledTemplateName(name));
					return;
				}
				else if ( code.regionDefType==ST.RegionType.IMPLICIT ||
					      prev.regionDefType==ST.RegionType.EXPLICIT )
				{
					errMgr.compileTimeError(ErrorType.REGION_REDEFINITION,
											null,
											defT,
											getUnMangledTemplateName(name));
					return;
				}
			}
		}
		code.nativeGroup = this;
		code.templateDefStartToken = defT;
		templates.put(name, code);
	}

	public void undefineTemplate(String name) {
		templates.remove(name);
	}

	/** Compile a template. */
	public CompiledST compile(String srcName,
							  String name,
							  List<FormalArgument> args,
							  String template,
							  Token templateToken) // for error location
    {
		//System.out.println("STGroup.compile: "+enclosingTemplateName);
		Compiler c = new Compiler(this);
		return c.compile(srcName, name, args, template, templateToken);
	}

    /** The {@code "foo"} of {@code t() ::= "<@foo()>"} is mangled to
	 *  {@code "/region__/t__foo"}
	 */
    public static String getMangledRegionName(String enclosingTemplateName,
                                              String name)
    {
		if ( enclosingTemplateName.charAt(0)!='/' ) {
			enclosingTemplateName = '/'+enclosingTemplateName;
		}
        return "/region__"+enclosingTemplateName+"__"+name;
    }

    /** Return {@code "t.foo"} from {@code "/region__/t__foo"} */
    public static String getUnMangledTemplateName(String mangledName) {
        String t = mangledName.substring("/region__".length(),
                                         mangledName.lastIndexOf("__"));
        String r = mangledName.substring(mangledName.lastIndexOf("__")+2,
                                         mangledName.length());
        return t+'.'+r;
    }

    /** Define a map for this group.
	 * <p>
	 * Not thread safe...do not keep adding these while you reference them.</p>
     */
    public void defineDictionary(String name, Map<String,Object> mapping) {
        dictionaries.put(name, mapping);
    }

    /**
     * Make this group import templates/dictionaries from {@code g}.
     *<p>
     * On unload imported templates are unloaded but stay in the {@link #imports} list.</p>
     */
    public void importTemplates(STGroup g) {
        importTemplates(g, false);
    }

	/** Import template files, directories, and group files.
	 *  Priority is given to templates defined in the current group;
	 *  this, in effect, provides inheritance. Polymorphism is in effect so
	 *  that if an inherited template references template {@code t()} then we
	 *  search for {@code t()} in the subgroup first.
	 *  <p>
	 *  Templates are loaded on-demand from import dirs.  Imported groups are
	 *  loaded on-demand when searching for a template.</p>
	 *  <p>
	 *  The listener of this group is passed to the import group so errors
	 *  found while loading imported element are sent to listener of this group.</p>
	 *  <p>
	 *  On unload imported templates are unloaded and removed from the imports
	 *  list.</p>
	 *  <p>
	 *  This method is called when processing import statements specified in
	 *  group files. Use {@link #importTemplates(STGroup)} to import templates
	 *  'programmatically'.</p>
	 */
	public void importTemplates(Token fileNameToken) {
		if ( verbose ) System.out.println("importTemplates("+fileNameToken.getText()+")");
		String fileName = fileNameToken.getText();
		// do nothing upon syntax error
		if ( fileName==null || fileName.equals("<missing STRING>") ) return;
		fileName = Misc.strip(fileName, 1);

		//System.out.println("import "+fileName);
		boolean isGroupFile = fileName.endsWith(GROUP_FILE_EXTENSION);
		boolean isTemplateFile = fileName.endsWith(TEMPLATE_FILE_EXTENSION);
		boolean isGroupDir = !(isGroupFile || isTemplateFile);

		STGroup g = null;

		// search path is: working dir, g.stg's dir, CLASSPATH
		URL thisRoot = getRootDirURL();
		URL fileUnderRoot;
//		System.out.println("thisRoot="+thisRoot);
		try {
			fileUnderRoot = new URL(thisRoot+"/"+fileName);
		}
		catch (MalformedURLException mfe) {
			errMgr.internalError(null, "can't build URL for "+thisRoot+"/"+fileName, mfe);
			return;
		}
		if ( isTemplateFile ) {
			g = new STGroup(delimiterStartChar, delimiterStopChar);
			g.setListener(this.getListener());
			URL fileURL;
			if ( Misc.urlExists(fileUnderRoot) ) fileURL = fileUnderRoot;
			else fileURL = getURL(fileName); // try CLASSPATH
			if ( fileURL!=null ) {
				try {
					InputStream s = fileURL.openStream();
					ANTLRInputStream templateStream = new ANTLRInputStream(s);
					templateStream.name = fileName;
					CompiledST code = g.loadTemplateFile("/", fileName, templateStream);
					if ( code==null ) g = null;
				}
				catch (IOException ioe) {
					errMgr.internalError(null, "can't read from "+fileURL, ioe);
					g = null;
				}
			}
			else {
				g = null;
			}
		}
		else if ( isGroupFile ) {
			//System.out.println("look for fileUnderRoot: "+fileUnderRoot);
			if ( Misc.urlExists(fileUnderRoot) ) {
				g = new STGroupFile(fileUnderRoot, encoding, delimiterStartChar, delimiterStopChar);
				g.setListener(this.getListener());
			}
			else {
				g = new STGroupFile(fileName, delimiterStartChar, delimiterStopChar);
				g.setListener(this.getListener());
			}
		}
		else if ( isGroupDir ) {
//			System.out.println("try dir "+fileUnderRoot);
			if ( Misc.urlExists(fileUnderRoot) ) {
				g = new STGroupDir(fileUnderRoot, encoding, delimiterStartChar, delimiterStopChar);
				g.setListener(this.getListener());
			}
			else {
				// try in CLASSPATH
//				System.out.println("try dir in CLASSPATH "+fileName);
				g = new STGroupDir(fileName, delimiterStartChar, delimiterStopChar);
				g.setListener(this.getListener());
			}
		}

		if ( g==null ) {
			errMgr.compileTimeError(ErrorType.CANT_IMPORT, null,
									fileNameToken, fileName);
		}
		else {
			importTemplates(g, true);
		}
	}

	protected void importTemplates(STGroup g, boolean clearOnUnload) {
		if ( g==null ) return;
		imports.add(g);
		if (clearOnUnload) {
			importsToClearOnUnload.add(g);
		}
	}

	public List<STGroup> getImportedGroups() { return imports; }

	/** Load a group file with full path {@code fileName}; it's relative to root by {@code prefix}. */
	public void loadGroupFile(String prefix, String fileName) {
		if ( verbose ) System.out.println(this.getClass().getSimpleName()+
										  ".loadGroupFile(group-file-prefix="+prefix+", fileName="+fileName+")");
		GroupParser parser;
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
			errMgr.IOError(null, ErrorType.CANT_LOAD_GROUP_FILE, e, fileName);
		}
	}

	/** Load template file into this group using absolute {@code fileName}. */
	public CompiledST loadAbsoluteTemplateFile(String fileName) {
		ANTLRFileStream fs;
		try {
			fs = new ANTLRFileStream(fileName, encoding);
			fs.name = fileName;
		}
		catch (IOException ioe) {
			// doesn't exist
			//errMgr.IOError(null, ErrorType.NO_SUCH_TEMPLATE, ioe, fileName);
			return null;
		}
		return loadTemplateFile("", fileName, fs);
	}

	/** Load template stream into this group. {@code unqualifiedFileName} is
	 *  {@code "a.st"}. The {@code prefix} is path from group root to
	 *  {@code unqualifiedFileName} like {@code "/subdir"} if file is in
	 *  {@code /subdir/a.st}.
	 */
	public CompiledST loadTemplateFile(String prefix, String unqualifiedFileName, CharStream templateStream) {
		GroupLexer lexer = new GroupLexer(templateStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		GroupParser parser = new GroupParser(tokens);
		parser.group = this;
		lexer.group = this;
		try {
			parser.templateDef(prefix);
		}
		catch (RecognitionException re) {
			errMgr.groupSyntaxError(ErrorType.SYNTAX_ERROR,
									unqualifiedFileName,
									re, re.getMessage());
		}
		String templateName = Misc.getFileNameNoSuffix(unqualifiedFileName);
		if ( prefix!=null && prefix.length()>0 ) templateName = prefix+templateName;
		CompiledST impl = rawGetTemplate(templateName);
		impl.prefix = prefix;
		return impl;
	}

	/**
	 * Add an adaptor for a kind of object so ST knows how to pull properties
	 * from them. Add adaptors in increasing order of specificity. ST adds
	 * {@link Object}, {@link Map}, {@link ST}, and {@link Aggregate} model
	 * adaptors for you first. Adaptors you add have priority over default
	 * adaptors.
	 * <p>
	 * If an adaptor for type {@code T} already exists, it is replaced by the
	 * {@code adaptor} argument.</p>
	 * <p>
	 * This must invalidate cache entries, so set your adaptors up before
	 * calling {@link ST#render} for efficiency.</p>
	 */
	public void registerModelAdaptor(Class<?> attributeType, ModelAdaptor adaptor) {
		if ( attributeType.isPrimitive() ) {
			throw new IllegalArgumentException("can't register ModelAdaptor for primitive type "+
											   attributeType.getSimpleName());
		}

		adaptors.put(attributeType, adaptor);
	}

	public ModelAdaptor getModelAdaptor(Class<?> attributeType) {
		return adaptors.get(attributeType);
	}

    /** Register a renderer for all objects of a particular "kind" for all
     *  templates evaluated relative to this group.  Use {@code r} to render if
	 *  object in question is an instance of {@code attributeType}.  Recursively
	 *  set renderer into all import groups.
     */
    public void registerRenderer(Class<?> attributeType, AttributeRenderer r) {
		registerRenderer(attributeType, r, true);
	}

	public void registerRenderer(Class<?> attributeType, AttributeRenderer r, boolean recursive) {
		if ( attributeType.isPrimitive() ) {
			throw new IllegalArgumentException("can't register renderer for primitive type "+
											   attributeType.getSimpleName());
		}

		if ( renderers == null ) {
			renderers = Collections.synchronizedMap(new TypeRegistry<AttributeRenderer>());
		}

		renderers.put(attributeType, r);

		if ( recursive ) {
			load(); // make sure imports exist (recursively)
			for (STGroup g : imports) {
				g.registerRenderer(attributeType, r, true);
			}
		}
	}

	/** Get renderer for class {@code T} associated with this group.
	 * <p>
	 *  For non-imported groups and object-to-render of class {@code T}, use renderer
	 *  (if any) registered for {@code T}.  For imports, any renderer
	 *  set on import group is ignored even when using an imported template.
	 *  You should set the renderer on the main group
	 *  you use (or all to be sure).  I look at import groups as
	 *  "helpers" that should give me templates and nothing else. If you
	 *  have multiple renderers for {@code String}, say, then just make uber combined
	 *  renderer with more specific format names.</p>
	 */
	public AttributeRenderer getAttributeRenderer(Class<?> attributeType) {
		if ( renderers==null ) {
			return null;
		}

		return renderers.get(attributeType);
	}

	public ST createStringTemplate(CompiledST impl) {
		ST st = new ST();
		st.impl = impl;
		st.groupThatCreatedThisInstance = this;
		if ( impl.formalArguments!=null ) {
			st.locals = new Object[impl.formalArguments.size()];
			Arrays.fill(st.locals, ST.EMPTY_ATTR);
		}
		return st;
	}

	/** Differentiate so we can avoid having creation events for regions,
	 *  map operations, and other implicit "new ST" events during rendering.
	 */
	public ST createStringTemplateInternally(CompiledST impl) {
		ST st = createStringTemplate(impl);
		if ( trackCreationEvents && st.debugState!=null ) {
			st.debugState.newSTEvent = null; // toss it out
		}
		return st;
	}

	public ST createStringTemplateInternally(ST proto) {
		return new ST(proto); // no need to wack debugState; not set in ST(proto).
	}

    public String getName() { return "<no name>;"; }
	public String getFileName() { return null; }

	/** Return root dir if this is group dir; return dir containing group file
	 *  if this is group file.  This is derived from original incoming
	 *  dir or filename.  If it was absolute, this should come back
	 *  as full absolute path.  If only a URL is available, return URL of
	 *  one dir up.
	 */
	public URL getRootDirURL() { return null; }

	public URL getURL(String fileName) {
		URL url;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		url = cl.getResource(fileName);
		if ( url==null ) {
			cl = this.getClass().getClassLoader();
			url = cl.getResource(fileName);
		}
		return url;
	}

	@Override
    public String toString() { return getName(); }

    public String show() {
        StringBuilder buf = new StringBuilder();
        if ( imports.size()!=0 ) buf.append(" : "+imports);
        for (String name : templates.keySet()) {
			CompiledST c = rawGetTemplate(name);
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

	public STErrorListener getListener() {
		return errMgr.listener;
	}

	public void setListener(STErrorListener listener) {
		errMgr = new ErrorManager(listener);
	}

	public Set<String> getTemplateNames() {
		load();
		HashSet<String> result = new HashSet<String>();
		for (Map.Entry<String, CompiledST> e: templates.entrySet()) {
			if (e.getValue() != NOT_FOUND_ST) {
				result.add(e.getKey());
			}
		}
		return result;
	}
}
