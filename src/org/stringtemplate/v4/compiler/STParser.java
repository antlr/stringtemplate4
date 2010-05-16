// $ANTLR ${project.version} ${buildNumber} STParser.g 2010-05-16 14:55:04

package org.stringtemplate.v4.compiler;

import org.antlr.runtime.*;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.Misc;

import java.util.ArrayList;
import java.util.List;

/** Recognize a single StringTemplate template text, expressions, and conditionals */
public class STParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IF", "ELSE", "ELSEIF", "ENDIF", "SUPER", "SEMI", "BANG", "ELLIPSIS", "EQUALS", "COLON", "LPAREN", "RPAREN", "LBRACK", "RBRACK", "COMMA", "DOT", "LCURLY", "RCURLY", "TEXT", "LDELIM", "RDELIM", "ID", "STRING", "WS", "PIPE", "OR", "AND", "INDENT", "NEWLINE", "AT", "END"
    };
    public static final int RBRACK=17;
    public static final int LBRACK=16;
    public static final int ELSE=5;
    public static final int ELLIPSIS=11;
    public static final int LCURLY=20;
    public static final int BANG=10;
    public static final int EQUALS=12;
    public static final int TEXT=22;
    public static final int AND=30;
    public static final int ID=25;
    public static final int EOF=-1;
    public static final int SEMI=9;
    public static final int INDENT=31;
    public static final int LPAREN=14;
    public static final int IF=4;
    public static final int ELSEIF=6;
    public static final int AT=33;
    public static final int COLON=13;
    public static final int RPAREN=15;
    public static final int WS=27;
    public static final int NEWLINE=32;
    public static final int COMMA=18;
    public static final int OR=29;
    public static final int RCURLY=21;
    public static final int ENDIF=7;
    public static final int RDELIM=24;
    public static final int PIPE=28;
    public static final int SUPER=8;
    public static final int END=34;
    public static final int DOT=19;
    public static final int LDELIM=23;
    public static final int STRING=26;

    // delegates
    // delegators


        public STParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public STParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return STParser.tokenNames; }
    public String getGrammarFileName() { return "STParser.g"; }


    /** The name of the template enclosing a subtemplate or region. */
    String enclosingTemplateName;
    Compiler gen = Compiler.NOOP_GEN;

    public STParser(TokenStream input, Compiler gen, String enclosingTemplateName) {
        this(input, new RecognizerSharedState(), gen, enclosingTemplateName);
    }
    public STParser(TokenStream input, RecognizerSharedState state, Compiler gen, String enclosingTemplateName) {
        super(null,null); // overcome bug in ANTLR 3.2
    	this.input = input;
    	this.state = state;
        if ( gen!=null ) this.gen = gen;
        this.enclosingTemplateName = enclosingTemplateName;
    }

    public void indent(String indent) {	gen.emit(Bytecode.INSTR_INDENT, indent); }

    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
    	throws RecognitionException
    {
    	throw new MismatchedTokenException(ttype, input);
    }



    // $ANTLR start "templateAndEOF"
    // STParser.g:73:1: templateAndEOF : template EOF ;
    public final void templateAndEOF() throws RecognitionException {
        try {
            // STParser.g:74:2: ( template EOF )
            // STParser.g:74:4: template EOF
            {
            pushFollow(FOLLOW_template_in_templateAndEOF55);
            template();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_templateAndEOF57); 

            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "templateAndEOF"


    // $ANTLR start "template"
    // STParser.g:77:1: template : ( element )* ;
    public final void template() throws RecognitionException {
        try {
            // STParser.g:78:2: ( ( element )* )
            // STParser.g:78:4: ( element )*
            {
            // STParser.g:78:4: ( element )*
            loop1:
            do {
                int alt1=2;
                switch ( input.LA(1) ) {
                case LDELIM:
                    {
                    int LA1_2 = input.LA(2);

                    if ( (LA1_2==IF||LA1_2==SUPER||LA1_2==LPAREN||LA1_2==LBRACK||LA1_2==LCURLY||(LA1_2>=ID && LA1_2<=STRING)||LA1_2==AT) ) {
                        alt1=1;
                    }


                    }
                    break;
                case INDENT:
                    {
                    int LA1_3 = input.LA(2);

                    if ( (LA1_3==LDELIM) ) {
                        int LA1_5 = input.LA(3);

                        if ( (LA1_5==IF||LA1_5==SUPER||LA1_5==LPAREN||LA1_5==LBRACK||LA1_5==LCURLY||(LA1_5>=ID && LA1_5<=STRING)||LA1_5==AT) ) {
                            alt1=1;
                        }


                    }
                    else if ( (LA1_3==TEXT||LA1_3==NEWLINE) ) {
                        alt1=1;
                    }


                    }
                    break;
                case TEXT:
                case NEWLINE:
                    {
                    alt1=1;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // STParser.g:78:4: element
            	    {
            	    pushFollow(FOLLOW_element_in_template68);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "template"


    // $ANTLR start "element"
    // STParser.g:81:1: element : ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE );
    public final void element() throws RecognitionException {
        CommonToken i=null;
        STParser.ifstat_return ifstat1 = null;

        STParser.region_return region2 = null;


        try {
            // STParser.g:82:2: ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE )
            int alt5=8;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // STParser.g:82:4: (i= INDENT )? ifstat ({...}? NEWLINE )?
                    {
                    // STParser.g:82:4: (i= INDENT )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==INDENT) ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // STParser.g:82:6: i= INDENT
                            {
                            i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element84); 

                            }
                            break;

                    }

                    int start_address = gen.address();
                    pushFollow(FOLLOW_ifstat_in_element93);
                    ifstat1=ifstat();

                    state._fsp--;

                    // STParser.g:85:3: ({...}? NEWLINE )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==NEWLINE) ) {
                        int LA3_1 = input.LA(2);

                        if ( (((ifstat1!=null?((CommonToken)ifstat1.start):null).getLine()!=input.LT(1).getLine())) ) {
                            alt3=1;
                        }
                    }
                    switch (alt3) {
                        case 1 :
                            // STParser.g:85:5: {...}? NEWLINE
                            {
                            if ( !(((ifstat1!=null?((CommonToken)ifstat1.start):null).getLine()!=input.LT(1).getLine())) ) {
                                throw new FailedPredicateException(input, "element", "$ifstat.start.getLine()!=input.LT(1).getLine()");
                            }
                            match(input,NEWLINE,FOLLOW_NEWLINE_in_element104); 

                            }
                            break;

                    }


                    		if ( i!=null && (ifstat1!=null?((CommonToken)ifstat1.start):null).getLine() == input.LT(1).getLine() ) {
                    			// need to emit INDENT if we found indent for IF on one line
                    			gen.insert(start_address, Bytecode.INSTR_INDENT, (i!=null?i.getText():null));
                    			gen.emit(Bytecode.INSTR_DEDENT);
                    		}
                    		

                    }
                    break;
                case 2 :
                    // STParser.g:94:4: i= INDENT exprTag
                    {
                    i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element119); 
                    indent((i!=null?i.getText():null));
                    pushFollow(FOLLOW_exprTag_in_element133);
                    exprTag();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_DEDENT);

                    }
                    break;
                case 3 :
                    // STParser.g:96:4: exprTag
                    {
                    pushFollow(FOLLOW_exprTag_in_element149);
                    exprTag();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // STParser.g:97:4: i= INDENT text
                    {
                    i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element156); 
                    indent((i!=null?i.getText():null));
                    pushFollow(FOLLOW_text_in_element170);
                    text();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_DEDENT);

                    }
                    break;
                case 5 :
                    // STParser.g:99:4: text
                    {
                    pushFollow(FOLLOW_text_in_element189);
                    text();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // STParser.g:100:6: (i= INDENT )? region
                    {
                    // STParser.g:100:6: (i= INDENT )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==INDENT) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // STParser.g:100:7: i= INDENT
                            {
                            i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element199); 
                            indent((i!=null?i.getText():null));

                            }
                            break;

                    }

                    pushFollow(FOLLOW_region_in_element205);
                    region2=region();

                    state._fsp--;


                    						 gen.emit(Bytecode.INSTR_NEW, (region2!=null?region2.name:null),
                    						 		  (region2!=null?((CommonToken)region2.start):null).getStartIndex(), (region2!=null?((CommonToken)region2.stop):null).getStopIndex());
                    						 gen.emit(Bytecode.INSTR_WRITE,
                    						          (region2!=null?((CommonToken)region2.start):null).getStartIndex(),
                    						          (region2!=null?((CommonToken)region2.stop):null).getStartIndex());
                    						 

                    }
                    break;
                case 7 :
                    // STParser.g:108:4: i= INDENT NEWLINE
                    {
                    i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element221); 
                    indent((i!=null?i.getText():null));
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_element236); 
                    gen.emit(Bytecode.INSTR_NEWLINE);
                    gen.emit(Bytecode.INSTR_DEDENT);

                    }
                    break;
                case 8 :
                    // STParser.g:111:4: NEWLINE
                    {
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_element275); 
                    gen.emit(Bytecode.INSTR_NEWLINE);

                    }
                    break;

            }
        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "element"


    // $ANTLR start "text"
    // STParser.g:114:1: text : TEXT ;
    public final void text() throws RecognitionException {
        CommonToken TEXT3=null;

        try {
            // STParser.g:115:2: ( TEXT )
            // STParser.g:115:4: TEXT
            {
            TEXT3=(CommonToken)match(input,TEXT,FOLLOW_TEXT_in_text297); 

            		if ( (TEXT3!=null?TEXT3.getText():null).length()>0 ) {
            			gen.emit(Bytecode.INSTR_LOAD_STR, (TEXT3!=null?TEXT3.getText():null),
            					 TEXT3.getStartIndex(), TEXT3.getStopIndex());
            			gen.emit(Bytecode.INSTR_WRITE,
            					 TEXT3.getStartIndex(),TEXT3.getStopIndex());
            		}
            		

            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "text"


    // $ANTLR start "exprTag"
    // STParser.g:126:1: exprTag : LDELIM expr ( ';' exprOptions | ) RDELIM ;
    public final void exprTag() throws RecognitionException {
        CommonToken LDELIM4=null;

        try {
            // STParser.g:127:2: ( LDELIM expr ( ';' exprOptions | ) RDELIM )
            // STParser.g:127:4: LDELIM expr ( ';' exprOptions | ) RDELIM
            {
            LDELIM4=(CommonToken)match(input,LDELIM,FOLLOW_LDELIM_in_exprTag312); 
            pushFollow(FOLLOW_expr_in_exprTag316);
            expr();

            state._fsp--;

            // STParser.g:129:3: ( ';' exprOptions | )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SEMI) ) {
                alt6=1;
            }
            else if ( (LA6_0==RDELIM) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // STParser.g:129:5: ';' exprOptions
                    {
                    match(input,SEMI,FOLLOW_SEMI_in_exprTag322); 
                    pushFollow(FOLLOW_exprOptions_in_exprTag324);
                    exprOptions();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_WRITE_OPT,
                    					  LDELIM4.getStartIndex(),((CommonToken)input.LT(1)).getStartIndex());

                    }
                    break;
                case 2 :
                    // STParser.g:132:5: 
                    {
                    gen.emit(Bytecode.INSTR_WRITE,
                    		              LDELIM4.getStartIndex(),((CommonToken)input.LT(1)).getStartIndex());

                    }
                    break;

            }

            match(input,RDELIM,FOLLOW_RDELIM_in_exprTag343); 

            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "exprTag"

    public static class region_return extends ParserRuleReturnScope {
        public String name;
    };

    // $ANTLR start "region"
    // STParser.g:138:1: region returns [String name] : LDELIM '@' ID RDELIM LDELIM '@end' RDELIM ;
    public final STParser.region_return region() throws RecognitionException {
        STParser.region_return retval = new STParser.region_return();
        retval.start = input.LT(1);

        CommonToken ID5=null;

        try {
            // STParser.g:139:2: ( LDELIM '@' ID RDELIM LDELIM '@end' RDELIM )
            // STParser.g:139:4: LDELIM '@' ID RDELIM LDELIM '@end' RDELIM
            {
            match(input,LDELIM,FOLLOW_LDELIM_in_region359); 
            match(input,AT,FOLLOW_AT_in_region361); 
            ID5=(CommonToken)match(input,ID,FOLLOW_ID_in_region363); 
            match(input,RDELIM,FOLLOW_RDELIM_in_region365); 
            retval.name = gen.compileRegion(enclosingTemplateName, (ID5!=null?ID5.getText():null), input, state);
            match(input,LDELIM,FOLLOW_LDELIM_in_region373); 
            match(input,END,FOLLOW_END_in_region375); 
            match(input,RDELIM,FOLLOW_RDELIM_in_region377); 

            }

            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "region"

    public static class subtemplate_return extends ParserRuleReturnScope {
        public String name;
    };

    // $ANTLR start "subtemplate"
    // STParser.g:144:1: subtemplate returns [String name] : '{' (ids+= ID ( ',' ids+= ID )* '|' )? ( INDENT )? '}' ;
    public final STParser.subtemplate_return subtemplate() throws RecognitionException {
        STParser.subtemplate_return retval = new STParser.subtemplate_return();
        retval.start = input.LT(1);

        CommonToken ids=null;
        List list_ids=null;

        try {
            // STParser.g:145:2: ( '{' (ids+= ID ( ',' ids+= ID )* '|' )? ( INDENT )? '}' )
            // STParser.g:145:4: '{' (ids+= ID ( ',' ids+= ID )* '|' )? ( INDENT )? '}'
            {
            match(input,LCURLY,FOLLOW_LCURLY_in_subtemplate393); 
            // STParser.g:145:8: (ids+= ID ( ',' ids+= ID )* '|' )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // STParser.g:145:10: ids+= ID ( ',' ids+= ID )* '|'
                    {
                    ids=(CommonToken)match(input,ID,FOLLOW_ID_in_subtemplate399); 
                    if (list_ids==null) list_ids=new ArrayList();
                    list_ids.add(ids);

                    // STParser.g:145:18: ( ',' ids+= ID )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==COMMA) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // STParser.g:145:19: ',' ids+= ID
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_subtemplate402); 
                    	    ids=(CommonToken)match(input,ID,FOLLOW_ID_in_subtemplate406); 
                    	    if (list_ids==null) list_ids=new ArrayList();
                    	    list_ids.add(ids);


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match(input,PIPE,FOLLOW_PIPE_in_subtemplate410); 

                    }
                    break;

            }

            retval.name = gen.compileAnonTemplate(enclosingTemplateName, input, list_ids, state);
            // STParser.g:147:9: ( INDENT )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==INDENT) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // STParser.g:147:9: INDENT
                    {
                    match(input,INDENT,FOLLOW_INDENT_in_subtemplate427); 

                    }
                    break;

            }

            match(input,RCURLY,FOLLOW_RCURLY_in_subtemplate439); 

            }

            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "subtemplate"


    // $ANTLR start "addTemplateEndTokensToFollowOfTemplateRule"
    // STParser.g:151:1: addTemplateEndTokensToFollowOfTemplateRule : template ( '}' | LDELIM '@end' ) ;
    public final void addTemplateEndTokensToFollowOfTemplateRule() throws RecognitionException {
        try {
            // STParser.g:156:44: ( template ( '}' | LDELIM '@end' ) )
            // STParser.g:156:46: template ( '}' | LDELIM '@end' )
            {
            pushFollow(FOLLOW_template_in_addTemplateEndTokensToFollowOfTemplateRule454);
            template();

            state._fsp--;

            // STParser.g:156:55: ( '}' | LDELIM '@end' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RCURLY) ) {
                alt10=1;
            }
            else if ( (LA10_0==LDELIM) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // STParser.g:156:56: '}'
                    {
                    match(input,RCURLY,FOLLOW_RCURLY_in_addTemplateEndTokensToFollowOfTemplateRule457); 

                    }
                    break;
                case 2 :
                    // STParser.g:156:60: LDELIM '@end'
                    {
                    match(input,LDELIM,FOLLOW_LDELIM_in_addTemplateEndTokensToFollowOfTemplateRule459); 
                    match(input,END,FOLLOW_END_in_addTemplateEndTokensToFollowOfTemplateRule461); 

                    }
                    break;

            }


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "addTemplateEndTokensToFollowOfTemplateRule"

    public static class ifstat_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "ifstat"
    // STParser.g:158:1: ifstat : LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM ;
    public final STParser.ifstat_return ifstat() throws RecognitionException {
        STParser.ifstat_return retval = new STParser.ifstat_return();
        retval.start = input.LT(1);

        CommonToken endif=null;


            /** Tracks address of branch operand (in code block).  It's how
             *  we backpatch forward references when generating code for IFs.
             */
            int prevBranchOperand = -1;
            /** Branch instruction operands that are forward refs to end of IF.
             *  We need to update them once we see the endif.
             */
            List<Integer> endRefs = new ArrayList<Integer>();

        try {
            // STParser.g:169:2: ( LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM )
            // STParser.g:169:4: LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM
            {
            match(input,LDELIM,FOLLOW_LDELIM_in_ifstat477); 
            match(input,IF,FOLLOW_IF_in_ifstat479); 
            match(input,LPAREN,FOLLOW_LPAREN_in_ifstat481); 
            pushFollow(FOLLOW_conditional_in_ifstat483);
            conditional();

            state._fsp--;

            match(input,RPAREN,FOLLOW_RPAREN_in_ifstat485); 
            match(input,RDELIM,FOLLOW_RDELIM_in_ifstat487); 

                    prevBranchOperand = gen.address()+1;
                    gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
            		
            pushFollow(FOLLOW_template_in_ifstat495);
            template();

            state._fsp--;

            // STParser.g:175:3: ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==INDENT) ) {
                    int LA12_1 = input.LA(2);

                    if ( (LA12_1==LDELIM) ) {
                        int LA12_2 = input.LA(3);

                        if ( (LA12_2==ELSEIF) ) {
                            alt12=1;
                        }


                    }


                }
                else if ( (LA12_0==LDELIM) ) {
                    int LA12_2 = input.LA(2);

                    if ( (LA12_2==ELSEIF) ) {
                        alt12=1;
                    }


                }


                switch (alt12) {
            	case 1 :
            	    // STParser.g:175:5: ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template
            	    {
            	    // STParser.g:175:5: ( INDENT )?
            	    int alt11=2;
            	    int LA11_0 = input.LA(1);

            	    if ( (LA11_0==INDENT) ) {
            	        alt11=1;
            	    }
            	    switch (alt11) {
            	        case 1 :
            	            // STParser.g:175:5: INDENT
            	            {
            	            match(input,INDENT,FOLLOW_INDENT_in_ifstat501); 

            	            }
            	            break;

            	    }

            	    match(input,LDELIM,FOLLOW_LDELIM_in_ifstat504); 
            	    match(input,ELSEIF,FOLLOW_ELSEIF_in_ifstat506); 

            	    			endRefs.add(gen.address()+1);
            	    			gen.emit(Bytecode.INSTR_BR, -1); // br end
            	    			// update previous branch instruction
            	    			gen.write(prevBranchOperand, (short)gen.address());
            	    			prevBranchOperand = -1;
            	    			
            	    match(input,LPAREN,FOLLOW_LPAREN_in_ifstat516); 
            	    pushFollow(FOLLOW_conditional_in_ifstat518);
            	    conditional();

            	    state._fsp--;

            	    match(input,RPAREN,FOLLOW_RPAREN_in_ifstat520); 
            	    match(input,RDELIM,FOLLOW_RDELIM_in_ifstat522); 

            	            	prevBranchOperand = gen.address()+1;
            	            	gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
            	    			
            	    pushFollow(FOLLOW_template_in_ifstat532);
            	    template();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            // STParser.g:190:3: ( ( INDENT )? LDELIM 'else' RDELIM template )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==INDENT) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==LDELIM) ) {
                    int LA14_2 = input.LA(3);

                    if ( (LA14_2==ELSE) ) {
                        alt14=1;
                    }
                }
            }
            else if ( (LA14_0==LDELIM) ) {
                int LA14_2 = input.LA(2);

                if ( (LA14_2==ELSE) ) {
                    alt14=1;
                }
            }
            switch (alt14) {
                case 1 :
                    // STParser.g:190:5: ( INDENT )? LDELIM 'else' RDELIM template
                    {
                    // STParser.g:190:5: ( INDENT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==INDENT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // STParser.g:190:5: INDENT
                            {
                            match(input,INDENT,FOLLOW_INDENT_in_ifstat543); 

                            }
                            break;

                    }

                    match(input,LDELIM,FOLLOW_LDELIM_in_ifstat546); 
                    match(input,ELSE,FOLLOW_ELSE_in_ifstat548); 
                    match(input,RDELIM,FOLLOW_RDELIM_in_ifstat550); 

                    			endRefs.add(gen.address()+1);
                    			gen.emit(Bytecode.INSTR_BR, -1); // br end
                    			// update previous branch instruction
                    			gen.write(prevBranchOperand, (short)gen.address());
                    			prevBranchOperand = -1;
                    			
                    pushFollow(FOLLOW_template_in_ifstat560);
                    template();

                    state._fsp--;


                    }
                    break;

            }

            // STParser.g:200:3: ( INDENT )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==INDENT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // STParser.g:200:3: INDENT
                    {
                    match(input,INDENT,FOLLOW_INDENT_in_ifstat569); 

                    }
                    break;

            }

            endif=(CommonToken)match(input,LDELIM,FOLLOW_LDELIM_in_ifstat574); 
            match(input,ENDIF,FOLLOW_ENDIF_in_ifstat576); 
            match(input,RDELIM,FOLLOW_RDELIM_in_ifstat578); 

            		if ( prevBranchOperand>=0 ) {
            			gen.write(prevBranchOperand, (short)gen.address());
            		}
                    for (int opnd : endRefs) gen.write(opnd, (short)gen.address());
            		

            }

            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ifstat"


    // $ANTLR start "conditional"
    // STParser.g:210:1: conditional : andConditional ( '||' andConditional )* ;
    public final void conditional() throws RecognitionException {
        try {
            // STParser.g:211:2: ( andConditional ( '||' andConditional )* )
            // STParser.g:211:4: andConditional ( '||' andConditional )*
            {
            pushFollow(FOLLOW_andConditional_in_conditional598);
            andConditional();

            state._fsp--;

            // STParser.g:211:19: ( '||' andConditional )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==OR) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // STParser.g:211:20: '||' andConditional
            	    {
            	    match(input,OR,FOLLOW_OR_in_conditional601); 
            	    pushFollow(FOLLOW_andConditional_in_conditional603);
            	    andConditional();

            	    state._fsp--;

            	    gen.emit(Bytecode.INSTR_OR);

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "conditional"


    // $ANTLR start "andConditional"
    // STParser.g:214:1: andConditional : notConditional ( '&&' notConditional )* ;
    public final void andConditional() throws RecognitionException {
        try {
            // STParser.g:215:2: ( notConditional ( '&&' notConditional )* )
            // STParser.g:215:4: notConditional ( '&&' notConditional )*
            {
            pushFollow(FOLLOW_notConditional_in_andConditional619);
            notConditional();

            state._fsp--;

            // STParser.g:215:19: ( '&&' notConditional )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==AND) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // STParser.g:215:20: '&&' notConditional
            	    {
            	    match(input,AND,FOLLOW_AND_in_andConditional622); 
            	    pushFollow(FOLLOW_notConditional_in_andConditional624);
            	    notConditional();

            	    state._fsp--;

            	    gen.emit(Bytecode.INSTR_AND);

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "andConditional"


    // $ANTLR start "notConditional"
    // STParser.g:218:1: notConditional : ( '!' memberExpr | memberExpr );
    public final void notConditional() throws RecognitionException {
        try {
            // STParser.g:219:2: ( '!' memberExpr | memberExpr )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==BANG) ) {
                alt18=1;
            }
            else if ( (LA18_0==SUPER||LA18_0==LPAREN||LA18_0==LBRACK||LA18_0==LCURLY||(LA18_0>=ID && LA18_0<=STRING)||LA18_0==AT) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // STParser.g:219:4: '!' memberExpr
                    {
                    match(input,BANG,FOLLOW_BANG_in_notConditional639); 
                    pushFollow(FOLLOW_memberExpr_in_notConditional641);
                    memberExpr();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_NOT);

                    }
                    break;
                case 2 :
                    // STParser.g:220:4: memberExpr
                    {
                    pushFollow(FOLLOW_memberExpr_in_notConditional649);
                    memberExpr();

                    state._fsp--;


                    }
                    break;

            }
        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "notConditional"


    // $ANTLR start "exprOptions"
    // STParser.g:223:1: exprOptions : option ( ',' option )* ;
    public final void exprOptions() throws RecognitionException {
        try {
            // STParser.g:224:2: ( option ( ',' option )* )
            // STParser.g:224:4: option ( ',' option )*
            {
            gen.emit(Bytecode.INSTR_OPTIONS);
            pushFollow(FOLLOW_option_in_exprOptions663);
            option();

            state._fsp--;

            // STParser.g:224:47: ( ',' option )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // STParser.g:224:48: ',' option
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_exprOptions666); 
            	    pushFollow(FOLLOW_option_in_exprOptions668);
            	    option();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "exprOptions"


    // $ANTLR start "option"
    // STParser.g:227:1: option : ID ( '=' exprNoComma | ) ;
    public final void option() throws RecognitionException {
        CommonToken ID6=null;

        try {
            // STParser.g:228:2: ( ID ( '=' exprNoComma | ) )
            // STParser.g:228:4: ID ( '=' exprNoComma | )
            {
            ID6=(CommonToken)match(input,ID,FOLLOW_ID_in_option681); 
            // STParser.g:228:7: ( '=' exprNoComma | )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==EQUALS) ) {
                alt20=1;
            }
            else if ( (LA20_0==COMMA||LA20_0==RDELIM) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // STParser.g:228:9: '=' exprNoComma
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_option685); 
                    pushFollow(FOLLOW_exprNoComma_in_option687);
                    exprNoComma();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // STParser.g:228:27: 
                    {
                    gen.defaultOption(ID6);

                    }
                    break;

            }

            gen.setOption(ID6);

            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "option"

    public static class exprNoComma_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "exprNoComma"
    // STParser.g:231:1: exprNoComma : memberExpr ( ':' templateRef )? ;
    public final STParser.exprNoComma_return exprNoComma() throws RecognitionException {
        STParser.exprNoComma_return retval = new STParser.exprNoComma_return();
        retval.start = input.LT(1);

        STParser.templateRef_return templateRef7 = null;


        try {
            // STParser.g:232:2: ( memberExpr ( ':' templateRef )? )
            // STParser.g:232:4: memberExpr ( ':' templateRef )?
            {
            pushFollow(FOLLOW_memberExpr_in_exprNoComma707);
            memberExpr();

            state._fsp--;

            // STParser.g:233:3: ( ':' templateRef )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==COLON) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // STParser.g:233:5: ':' templateRef
                    {
                    match(input,COLON,FOLLOW_COLON_in_exprNoComma713); 
                    pushFollow(FOLLOW_templateRef_in_exprNoComma715);
                    templateRef7=templateRef();

                    state._fsp--;


                    						   gen.emit(Bytecode.INSTR_MAP,
                    								    (templateRef7!=null?((CommonToken)templateRef7.start):null).getStartIndex(),
                    								    (templateRef7!=null?((CommonToken)templateRef7.stop):null).getStopIndex());
                    						   

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exprNoComma"


    // $ANTLR start "expr"
    // STParser.g:242:1: expr : mapExpr ;
    public final void expr() throws RecognitionException {
        try {
            // STParser.g:242:6: ( mapExpr )
            // STParser.g:242:8: mapExpr
            {
            pushFollow(FOLLOW_mapExpr_in_expr741);
            mapExpr();

            state._fsp--;


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr"

    public static class mapExpr_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "mapExpr"
    // STParser.g:244:1: mapExpr : memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )* ;
    public final STParser.mapExpr_return mapExpr() throws RecognitionException {
        STParser.mapExpr_return retval = new STParser.mapExpr_return();
        retval.start = input.LT(1);

        CommonToken c=null;

        int nt=1, ne=1; int a=((CommonToken)retval.start).getStartIndex();
        try {
            // STParser.g:246:2: ( memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )* )
            // STParser.g:246:4: memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )*
            {
            pushFollow(FOLLOW_memberExpr_in_mapExpr756);
            memberExpr();

            state._fsp--;

            // STParser.g:246:15: (c= ',' memberExpr )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==COMMA) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // STParser.g:246:16: c= ',' memberExpr
            	    {
            	    c=(CommonToken)match(input,COMMA,FOLLOW_COMMA_in_mapExpr761); 
            	    pushFollow(FOLLOW_memberExpr_in_mapExpr763);
            	    memberExpr();

            	    state._fsp--;

            	    ne++;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            // STParser.g:247:3: ( ':' templateRef ( ( ',' templateRef )+ | ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COLON) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // STParser.g:247:5: ':' templateRef ( ( ',' templateRef )+ | )
            	    {
            	    match(input,COLON,FOLLOW_COLON_in_mapExpr774); 
            	    pushFollow(FOLLOW_templateRef_in_mapExpr776);
            	    templateRef();

            	    state._fsp--;

            	    // STParser.g:248:4: ( ( ',' templateRef )+ | )
            	    int alt24=2;
            	    int LA24_0 = input.LA(1);

            	    if ( (LA24_0==COMMA) ) {
            	        alt24=1;
            	    }
            	    else if ( (LA24_0==SEMI||LA24_0==COLON||LA24_0==RPAREN||LA24_0==RDELIM) ) {
            	        alt24=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 24, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // STParser.g:248:6: ( ',' templateRef )+
            	            {
            	            // STParser.g:248:6: ( ',' templateRef )+
            	            int cnt23=0;
            	            loop23:
            	            do {
            	                int alt23=2;
            	                int LA23_0 = input.LA(1);

            	                if ( (LA23_0==COMMA) ) {
            	                    alt23=1;
            	                }


            	                switch (alt23) {
            	            	case 1 :
            	            	    // STParser.g:248:7: ',' templateRef
            	            	    {
            	            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpr784); 
            	            	    pushFollow(FOLLOW_templateRef_in_mapExpr786);
            	            	    templateRef();

            	            	    state._fsp--;

            	            	    nt++;

            	            	    }
            	            	    break;

            	            	default :
            	            	    if ( cnt23 >= 1 ) break loop23;
            	                        EarlyExitException eee =
            	                            new EarlyExitException(23, input);
            	                        throw eee;
            	                }
            	                cnt23++;
            	            } while (true);

            	            gen.emit(Bytecode.INSTR_ROT_MAP, nt, a,
            	            						              ((CommonToken)input.LT(-1)).getStopIndex());

            	            }
            	            break;
            	        case 2 :
            	            // STParser.g:251:17: 
            	            {

            	            			               if ( c!=null ) gen.emit(Bytecode.INSTR_PAR_MAP, ne, a,
            	            						                            ((CommonToken)input.LT(-1)).getStopIndex());
            	            						   else gen.emit(Bytecode.INSTR_MAP, a,
            	            							             ((CommonToken)input.LT(-1)).getStopIndex());
            	            						   

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapExpr"


    // $ANTLR start "memberExpr"
    // STParser.g:261:1: memberExpr : callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )* ;
    public final void memberExpr() throws RecognitionException {
        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken ID8=null;

        try {
            // STParser.g:262:2: ( callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )* )
            // STParser.g:262:4: callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )*
            {
            pushFollow(FOLLOW_callExpr_in_memberExpr840);
            callExpr();

            state._fsp--;

            // STParser.g:263:3: ( '.' ID | '.' lp= '(' mapExpr rp= ')' )*
            loop26:
            do {
                int alt26=3;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==DOT) ) {
                    int LA26_2 = input.LA(2);

                    if ( (LA26_2==ID) ) {
                        alt26=1;
                    }
                    else if ( (LA26_2==LPAREN) ) {
                        alt26=2;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // STParser.g:263:5: '.' ID
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_memberExpr846); 
            	    ID8=(CommonToken)match(input,ID,FOLLOW_ID_in_memberExpr848); 
            	    gen.emit(Bytecode.INSTR_LOAD_PROP, (ID8!=null?ID8.getText():null),
            	    					                 ID8.getStartIndex(), ID8.getStopIndex());

            	    }
            	    break;
            	case 2 :
            	    // STParser.g:265:5: '.' lp= '(' mapExpr rp= ')'
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_memberExpr864); 
            	    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_memberExpr868); 
            	    pushFollow(FOLLOW_mapExpr_in_memberExpr870);
            	    mapExpr();

            	    state._fsp--;

            	    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_memberExpr874); 
            	    gen.emit(Bytecode.INSTR_LOAD_PROP_IND,
            	    						   		     lp.getStartIndex(),rp.getStartIndex());

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "memberExpr"

    public static class callExpr_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "callExpr"
    // STParser.g:271:1: callExpr options {k=2; } : ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary );
    public final STParser.callExpr_return callExpr() throws RecognitionException {
        STParser.callExpr_return retval = new STParser.callExpr_return();
        retval.start = input.LT(1);

        CommonToken s=null;
        CommonToken rp=null;
        CommonToken ID9=null;
        CommonToken ID10=null;
        CommonToken ID11=null;

        try {
            // STParser.g:273:2: ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary )
            int alt30=4;
            alt30 = dfa30.predict(input);
            switch (alt30) {
                case 1 :
                    // STParser.g:273:4: {...}? ID '(' expr ')'
                    {
                    if ( !((Compiler.funcs.containsKey(input.LT(1).getText()))) ) {
                        throw new FailedPredicateException(input, "callExpr", "Compiler.funcs.containsKey(input.LT(1).getText())");
                    }
                    ID9=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr914); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr916); 
                    pushFollow(FOLLOW_expr_in_callExpr918);
                    expr();

                    state._fsp--;

                    match(input,RPAREN,FOLLOW_RPAREN_in_callExpr920); 
                    gen.func(ID9);

                    }
                    break;
                case 2 :
                    // STParser.g:275:4: (s= 'super' '.' )? ID '(' ( args )? ')'
                    {
                    // STParser.g:275:4: (s= 'super' '.' )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==SUPER) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // STParser.g:275:5: s= 'super' '.'
                            {
                            s=(CommonToken)match(input,SUPER,FOLLOW_SUPER_in_callExpr933); 
                            match(input,DOT,FOLLOW_DOT_in_callExpr935); 

                            }
                            break;

                    }

                    ID10=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr939); 
                    gen.emit(s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
                    								     gen.prefixedName((ID10!=null?ID10.getText():null)),
                    								     ((CommonToken)retval.start).getStartIndex(), ID10.getStopIndex());
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr954); 
                    // STParser.g:279:7: ( args )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==SUPER||LA28_0==ELLIPSIS||LA28_0==LPAREN||LA28_0==LBRACK||LA28_0==LCURLY||(LA28_0>=ID && LA28_0<=STRING)||LA28_0==AT) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // STParser.g:279:7: args
                            {
                            pushFollow(FOLLOW_args_in_callExpr956);
                            args();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_callExpr959); 

                    }
                    break;
                case 3 :
                    // STParser.g:280:4: '@' (s= 'super' '.' )? ID '(' rp= ')'
                    {
                    match(input,AT,FOLLOW_AT_in_callExpr964); 
                    // STParser.g:280:8: (s= 'super' '.' )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==SUPER) ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // STParser.g:280:9: s= 'super' '.'
                            {
                            s=(CommonToken)match(input,SUPER,FOLLOW_SUPER_in_callExpr969); 
                            match(input,DOT,FOLLOW_DOT_in_callExpr971); 

                            }
                            break;

                    }

                    ID11=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr975); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr977); 
                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_callExpr981); 

                    						   gen.defineBlankRegion(enclosingTemplateName, (ID11!=null?ID11.getText():null));
                    						   String mangled = STGroup.getMangledRegionName(enclosingTemplateName, (ID11!=null?ID11.getText():null));
                    						   gen.emit(s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
                    							   	    gen.prefixedName(mangled),
                    								    ((CommonToken)retval.start).getStartIndex(), rp.getStartIndex());
                    						   

                    }
                    break;
                case 4 :
                    // STParser.g:288:4: primary
                    {
                    pushFollow(FOLLOW_primary_in_callExpr998);
                    primary();

                    state._fsp--;


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "callExpr"


    // $ANTLR start "primary"
    // STParser.g:291:1: primary : (o= ID | STRING | subtemplate | list | lp= '(' expr rp= ')' ( '(' ( args )? ')' )? );
    public final void primary() throws RecognitionException {
        CommonToken o=null;
        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken STRING12=null;
        STParser.subtemplate_return subtemplate13 = null;


        try {
            // STParser.g:292:2: (o= ID | STRING | subtemplate | list | lp= '(' expr rp= ')' ( '(' ( args )? ')' )? )
            int alt33=5;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt33=1;
                }
                break;
            case STRING:
                {
                alt33=2;
                }
                break;
            case LCURLY:
                {
                alt33=3;
                }
                break;
            case LBRACK:
                {
                alt33=4;
                }
                break;
            case LPAREN:
                {
                alt33=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // STParser.g:292:4: o= ID
                    {
                    o=(CommonToken)match(input,ID,FOLLOW_ID_in_primary1011); 
                    gen.refAttr(o);

                    }
                    break;
                case 2 :
                    // STParser.g:293:4: STRING
                    {
                    STRING12=(CommonToken)match(input,STRING,FOLLOW_STRING_in_primary1029); 
                    gen.emit(Bytecode.INSTR_LOAD_STR,
                    									 Misc.strip((STRING12!=null?STRING12.getText():null),1),
                    							 		 STRING12.getStartIndex(), STRING12.getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:296:4: subtemplate
                    {
                    pushFollow(FOLLOW_subtemplate_in_primary1048);
                    subtemplate13=subtemplate();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_NEW, gen.prefixedName((subtemplate13!=null?subtemplate13.name:null)),
                    									 (subtemplate13!=null?((CommonToken)subtemplate13.start):null).getStartIndex(),
                    									 (subtemplate13!=null?((CommonToken)subtemplate13.stop):null).getStopIndex());

                    }
                    break;
                case 4 :
                    // STParser.g:300:4: list
                    {
                    pushFollow(FOLLOW_list_in_primary1076);
                    list();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // STParser.g:301:4: lp= '(' expr rp= ')' ( '(' ( args )? ')' )?
                    {
                    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_primary1083); 
                    pushFollow(FOLLOW_expr_in_primary1085);
                    expr();

                    state._fsp--;

                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_primary1089); 
                    gen.emit(Bytecode.INSTR_TOSTR,
                    									 lp.getStartIndex(),rp.getStartIndex());
                    // STParser.g:303:3: ( '(' ( args )? ')' )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==LPAREN) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // STParser.g:303:20: '(' ( args )? ')'
                            {
                            gen.emit(Bytecode.INSTR_NEW_IND,
                                                    		     lp.getStartIndex(),rp.getStartIndex());
                            match(input,LPAREN,FOLLOW_LPAREN_in_primary1117); 
                            // STParser.g:305:8: ( args )?
                            int alt31=2;
                            int LA31_0 = input.LA(1);

                            if ( (LA31_0==SUPER||LA31_0==ELLIPSIS||LA31_0==LPAREN||LA31_0==LBRACK||LA31_0==LCURLY||(LA31_0>=ID && LA31_0<=STRING)||LA31_0==AT) ) {
                                alt31=1;
                            }
                            switch (alt31) {
                                case 1 :
                                    // STParser.g:305:8: args
                                    {
                                    pushFollow(FOLLOW_args_in_primary1119);
                                    args();

                                    state._fsp--;


                                    }
                                    break;

                            }

                            match(input,RPAREN,FOLLOW_RPAREN_in_primary1122); 

                            }
                            break;

                    }


                    }
                    break;

            }
        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "primary"


    // $ANTLR start "args"
    // STParser.g:309:1: args : arg ( ',' arg )* ;
    public final void args() throws RecognitionException {
        try {
            // STParser.g:309:5: ( arg ( ',' arg )* )
            // STParser.g:309:7: arg ( ',' arg )*
            {
            pushFollow(FOLLOW_arg_in_args1138);
            arg();

            state._fsp--;

            // STParser.g:309:11: ( ',' arg )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // STParser.g:309:12: ',' arg
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args1141); 
            	    pushFollow(FOLLOW_arg_in_args1143);
            	    arg();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "args"


    // $ANTLR start "arg"
    // STParser.g:311:1: arg : ( ID '=' exprNoComma | exprNoComma | elip= '...' );
    public final void arg() throws RecognitionException {
        CommonToken elip=null;
        CommonToken ID14=null;
        STParser.exprNoComma_return exprNoComma15 = null;

        STParser.exprNoComma_return exprNoComma16 = null;


        try {
            // STParser.g:311:5: ( ID '=' exprNoComma | exprNoComma | elip= '...' )
            int alt35=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==EQUALS) ) {
                    alt35=1;
                }
                else if ( ((LA35_1>=COLON && LA35_1<=RPAREN)||(LA35_1>=COMMA && LA35_1<=DOT)) ) {
                    alt35=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 1, input);

                    throw nvae;
                }
                }
                break;
            case SUPER:
            case LPAREN:
            case LBRACK:
            case LCURLY:
            case STRING:
            case AT:
                {
                alt35=2;
                }
                break;
            case ELLIPSIS:
                {
                alt35=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // STParser.g:311:7: ID '=' exprNoComma
                    {
                    ID14=(CommonToken)match(input,ID,FOLLOW_ID_in_arg1154); 
                    match(input,EQUALS,FOLLOW_EQUALS_in_arg1156); 
                    pushFollow(FOLLOW_exprNoComma_in_arg1158);
                    exprNoComma15=exprNoComma();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_STORE_ATTR, (ID14!=null?ID14.getText():null),
                    					 				 ID14.getStartIndex(), (exprNoComma15!=null?((CommonToken)exprNoComma15.stop):null).getStopIndex());

                    }
                    break;
                case 2 :
                    // STParser.g:313:4: exprNoComma
                    {
                    pushFollow(FOLLOW_exprNoComma_in_arg1165);
                    exprNoComma16=exprNoComma();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_STORE_SOLE_ARG,
                    									 (exprNoComma16!=null?((CommonToken)exprNoComma16.start):null).getStartIndex(),
                    									 (exprNoComma16!=null?((CommonToken)exprNoComma16.stop):null).getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:316:4: elip= '...'
                    {
                    elip=(CommonToken)match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_arg1181); 
                    gen.emit(Bytecode.INSTR_SET_PASS_THRU);

                    }
                    break;

            }
        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "arg"

    public static class templateRef_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "templateRef"
    // STParser.g:319:1: templateRef : ( ID '(' ')' | subtemplate | lp= '(' mapExpr rp= ')' '(' ')' );
    public final STParser.templateRef_return templateRef() throws RecognitionException {
        STParser.templateRef_return retval = new STParser.templateRef_return();
        retval.start = input.LT(1);

        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken ID17=null;
        STParser.subtemplate_return subtemplate18 = null;


        try {
            // STParser.g:325:2: ( ID '(' ')' | subtemplate | lp= '(' mapExpr rp= ')' '(' ')' )
            int alt36=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt36=1;
                }
                break;
            case LCURLY:
                {
                alt36=2;
                }
                break;
            case LPAREN:
                {
                alt36=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // STParser.g:325:4: ID '(' ')'
                    {
                    ID17=(CommonToken)match(input,ID,FOLLOW_ID_in_templateRef1200); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1203); 
                    match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1205); 
                    gen.emit(Bytecode.INSTR_LOAD_STR,gen.prefixedName((ID17!=null?ID17.getText():null)),
                                       		 		     ID17.getStartIndex(), ID17.getStopIndex());

                    }
                    break;
                case 2 :
                    // STParser.g:327:4: subtemplate
                    {
                    pushFollow(FOLLOW_subtemplate_in_templateRef1216);
                    subtemplate18=subtemplate();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_LOAD_STR,
                    	                                 gen.prefixedName((subtemplate18!=null?subtemplate18.name:null)),
                    									 (subtemplate18!=null?((CommonToken)subtemplate18.start):null).getStartIndex(),
                    									 (subtemplate18!=null?((CommonToken)subtemplate18.stop):null).getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:331:4: lp= '(' mapExpr rp= ')' '(' ')'
                    {
                    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1232); 
                    pushFollow(FOLLOW_mapExpr_in_templateRef1234);
                    mapExpr();

                    state._fsp--;

                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1238); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1240); 
                    match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1242); 
                    gen.emit(Bytecode.INSTR_TOSTR,
                    		                             lp.getStartIndex(),rp.getStartIndex());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return retval;
    }
    // $ANTLR end "templateRef"


    // $ANTLR start "list"
    // STParser.g:336:1: list : ( '[' listElement ( ',' listElement )* ']' | '[' ']' );
    public final void list() throws RecognitionException {
        try {
            // STParser.g:336:5: ( '[' listElement ( ',' listElement )* ']' | '[' ']' )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==LBRACK) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==RBRACK) ) {
                    alt38=2;
                }
                else if ( (LA38_1==SUPER||LA38_1==LPAREN||LA38_1==LBRACK||LA38_1==LCURLY||(LA38_1>=ID && LA38_1<=STRING)||LA38_1==AT) ) {
                    alt38=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // STParser.g:336:7: '[' listElement ( ',' listElement )* ']'
                    {
                    gen.emit(Bytecode.INSTR_LIST);
                    match(input,LBRACK,FOLLOW_LBRACK_in_list1277); 
                    pushFollow(FOLLOW_listElement_in_list1279);
                    listElement();

                    state._fsp--;

                    // STParser.g:336:56: ( ',' listElement )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==COMMA) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // STParser.g:336:57: ',' listElement
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_list1282); 
                    	    pushFollow(FOLLOW_listElement_in_list1284);
                    	    listElement();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop37;
                        }
                    } while (true);

                    match(input,RBRACK,FOLLOW_RBRACK_in_list1288); 

                    }
                    break;
                case 2 :
                    // STParser.g:337:4: '[' ']'
                    {
                    gen.emit(Bytecode.INSTR_LIST);
                    match(input,LBRACK,FOLLOW_LBRACK_in_list1295); 
                    match(input,RBRACK,FOLLOW_RBRACK_in_list1297); 

                    }
                    break;

            }
        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "list"


    // $ANTLR start "listElement"
    // STParser.g:340:1: listElement : exprNoComma ;
    public final void listElement() throws RecognitionException {
        STParser.exprNoComma_return exprNoComma19 = null;


        try {
            // STParser.g:341:5: ( exprNoComma )
            // STParser.g:341:9: exprNoComma
            {
            pushFollow(FOLLOW_exprNoComma_in_listElement1313);
            exprNoComma19=exprNoComma();

            state._fsp--;

            gen.emit(Bytecode.INSTR_ADD,
            								    (exprNoComma19!=null?((CommonToken)exprNoComma19.start):null).getStartIndex(),
            								    (exprNoComma19!=null?((CommonToken)exprNoComma19.stop):null).getStopIndex());

            }

        }

           catch (RecognitionException re) { throw re; }
        finally {
        }
        return ;
    }
    // $ANTLR end "listElement"

    // Delegated rules


    protected DFA5 dfa5 = new DFA5(this);
    protected DFA30 dfa30 = new DFA30(this);
    static final String DFA5_eotS =
        "\20\uffff";
    static final String DFA5_eofS =
        "\20\uffff";
    static final String DFA5_minS =
        "\2\26\1\4\2\uffff\1\4\3\uffff\1\10\1\uffff\1\10\1\uffff\2\16\1\uffff";
    static final String DFA5_maxS =
        "\2\40\1\41\2\uffff\1\41\3\uffff\1\31\1\uffff\1\31\1\uffff\2\30\1"+
        "\uffff";
    static final String DFA5_acceptS =
        "\3\uffff\1\5\1\10\1\uffff\1\4\1\7\1\1\1\uffff\1\3\1\uffff\1\2\2"+
        "\uffff\1\6";
    static final String DFA5_specialS =
        "\20\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\3\1\2\7\uffff\1\1\1\4",
            "\1\6\1\5\10\uffff\1\7",
            "\1\10\3\uffff\1\12\5\uffff\1\12\1\uffff\1\12\3\uffff\1\12\4"+
            "\uffff\2\12\6\uffff\1\11",
            "",
            "",
            "\1\10\3\uffff\1\14\5\uffff\1\14\1\uffff\1\14\3\uffff\1\14\4"+
            "\uffff\2\14\6\uffff\1\13",
            "",
            "",
            "",
            "\1\12\20\uffff\1\15",
            "",
            "\1\14\20\uffff\1\16",
            "",
            "\1\12\11\uffff\1\17",
            "\1\14\11\uffff\1\17",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "81:1: element : ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE );";
        }
    }
    static final String DFA30_eotS =
        "\23\uffff";
    static final String DFA30_eofS =
        "\23\uffff";
    static final String DFA30_minS =
        "\1\10\1\11\6\uffff\1\0\12\uffff";
    static final String DFA30_maxS =
        "\1\41\1\36\6\uffff\1\0\12\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\1\3\1\4\15\uffff\1\1";
    static final String DFA30_specialS =
        "\10\uffff\1\0\12\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\2\5\uffff\1\4\1\uffff\1\4\3\uffff\1\4\4\uffff\1\1\1\4\6\uffff"+
            "\1\3",
            "\1\4\3\uffff\1\4\1\10\1\4\1\uffff\3\4\4\uffff\1\4\4\uffff\2"+
            "\4",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "271:1: callExpr options {k=2; } : ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_8 = input.LA(1);

                         
                        int index30_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((Compiler.funcs.containsKey(input.LT(1).getText()))) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index30_8);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_template_in_templateAndEOF55 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_templateAndEOF57 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_template68 = new BitSet(new long[]{0x0000000180C00002L});
    public static final BitSet FOLLOW_INDENT_in_element84 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_ifstat_in_element93 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_NEWLINE_in_element104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDENT_in_element119 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_exprTag_in_element133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprTag_in_element149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDENT_in_element156 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_text_in_element170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_text_in_element189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDENT_in_element199 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_region_in_element205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDENT_in_element221 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_NEWLINE_in_element236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_element275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEXT_in_text297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_exprTag312 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_expr_in_exprTag316 = new BitSet(new long[]{0x0000000001000200L});
    public static final BitSet FOLLOW_SEMI_in_exprTag322 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_exprOptions_in_exprTag324 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_exprTag343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_region359 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_AT_in_region361 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_region363 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_region365 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_region373 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_END_in_region375 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_region377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_subtemplate393 = new BitSet(new long[]{0x0000000082200000L});
    public static final BitSet FOLLOW_ID_in_subtemplate399 = new BitSet(new long[]{0x0000000010040000L});
    public static final BitSet FOLLOW_COMMA_in_subtemplate402 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_subtemplate406 = new BitSet(new long[]{0x0000000010040000L});
    public static final BitSet FOLLOW_PIPE_in_subtemplate410 = new BitSet(new long[]{0x0000000080200000L});
    public static final BitSet FOLLOW_INDENT_in_subtemplate427 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RCURLY_in_subtemplate439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_addTemplateEndTokensToFollowOfTemplateRule454 = new BitSet(new long[]{0x0000000000A00000L});
    public static final BitSet FOLLOW_RCURLY_in_addTemplateEndTokensToFollowOfTemplateRule457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_addTemplateEndTokensToFollowOfTemplateRule459 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_END_in_addTemplateEndTokensToFollowOfTemplateRule461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat477 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IF_in_ifstat479 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_ifstat481 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_conditional_in_ifstat483 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_ifstat485 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat487 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat495 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat501 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat504 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ELSEIF_in_ifstat506 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_ifstat516 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_conditional_in_ifstat518 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_ifstat520 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat522 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat532 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat543 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat546 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ELSE_in_ifstat548 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat550 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat560 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat569 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat574 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ENDIF_in_ifstat576 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andConditional_in_conditional598 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_OR_in_conditional601 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_andConditional_in_conditional603 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_notConditional_in_andConditional619 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_AND_in_andConditional622 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_notConditional_in_andConditional624 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_BANG_in_notConditional639 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_memberExpr_in_notConditional641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_notConditional649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_option_in_exprOptions663 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_exprOptions666 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_option_in_exprOptions668 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_option681 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_EQUALS_in_option685 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_exprNoComma_in_option687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_exprNoComma707 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_COLON_in_exprNoComma713 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_exprNoComma715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapExpr_in_expr741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_mapExpr756 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpr761 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_memberExpr_in_mapExpr763 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COLON_in_mapExpr774 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_mapExpr776 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpr784 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_mapExpr786 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_callExpr_in_memberExpr840 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_memberExpr846 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_memberExpr848 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_memberExpr864 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_memberExpr868 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_mapExpr_in_memberExpr870 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_memberExpr874 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_ID_in_callExpr914 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr916 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_expr_in_callExpr918 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_callExpr933 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_callExpr935 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_callExpr939 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr954 = new BitSet(new long[]{0x000000020611C900L});
    public static final BitSet FOLLOW_args_in_callExpr956 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_callExpr964 = new BitSet(new long[]{0x0000000002000100L});
    public static final BitSet FOLLOW_SUPER_in_callExpr969 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_callExpr971 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_callExpr975 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr977 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_callExpr998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_primary1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subtemplate_in_primary1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_primary1076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary1083 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_expr_in_primary1085 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_primary1089 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_LPAREN_in_primary1117 = new BitSet(new long[]{0x000000020611C900L});
    public static final BitSet FOLLOW_args_in_primary1119 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_primary1122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arg_in_args1138 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_args1141 = new BitSet(new long[]{0x0000000206114900L});
    public static final BitSet FOLLOW_arg_in_args1143 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_arg1154 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_EQUALS_in_arg1156 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_exprNoComma_in_arg1158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprNoComma_in_arg1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELLIPSIS_in_arg1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_templateRef1200 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1203 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subtemplate_in_templateRef1216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1232 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_mapExpr_in_templateRef1234 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1238 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1240 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_list1277 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_listElement_in_list1279 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_COMMA_in_list1282 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_listElement_in_list1284 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_RBRACK_in_list1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_list1295 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACK_in_list1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprNoComma_in_listElement1313 = new BitSet(new long[]{0x0000000000000002L});

}