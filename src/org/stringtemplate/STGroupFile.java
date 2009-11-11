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
