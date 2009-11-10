package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

// TODO: caching?

public class STGroupDir extends STGroup {
    public String dirName;

    public STGroupDir(String fullyQualifiedRootDirName) {
        this.parent = null;
        this.root = this;
        this.fullyQualifiedRootDirName = fullyQualifiedRootDirName;
        File dir = new File(fullyQualifiedRootDirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+
                                               fullyQualifiedRootDirName);
        }
    }

    public STGroupDir(STGroupDir parent, String dirName) {
        if ( parent==null ) {
            throw new IllegalArgumentException("Relative dir "+dirName+" can't have null parent");            
        }
        this.parent = parent;
        this.root = parent.root; // cache root ptr
        this.dirName = dirName;
        File dir = new File(getPathFromRoot());
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+ dir);
        }
    }

    public STGroupDir(STGroupDir parent, String dirName, String encoding) {
        this(parent, dirName);
        this.encoding = encoding;
    }

    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            if ( this!=root ) return root.lookupTemplate(name);
            // we're the root; strip '/' and try again
            name = name.substring(1);
        }
        if ( name.indexOf('/')>=0 ) return lookupQualifiedTemplate(name);

        // else plain old template name, check if already here
        CompiledST code = templates.get(name);
        if ( code!=null ) return code;
        return lookupTemplateFile(name); // try to load then
    }

    /** Look up template name with '/' anywhere but first char */
    protected CompiledST lookupQualifiedTemplate(String name) {
        // TODO: slow to load a template!
        String d = getPathFromRoot();

        String[] names = name.split("/");
        File templateFile = new File(d, names[0]+".st");
        if ( templates.get(names[0])!=null || templateFile.exists() ) {
            throw new IllegalArgumentException(names[0]+" is a template not a dir or group file");
        }
        // look for a directory or group file called names[0]
        STGroup sub = null;
        File group = new File(d, names[0]);
        if ( group.exists() && group.isDirectory() ) {
            sub = new STGroupDir(this, names[0]);
        }
        else if ( new File(d, names[0]+".stg").exists() ) {
            try {
                sub = new STGroupFile(this, names[0]+".stg");
            }
            catch (Exception e) {
                listener.error("can't load group file: "+ names[0]+".stg", e);
            }
        }
        else {
            throw new IllegalArgumentException("no such subdirectory or group file: "+names[0]);
        }
        String allButFirstName = Misc.join(names, "/", 1, names.length);
        CompiledST st = sub.lookupTemplate(allButFirstName);
        if ( st==null ) { // try list of imports at root
            System.out.println("look for "+name+" in "+imports);
        }
        return st;
    }

    public CompiledST lookupTemplateFile(String name) { // load from disk
        String d = getPathFromRoot();
        File f = new File(d, name + ".st");
        if ( !f.exists() ) { // TODO: add tolerance check here
            throw new IllegalArgumentException("no such template: /"+ getAbsoluteTemplatePath()+"/"+name);
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(f.toString(), encoding);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group = this;
            parser.templateDef();
            return templates.get(name);
        }
        catch (Exception e) {
            listener.error("can't load template file: "+ f.getAbsolutePath() +"/"+name, e);
        }
        return null;
    }

    public String getName() {
        if ( parent==null ) return "/";
        return dirName;
    }
}
