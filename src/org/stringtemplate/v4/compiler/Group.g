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
grammar Group;

options {
	language=Java;
}

tokens {
	TRUE='true'; FALSE='false'; LBRACK='['; RBRACK=']';
}

@header {
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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.stringtemplate.v4.misc.*;
import org.stringtemplate.v4.*;
import java.io.File;
}

@lexer::header {
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
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;
import java.io.File;
}

@members {
public STGroup group;

@Override
public void displayRecognitionError(String[] tokenNames,
                                    RecognitionException e)
{
    String msg = getErrorMessage(e, tokenNames);
    group.errMgr.groupSyntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
}
@Override
public String getSourceName() {
    String fullFileName = super.getSourceName();
    File f = new File(fullFileName); // strip to simple name
    return f.getName();
}
public void error(String msg) {
    NoViableAltException e = new NoViableAltException("", 0, 0, input);
    group.errMgr.groupSyntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
    recover(input, null);
}
}

@lexer::members {
public STGroup group;

@Override
public void reportError(RecognitionException e) {
    String msg = null;
    if ( e instanceof NoViableAltException ) {
        msg = "invalid character '"+(char)input.LA(1)+"'";
    }
    else if ( e instanceof MismatchedTokenException && ((MismatchedTokenException)e).expecting=='"' ) {
        msg = "unterminated string";
    }
    else {
        msg = getErrorMessage(e, getTokenNames());
    }
    group.errMgr.groupSyntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
}
@Override
public String getSourceName() {
    String fullFileName = super.getSourceName();
    File f = new File(fullFileName); // strip to simple name
    return f.getName();
}
}

group[STGroup group, String prefix]
@init {
GroupLexer lexer = (GroupLexer)input.getTokenSource();
this.group = lexer.group = $group;
}
	:	oldStyleHeader?
		delimiters?
	    (	'import' STRING {group.importTemplates($STRING);}
		|	'import' // common error: name not in string
			{
			MismatchedTokenException e = new MismatchedTokenException(STRING, input);
			reportError(e);
			}
			ID ('.' ID)* // might be a.b.c.d
		)*
		def[prefix]*
		EOF
	;

oldStyleHeader // ignore but lets us use this parser in AW for both v3 and v4
    :   'group' ID ( ':' ID )?
	    ( 'implements' ID (',' ID)* )?
	    ';'
	;

groupName returns [String name]
@init {StringBuilder buf = new StringBuilder();}
	:	a=ID {buf.append($a.text);} ('.' a=ID {buf.append($a.text);})*
	;

delimiters
    :	'delimiters' a=STRING ',' b=STRING
     	{
     	group.delimiterStartChar=$a.getText().charAt(1);
        group.delimiterStopChar=$b.getText().charAt(1);
        }
    ;

/** Match template and dictionary defs outside of (...)+ loop in group.
 *  The key is catching while still in the loop; must keep prediction of
 *  elements separate from "stay in loop" prediction.
 */
def[String prefix] : templateDef[prefix] | dictDef ;
	catch[RecognitionException re] {
		// pretend we already saw an error here
		state.lastErrorIndex = input.index();
		error("garbled template definition starting at '"+input.LT(1).getText()+"'");
	}

templateDef[String prefix]
@init {
    String template=null;
    int n=0; // num char to strip from left, right of template def
}
	:	(	'@' enclosing=ID '.' name=ID '(' ')'
		|	name=ID '(' formalArgs ')'
		)
	    '::='
	    {Token templateToken = input.LT(1);}
	    (	STRING     {template=$STRING.text; n=1;}
	    |	BIGSTRING  {template=$BIGSTRING.text; n=2;}
	    |	BIGSTRING_NO_NL  {template=$BIGSTRING_NO_NL.text; n=2;}
	    |	{
	    	template = "";
	    	String msg = "missing template at '"+input.LT(1).getText()+"'";
            NoViableAltException e = new NoViableAltException("", 0, 0, input);
    	    group.errMgr.groupSyntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
    	    }
	    )
	    {
	    if ( $name.index >= 0 ) { // if ID missing
			template = Misc.strip(template, n);
			String templateName = $name.text;
			if ( prefix.length()>0 ) templateName = prefix+$name.text;
			String enclosingTemplateName = $enclosing.text;
			if (enclosingTemplateName != null && enclosingTemplateName.length()>0 && prefix.length()>0) {
				enclosingTemplateName = prefix + enclosingTemplateName;
			}
			group.defineTemplateOrRegion(templateName, enclosingTemplateName, templateToken,
										 template, $name, $formalArgs.args);
		}
	    }
	|   alias=ID '::=' target=ID  {group.defineTemplateAlias($alias, $target);}
	;

formalArgs returns[List<FormalArgument> args = new ArrayList<FormalArgument>()]
scope {
	boolean hasOptionalParameter;
}
@init { $formalArgs::hasOptionalParameter = false; }
	:	formalArg[$args] (',' formalArg[$args])*
	|
	;

