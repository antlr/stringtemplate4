/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
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
    language=ObjC;
    tokenVocab=Group1;
}

tokens { ID; WS; STRING; ANONYMOUS_TEMPLATE; COMMENT; LINE_COMMENT; BIGSTRING; BIGSTRING_NO_NL;
            T_TRUE; T_FALSE; }

@header {
/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
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
#import "STGroup.h"
#import "ErrorType.h"
#import "STLexer.h"
#import "Misc.h"
#import "GroupLexer.h"
#import "FormalArgument.h"
#import "ACNumber.h"
}

@lexer::header {
/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
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
#import <ANTLR/ANTLR.h>
#import "STGroup.h"
#import "ErrorType.h"
#import "STLexer.h"

@class STGroup;
}

@memVars {
STGroup *group;
}

@properties {
@property (retain) STGroup *group;
}

@methodsDecl {
+ (NSInteger) TANONYMOUS_TEMPLATE;
+ (NSInteger) TBIGSTRING;
+ (NSInteger) TBIGSTRING_NO_NL;
+ (NSInteger) TID;
+ (NSInteger) TTRUE;
- (void) displayRecognitionError:(AMutableArray *) tokenNames e:(RecognitionException *)e;
- (NSString *) getSourceName;
- (void) error:(NSString *)msg;
- (NSString *) getErrorMessage:(NSException *)e TokenNames:(AMutableArray *)TokenNames;
}

@synthesize {
@synthesize group;
}

@methods {
+ (NSInteger) TANONYMOUS_TEMPLATE { return ANONYMOUS_TEMPLATE; }
+ (NSInteger) TBIGSTRING { return BIGSTRING; }
+ (NSInteger) TBIGSTRING_NO_NL { return BIGSTRING_NO_NL; }
+ (NSInteger) TID { return ID; }
+ (NSInteger) TTRUE { return T_TRUE; }
- (void) displayRecognitionError:(AMutableArray *) tokenNames e:(RecognitionException *)e
{
    NSString *msg = [self getErrorMessage:e TokenNames:[self getTokenNames]];
    [group.errMgr groupSyntaxError:SYNTAX_ERROR srcName:[self getSourceName] e:e msg:msg];
}

- (NSString *) getSourceName
{
#ifdef DONTUSENOMO
    NSError **outError;
    NSString *fullFileName = [super getSourceName];
    NSFileWrapper *f = [[NSFileWrapper alloc] initWithURL:fullFileName options:NSFileWrapperReadingImmediate error:outError]; // strip to simple name
    return [f filename];
#endif
    return [super getSourceName];
}

- (void) error:(NSString *)msg
{
    NoViableAltException *nvae = [NoViableAltException newException:0 state:0 stream:input];
    [group.errMgr groupSyntaxError:SYNTAX_ERROR srcName:[self getSourceName] e:nvae msg:msg];
    [self recover:input Exception:nil];
}

- (NSString *) getErrorMessage:(NSException *)e TokenNames:(AMutableArray *)TokenNames
{
    return [NSString stringWithFormat:@"\%@--\%@", e.name, e.reason];
}

}

@lexer::memVars {
STGroup *group;
}

@lexer::methodsDecl {
@property (retain, getter=getGroup, setter=setGroup:) STGroup *group;

- (void) reportError:(RecognitionException *)e;
- (NSString *) getSourceName;
}

@lexer::methods {
@synthesize group;

- (void) reportError:(RecognitionException *)e
{
    NSString *msg = nil;
    if ( [e isKindOfClass:[NoViableAltException class]] ) {
#pragma error fix formatting
        msg = [NSString stringWithFormat:@"invalid character '\%C'", [input LA:1]];
    }
    else if ( [e isKindOfClass:[MismatchedTokenException class]] && ((MismatchedTokenException *)e).expecting=='"' ) {
        msg = @"unterminated string";
    }
    else {
        msg = [self getErrorMessage:e TokenNames:[self getTokenNames]];
    }
    [group.errMgr groupSyntaxError:SYNTAX_ERROR srcName:[self getSourceName] e:e msg:msg];
}

- (NSString *) getSourceName
{
    return [super getSourceName];
}

}

group[STGroup *aGroup, NSString *prefix]
@init {
GroupLexer *lexer = (GroupLexer *)[input getTokenSource];
self.group = lexer.group = $aGroup;
}
    :   oldStyleHeader?
		delimiters?
    (   'import' STRING {[aGroup importTemplatesWithFileName:$STRING];}
    |   'import' // common error: name not in string
            {
            MismatchedTokenException *mte = [MismatchedTokenException newException:STRING Stream:input];
            [self reportError:mte];
            }
            ID ('.' ID)* // might be a.b.c.d
        )*
        def[prefix]+
    ;

