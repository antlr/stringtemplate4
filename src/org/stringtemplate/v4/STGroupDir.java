/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.v4;

import org.antlr.runtime.ANTLRInputStream;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.STException;
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
 *  We load files on-demand. Dir search path: current working dir then
 *  CLASSPATH (as a resource).  Do not look for templates outside of this dir
 *  subtree (except via imports).
 */
public class STGroupDir extends STGroup {
    public String groupDirName;
    public URL root;

    public STGroupDir(String dirName) { this(dirName, '<', '>'); }

    public STGroupDir(String dirName, char delimiterStartChar, char delimiterStopChar) {
        super(delimiterStartChar, delimiterStopChar);
        this.groupDirName = dirName;
		File dir = new File(dirName);
		if ( dir.exists() && dir.isDirectory() ) {
			// we found the directory and it'll be file based
			try {
				root = dir.toURI().toURL();
			}
			catch (MalformedURLException e) {
				throw new STException("can't load dir "+dirName, e);
			}
//			System.out.println("found "+dirName+" at "+root);
		}
		else {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			root = cl.getResource(dirName);
			if ( root==null ) {
				cl = this.getClass().getClassLoader();
				root = cl.getResource(dirName);
			}
//			System.out.println("found "+dirName+" via CLASSPATH at "+root);
			if ( root==null ) {
				throw new IllegalArgumentException("No such directory: "+
													   dirName);
			}
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
	@Override
    protected CompiledST load(String name) {
//		System.out.println("load in groupdir: "+name);
        String parent = Misc.getPrefix(name);

        URL groupFileURL = null;
        try { // see if parent of template name is a group file
            groupFileURL = new URL(root+parent+".stg");
        }
        catch (MalformedURLException e) {
            errMgr.internalError(null, "bad URL: "+root+parent+".stg", e);
			return null;
        }
        InputStream is = null;
        try {
            is = groupFileURL.openStream();
        }
        catch (FileNotFoundException fnfe) {
            // must not be in a group file
            return loadTemplateFile(parent, name+".st"); // load t.st file
        }
        catch (IOException ioe) {
            errMgr.internalError(null, "can't load template file "+name, ioe);
        }
        try { // clean up
            if (is!=null ) is.close();
        }
        catch (IOException ioe) {
            errMgr.internalError(null, "can't close template file stream "+name, ioe);
        }
        loadGroupFile(parent, root+parent+".stg");
        return rawGetTemplate(name);
    }

	/** Load .st as relative file name relative to root by prefix */
	public CompiledST loadTemplateFile(String prefix, String fileName) {
//		System.out.println("load in groupdir "+fileName+" from "+root+" prefix="+prefix);
		URL f = null;
		try {
			f = new URL(root+"/"+fileName);
		}
		catch (MalformedURLException me) {
			errMgr.runTimeError(null, null, 0, ErrorType.INVALID_TEMPLATE_NAME,
								me, root + fileName);
			return null;
		}
		ANTLRInputStream fs;
		try {
			fs = new ANTLRInputStream(f.openStream(), encoding);
			fs.name = fileName;
		}
		catch (IOException ioe) {
			// doesn't exist
			//errMgr.IOError(null, ErrorType.NO_SUCH_TEMPLATE, ioe, fileName);
			return null;
		}
		return loadTemplateFile(prefix, fileName, fs);
	}

	public String getName() { return groupDirName; }
	public String getFileName() { return root.getFile(); }
	@Override
	public URL getRootDirURL() { return root; }
}
