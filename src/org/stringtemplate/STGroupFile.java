package org.stringtemplate;

import org.stringtemplate.compiler.CompiledST;
import org.stringtemplate.misc.ErrorManager;
import org.stringtemplate.misc.Misc;

import java.io.File;
import java.net.URL;

public class STGroupFile extends STGroup {
    public String fileName;
    public URL url;

    protected boolean alreadyLoaded = false;
        
    /** Load a file relative to current dir or from root or via CLASSPATH. */
    public STGroupFile(String fileName) {
        if ( !fileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fileName);
        }
        try {
            File dir = new File(fileName);
            if ( dir.exists() ) {
                url = dir.toURI().toURL();
            }
            else { // try in classpath
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                URL groupFileURL = cl.getResource(fileName);
                if ( groupFileURL==null ) {
                    cl = this.getClass().getClassLoader();
                    groupFileURL = cl.getResource(fileName);
                }
                if ( groupFileURL==null ) {
                    throw new IllegalArgumentException("No such group file: "+
                                                       fileName);
                }
            }
        }
        catch (Exception e) {
            ErrorManager.internalError(null, "can't load group file "+fileName, e);
        }
        this.fileName = fileName;
    }

    public STGroupFile(String fullyQualifiedFileName, String encoding) {
        this(fullyQualifiedFileName);
        this.encoding = encoding;
    }

    protected CompiledST load(String name) {
        String prefix = new File(name).getParent();
        if ( !prefix.endsWith("/") ) prefix += "/";
        _load(prefix);
        return templates.get(name);
    }

    public void load() { _load("/"); }

    protected void _load(String prefix) {
        if ( alreadyLoaded ) return;
        loadGroupFile(prefix, url.toString());
        alreadyLoaded = true;
    }

    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();
    }

    public String getName() { return Misc.getFileNameNoSuffix(fileName); }
}