oldStyleHeader // ignore but lets us use this parser in AW for both v3 and v4
    :   'group' ID ( ':' ID )?
        ( 'implements' ID (',' ID)* )?
        ';'
    ;

groupName returns [NSString *name]
@init {NSMutableString *buf = [NSMutableString stringWithCapacity:16];}
    :   a=ID {[buf appendString:$a.text];} ('.' a=ID {[buf appendString:$a.text];})*
    ;

delimiters
    :	'delimiters' a=STRING ',' b=STRING
     	{
     	group.delimiterStartChar=[$a.text characterAtIndex:0];
        group.delimiterStopChar=[$b.text characterAtIndex:0];
        }
    ;

/** Match template and dictionary defs outside of (...)+ loop in group.
 *  The key is catching while still in the loop; must keep prediction of
 *  elements separate from "stay in loop" prediction.
 */
def[NSString *prefix] : templateDef[prefix] | dictDef ;
    catch[RecognitionException *re] {
        // pretend we already saw an error here
        state.lastErrorIndex = input.index;
        [self error:[NSString stringWithFormat:@"garbled template definition starting at '\%@'", [[input LT:1] text]]];
    }

templateDef[NSString *prefix]
@init {
    NSString *template=nil;
    NSInteger n=0; // num char to strip from left, right of template def
}
    :   (   '@' enclosing=ID '.' name=ID '(' ')'
        |   name=ID '(' formalArgs ')'
        )
        '::='
        {CommonToken *templateToken = [input LT:1];}
        (   STRING     {template=$STRING.text; n=1;}
        |   BIGSTRING  {template=$BIGSTRING.text; n=2;}
        |   BIGSTRING_NO_NL  {template=$BIGSTRING_NO_NL.text; n=2;}
        |   {
            template = @"";
            NSString *msg = [NSString stringWithFormat:@"missing template at '\%@'", [[input LT:1] text]];
            NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
            [group.errMgr groupSyntaxError:SYNTAX_ERROR srcName:[self getSourceName] e:e msg:msg];
            }
        )
        {
        if ( $name.index >= 0 ) { // if ID missing
            template = [Misc strip:template n:n];
            NSString *templateName = $name.text;
            if ( [prefix length] > 0 ) templateName = [NSString stringWithFormat: @"\%@\%@", prefix, $name.text];
            [group defineTemplateOrRegion:templateName
            regionSurroundingTemplateName:$enclosing.text
                            templateToken:templateToken
                                 template:template
                                nameToken:$name
                                     args:$formalArgs.args];
        }
        }
    |   alias=ID '::=' target=ID  {[group defineTemplateAlias:$alias targetT:$target];}
    ;

formalArgs returns[AMutableArray *args = [AMutableArray arrayWithCapacity:5\]]
scope {
    BOOL hasOptionalParameter;
}
@init { $formalArgs::hasOptionalParameter = NO;}
    :   formalArg[$args] (',' formalArg[$args])*
    |
    ;

formalArg[AMutableArray *args]
    :   ID
        (   '=' a=(STRING|ANONYMOUS_TEMPLATE|T_TRUE|T_FALSE) {$formalArgs::hasOptionalParameter = YES;}
        |   {
            if ( $formalArgs::hasOptionalParameter ) {
                [group.errMgr compileTimeError:REQUIRED_PARAMETER_AFTER_OPTIONAL templateToken:nil t:$ID];
            }
            }
        )
        {[$args addObject:[[FormalArgument newFormalArgument:$ID.text token:$a] retain]];}
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
    :   ID '::=' dict
        {
        if ( [group rawGetDictionary:$ID.text] != nil ) {
            [group.errMgr compileTimeError:MAP_REDEFINITION templateToken:nil t:$ID];
        }
        else if ( [group rawGetTemplate:$ID.text] != nil ) {
            [group.errMgr compileTimeError:TEMPLATE_REDEFINITION_AS_MAP templateToken:nil t:$ID];
        }
        else {
            [group defineDictionary:$ID.text mapping:$dict.mapping];
        }
        }
    ;

dict returns [AMutableDictionary *mapping]
@init {mapping=[AMutableDictionary dictionaryWithCapacity:16];}
    :   '[' dictPairs[mapping] ']'
    ;

