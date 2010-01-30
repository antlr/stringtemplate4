// $ANTLR 3.2 Sep 23, 2009 12:02:23 Group.g 2009-12-21 12:39:08

package org.stringtemplate.v4.compiler;

import org.antlr.runtime.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GroupParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "STRING", "BIGSTRING", "ANONYMOUS_TEMPLATE", "COMMENT", "LINE_COMMENT", "WS", "'@'", "'.'", "'('", "')'", "'::='", "','", "'='", "'['", "']'", "'default'", "':'"
    };
    public static final int LINE_COMMENT=9;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int ANONYMOUS_TEMPLATE=7;
    public static final int ID=4;
    public static final int EOF=-1;
    public static final int T__19=19;
    public static final int WS=10;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int BIGSTRING=6;
    public static final int COMMENT=8;
    public static final int STRING=5;

    // delegates
    // delegators


        public GroupParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GroupParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return GroupParser.tokenNames; }
    public String getGrammarFileName() { return "Group.g"; }


    public org.stringtemplate.v4.STGroup group;

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



    // $ANTLR start "group"
    // Group.g:93:1: group[STGroup group, String prefix] : ( def[prefix] )+ ;
    public final void group(org.stringtemplate.v4.STGroup group, String prefix) throws RecognitionException {

        GroupLexer lexer = (GroupLexer)input.getTokenSource();
        this.group = lexer.group = group;

        try {
            // Group.g:98:2: ( ( def[prefix] )+ )
            // Group.g:98:4: ( def[prefix] )+
            {
            // Group.g:98:4: ( def[prefix] )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||LA1_0==11) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Group.g:98:4: def[prefix]
            	    {
            	    pushFollow(FOLLOW_def_in_group49);
            	    def(prefix);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "group"


    // $ANTLR start "def"
    // Group.g:101:1: def[String prefix] : ( templateDef[prefix] | dictDef );
    public final void def(String prefix) throws RecognitionException {
        try {
            // Group.g:105:20: ( templateDef[prefix] | dictDef )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==11) ) {
                alt2=1;
            }
            else if ( (LA2_0==ID) ) {
                int LA2_2 = input.LA(2);

                if ( (LA2_2==13) ) {
                    alt2=1;
                }
                else if ( (LA2_2==15) ) {
                    int LA2_3 = input.LA(3);

                    if ( (LA2_3==ID) ) {
                        alt2=1;
                    }
                    else if ( (LA2_3==18) ) {
                        alt2=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // Group.g:105:22: templateDef[prefix]
                    {
                    pushFollow(FOLLOW_templateDef_in_def67);
                    templateDef(prefix);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // Group.g:105:44: dictDef
                    {
                    pushFollow(FOLLOW_dictDef_in_def72);
                    dictDef();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {

            		// pretend we already saw an error here
            		state.lastErrorIndex = input.index();
            		error("garbled template definition starting at '"+input.LT(1).getText()+"'");
            	
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "def"


    // $ANTLR start "templateDef"
    // Group.g:112:1: templateDef[String prefix] : ( ( '@' enclosing= ID '.' name= ID '(' ')' | name= ID '(' ( formalArgs )? ')' ) '::=' ( STRING | BIGSTRING | ) | alias= ID '::=' target= ID );
    public final void templateDef(String prefix) throws RecognitionException {
        Token enclosing=null;
        Token name=null;
        Token alias=null;
        Token target=null;
        Token STRING1=null;
        Token BIGSTRING2=null;
        LinkedHashMap<String,FormalArgument> formalArgs3 = null;



            String template=null, fullName=null;
            int n=0; // num char to strip from left, right of template def

        try {
            // Group.g:117:2: ( ( '@' enclosing= ID '.' name= ID '(' ')' | name= ID '(' ( formalArgs )? ')' ) '::=' ( STRING | BIGSTRING | ) | alias= ID '::=' target= ID )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==11) ) {
                alt6=1;
            }
            else if ( (LA6_0==ID) ) {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==13) ) {
                    alt6=1;
                }
                else if ( (LA6_2==15) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // Group.g:117:4: ( '@' enclosing= ID '.' name= ID '(' ')' | name= ID '(' ( formalArgs )? ')' ) '::=' ( STRING | BIGSTRING | )
                    {
                    // Group.g:117:4: ( '@' enclosing= ID '.' name= ID '(' ')' | name= ID '(' ( formalArgs )? ')' )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==11) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==ID) ) {
                        alt4=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // Group.g:117:6: '@' enclosing= ID '.' name= ID '(' ')'
                            {
                            match(input,11,FOLLOW_11_in_templateDef96); 
                            enclosing=(Token)match(input,ID,FOLLOW_ID_in_templateDef100); 
                            match(input,12,FOLLOW_12_in_templateDef102); 
                            name=(Token)match(input,ID,FOLLOW_ID_in_templateDef106); 
                            match(input,13,FOLLOW_13_in_templateDef108); 
                            match(input,14,FOLLOW_14_in_templateDef110); 
                            fullName = STGroup.getMangledRegionName((enclosing!=null?enclosing.getText():null), (name!=null?name.getText():null));

                            }
                            break;
                        case 2 :
                            // Group.g:119:5: name= ID '(' ( formalArgs )? ')'
                            {
                            name=(Token)match(input,ID,FOLLOW_ID_in_templateDef123); 
                            match(input,13,FOLLOW_13_in_templateDef125); 
                            // Group.g:119:17: ( formalArgs )?
                            int alt3=2;
                            int LA3_0 = input.LA(1);

                            if ( (LA3_0==ID) ) {
                                alt3=1;
                            }
                            switch (alt3) {
                                case 1 :
                                    // Group.g:119:17: formalArgs
                                    {
                                    pushFollow(FOLLOW_formalArgs_in_templateDef127);
                                    formalArgs3=formalArgs();

                                    state._fsp--;


                                    }
                                    break;

                            }

                            match(input,14,FOLLOW_14_in_templateDef130); 
                            fullName = (name!=null?name.getText():null);

                            }
                            break;

                    }

                    match(input,15,FOLLOW_15_in_templateDef143); 
                    Token templateToken = input.LT(1);
                    // Group.g:123:6: ( STRING | BIGSTRING | )
                    int alt5=3;
                    switch ( input.LA(1) ) {
                    case STRING:
                        {
                        alt5=1;
                        }
                        break;
                    case BIGSTRING:
                        {
                        alt5=2;
                        }
                        break;
                    case EOF:
                    case ID:
                    case 11:
                        {
                        alt5=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        throw nvae;
                    }

                    switch (alt5) {
                        case 1 :
                            // Group.g:123:8: STRING
                            {
                            STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_templateDef159); 
                            template=(STRING1!=null?STRING1.getText():null); n=1;

                            }
                            break;
                        case 2 :
                            // Group.g:124:8: BIGSTRING
                            {
                            BIGSTRING2=(Token)match(input,BIGSTRING,FOLLOW_BIGSTRING_in_templateDef174); 
                            template=(BIGSTRING2!=null?BIGSTRING2.getText():null); n=2;

                            }
                            break;
                        case 3 :
                            // Group.g:125:8: 
                            {

                            	    	template = "";
                            	    	String msg = "missing template at '"+input.LT(1).getText()+"'";
                                        NoViableAltException e = new NoViableAltException("", 0, 0, input);
                                	    ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e, msg);
                                	    

                            }
                            break;

                    }


                            template = Misc.strip(template, n);
                    	    group.defineTemplateOrRegion(templateToken, template, prefix, (enclosing!=null?enclosing.getText():null),
                    	                                 name, formalArgs3);
                    	    

                    }
                    break;
                case 2 :
                    // Group.g:137:6: alias= ID '::=' target= ID
                    {
                    alias=(Token)match(input,ID,FOLLOW_ID_in_templateDef209); 
                    match(input,15,FOLLOW_15_in_templateDef211); 
                    target=(Token)match(input,ID,FOLLOW_ID_in_templateDef215); 
                    group.defineTemplateAlias(alias, target);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "templateDef"


    // $ANTLR start "formalArgs"
    // Group.g:140:1: formalArgs returns [LinkedHashMap<String,FormalArgument> args] : formalArg[$args] ( ',' formalArg[$args] )* ;
    public final LinkedHashMap<String,FormalArgument> formalArgs() throws RecognitionException {
        LinkedHashMap<String,FormalArgument> args = null;

        args = new LinkedHashMap<String,FormalArgument>();
        try {
            // Group.g:142:5: ( formalArg[$args] ( ',' formalArg[$args] )* )
            // Group.g:142:7: formalArg[$args] ( ',' formalArg[$args] )*
            {
            pushFollow(FOLLOW_formalArg_in_formalArgs242);
            formalArg(args);

            state._fsp--;

            // Group.g:142:24: ( ',' formalArg[$args] )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==16) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // Group.g:142:26: ',' formalArg[$args]
            	    {
            	    match(input,16,FOLLOW_16_in_formalArgs247); 
            	    pushFollow(FOLLOW_formalArg_in_formalArgs249);
            	    formalArg(args);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return args;
    }
    // $ANTLR end "formalArgs"


    // $ANTLR start "formalArg"
    // Group.g:145:1: formalArg[LinkedHashMap<String,FormalArgument> args] : ID ( '=' a= STRING | '=' a= ANONYMOUS_TEMPLATE )? ;
    public final void formalArg(LinkedHashMap<String,FormalArgument> args) throws RecognitionException {
        Token a=null;
        Token ID4=null;

        try {
            // Group.g:146:2: ( ID ( '=' a= STRING | '=' a= ANONYMOUS_TEMPLATE )? )
            // Group.g:146:4: ID ( '=' a= STRING | '=' a= ANONYMOUS_TEMPLATE )?
            {
            ID4=(Token)match(input,ID,FOLLOW_ID_in_formalArg265); 
            // Group.g:147:3: ( '=' a= STRING | '=' a= ANONYMOUS_TEMPLATE )?
            int alt8=3;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==17) ) {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==STRING) ) {
                    alt8=1;
                }
                else if ( (LA8_1==ANONYMOUS_TEMPLATE) ) {
                    alt8=2;
                }
            }
            switch (alt8) {
                case 1 :
                    // Group.g:147:5: '=' a= STRING
                    {
                    match(input,17,FOLLOW_17_in_formalArg271); 
                    a=(Token)match(input,STRING,FOLLOW_STRING_in_formalArg275); 

                    }
                    break;
                case 2 :
                    // Group.g:148:5: '=' a= ANONYMOUS_TEMPLATE
                    {
                    match(input,17,FOLLOW_17_in_formalArg284); 
                    a=(Token)match(input,ANONYMOUS_TEMPLATE,FOLLOW_ANONYMOUS_TEMPLATE_in_formalArg288); 

                    }
                    break;

            }

            args.put((ID4!=null?ID4.getText():null), new FormalArgument((ID4!=null?ID4.getText():null), a));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "formalArg"


    // $ANTLR start "dictDef"
    // Group.g:162:1: dictDef : ID '::=' dict ;
    public final void dictDef() throws RecognitionException {
        Token ID5=null;
        Map<String,Object> dict6 = null;


        try {
            // Group.g:163:2: ( ID '::=' dict )
            // Group.g:163:4: ID '::=' dict
            {
            ID5=(Token)match(input,ID,FOLLOW_ID_in_dictDef314); 
            match(input,15,FOLLOW_15_in_dictDef316); 
            pushFollow(FOLLOW_dict_in_dictDef318);
            dict6=dict();

            state._fsp--;


                    if ( group.rawGetDictionary((ID5!=null?ID5.getText():null))!=null ) {
            			ErrorManager.compileTimeError(ErrorType.MAP_REDEFINITION, ID5);
                    }
                    else if ( group.rawGetTemplate((ID5!=null?ID5.getText():null))!=null ) {
            			ErrorManager.compileTimeError(ErrorType.TEMPLATE_REDEFINITION_AS_MAP, ID5);
                    }
                    else {
                        group.defineDictionary((ID5!=null?ID5.getText():null), dict6);
                    }
                    

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dictDef"


    // $ANTLR start "dict"
    // Group.g:177:1: dict returns [Map<String,Object> mapping] : '[' dictPairs[mapping] ']' ;
    public final Map<String,Object> dict() throws RecognitionException {
        Map<String,Object> mapping = null;

        mapping=new HashMap<String,Object>();
        try {
            // Group.g:179:2: ( '[' dictPairs[mapping] ']' )
            // Group.g:179:6: '[' dictPairs[mapping] ']'
            {
            match(input,18,FOLLOW_18_in_dict350); 
            pushFollow(FOLLOW_dictPairs_in_dict352);
            dictPairs(mapping);

            state._fsp--;

            match(input,19,FOLLOW_19_in_dict355); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return mapping;
    }
    // $ANTLR end "dict"


    // $ANTLR start "dictPairs"
    // Group.g:182:1: dictPairs[Map<String,Object> mapping] : ( keyValuePair[mapping] ( ',' keyValuePair[mapping] )* ( ',' defaultValuePair[mapping] )? | defaultValuePair[mapping] );
    public final void dictPairs(Map<String,Object> mapping) throws RecognitionException {
        try {
            // Group.g:183:5: ( keyValuePair[mapping] ( ',' keyValuePair[mapping] )* ( ',' defaultValuePair[mapping] )? | defaultValuePair[mapping] )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==STRING) ) {
                alt11=1;
            }
            else if ( (LA11_0==20) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // Group.g:183:7: keyValuePair[mapping] ( ',' keyValuePair[mapping] )* ( ',' defaultValuePair[mapping] )?
                    {
                    pushFollow(FOLLOW_keyValuePair_in_dictPairs371);
                    keyValuePair(mapping);

                    state._fsp--;

                    // Group.g:184:6: ( ',' keyValuePair[mapping] )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==16) ) {
                            int LA9_1 = input.LA(2);

                            if ( (LA9_1==STRING) ) {
                                alt9=1;
                            }


                        }


                        switch (alt9) {
                    	case 1 :
                    	    // Group.g:184:7: ',' keyValuePair[mapping]
                    	    {
                    	    match(input,16,FOLLOW_16_in_dictPairs380); 
                    	    pushFollow(FOLLOW_keyValuePair_in_dictPairs382);
                    	    keyValuePair(mapping);

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // Group.g:184:35: ( ',' defaultValuePair[mapping] )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==16) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // Group.g:184:36: ',' defaultValuePair[mapping]
                            {
                            match(input,16,FOLLOW_16_in_dictPairs388); 
                            pushFollow(FOLLOW_defaultValuePair_in_dictPairs390);
                            defaultValuePair(mapping);

                            state._fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Group.g:185:7: defaultValuePair[mapping]
                    {
                    pushFollow(FOLLOW_defaultValuePair_in_dictPairs401);
                    defaultValuePair(mapping);

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {

            		error("missing dictionary entry at '"+input.LT(1).getText()+"'");
            	
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dictPairs"


    // $ANTLR start "defaultValuePair"
    // Group.g:191:1: defaultValuePair[Map<String,Object> mapping] : 'default' ':' keyValue ;
    public final void defaultValuePair(Map<String,Object> mapping) throws RecognitionException {
        Object keyValue7 = null;


        try {
            // Group.g:192:2: ( 'default' ':' keyValue )
            // Group.g:192:4: 'default' ':' keyValue
            {
            match(input,20,FOLLOW_20_in_defaultValuePair427); 
            match(input,21,FOLLOW_21_in_defaultValuePair429); 
            pushFollow(FOLLOW_keyValue_in_defaultValuePair431);
            keyValue7=keyValue();

            state._fsp--;

            mapping.put(org.stringtemplate.v4.STGroup.DEFAULT_KEY, keyValue7);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "defaultValuePair"


    // $ANTLR start "keyValuePair"
    // Group.g:195:1: keyValuePair[Map<String,Object> mapping] : STRING ':' keyValue ;
    public final void keyValuePair(Map<String,Object> mapping) throws RecognitionException {
        Token STRING8=null;
        Object keyValue9 = null;


        try {
            // Group.g:196:2: ( STRING ':' keyValue )
            // Group.g:196:4: STRING ':' keyValue
            {
            STRING8=(Token)match(input,STRING,FOLLOW_STRING_in_keyValuePair445); 
            match(input,21,FOLLOW_21_in_keyValuePair447); 
            pushFollow(FOLLOW_keyValue_in_keyValuePair449);
            keyValue9=keyValue();

            state._fsp--;

            mapping.put(Misc.replaceEscapes(Misc.strip((STRING8!=null?STRING8.getText():null), 1)), keyValue9);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "keyValuePair"


    // $ANTLR start "keyValue"
    // Group.g:199:1: keyValue returns [Object value] : ( BIGSTRING | ANONYMOUS_TEMPLATE | STRING | {...}? => ID );
    public final Object keyValue() throws RecognitionException {
        Object value = null;

        Token BIGSTRING10=null;
        Token ANONYMOUS_TEMPLATE11=null;
        Token STRING12=null;

        try {
            // Group.g:200:2: ( BIGSTRING | ANONYMOUS_TEMPLATE | STRING | {...}? => ID )
            int alt12=4;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BIGSTRING) ) {
                alt12=1;
            }
            else if ( (LA12_0==ANONYMOUS_TEMPLATE) ) {
                alt12=2;
            }
            else if ( (LA12_0==STRING) ) {
                alt12=3;
            }
            else if ( (LA12_0==ID) && ((input.LT(1).getText().equals("key")))) {
                alt12=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // Group.g:200:4: BIGSTRING
                    {
                    BIGSTRING10=(Token)match(input,BIGSTRING,FOLLOW_BIGSTRING_in_keyValue466); 
                    value = new org.stringtemplate.v4.ST(Misc.strip((BIGSTRING10!=null?BIGSTRING10.getText():null),2));

                    }
                    break;
                case 2 :
                    // Group.g:201:4: ANONYMOUS_TEMPLATE
                    {
                    ANONYMOUS_TEMPLATE11=(Token)match(input,ANONYMOUS_TEMPLATE,FOLLOW_ANONYMOUS_TEMPLATE_in_keyValue475); 
                    value = new ST(Misc.strip((ANONYMOUS_TEMPLATE11!=null?ANONYMOUS_TEMPLATE11.getText():null),1));

                    }
                    break;
                case 3 :
                    // Group.g:202:4: STRING
                    {
                    STRING12=(Token)match(input,STRING,FOLLOW_STRING_in_keyValue482); 
                    value = Misc.replaceEscapes(Misc.strip((STRING12!=null?STRING12.getText():null), 1));

                    }
                    break;
                case 4 :
                    // Group.g:203:4: {...}? => ID
                    {
                    if ( !((input.LT(1).getText().equals("key"))) ) {
                        throw new FailedPredicateException(input, "keyValue", "input.LT(1).getText().equals(\"key\")");
                    }
                    match(input,ID,FOLLOW_ID_in_keyValue495); 
                    value = STGroup.DICT_KEY;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {

            		error("missing value for key at '"+input.LT(1).getText()+"'");
            	
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "keyValue"

    // Delegated rules


 

    public static final BitSet FOLLOW_def_in_group49 = new BitSet(new long[]{0x0000000000000812L});
    public static final BitSet FOLLOW_templateDef_in_def67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dictDef_in_def72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_templateDef96 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_templateDef100 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_templateDef102 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_templateDef106 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_templateDef108 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_templateDef110 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ID_in_templateDef123 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_templateDef125 = new BitSet(new long[]{0x0000000000004010L});
    public static final BitSet FOLLOW_formalArgs_in_templateDef127 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_templateDef130 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_templateDef143 = new BitSet(new long[]{0x0000000000000062L});
    public static final BitSet FOLLOW_STRING_in_templateDef159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIGSTRING_in_templateDef174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_templateDef209 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_templateDef211 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_templateDef215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalArg_in_formalArgs242 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_formalArgs247 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_formalArg_in_formalArgs249 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ID_in_formalArg265 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_17_in_formalArg271 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_STRING_in_formalArg275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_formalArg284 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ANONYMOUS_TEMPLATE_in_formalArg288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dictDef314 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_dictDef316 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_dict_in_dictDef318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_dict350 = new BitSet(new long[]{0x0000000000100020L});
    public static final BitSet FOLLOW_dictPairs_in_dict352 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_dict355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keyValuePair_in_dictPairs371 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_dictPairs380 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_keyValuePair_in_dictPairs382 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_dictPairs388 = new BitSet(new long[]{0x0000000000100020L});
    public static final BitSet FOLLOW_defaultValuePair_in_dictPairs390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_defaultValuePair_in_dictPairs401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_defaultValuePair427 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_defaultValuePair429 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_keyValue_in_defaultValuePair431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_keyValuePair445 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_keyValuePair447 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_keyValue_in_keyValuePair449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIGSTRING_in_keyValue466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANONYMOUS_TEMPLATE_in_keyValue475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_keyValue482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_keyValue495 = new BitSet(new long[]{0x0000000000000002L});

}