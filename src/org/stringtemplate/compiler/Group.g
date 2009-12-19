/*
 [The "BSD licence"]
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
grammar Group;

@header {
package org.stringtemplate.compiler;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.stringtemplate.misc.*;
import org.stringtemplate.*;
import java.io.File;
}

@lexer::header {
package org.stringtemplate.compiler;
import org.stringtemplate.*;
import org.stringtemplate.misc.*;
import java.io.File;
}

@members {
public STGroup group;

public void displayRecognitionError(String[] tokenNames,
                                    RecognitionException e)
{
    String msg = getErrorMessage(e, tokenNames);
    ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
}
public String getSourceName() {
    String fullFileName = super.getSourceName();    
    File f = new File(fullFileName); // strip to simple name
    return f.getName();
}
public void error(String msg) {
    NoViableAltException e = new NoViableAltException("", 0, 0, input);
    ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
    recover(input, null);
}
}

@lexer::members {
protected STGroup group;

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
    ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
}
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
	:	def[prefix]+
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
    String template=null, fullName=null;
    int n=0; // num char to strip from left, right of template def
}
	:	(	'@' enclosing=ID '.' name=ID '(' ')'
			{fullName = STGroup.getMangledRegionName($enclosing.text, $name.text);}
		|	name=ID '(' formalArgs? ')' {fullName = $name.text;}
		)
	    '::='
	    {Token templateToken = input.LT(1);}
	    (	STRING     {template=$STRING.text; n=1;}
	    |	BIGSTRING  {template=$BIGSTRING.text; n=2;}
	    |	{
	    	template = "";
	    	String msg = "missing template at '"+input.LT(1).getText()+"'";
            NoViableAltException e = new NoViableAltException("", 0, 0, input);
    	    ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
    	    }
	    )
	    {
        template = Misc.strip(template, n);
	    group.defineTemplateOrRegion(templateToken, template, prefix, $enclosing.text,
	                                 $name, $formalArgs.args);
	    }
	|   alias=ID '::=' target=ID  {group.defineTemplateAlias($alias, $target);}
	;
		
formalArgs returns[LinkedHashMap<String,FormalArgument> args]
@init {$args = new LinkedHashMap<String,FormalArgument>();}
    :	formalArg[$args] ( ',' formalArg[$args] )*
	;

formalArg[LinkedHashMap<String,FormalArgument> args]
	:	ID
		(	'=' a=STRING			
		|	'=' a=ANONYMOUS_TEMPLATE
		)?
		{$args.put($ID.text, new FormalArgument($ID.text, $a));}
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
			ErrorManager.compileTimeError(ErrorType.MAP_REDEFINITION, $ID);
        }
        else if ( group.rawGetTemplate($ID.text)!=null ) {
			ErrorManager.compileTimeError(ErrorType.TEMPLATE_REDEFINITION_AS_MAP, $ID);
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
	:	BIGSTRING			{$value = new ST(Misc.strip($BIGSTRING.text,2));}
	|	ANONYMOUS_TEMPLATE	{$value = new ST(Misc.strip($ANONYMOUS_TEMPLATE.text,1));}
	|	STRING				{$value = Misc.replaceEscapes(Misc.strip($STRING.text, 1));}
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
			ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
			}
			'\n'
		|	~('\\'|'"'|'\n')
		)*
		'"'
	;

BIGSTRING
	:	'<<'
		(	options {greedy=false;}
		:	'\\' '>'  // \> escape
		|	'\\' ~'>'
		|	~'\\'
		)*
        '>>'
        {
        String txt = getText().replaceAll("\\\\>",">");;
		setText(txt);
		}
	;

ANONYMOUS_TEMPLATE
    :	'{'
    	{
		STLexer lexer =
			new STLexer(input, group.delimiterStartChar, group.delimiterStopChar);
		lexer.subtemplateDepth = 1;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
        STParser parser = new STParser(tokens, (Compiler)null, null);
		parser.template();
		}
    	// don't match '}' here; subparser matches it to terminate
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {skip();}
    ;

LINE_COMMENT
    :	'//' ~('\n'|'\r')* '\r'? '\n' {skip();}
    ;

WS  :	(' '|'\r'|'\t'|'\n') {skip();} ;
