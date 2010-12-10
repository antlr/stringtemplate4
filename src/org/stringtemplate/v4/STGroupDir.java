/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.v4;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.GroupLexer;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

// TODO: caching?

/** A directory or directory tree full of templates and/or group files.
 *  We load files on-demand. If we fail to find a file, we look for it via
 *  the CLASSPATH as a resource.  I track everything with URLs not file names.
 */
public class STGroupDir extends STGroup {
    public String groupDirName;
    public URL root;

    public STGroupDir(String dirName) { this(dirName, '<', '>'); }

    public STGroupDir(String dirName, char delimiterStartChar, char delimiterStopChar) {
        super(delimiterStartChar, delimiterStopChar);
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
    }

    public STGroupDir(String dirName, String encoding) {
        this(dirName, encoding, '<', '>');
    }

    public STGroupDir(String dirName, String encoding,
                      char delimiterStartChar, char delimiterStopChar)
    {
        this(dirName, delimiterStartChar, delimiterStopChar);
        this.encoding = encoding;
    }

	public STGroupDir(URL root, String encoding,
					  char delimiterStartChar, char delimiterStopChar)
	{
		super(delimiterStartChar, delimiterStopChar);
		this.root = root;
		this.encoding = encoding;
	}

    /** Load a template from dir or group file.  Group file is given
     *  precedence over dir with same name.
     */
    protected CompiledST load(String name) {
        String parent = Misc.getUnixStyleParent(name);
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
		String prefix = Misc.getUnixStyleParent(fileName);
		if ( !prefix.endsWith("/") ) prefix += "/";
		String templateName = Misc.getFileNameNoSuffix(fileName);
		URL f = null;
		try {
			f = new URL(root+fileName);
		}
		catch (MalformedURLException me) {
			ErrorManager.runTimeError(null, 0, ErrorType.INVALID_TEMPLATE_NAME,
									 me, Misc.getFileName(f.getFile()));
			return null;
		}
		ANTLRInputStream fs = null;
		try {
			fs = new ANTLRInputStream(f.openStream(), encoding);
		}
		catch (IOException ioe) {
			// doesn't exist; just return null to say not found
			return null;
		}
		if ( ErrorManager.v3_mode ) {
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
			fs.name = fileName;
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			GroupParser parser = new GroupParser(tokens);
			parser.group = this;
			try {
				parser.templateDef(prefix);
			}
			catch (RecognitionException re) {
				ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR,
										 Misc.getFileName(f.getFile()),
										 re, re.getMessage());
			}
		}
		return templates.get(templateName);
	}

	public String getName() { return groupDirName; }
}