dictPairs[AMutableDictionary *mapping]
    :   keyValuePair[mapping]
        (',' keyValuePair[mapping])* (',' defaultValuePair[mapping])?
    |   defaultValuePair[mapping]
    ;
    catch[RecognitionException *re] {
        [self error:[NSString stringWithFormat:@"missing dictionary entry at '\%@'", [input LT:1].text]];
    }

defaultValuePair[AMutableDictionary *mapping]
    :   'default' ':' keyValue {[mapping setObject:$keyValue.value forKey:STGroup.DEFAULT_KEY];}
    ;

keyValuePair[AMutableDictionary *mapping]
    :   STRING ':' keyValue {[mapping setObject:$keyValue.value forKey:[Misc replaceEscapes:[Misc strip:$STRING.text n:1]]];}
    ;

keyValue returns [id value]
    :   BIGSTRING           {$value = [group createSingleton:$BIGSTRING];}
    |   BIGSTRING_NO_NL     {$value = [group createSingleton:$BIGSTRING_NO_NL];}
    |   ANONYMOUS_TEMPLATE  {$value = [group createSingleton:$ANONYMOUS_TEMPLATE];}
    |   STRING              {$value = [Misc replaceEscapes:[Misc strip:$STRING.text n:1]];}
    |   T_TRUE              {$value = [ACNumber numberWithBool:YES];}
    |   T_FALSE             {$value = [ACNumber numberWithBool:NO];}
    |   {[[[input LT:1] text] isEqualToString:@"key"]}?=> ID
                            {$value = STGroup.DICT_KEY;}
    ;
    catch[RecognitionException *re] {
        [self error:[NSString stringWithFormat:@"missing value for key at '\%@'", [[input LT:1] text]]];
    }

T_TRUE : 'true' ;

T_FALSE : 'false' ;

ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')*
    ;

STRING
    :   '"'
        (   '\\' '"'
        |   '\\' ~'"'
        |   {
            NSString *msg = @"\\n in string";
            NoViableAltException *e = [NoViableAltException newException:0 state:0 stream:input];
            [group.errMgr groupLexerError:SYNTAX_ERROR srcName:[self getSourceName] e:e msg:msg];
            }
            '\n'
        |   ~('\\'|'"'|'\n')
        )*
        '"'
        {
        NSString *txt = [self.text  stringByReplacingOccurrencesOfString:@"\\\\\"" withString:@"\""];
        [self setText:txt];
        }
    ;

BIGSTRING_NO_NL // same as BIGSTRING but means ignore newlines later
    :   '<%' (options {greedy=false;} : .)* '%>'
    ;

BIGSTRING
    :   '<<'
        (   options {greedy=false;}
        :   '\\' '>'  // \> escape
        |   '\\' ~'>'
        |   ~'\\'
        )*
        '>>'
        {
        NSString *txt = [self.text stringByReplacingOccurrencesOfString:@"\\\\>" withString:@">"];
        [self setText:txt];
        }
    ;

ANONYMOUS_TEMPLATE
    :   '{'
        {
        CommonToken *templateToken = [CommonToken newToken:input
                                              Type:ANONYMOUS_TEMPLATE
                                           Channel:0
                                             Start:input.index
                                              Stop:input.index];
        STLexer *lexer = [STLexer newSTLexer:group.errMgr
                                       input:input
                               templateToken:templateToken
                          delimiterStartChar:group.delimiterStartChar
                           delimiterStopChar:group.delimiterStopChar];
        [lexer setSubtemplateDepth:1];
        CommonToken *t = [lexer nextToken];
        while ( [lexer subtemplateDepth] >= 1 || t.type != STLexer.RCURLY ) {
            if ( t.type == STLexer.EOF_TYPE ) {
                MismatchedTokenException *mte = [MismatchedTokenException newException:'}' Stream:input];
                NSString *msg = @"missing final '}' in {...} anonymous template";
                [group.errMgr groupLexerError:SYNTAX_ERROR srcName:[self getSourceName] e:mte msg:msg];
                break;
            }
            t = [lexer nextToken];
        }
        }
        // don't match '}' here; our little {...} scanner loop matches it
        // to terminate.
    ;

COMMENT
    :   '/*' ( options {greedy=NO;} : . )* '*/' { [self skip]; }
    ;

LINE_COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' { [self skip]; }
    ;

WS  :   (' '|'\r'|'\t'|'\n') { [self skip]; }
    ;
