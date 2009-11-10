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
        this.fullyQualifiedRootDirName = f.getParent();
        this.fileName = f.getName();
        this.parent = null;
        this.root = this;
    }

    public STGroupFile(STGroupDir parent, String fileName) {
        if ( parent==null ) {
            throw new IllegalArgumentException("Relative dir "+fileName+" can't have null parent");            
        }
        this.fileName = fileName;
        // doubly-link this node; we point at parent and it has us as child
        this.parent = parent;
        parent.addChild(this);        
        this.root = parent.root; // cache root ptr
    }

    public STGroupFile(STGroupDir parent, String fileName, String encoding) {
        this(parent, fileName);
        this.encoding = encoding;
    }

    public String getName() { return fileName.substring(0,fileName.lastIndexOf('.')); }

    /* /group if this is root else /dir1/dir2/group if in subdir of STGroupDir */
    public String getAbsoluteTemplatePath() {
        return super.getAbsoluteTemplatePath();
    }

    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) {
            if ( this!=root ) return root.lookupTemplate(name);
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

    public void load() {
        if ( alreadyLoaded ) return;
        String absoluteFileName = root.fullyQualifiedRootDirName+"/"+fileName;
        if ( this!=root ) {
            absoluteFileName = root.fullyQualifiedRootDirName+
                               getAbsoluteTemplatePath()+".stg";
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(absoluteFileName, encoding);
            GroupLexer lexer = new GroupLexer(fs);
            UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group(this);
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load group file: "+absoluteFileName, e);
        }
    }

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();        
    }
}
