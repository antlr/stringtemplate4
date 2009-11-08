package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

public class STGroupFile extends STGroup {
    public String fileName = null;

    public STGroupFile(String fileName) {
        if ( !fileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fileName);
        }
        this.fileName = fileName;
    }

    public String getName() { return new File(fileName).getName(); }

    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            if ( root!=null ) return root.lookupTemplate(name);
            // if no root, name must be "/groupfile/templatename"
            String[] names = name.split("/");
            String fname = new File(fileName).getName();
            String base = fname.substring(0,fname.lastIndexOf('.'));
            if ( names.length>2 || !names[0].equals(base) ) {
                throw new IllegalArgumentException("name must be of form /"+base+"/templatename: "+name);
            }
        }
        if ( name.indexOf('/')>=0 ) {
            throw new IllegalArgumentException("can't use relative template name "+name);
        }

        // else plain old template name
        if ( !alreadyLoaded ) load();
        return templates.get(name);
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

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();        
    }
}
