package org.stringtemplate;

import org.antlr.runtime.*;
import org.stringtemplate.compiler.*;
import org.stringtemplate.misc.ErrorManager;
import org.stringtemplate.misc.ErrorType;
import org.stringtemplate.misc.Misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

// TODO: caching?

public class STGroupDir extends STGroup {
    public String groupDirName;
    public URL root;

    public STGroupDir(String dirName) {
        this.groupDirName = dirName;
        try {
            File dir = new File(dirName);
            if ( dir.exists() && dir.isDirectory() ) {
                // we found the directory and it'll be file based
                root = dir.toURI().toURL();
            }
            else {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                root = cl.getResource(dirName);
                if ( root==null ) {
                    cl = this.getClass().getClassLoader();
                    root = cl.getResource(dirName);
                }
                if ( root==null ) {
                    throw new IllegalArgumentException("No such directory: "+
                                                       dirName);
                }
            }
        }
        catch (Exception e) {
            ErrorManager.internalError(null, "can't load group dir "+dirName, e);
        }

        //System.out.println("STGroupDir("+dirName+") found as "+root);
    }

    public STGroupDir(String fullyQualifiedRootDirName, String encoding) {
        this(fullyQualifiedRootDirName);
        this.encoding = encoding;
    }

    /** walk dir and all subdir to load templates, group files */
/*
    public void load() {
        _load("/");
        alreadyLoaded = true;
    }
    */

    /** Load a template from dir or group file.  Group file is given
     *  precedence over dir with same name.
     */
    protected CompiledST load(String name) {
        String parent = new File(name).getParent();
        String prefix = parent;
        if ( !prefix.endsWith("/") ) prefix += "/";

        URL groupFileURL = null;
        try { // see if parent of template name is a group file
            groupFileURL = new URL(root+parent+".stg");
        }
        catch (MalformedURLException e) {
            ErrorManager.internalError(null, "bad URL: "+root+parent+".stg", e);
        }
        InputStream is = null;
        try {
            is = groupFileURL.openStream();
        }
        catch (FileNotFoundException fnfe) {
            // must not be in a group file
            return loadTemplateFile(name+".st"); // load /prefix/t.st file
        }
        catch (IOException ioe) {
            ErrorManager.internalError(null, "can't load template file "+name, ioe);
        }
        try { // clean up
            is.close();
        }
        catch (IOException ioe) {
            ErrorManager.internalError(null, "can't close template file stream "+name, ioe);
        }
        loadGroupFile(prefix, root+parent+".stg");
        return templates.get(name);
    }

    public CompiledST loadTemplateFile(String fileName) {
        //System.out.println("load "+fileName+" from "+root);
        String prefix = new File(fileName).getParent();
        if ( !prefix.endsWith("/") ) prefix += "/";
        try {
            String templateName = Misc.getFileNameNoSuffix(fileName);
            URL f = new URL(root+fileName);
            CharStream fs = new ANTLRInputStream(f.openStream());
            if ( ErrorManager.v3_mode) {
                String template = fs.toString(); // needs > ANTLR 3.2
                template = template.trim();
                String justName = new File(templateName).getName();
                defineTemplate(prefix,
                               new CommonToken(GroupParser.ID,justName),
                               null,
                               template);
            }
            else {
                GroupLexer lexer = new GroupLexer(fs);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                GroupParser parser = new GroupParser(tokens);
                parser.group = this;
                parser.templateDef(prefix);
            }
            return templates.get(templateName);
        }
        catch (Exception e) {
            ErrorManager.IOError(null, ErrorType.CANT_LOAD_TEMPLATE_FILE, e, root + fileName);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public String getName() { return groupDirName; }
}
