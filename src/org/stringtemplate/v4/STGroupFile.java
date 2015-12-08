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

import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.STException;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/** The internal representation of a single group file (which must end in
 *  ".stg").  If we fail to find a group file, look for it via the
 *  CLASSPATH as a resource.  Templates are only looked up in this file
 *  or an import.
 */
public class STGroupFile extends STGroup {
	/** Just records how user "spelled" the file name they wanted to load.
	 *  The url is the key field here for loading content.
	 *
	 *  If they use ctor with URL arg, this field is null.
	 */
    public String fileName;

	/** Where to find the group file. NonNull. */
    public URL url;

    protected boolean alreadyLoaded = false;

    /** Load a file relative to current directory or from root or via CLASSPATH. */
	public STGroupFile(String fileName) { this(fileName, '<', '>'); }

	public STGroupFile(String fileName, char delimiterStartChar, char delimiterStopChar) {
		super(delimiterStartChar, delimiterStopChar);
		if ( !fileName.endsWith(GROUP_FILE_EXTENSION) ) {
			throw new IllegalArgumentException("Group file names must end in .stg: "+fileName);
		}
		//try {
		File f = new File(fileName);
		if ( f.exists() ) {
			try {
				url = f.toURI().toURL();
			}
			catch (MalformedURLException e) {
				throw new STException("can't load group file "+fileName, e);
			}
			if ( verbose ) System.out.println("STGroupFile(" + fileName + ") == file "+f.getAbsolutePath());
		}
		else { // try in classpath
			url = getURL(fileName);
			if ( url==null ) {
				throw new IllegalArgumentException("No such group file: "+
													   fileName);
			}
			if ( verbose ) System.out.println("STGroupFile(" + fileName + ") == url "+url);
		}
		this.fileName = fileName;
	}

	public STGroupFile(String fullyQualifiedFileName, String encoding) {
        this(fullyQualifiedFileName, encoding, '<', '>');
    }

    public STGroupFile(String fullyQualifiedFileName, String encoding,
                       char delimiterStartChar, char delimiterStopChar)
    {
        this(fullyQualifiedFileName, delimiterStartChar, delimiterStopChar);
        this.encoding = encoding;
    }

	/** Pass in a URL with the location of a group file. E.g.,
	 * 	STGroup g = new STGroupFile(loader.getResource("org/foo/templates/g.stg"), "UTF-8", '<', '>');
	  */
	public STGroupFile(URL url, String encoding,
					   char delimiterStartChar, char delimiterStopChar)
	{
		super(delimiterStartChar, delimiterStopChar);
		this.url = url;
		this.encoding = encoding;
		this.fileName = null;
	}

	@Override
	public boolean isDictionary(String name) {
		if ( !alreadyLoaded ) load();
		return super.isDictionary(name);
	}

	@Override
    public boolean isDefined(String name) {
        if ( !alreadyLoaded ) load();
        return super.isDefined(name);
    }

	@Override
	public synchronized void unload() {
		super.unload();
		alreadyLoaded = false;
	}

	@Override
	protected CompiledST load(String name) {
        if ( !alreadyLoaded ) load();
        return rawGetTemplate(name);
    }

	@Override
    public void load() {
        if ( alreadyLoaded ) return;
        alreadyLoaded = true; // do before actual load to say we're doing it
		// no prefix since this group file is the entire group, nothing lives
		// beneath it.
		if ( verbose ) System.out.println("loading group file "+url.toString());
        loadGroupFile("/", url.toString());
		if ( verbose ) System.out.println("found "+templates.size()+" templates in "+url.toString()+" = "+templates.keySet());
    }

	@Override
    public String show() {
        if ( !alreadyLoaded ) load();
        return super.show();
    }

	@Override
    public String getName() { return Misc.getFileNameNoSuffix(getFileName()); }

	@Override
	public String getFileName() {
		if ( fileName!=null ) {
			return fileName;
		}
		return url.getFile();
	}

	@Override
	public URL getRootDirURL() {
//		System.out.println("url of "+fileName+" is "+url.toString());
		String parent = Misc.stripLastPathElement(url.toString());
		if ( parent.endsWith(".jar!") ) {
			parent = parent + "/."; // whooops. at the root so add "current dir" after jar spec
		}
		try {
			URL parentURL = new URL(parent);
//			System.out.println("parent URL "+parentURL.toString());
			return parentURL;
		}
		catch (MalformedURLException mue) {
			errMgr.runTimeError(null, null, ErrorType.INVALID_TEMPLATE_NAME,
								mue, parent);
		}
		return null;
	}
}
