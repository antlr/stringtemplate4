package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

// TODO: caching?

public class STGroupDir extends STGroup {
    public String dirName;
    public List<STGroup> children;

    public STGroupDir(String fullyQualifiedRootDirName) {
        this.parent = null;
        this.root = this;
        this.fullyQualifiedRootDirName = fullyQualifiedRootDirName;
        this.dirName = "/"; // it's the root
        File dir = new File(fullyQualifiedRootDirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+
                                               fullyQualifiedRootDirName);
        }
    }

    public STGroupDir(STGroupDir parent, String relativeDirName) {
        if ( parent==null ) {
            throw new IllegalArgumentException("Relative dir "+relativeDirName+" can't have null parent");
        }
        // doubly-link this node; we point at parent and it has us as child
        this.parent = parent;
        parent.addChild(this);
        this.root = parent.root; // cache root ptr
        this.dirName = relativeDirName;

        String absoluteDirName = root.fullyQualifiedRootDirName+
                                 getAbsoluteTemplatePath();
        File dir = new File(absoluteDirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+ absoluteDirName);
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
        code = lookupTemplateFile(name); // try to load then
        if ( code==null ) {
            code = lookupImportedTemplate(name);
            if ( code==null ) { // TODO: tolerance?
                throw new IllegalArgumentException("no such template: /"+
                                                   getAbsoluteTemplateName(name));
            }
        }
        return code;
    }

    /** Look up template name with '/' anywhere but first char */
    protected CompiledST lookupQualifiedTemplate(String name) {
        // TODO: slow to load a template!
        String absoluteDirName = root.fullyQualifiedRootDirName+
                                 getAbsoluteTemplatePath();

        String[] names = name.split("/");
        File templateFile = new File(absoluteDirName, names[0]+".st");
        if ( templates.get(names[0])!=null || templateFile.exists() ) {
            throw new IllegalArgumentException(names[0]+" is a template not a dir or group file");
        }
        // look for a directory or group file called names[0]
        STGroup sub = null;
        File group = new File(absoluteDirName, names[0]);
        if ( group.exists() && group.isDirectory() ) {
            sub = new STGroupDir(this, names[0]);
            if ( children==null ) children = new ArrayList<STGroup>();
            children.add(sub);
        }
        else if ( new File(absoluteDirName, names[0]+".stg").exists() ) {
            try {
                sub = new STGroupFile(this, names[0]+".stg");
                if ( children==null ) children = new ArrayList<STGroup>();
                children.add(sub);
            }
            catch (Exception e) {
                listener.error("can't load group file: "+ names[0]+".stg", e);
            }
        }
        else {
            throw new IllegalArgumentException("no such subdirectory or group file: "+names[0]);
        }
        String allButFirstName = Misc.join(names, "/", 1, names.length);
        return sub.lookupTemplate(allButFirstName);
    }

    public CompiledST lookupTemplateFile(String name) { // load from disk
        String absoluteDirName = root.fullyQualifiedRootDirName+
                                 getAbsoluteTemplatePath();
        File f = new File(absoluteDirName, name + ".st");
        if ( !f.exists() ) { // TODO: add tolerance check here
            return null;
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

    /** Make this group import templates/dictionaries from g.
     *  If this group has children, make them import stuff from g's
     *  children (if any).
     */
    public void importTemplates(STGroup g) {
        if ( g==null ) return;
        if ( imports==null ) imports = new ArrayList<STGroup>();
        imports.add(g);
        // now, hook up children. If this has group called x, then look
        // for x in g's children.  If found, then make our x import from
        // g's x.
        /*
        if ( g instanceof STGroupDir ) {
            STGroupDir gDir = (STGroupDir)g;
            for (STGroup child : children) {
                CompiledST importedST = gDir.lookupTemplate(child.getName());
                int i = gDir.children.indexOf(child);
                if ( i>=0 ) child.importTemplates(gDir.children.get(i));
            }
        }
        */
    }

    public void addChild(STGroup g) {
        if ( children==null ) children = new ArrayList<STGroup>();
        children.add(g);        
    }
    
    public String getName() {
        if ( parent==null ) return "/";
        return dirName;
    }
}
