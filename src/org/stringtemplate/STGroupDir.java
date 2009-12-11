package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.UnbufferedTokenStream;
import org.antlr.runtime.CommonTokenStream;
import org.stringtemplate.misc.Misc;
import org.stringtemplate.compiler.CompiledST;
import org.stringtemplate.compiler.GroupLexer;
import org.stringtemplate.compiler.GroupParser;

import java.io.File;

// TODO: caching?

public class STGroupDir extends STGroup {
    public String dirName;

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

    /** walk dir and all subdir to load templates, group files */
    public void load() {
        _load("/");
        alreadyLoaded = true;
    }

    protected void _load(String prefix) {
        File dir = new File(fullyQualifiedRootDirName+"/"+prefix);
        //System.out.println("load dir '"+prefix+"' under "+fullyQualifiedRootDirName);
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
        //System.out.println("load "+absoluteFileName);
        File f = new File(absoluteFileName);
        if ( !f.exists() ) { // TODO: add tolerance check here
            return null;
        }
        try {
            String templateName = Misc.getFileNameNoSuffix(fileName);
            if ( ErrorManager.v3_mode) {
                String template = Misc.readLines(absoluteFileName);
                template = template.trim();
                defineTemplate(prefix,
                               new CommonToken(GroupParser.ID,templateName),
                               null,
                               template);
            }
            else {
                ANTLRFileStream fs = new ANTLRFileStream(f.toString(), encoding);
                GroupLexer lexer = new GroupLexer(fs);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                GroupParser parser = new GroupParser(tokens);
                parser.group = this;
                parser.templateDef(prefix);
            }
            return templates.get("/"+prefix+templateName);
        }
        catch (Exception e) {
            ErrorManager.IOError(null, ErrorType.CANT_LOAD_TEMPLATE_FILE, e, absoluteFileName);
            e.printStackTrace(System.err);
        }
        return null;
    }    

    public String getName() { return dirName; }
}
