package org.stringtemplate;

import org.stringtemplate.misc.Misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class STGroupFile extends STGroup {
    public String fileName;

    /** Load a file relative to current dir or from root or via CLASSPATH. */
    public STGroupFile(String fileName) {
        if ( !fileName.endsWith(".stg") ) {
            throw new IllegalArgumentException("Group file names must end in .stg: "+fileName);
        }
        this.fileName = fileName;
        /*
        File f = new File(fullyQualifiedFileName);
        if ( f.exists() ) {
            File absF = f.getAbsoluteFile();
            this.fullyQualifiedRootDirName = absF.getParent();
            this.fileName = f.getName();
        }
        else {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL u = cl.getResource(fileName);
            if ( u==null ) {
                cl = this.getClass().getClassLoader();
                u = cl.getResource(fileName);
            }
            if ( u==null ) {
                throw new IllegalArgumentException("Can't find "+fullyQualifiedFileName);
            }
        }
        */
    }

    public STGroupFile(String fullyQualifiedFileName, String encoding) {
        this(fullyQualifiedFileName);
        this.encoding = encoding;
    }

    public void load() {
        loadGroupFile("/", fileName);
        alreadyLoaded = true;
    }

    public String getName() { return Misc.getFileNameNoSuffix(fileName); }
}