formalArg[List<FormalArgument> args]
	:	ID
		(	'=' a=(STRING|ANONYMOUS_TEMPLATE|'true'|'false') {$formalArgs::hasOptionalParameter = true;}
		|	'=' a='[' ']' {$formalArgs::hasOptionalParameter = true;}
		|	{
			if ($formalArgs::hasOptionalParameter) {
				group.errMgr.compileTimeError(ErrorType.REQUIRED_PARAMETER_AFTER_OPTIONAL,
				 							  null, $ID);
			}
			}
		)
		{$args.add(new FormalArgument($ID.text, $a));}
    ;

/*
suffix returns [int cardinality=FormalArgument.REQUIRED]
    :   OPTIONAL
    |   STAR
    |   PLUS
	|
    ;
        */

dictDef
	:	ID '::=' dict
        {
        if ( group.rawGetDictionary($ID.text)!=null ) {
			group.errMgr.compileTimeError(ErrorType.MAP_REDEFINITION, null, $ID);
        }
        else if ( group.rawGetTemplate($ID.text)!=null ) {
			group.errMgr.compileTimeError(ErrorType.TEMPLATE_REDEFINITION_AS_MAP, null, $ID);
        }
        else {
            group.defineDictionary($ID.text, $dict.mapping);
        }
        }
	;

dict returns [Map<String,Object> mapping]
@init {mapping=new HashMap<String,Object>();}
	:   '[' dictPairs[mapping] ']'
	;

dictPairs[Map<String,Object> mapping]
    :	keyValuePair[mapping]
    	(',' keyValuePair[mapping])* (',' defaultValuePair[mapping])?
    |	defaultValuePair[mapping]
    ;
 	catch[RecognitionException re] {
		error("missing dictionary entry at '"+input.LT(1).getText()+"'");
	}

defaultValuePair[Map<String,Object> mapping]
	:	'default' ':' keyValue {mapping.put(STGroup.DEFAULT_KEY, $keyValue.value);}
	;

keyValuePair[Map<String,Object> mapping]
	:	STRING ':' keyValue {mapping.put(Misc.replaceEscapes(Misc.strip($STRING.text, 1)), $keyValue.value);}
	;

keyValue returns [Object value]
	:	BIGSTRING			{$value = group.createSingleton($BIGSTRING);}
	|	BIGSTRING_NO_NL		{$value = group.createSingleton($BIGSTRING_NO_NL);}
	|	ANONYMOUS_TEMPLATE	{$value = group.createSingleton($ANONYMOUS_TEMPLATE);}
	|	STRING				{$value = Misc.replaceEscapes(Misc.strip($STRING.text, 1));}
	|	TRUE				{$value = true;}
	|	FALSE				{$value = false;}
	|	'[' ']'				{$value = Collections.emptyList();}
	|	{input.LT(1).getText().equals("key")}?=> ID
							{$value = STGroup.DICT_KEY;}
	;
 	catch[RecognitionException re] {
		error("missing value for key at '"+input.LT(1).getText()+"'");
	}

ID	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')*
	;

STRING
	:	'"'
		(	'\\' '"'
		|	'\\' ~'"'
		|	{
			String msg = "\\n in string";
    		NoViableAltException e = new NoViableAltException("", 0, 0, input);
			group.errMgr.groupLexerError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
			}
			'\n'
		|	~('\\'|'"'|'\n')
		)*
		'"'
        {
        String txt = getText().replaceAll("\\\\\"","\"");
		setText(txt);
		}
	;

BIGSTRING_NO_NL // same as BIGSTRING but means ignore newlines later
	:	'<%' ( . )* '%>'
        // %\> is the escape to avoid end of string
        {
        String txt = getText().replaceAll("\%\\\\>","\%>");
		setText(txt);
		}
	;

/** Match <<...>> but also allow <<..<x>>> so we can have tag on end.
    Escapes: >\> means >> inside of <<...>>.
    Escapes: \>> means >> inside of <<...>> unless at end like <<...\>>>>.
    In that case, use <%..>>%> instead.
 */
BIGSTRING
	:	'<<'
		(	options {greedy=false;}
		:	'\\' '>'  // \> escape
		|	'\\' ~'>' // allow this but don't collapse in action
		|	~'\\'
		)*
        '>>'
        {
        String txt = getText();
        txt = Misc.replaceEscapedRightAngle(txt); // replace \> with > unless <\\>
		setText(txt);
		}
	;

ANONYMOUS_TEMPLATE
    :	'{'
    	{
		Token templateToken = new CommonToken(input, ANONYMOUS_TEMPLATE, 0, getCharIndex(), getCharIndex());
		STLexer lexer =
			new STLexer(group.errMgr, input, templateToken, group.delimiterStartChar, group.delimiterStopChar);
		lexer.subtemplateDepth = 1;
		Token t = lexer.nextToken();
		while ( lexer.subtemplateDepth>=1 || t.getType()!=STLexer.RCURLY ) {
			if ( t.getType()==STLexer.EOF_TYPE ) {
            	MismatchedTokenException e = new MismatchedTokenException('}', input);
				String msg = "missing final '}' in {...} anonymous template";
    			group.errMgr.groupLexerError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
				break;
			}
			t = lexer.nextToken();
		}
		}
    	// don't match '}' here; our little {...} scanner loop matches it
    	// to terminate.
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {skip();}
    ;

LINE_COMMENT
    :	'//' ~('\n'|'\r')* '\r'? '\n' {skip();}
    ;

WS  :	(' '|'\r'|'\t'|'\n') {skip();} ;
