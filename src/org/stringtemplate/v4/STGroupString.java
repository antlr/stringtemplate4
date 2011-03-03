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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.GroupLexer;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.misc.ErrorType;

import java.util.List;

/** A group derived from a string not a file or dir. */
public class STGroupString extends STGroup {
	public String sourceName;
	public String text;
	protected boolean alreadyLoaded = false;
	protected CommonTokenStream tokens; // keep around for tools that display ST files

	public STGroupString(String text) { this("<string>", text, '<', '>'); }

	public STGroupString(String sourceName, String text) { this(sourceName, text, '<', '>'); }

    public STGroupString(String sourceName, String text, char delimiterStartChar, char delimiterStopChar) {
        super(delimiterStartChar, delimiterStopChar);
		this.sourceName = sourceName;
		this.text = text;
    }

    public boolean isDefined(String name) {
		if ( !alreadyLoaded ) load();
        return super.isDefined(name);
    }

	protected CompiledST load(String name) {
		if ( !alreadyLoaded ) load();
        return rawGetTemplate(name);
    }

    public void load() {
		if (alreadyLoaded) return;
		GroupParser parser = null;
		try {
			ANTLRStringStream fs = new ANTLRStringStream(text);
			fs.name = sourceName;
			GroupLexer lexer = new GroupLexer(fs);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new GroupParser(tokens);
			// no prefix since this group file is the entire group, nothing lives
			// beneath it.
			parser.group(this, "");
		}
		catch (Exception e) {
			errMgr.IOError(null, ErrorType.CANT_LOAD_GROUP_FILE, e, "<string>");
		}
	}

	public List<Token> getTokens() {
		return tokens.getTokens();
	}

	public String getFileName() { return "<string>"; }
}
