package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

// TODO: caching?

public class STGroupDir extends STGroup {
    public File dir;

    public STGroupDir(String dirName) {
        dir = new File(dirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+ dirName);
        }
    }

    public STGroupDir(STGroup root, String dirName) {
        this(dirName);
        this.root = root;
    }

    public STGroupDir(STGroup root, String dirName, String encoding) {
        this(root, dirName);
        this.encoding = encoding;
    }

    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            if ( root!=null ) return root.lookupTemplate(name);
            // strip '/' and try again; we're root
            //return lookupTemplate(name.substring(1));
            name = name.substring(1);
            String[] names = name.split("/");
            if ( !names[0].equals(dir.getName()) ) {
                throw new IllegalArgumentException(names[0]+" doesn't match directory name "+dir.getName());
            }
        }
        if ( name.indexOf('/')>=0 ) return lookupQualifiedTemplate(dir, name);

        // else plain old template name, check if already here
        CompiledST code = templates.get(name);
        if ( code!=null ) return code;
        return lookupTemplateFile(name);
    }

    /** Look up template name with '/' anywhere but first char */
    protected CompiledST lookupQualifiedTemplate(File dir, String name) {
        // TODO: slow to load a template!
        String[] names = name.split("/");
        File templateFile = new File(dir+"/"+names[0]+".st");
        if ( templates.get(names[0])!=null || templateFile.exists() ) {
            throw new IllegalArgumentException(names[0]+" is a template not a dir or group file");
        }
        // look for a directory or group file called names[0]
        STGroup sub = null;
        File subF = new File(dir, names[0]);
        if ( subF.isDirectory() ) {
            sub = new STGroupDir(root, dir+"/"+names[0]);
        }
        else if ( new File(dir, names[0]+".stg").exists() ) {
            try {
                sub = new STGroupFile(dir+"/"+names[0]+".stg");
            }
            catch (Exception e) {
                listener.error("can't load group file: "+ names[0]+".stg", e);
            }
        }
        else {
            throw new IllegalArgumentException("no such subgroup: "+names[0]);
        }
        String allButFirstName = Misc.join(names, "/", 1, names.length);
        return sub.lookupTemplate(allButFirstName);
    }

    public CompiledST lookupTemplateFile(String name) {
        // not in templates list, load it from disk
        File f = new File(dir, "/" + name + ".st");
        if ( !f.exists() ) { // TODO: add tolerance check here
            throw new IllegalArgumentException("no such template: "+name);
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
            listener.error("can't load template file: "+ f +"/"+name, e);
        }
        return null;
    }

    public String getName() { return dir.getName(); }

}
