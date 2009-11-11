package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;

public class STGroupFile extends STGroup {
    public String fileName;

    public STGroupFile(String fullyQualifiedFileName) {
        if ( !fullyQualifiedFileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fullyQualifiedFileName);
        }
        File f = new File(fullyQualifiedFileName);
        this.fullyQualifiedRootDirName = f.getParent();
        this.fileName = f.getName();
    }

    public STGroupFile(STGroupDir parent, String fileName) {
        if ( parent==null ) {
            throw new IllegalArgumentException("Relative dir "+fileName+" can't have null parent");            
        }
        this.fileName = fileName;
        // doubly-link this node; we point at parent and it has us as child
        parent.addChild(this);
    }

    public STGroupFile(STGroupDir parent, String fileName, String encoding) {
        this(parent, fileName);
        this.encoding = encoding;
    }

/*
    public ST getInstanceOf(String name) {
        if ( name.charAt(0)!='/' ) name = "/"+name;
        return super.getInstanceOf(name);
    }
     */

    public void load() {
        loadGroupFile("/", fileName);
        alreadyLoaded = true;
    }

    /*
    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            // if no root, name must be "/groupfile/templatename"
            String[] names = name.substring(1).split("/");
            if ( names.length>1 ) {
                throw new IllegalArgumentException("name must be of form /templatename: "+name);
            }
            name = names[0];
        }
        if ( name.indexOf('/')>=0 ) {
            throw new IllegalArgumentException("can't use relative template name "+name);
        }

        // else plain old template name
        if ( !alreadyLoaded ) load();
        CompiledST code = templates.get(name);
        if ( code==null ) {
            code = lookupImportedTemplate(name);
            if ( code==null ) { // TODO: tolerance?
                throw new IllegalArgumentException("no such template: /"+
                                                   getAbsoluteTemplateName(name));
            }
        }
        return code;
    }
    */

    public String getName() { return Misc.getFileNameNoSuffix(fileName); }

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();        
    }
}
