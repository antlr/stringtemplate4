package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

public class STGroupFile extends STGroup {
    public String fileName;
    public String encoding;

    public STGroupFile(String fullyQualifiedFileName) {
        if ( !fullyQualifiedFileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fullyQualifiedFileName);
        }
        File f = new File(fullyQualifiedFileName);
        this.fileName = f.getName();
        this.parent = null;
        this.root = this;
        this.fullyQualifiedRootDirName = f.getParent();
    }

    public STGroupFile(STGroupDir parent, String fileName) {
        if ( parent==null ) {
            throw new IllegalArgumentException("Relative dir "+fileName+" can't have null parent");            
        }
        this.fileName = fileName;
        this.parent = parent;
        this.root = parent.root; // cache root ptr        
    }

    public STGroupFile(STGroupDir parent, String fileName, String encoding) {
        this(parent, fileName);
        this.encoding = encoding;
    }

    public String getName() { return fileName.substring(0,fileName.lastIndexOf('.')); }
    
    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            if ( this!=root ) return root.lookupTemplate(name);
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
        String fullFileName = getPathFromRoot()+".stg";
        try {
            ANTLRFileStream fs = new ANTLRFileStream(fullFileName, encoding);
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group(this);
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load group file: "+fullFileName, e);
        }
    }

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();        
    }
}
