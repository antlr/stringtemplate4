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
package org.stringtemplate.v4.compiler;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.ErrorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A compiler for a single template. */
public class Compiler {
	public static final String SUBTEMPLATE_PREFIX = "_sub";

    public static final int TEMPLATE_INITIAL_CODE_SIZE = 15;

    public static final Map<String, Interpreter.Option> supportedOptions =
        new HashMap<String, Interpreter.Option>() {
            {
                put("anchor",       Interpreter.Option.ANCHOR);
                put("format",       Interpreter.Option.FORMAT);
                put("null",         Interpreter.Option.NULL);
                put("separator",    Interpreter.Option.SEPARATOR);
                put("wrap",         Interpreter.Option.WRAP);
            }
        };

    public static final int NUM_OPTIONS = supportedOptions.size();

    public static final Map<String,String> defaultOptionValues =
        new HashMap<String,String>() {
            {
                put("anchor", "true");
                put("wrap",   "\n");
            }
        };

    public static Map<String, Short> funcs = new HashMap<String, Short>() {
        {
            put("first", Bytecode.INSTR_FIRST);
            put("last", Bytecode.INSTR_LAST);
            put("rest", Bytecode.INSTR_REST);
            put("trunc", Bytecode.INSTR_TRUNC);
            put("strip", Bytecode.INSTR_STRIP);
            put("trim", Bytecode.INSTR_TRIM);
            put("length", Bytecode.INSTR_LENGTH);
            put("strlen", Bytecode.INSTR_STRLEN);
            put("reverse", Bytecode.INSTR_REVERSE);
        }
    };

	/** Name subtemplates {@code _sub1}, {@code _sub2}, ... */
	public static int subtemplateCount = 0;

	public STGroup group;

	public Compiler() { this(STGroup.defaultGroup); }
	public Compiler(STGroup group) { this.group = group; }

	public CompiledST compile(String template) {
		CompiledST code = compile(null, null, null, template, null);
		code.hasFormalArgs = false;
		return code;
	}

	/** Compile full template with unknown formal arguments. */
	public CompiledST compile(String name, String template) {
		CompiledST code = compile(null, name, null, template, null);
		code.hasFormalArgs = false;
		return code;
	}

	/** Compile full template with respect to a list of formal arguments. */
	public CompiledST compile(String srcName,
							  String name,
							  List<FormalArgument> args,
							  String template,
							  Token templateToken)
	{
		ANTLRStringStream is = new ANTLRStringStream(template);
		is.name = srcName!=null ? srcName : name;
		STLexer lexer;
		if ( templateToken!=null &&
			 templateToken.getType() == GroupParser.BIGSTRING_NO_NL )
		{
			lexer = new STLexer(group.errMgr, is, templateToken,
						group.delimiterStartChar, group.delimiterStopChar) {
				/** Throw out \n and indentation tokens inside BIGSTRING_NO_NL */
				@Override
				public Token nextToken() {
					Token t = super.nextToken();
					while ( t.getType() == STLexer.NEWLINE ||
						    t.getType()==STLexer.INDENT )
					{
						t = super.nextToken();
					}
					return t;
				}
			};
		}
		else {
			lexer = new STLexer(group.errMgr, is, templateToken,
								group.delimiterStartChar, group.delimiterStopChar);
		}
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		STParser p = new STParser(tokens, group.errMgr, templateToken);
		STParser.templateAndEOF_return r;
		try {
			r = p.templateAndEOF();
		}
		catch (RecognitionException re) {
			reportMessageAndThrowSTException(tokens, templateToken, p, re);
			return null;
		}
		if ( p.getNumberOfSyntaxErrors()>0 || r.getTree()==null ) {
			CompiledST impl = new CompiledST();
			impl.defineFormalArgs(args);
			return impl;
		}

		//System.out.println(((CommonTree)r.getTree()).toStringTree());
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(r.getTree());
		nodes.setTokenStream(tokens);
		CodeGenerator gen = new CodeGenerator(nodes, group.errMgr, name, template, templateToken);

		CompiledST impl=null;
		try {
			impl = gen.template(name,args);
			impl.nativeGroup = group;
			impl.template = template;
			impl.ast = r.getTree();
			impl.ast.setUnknownTokenBoundaries();
			impl.tokens = tokens;
		}
		catch (RecognitionException re) {
			group.errMgr.internalError(null, "bad tree structure", re);
		}

		return impl;
	}

	public static CompiledST defineBlankRegion(CompiledST outermostImpl, Token nameToken) {
		String outermostTemplateName = outermostImpl.name;
		String mangled = STGroup.getMangledRegionName(outermostTemplateName, nameToken.getText());
		CompiledST blank = new CompiledST();
		blank.isRegion = true;
		blank.templateDefStartToken = nameToken;
		blank.regionDefType = ST.RegionType.IMPLICIT;
		blank.name = mangled;
		outermostImpl.addImplicitlyDefinedTemplate(blank);
		return blank;
	}

	public static String getNewSubtemplateName() {
		subtemplateCount++;
		return SUBTEMPLATE_PREFIX+subtemplateCount;
	}

	protected void reportMessageAndThrowSTException(TokenStream tokens, Token templateToken,
													Parser parser, RecognitionException re)
	{
		if ( re.token.getType() == STLexer.EOF_TYPE ) {
			String msg = "premature EOF";
			group.errMgr.compileTimeError(ErrorType.SYNTAX_ERROR, templateToken, re.token, msg);
		}
		else if ( re instanceof NoViableAltException) {
			String msg = "'"+re.token.getText()+"' came as a complete surprise to me";
			group.errMgr.compileTimeError(ErrorType.SYNTAX_ERROR, templateToken, re.token, msg);
		}
		else if ( tokens.index() == 0 ) { // couldn't parse anything
			String msg = "this doesn't look like a template: \""+tokens+"\"";
			group.errMgr.compileTimeError(ErrorType.SYNTAX_ERROR, templateToken, re.token, msg);
		}
		else if ( tokens.LA(1) == STLexer.LDELIM ) { // couldn't parse expr
			String msg = "doesn't look like an expression";
			group.errMgr.compileTimeError(ErrorType.SYNTAX_ERROR, templateToken, re.token, msg);
		}
		else {
			String msg = parser.getErrorMessage(re, parser.getTokenNames());
			group.errMgr.compileTimeError(ErrorType.SYNTAX_ERROR, templateToken, re.token, msg);
		}
		throw new STException(); // we have reported the error, so just blast out
	}

}
