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
        this.fullyQualifiedRootDirName = fullyQualifiedRootDirName;
        this.dirName = "/"; // it's the root
        File dir = new File(fullyQualifiedRootDirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+
                                               fullyQualifiedRootDirName);
        }
    }

    public STGroupDir(String fullyQualifiedRootDirName, String encoding) {
        this(fullyQualifiedRootDirName);
        this.encoding = encoding;
    }

    public void load() {
        // walk dir and all subdir to load templates, group files
        _load("/");
        alreadyLoaded = true;
    }

    protected void _load(String prefix) {
        // walk dir and all subdir to load templates, group files
        File dir = new File(fullyQualifiedRootDirName+"/"+prefix);
        System.out.println("load dir '"+prefix+"' under "+fullyQualifiedRootDirName);
        File[] filesAndDirs = dir.listFiles();
        for (File f : filesAndDirs) {
            if ( f.isDirectory() ) _load(prefix+f.getName()+"/");
            // otherwise, load template or group file
            if ( f.getName().endsWith(".st") ) {
                loadTemplateFile(prefix, f.getName());
            }
            else if ( f.getName().endsWith(".stg") ) {
                loadGroupFile(prefix+Misc.getFileNameNoSuffix(f.getName())+"/", prefix+f.getName());
            }
        }
    }

    public CompiledST loadTemplateFile(String prefix, String fileName) { // load from disk
        String absoluteFileName = fullyQualifiedRootDirName + "/" + prefix + "/" + fileName;
        System.out.println("load "+absoluteFileName);
        File f = new File(absoluteFileName);
        if ( !f.exists() ) { // TODO: add tolerance check here
            return null;
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(f.toString(), encoding);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group = this;
            parser.templateDef(prefix);
            return templates.get("/"+prefix+Misc.getFileNameNoSuffix(fileName));
        }
        catch (Exception e) {
            System.err.println("can't load template file: "+absoluteFileName);
            e.printStackTrace(System.err);
        }
        return null;
    }    

    public String getName() { return dirName; }

    public void addChild(STGroup g) {
        if ( children==null ) children = new ArrayList<STGroup>();
        children.add(g);        
    }

}
