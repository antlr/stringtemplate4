package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

public class STGroupFile extends STGroup {
    public String fileName = null;

    public STGroupFile(String fileName) {
        if ( !fileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fileName);
        }
        this.name = fileName.substring(0,fileName.lastIndexOf('.'));
        this.fileName = fileName;
    }

    public void load() {
        if ( alreadyLoaded ) return;
        try {
            ANTLRFileStream fs = new ANTLRFileStream(fileName);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group(this);
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load group file: "+fileName, e);
        }
    }

    public CompiledST lookupTemplate(String name) {
        if ( !alreadyLoaded ) load();
        return super.lookupTemplate(name);
    }

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();        
    }
}
