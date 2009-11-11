package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;
import java.util.List;
import java.util.LinkedHashMap;

public class STTree {
    public String fullyQualifiedRootDirName; // if we're root

    /** Load files using what encoding? */
    public String encoding;

    /** Every group can import templates/dictionaries from other groups */
    protected List<STGroup> imports;

    /** Maps template name to StringTemplate object */
    protected LinkedHashMap<String, CompiledST> templates =
        new LinkedHashMap<String,CompiledST>();
    
    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    protected boolean alreadyLoaded = false;

    public STTree(String fullyQualifiedRootDirName) {
        this.fullyQualifiedRootDirName = fullyQualifiedRootDirName;
        File dir = new File(fullyQualifiedRootDirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+
                                               fullyQualifiedRootDirName);
        }
    }

    /** The primary means of getting an instance of a template from this
     *  group. name must be fully qualified, absolute like "/a/b".
     */
    public ST getInstanceOf(String name) {
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
            ST instanceST = createStringTemplate();
            //instanceST.groupThatCreatedThisInstance = this;
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
        //code.nativeGroup = this;
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
    
    public CompiledST lookupTemplate(String name) {
        if ( !alreadyLoaded ) load();
        return templates.get(name);
    }

    public void load() {
        // walk dir and all subdir to load templates, group files
        _load("");
        alreadyLoaded = true;
    }

    protected void _load(String prefix) {
        // walk dir and all subdir to load templates, group files
        File dir = new File(fullyQualifiedRootDirName+"/"+prefix);
        File[] filesAndDirs = dir.listFiles();
        for (File f : filesAndDirs) {
            if ( f.isDirectory() ) _load(prefix+f.getName());
            // otherwise, load template or group file
            if ( f.getName().endsWith(".st") ) {
                loadTemplateFile(prefix, f.getName());
            }
        }
    }

    public CompiledST loadTemplateFile(String prefix, String fileName) { // load from disk
        String absoluteFileName = fullyQualifiedRootDirName + "/" + prefix + "/" + fileName;
        File f = new File(absoluteFileName);
        if ( !f.exists() ) { // TODO: add tolerance check here
            return null;
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(f.toString(), encoding);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            //parser.group = this;
            //parser.templateDef();
            return lookupTemplate("/"+prefix+Misc.getFileNameNoSuffix(fileName));
        }
        catch (Exception e) {
            System.err.println("can't load template file: "+absoluteFileName);
        }
        return null;
    }    

    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        ST st = new ST();
        return st;
    }    
}
