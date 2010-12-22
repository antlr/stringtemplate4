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
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;

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
        //new SynchronizedLinkedHashMap<String,CompiledST>();

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
     *  This structure is synchronized.
     */
    protected Map<Class, AttributeRenderer> renderers;

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
            return instanceST;
        }
        return null;
    }

    public ST getInstanceOf(String name, Map<String,Object> attributes) {
		if ( name==null ) return null;
        ST st = getInstanceOf(name);
        if ( st!=null ) st.setAttributes(attributes);
        return st;
    }

    protected ST getEmbeddedInstanceOf(ST enclosingInstance, int ip, String name) {
        ST st = getInstanceOf(name);
        if ( st==null ) {
            ErrorManager.runTimeError(enclosingInstance, ip, ErrorType.NO_SUCH_TEMPLATE,
                                      name);
            return ST.BLANK;
        }
        st.enclosingInstance = enclosingInstance;
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

    // for testing
    public CompiledST defineTemplate(String templateName, String template) {
        return defineTemplate(templateName, new CommonToken(GroupParser.ID,templateName),
                              FormalArgument.UNKNOWN, template);
    }

	public CompiledST defineTemplate(String name, String argsS, String template) {
		String[] args = argsS.split(",");
		LinkedHashMap
<String, FormalArgument> a =
			new LinkedHashMap
<String,FormalArgument>();
		for (int i = 0; i  < args.length; i ++) {
			a.put(args[i], new FormalArgument(args[i]));
		}
		return defineTemplate(name, new CommonToken(GroupParser.ID, name),
							  a, template);
	}

    public CompiledST defineTemplate(String templateName, Token nameT,
                                     LinkedHashMap
<String,FormalArgument> args,
                                     String template)
    {
//        String name = nameT.getText();
		if ( templateName==null || templateName.length()==0 ) {
			throw new IllegalArgumentException("empty template name");
		}
		if ( templateName.indexOf('.')>=0 ) {
			throw new IllegalArgumentException("cannot have '.' in template names");
		}
        template = Misc.trimOneStartingNewline(template);
        template = Misc.trimOneTrailingNewline(template);
        CompiledST code = compile(templateName, template);
        code.name = templateName;
        code.formalArguments = args;
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
        CompiledST code = compile(enclosingTemplateName, template);
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
        LinkedHashMap
<String,FormalArgument> args)
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

    protected CompiledST compile(String enclosingTemplateName,
                                 String template)
    {
        Compiler c = new Compiler(enclosingTemplateName,
                                  delimiterStartChar, delimiterStopChar);
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

    // TODO: make this happen in background then flip ptr to new list of templates/dictionaries?
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

    /** Register a renderer for all objects of a particular type for all
     *  templates evaluated relative to this group.
     */
    public void registerRenderer(Class attributeType, AttributeRenderer r) {
        if ( renderers ==null ) {
            renderers = Collections.synchronizedMap(new HashMap<Class, AttributeRenderer>());
        }
        renderers.put(attributeType, r);
    }

    public AttributeRenderer getAttributeRenderer(Class attributeType) {
        if ( renderers==null ) return null;
        return renderers.get(attributeType);
    }

    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        // TODO: try making a mem pool?
        if ( debug ) {
            return new DebugST();
        }
        return new ST();
    }

    public String getName() { return "<no name>;"; }

    public String toString() { return getName(); }

    public String show() {
        StringBuilder buf = new StringBuilder();
        if ( imports!=null ) buf.append(" : "+imports);
        for (String name : templates.keySet()) {
			CompiledST c = templates.get(name);
			if ( c.isSubtemplate || c==NOT_FOUND_ST ) continue;
            int slash = name.lastIndexOf('/');
            name = name.substring(slash+1, name.length());
            buf.append(name);
            buf.append('(');
            buf.append( Misc.join(c.formalArguments.values().iterator(), ",") );
            buf.append(')');
            buf.append(" ::= <<"+Misc.newline);
            buf.append(c.template+ Misc.newline);
            buf.append(">>"+Misc.newline);
        }
        return buf.toString();
    }
}
