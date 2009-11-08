package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

// TODO: caching?

public class STGroupDir extends STGroup {
    public String rootDirName = null;

    public STGroupDir(String rootDirName) {
        File f = new File(rootDirName);
        if ( !f.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+rootDirName);
        }
        this.name = new File(rootDirName).getName();
        this.rootDirName = rootDirName;
    }

    public CompiledST lookupTemplate(String name) {
        String fileName = rootDirName + "/" + name + ".st";
        File f = new File(fileName);
        if ( !f.exists() ) { // TODO: add tolerance check here
            throw new IllegalArgumentException("no such template: "+name);
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(fileName);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group = this;
            parser.templateDef();
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load group file: "+rootDirName+"/"+name, e);
        }
        return super.lookupTemplate(name);
    }    
}
