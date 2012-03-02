package org.stringtemplate.v4;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.stringtemplate.v4.compiler.*;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.misc.Misc;

import java.net.URL;

/** A directory of templates without headers like ST v3 had.  Still allows group
 *  files in directory though like {@link STGroupDir} parent.
 */
public class STRawGroupDir extends STGroupDir {
	public STRawGroupDir(String dirName) {
		super(dirName);
	}

	public STRawGroupDir(String dirName, char delimiterStartChar, char delimiterStopChar) {
		super(dirName, delimiterStartChar, delimiterStopChar);
	}

	public STRawGroupDir(String dirName, String encoding) {
		super(dirName, encoding);
	}

	public STRawGroupDir(String dirName, String encoding, char delimiterStartChar, char delimiterStopChar) {
		super(dirName, encoding, delimiterStartChar, delimiterStopChar);
	}

	public STRawGroupDir(URL root, String encoding, char delimiterStartChar, char delimiterStopChar) {
		super(root, encoding, delimiterStartChar, delimiterStopChar);
	}

	@Override
	public CompiledST loadTemplateFile(String prefix, String unqualifiedFileName,
									   CharStream templateStream)
	{
		String template = templateStream.substring(0, templateStream.size() - 1);
		String templateName = Misc.getFileNameNoSuffix(unqualifiedFileName);
		String fullyQualifiedTemplateName = prefix + templateName;
		CompiledST impl = new Compiler(this).compile(fullyQualifiedTemplateName, template);
		CommonToken nameT = new CommonToken(STLexer.SEMI); // Seems like a hack, best I could come up with.
		nameT.setInputStream(templateStream);
		rawDefineTemplate(fullyQualifiedTemplateName, impl, nameT);
		impl.defineImplicitlyDefinedTemplates(this);
		return impl;
	}
}
