// $ANTLR 3.2 Sep 23, 2009 12:02:23 STParser.g 2009-12-21 12:39:07

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
    // STParser.g:72:1: templateAndEOF : template EOF ;
    public final void templateAndEOF() throws RecognitionException {
        try {
            // STParser.g:73:2: ( template EOF )
            // STParser.g:73:4: template EOF
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
    // STParser.g:76:1: template : ( element )* ;
    public final void template() throws RecognitionException {
        try {
            // STParser.g:77:2: ( ( element )* )
            // STParser.g:77:4: ( element )*
            {
            // STParser.g:77:4: ( element )*
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
            	    // STParser.g:77:4: element
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
    // STParser.g:80:1: element : ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE );
    public final void element() throws RecognitionException {
        CommonToken i=null;
        STParser.ifstat_return ifstat1 = null;

        STParser.region_return region2 = null;


        try {
            // STParser.g:81:2: ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE )
            int alt5=8;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // STParser.g:81:4: (i= INDENT )? ifstat ({...}? NEWLINE )?
                    {
                    // STParser.g:81:4: (i= INDENT )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==INDENT) ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // STParser.g:81:6: i= INDENT
                            {
                            i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element84); 

                            }
                            break;

                    }

                    int start_address = gen.address();
                    pushFollow(FOLLOW_ifstat_in_element93);
                    ifstat1=ifstat();

                    state._fsp--;

                    // STParser.g:84:3: ({...}? NEWLINE )?
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
                            // STParser.g:84:5: {...}? NEWLINE
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
                    // STParser.g:93:4: i= INDENT exprTag
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
                    // STParser.g:95:4: exprTag
                    {
                    pushFollow(FOLLOW_exprTag_in_element149);
                    exprTag();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // STParser.g:96:4: i= INDENT text
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
                    // STParser.g:98:4: text
                    {
                    pushFollow(FOLLOW_text_in_element189);
                    text();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // STParser.g:99:6: (i= INDENT )? region
                    {
                    // STParser.g:99:6: (i= INDENT )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==INDENT) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // STParser.g:99:7: i= INDENT
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
                    // STParser.g:107:4: i= INDENT NEWLINE
                    {
                    i=(CommonToken)match(input,INDENT,FOLLOW_INDENT_in_element221); 
                    indent((i!=null?i.getText():null));
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_element236); 
                    gen.emit(Bytecode.INSTR_NEWLINE);
                    gen.emit(Bytecode.INSTR_DEDENT);

                    }
                    break;
                case 8 :
                    // STParser.g:110:4: NEWLINE
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
    // STParser.g:113:1: text : TEXT ;
    public final void text() throws RecognitionException {
        CommonToken TEXT3=null;

        try {
            // STParser.g:114:2: ( TEXT )
            // STParser.g:114:4: TEXT
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
    // STParser.g:125:1: exprTag : LDELIM expr ( ';' exprOptions | ) RDELIM ;
    public final void exprTag() throws RecognitionException {
        CommonToken LDELIM4=null;

        try {
            // STParser.g:126:2: ( LDELIM expr ( ';' exprOptions | ) RDELIM )
            // STParser.g:126:4: LDELIM expr ( ';' exprOptions | ) RDELIM
            {
            LDELIM4=(CommonToken)match(input,LDELIM,FOLLOW_LDELIM_in_exprTag312); 
            pushFollow(FOLLOW_expr_in_exprTag316);
            expr();

            state._fsp--;

            // STParser.g:128:3: ( ';' exprOptions | )
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
                    // STParser.g:128:5: ';' exprOptions
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
                    // STParser.g:131:5: 
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
    // STParser.g:137:1: region returns [String name] : LDELIM '@' ID RDELIM LDELIM '@end' RDELIM ;
    public final STParser.region_return region() throws RecognitionException {
        STParser.region_return retval = new STParser.region_return();
        retval.start = input.LT(1);

        CommonToken ID5=null;

        try {
            // STParser.g:138:2: ( LDELIM '@' ID RDELIM LDELIM '@end' RDELIM )
            // STParser.g:138:4: LDELIM '@' ID RDELIM LDELIM '@end' RDELIM
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
    // STParser.g:143:1: subtemplate returns [String name] : '{' (ids+= ID ( ',' ids+= ID )* '|' )? '}' ;
    public final STParser.subtemplate_return subtemplate() throws RecognitionException {
        STParser.subtemplate_return retval = new STParser.subtemplate_return();
        retval.start = input.LT(1);

        CommonToken ids=null;
        List list_ids=null;

        try {
            // STParser.g:144:2: ( '{' (ids+= ID ( ',' ids+= ID )* '|' )? '}' )
            // STParser.g:144:4: '{' (ids+= ID ( ',' ids+= ID )* '|' )? '}'
            {
            match(input,LCURLY,FOLLOW_LCURLY_in_subtemplate393); 
            // STParser.g:144:8: (ids+= ID ( ',' ids+= ID )* '|' )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // STParser.g:144:10: ids+= ID ( ',' ids+= ID )* '|'
                    {
                    ids=(CommonToken)match(input,ID,FOLLOW_ID_in_subtemplate399); 
                    if (list_ids==null) list_ids=new ArrayList();
                    list_ids.add(ids);

                    // STParser.g:144:18: ( ',' ids+= ID )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==COMMA) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // STParser.g:144:19: ',' ids+= ID
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
            match(input,RCURLY,FOLLOW_RCURLY_in_subtemplate427); 

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
    // STParser.g:149:1: addTemplateEndTokensToFollowOfTemplateRule : template ( '}' | LDELIM '@end' ) ;
    public final void addTemplateEndTokensToFollowOfTemplateRule() throws RecognitionException {
        try {
            // STParser.g:154:44: ( template ( '}' | LDELIM '@end' ) )
            // STParser.g:154:46: template ( '}' | LDELIM '@end' )
            {
            pushFollow(FOLLOW_template_in_addTemplateEndTokensToFollowOfTemplateRule442);
            template();

            state._fsp--;

            // STParser.g:154:55: ( '}' | LDELIM '@end' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==RCURLY) ) {
                alt9=1;
            }
            else if ( (LA9_0==LDELIM) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // STParser.g:154:56: '}'
                    {
                    match(input,RCURLY,FOLLOW_RCURLY_in_addTemplateEndTokensToFollowOfTemplateRule445); 

                    }
                    break;
                case 2 :
                    // STParser.g:154:60: LDELIM '@end'
                    {
                    match(input,LDELIM,FOLLOW_LDELIM_in_addTemplateEndTokensToFollowOfTemplateRule447); 
                    match(input,END,FOLLOW_END_in_addTemplateEndTokensToFollowOfTemplateRule449); 

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
    // STParser.g:156:1: ifstat : LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM ;
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
            // STParser.g:167:2: ( LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM )
            // STParser.g:167:4: LDELIM 'if' '(' conditional ')' RDELIM template ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )* ( ( INDENT )? LDELIM 'else' RDELIM template )? ( INDENT )? endif= LDELIM 'endif' RDELIM
            {
            match(input,LDELIM,FOLLOW_LDELIM_in_ifstat465); 
            match(input,IF,FOLLOW_IF_in_ifstat467); 
            match(input,LPAREN,FOLLOW_LPAREN_in_ifstat469); 
            pushFollow(FOLLOW_conditional_in_ifstat471);
            conditional();

            state._fsp--;

            match(input,RPAREN,FOLLOW_RPAREN_in_ifstat473); 
            match(input,RDELIM,FOLLOW_RDELIM_in_ifstat475); 

                    prevBranchOperand = gen.address()+1;
                    gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
            		
            pushFollow(FOLLOW_template_in_ifstat483);
            template();

            state._fsp--;

            // STParser.g:173:3: ( ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==INDENT) ) {
                    int LA11_1 = input.LA(2);

                    if ( (LA11_1==LDELIM) ) {
                        int LA11_2 = input.LA(3);

                        if ( (LA11_2==ELSEIF) ) {
                            alt11=1;
                        }


                    }


                }
                else if ( (LA11_0==LDELIM) ) {
                    int LA11_2 = input.LA(2);

                    if ( (LA11_2==ELSEIF) ) {
                        alt11=1;
                    }


                }


                switch (alt11) {
            	case 1 :
            	    // STParser.g:173:5: ( INDENT )? LDELIM 'elseif' '(' conditional ')' RDELIM template
            	    {
            	    // STParser.g:173:5: ( INDENT )?
            	    int alt10=2;
            	    int LA10_0 = input.LA(1);

            	    if ( (LA10_0==INDENT) ) {
            	        alt10=1;
            	    }
            	    switch (alt10) {
            	        case 1 :
            	            // STParser.g:173:5: INDENT
            	            {
            	            match(input,INDENT,FOLLOW_INDENT_in_ifstat489); 

            	            }
            	            break;

            	    }

            	    match(input,LDELIM,FOLLOW_LDELIM_in_ifstat492); 
            	    match(input,ELSEIF,FOLLOW_ELSEIF_in_ifstat494); 

            	    			endRefs.add(gen.address()+1);
            	    			gen.emit(Bytecode.INSTR_BR, -1); // br end
            	    			// update previous branch instruction
            	    			gen.write(prevBranchOperand, (short)gen.address());
            	    			prevBranchOperand = -1;
            	    			
            	    match(input,LPAREN,FOLLOW_LPAREN_in_ifstat504); 
            	    pushFollow(FOLLOW_conditional_in_ifstat506);
            	    conditional();

            	    state._fsp--;

            	    match(input,RPAREN,FOLLOW_RPAREN_in_ifstat508); 
            	    match(input,RDELIM,FOLLOW_RDELIM_in_ifstat510); 

            	            	prevBranchOperand = gen.address()+1;
            	            	gen.emit(Bytecode.INSTR_BRF, -1); // write placeholder as branch target
            	    			
            	    pushFollow(FOLLOW_template_in_ifstat520);
            	    template();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            // STParser.g:188:3: ( ( INDENT )? LDELIM 'else' RDELIM template )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==INDENT) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==LDELIM) ) {
                    int LA13_2 = input.LA(3);

                    if ( (LA13_2==ELSE) ) {
                        alt13=1;
                    }
                }
            }
            else if ( (LA13_0==LDELIM) ) {
                int LA13_2 = input.LA(2);

                if ( (LA13_2==ELSE) ) {
                    alt13=1;
                }
            }
            switch (alt13) {
                case 1 :
                    // STParser.g:188:5: ( INDENT )? LDELIM 'else' RDELIM template
                    {
                    // STParser.g:188:5: ( INDENT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==INDENT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // STParser.g:188:5: INDENT
                            {
                            match(input,INDENT,FOLLOW_INDENT_in_ifstat531); 

                            }
                            break;

                    }

                    match(input,LDELIM,FOLLOW_LDELIM_in_ifstat534); 
                    match(input,ELSE,FOLLOW_ELSE_in_ifstat536); 
                    match(input,RDELIM,FOLLOW_RDELIM_in_ifstat538); 

                    			endRefs.add(gen.address()+1);
                    			gen.emit(Bytecode.INSTR_BR, -1); // br end
                    			// update previous branch instruction
                    			gen.write(prevBranchOperand, (short)gen.address());
                    			prevBranchOperand = -1;
                    			
                    pushFollow(FOLLOW_template_in_ifstat548);
                    template();

                    state._fsp--;


                    }
                    break;

            }

            // STParser.g:198:3: ( INDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==INDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // STParser.g:198:3: INDENT
                    {
                    match(input,INDENT,FOLLOW_INDENT_in_ifstat557); 

                    }
                    break;

            }

            endif=(CommonToken)match(input,LDELIM,FOLLOW_LDELIM_in_ifstat562); 
            match(input,ENDIF,FOLLOW_ENDIF_in_ifstat564); 
            match(input,RDELIM,FOLLOW_RDELIM_in_ifstat566); 

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
    // STParser.g:208:1: conditional : andConditional ( '||' andConditional )* ;
    public final void conditional() throws RecognitionException {
        try {
            // STParser.g:209:2: ( andConditional ( '||' andConditional )* )
            // STParser.g:209:4: andConditional ( '||' andConditional )*
            {
            pushFollow(FOLLOW_andConditional_in_conditional586);
            andConditional();

            state._fsp--;

            // STParser.g:209:19: ( '||' andConditional )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==OR) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // STParser.g:209:20: '||' andConditional
            	    {
            	    match(input,OR,FOLLOW_OR_in_conditional589); 
            	    pushFollow(FOLLOW_andConditional_in_conditional591);
            	    andConditional();

            	    state._fsp--;

            	    gen.emit(Bytecode.INSTR_OR);

            	    }
            	    break;

            	default :
            	    break loop15;
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
    // STParser.g:212:1: andConditional : notConditional ( '&&' notConditional )* ;
    public final void andConditional() throws RecognitionException {
        try {
            // STParser.g:213:2: ( notConditional ( '&&' notConditional )* )
            // STParser.g:213:4: notConditional ( '&&' notConditional )*
            {
            pushFollow(FOLLOW_notConditional_in_andConditional607);
            notConditional();

            state._fsp--;

            // STParser.g:213:19: ( '&&' notConditional )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==AND) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // STParser.g:213:20: '&&' notConditional
            	    {
            	    match(input,AND,FOLLOW_AND_in_andConditional610); 
            	    pushFollow(FOLLOW_notConditional_in_andConditional612);
            	    notConditional();

            	    state._fsp--;

            	    gen.emit(Bytecode.INSTR_AND);

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
    // $ANTLR end "andConditional"


    // $ANTLR start "notConditional"
    // STParser.g:216:1: notConditional : ( '!' memberExpr | memberExpr );
    public final void notConditional() throws RecognitionException {
        try {
            // STParser.g:217:2: ( '!' memberExpr | memberExpr )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==BANG) ) {
                alt17=1;
            }
            else if ( (LA17_0==SUPER||LA17_0==LPAREN||LA17_0==LBRACK||LA17_0==LCURLY||(LA17_0>=ID && LA17_0<=STRING)||LA17_0==AT) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // STParser.g:217:4: '!' memberExpr
                    {
                    match(input,BANG,FOLLOW_BANG_in_notConditional627); 
                    pushFollow(FOLLOW_memberExpr_in_notConditional629);
                    memberExpr();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_NOT);

                    }
                    break;
                case 2 :
                    // STParser.g:218:4: memberExpr
                    {
                    pushFollow(FOLLOW_memberExpr_in_notConditional637);
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
    // STParser.g:221:1: exprOptions : option ( ',' option )* ;
    public final void exprOptions() throws RecognitionException {
        try {
            // STParser.g:222:2: ( option ( ',' option )* )
            // STParser.g:222:4: option ( ',' option )*
            {
            gen.emit(Bytecode.INSTR_OPTIONS);
            pushFollow(FOLLOW_option_in_exprOptions651);
            option();

            state._fsp--;

            // STParser.g:222:47: ( ',' option )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // STParser.g:222:48: ',' option
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_exprOptions654); 
            	    pushFollow(FOLLOW_option_in_exprOptions656);
            	    option();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop18;
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
    // STParser.g:225:1: option : ID ( '=' exprNoComma | ) ;
    public final void option() throws RecognitionException {
        CommonToken ID6=null;

        try {
            // STParser.g:226:2: ( ID ( '=' exprNoComma | ) )
            // STParser.g:226:4: ID ( '=' exprNoComma | )
            {
            ID6=(CommonToken)match(input,ID,FOLLOW_ID_in_option669); 
            // STParser.g:226:7: ( '=' exprNoComma | )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==EQUALS) ) {
                alt19=1;
            }
            else if ( (LA19_0==COMMA||LA19_0==RDELIM) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // STParser.g:226:9: '=' exprNoComma
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_option673); 
                    pushFollow(FOLLOW_exprNoComma_in_option675);
                    exprNoComma();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // STParser.g:226:27: 
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
    // STParser.g:229:1: exprNoComma : memberExpr ( ':' templateRef )? ;
    public final STParser.exprNoComma_return exprNoComma() throws RecognitionException {
        STParser.exprNoComma_return retval = new STParser.exprNoComma_return();
        retval.start = input.LT(1);

        STParser.templateRef_return templateRef7 = null;


        try {
            // STParser.g:230:2: ( memberExpr ( ':' templateRef )? )
            // STParser.g:230:4: memberExpr ( ':' templateRef )?
            {
            pushFollow(FOLLOW_memberExpr_in_exprNoComma695);
            memberExpr();

            state._fsp--;

            // STParser.g:231:3: ( ':' templateRef )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==COLON) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // STParser.g:231:5: ':' templateRef
                    {
                    match(input,COLON,FOLLOW_COLON_in_exprNoComma701); 
                    pushFollow(FOLLOW_templateRef_in_exprNoComma703);
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
    // STParser.g:240:1: expr : mapExpr ;
    public final void expr() throws RecognitionException {
        try {
            // STParser.g:240:6: ( mapExpr )
            // STParser.g:240:8: mapExpr
            {
            pushFollow(FOLLOW_mapExpr_in_expr729);
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
    // STParser.g:242:1: mapExpr : memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )* ;
    public final STParser.mapExpr_return mapExpr() throws RecognitionException {
        STParser.mapExpr_return retval = new STParser.mapExpr_return();
        retval.start = input.LT(1);

        CommonToken c=null;

        int nt=1, ne=1; int a=((CommonToken)retval.start).getStartIndex();
        try {
            // STParser.g:244:2: ( memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )* )
            // STParser.g:244:4: memberExpr (c= ',' memberExpr )* ( ':' templateRef ( ( ',' templateRef )+ | ) )*
            {
            pushFollow(FOLLOW_memberExpr_in_mapExpr744);
            memberExpr();

            state._fsp--;

            // STParser.g:244:15: (c= ',' memberExpr )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==COMMA) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // STParser.g:244:16: c= ',' memberExpr
            	    {
            	    c=(CommonToken)match(input,COMMA,FOLLOW_COMMA_in_mapExpr749); 
            	    pushFollow(FOLLOW_memberExpr_in_mapExpr751);
            	    memberExpr();

            	    state._fsp--;

            	    ne++;

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // STParser.g:245:3: ( ':' templateRef ( ( ',' templateRef )+ | ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==COLON) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // STParser.g:245:5: ':' templateRef ( ( ',' templateRef )+ | )
            	    {
            	    match(input,COLON,FOLLOW_COLON_in_mapExpr762); 
            	    pushFollow(FOLLOW_templateRef_in_mapExpr764);
            	    templateRef();

            	    state._fsp--;

            	    // STParser.g:246:4: ( ( ',' templateRef )+ | )
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==COMMA) ) {
            	        alt23=1;
            	    }
            	    else if ( (LA23_0==SEMI||LA23_0==COLON||LA23_0==RPAREN||LA23_0==RDELIM) ) {
            	        alt23=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 23, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // STParser.g:246:6: ( ',' templateRef )+
            	            {
            	            // STParser.g:246:6: ( ',' templateRef )+
            	            int cnt22=0;
            	            loop22:
            	            do {
            	                int alt22=2;
            	                int LA22_0 = input.LA(1);

            	                if ( (LA22_0==COMMA) ) {
            	                    alt22=1;
            	                }


            	                switch (alt22) {
            	            	case 1 :
            	            	    // STParser.g:246:7: ',' templateRef
            	            	    {
            	            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpr772); 
            	            	    pushFollow(FOLLOW_templateRef_in_mapExpr774);
            	            	    templateRef();

            	            	    state._fsp--;

            	            	    nt++;

            	            	    }
            	            	    break;

            	            	default :
            	            	    if ( cnt22 >= 1 ) break loop22;
            	                        EarlyExitException eee =
            	                            new EarlyExitException(22, input);
            	                        throw eee;
            	                }
            	                cnt22++;
            	            } while (true);

            	            gen.emit(Bytecode.INSTR_ROT_MAP, nt, a,
            	            						              ((CommonToken)input.LT(-1)).getStopIndex());

            	            }
            	            break;
            	        case 2 :
            	            // STParser.g:249:17: 
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
            	    break loop24;
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
    // STParser.g:259:1: memberExpr : callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )* ;
    public final void memberExpr() throws RecognitionException {
        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken ID8=null;

        try {
            // STParser.g:260:2: ( callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )* )
            // STParser.g:260:4: callExpr ( '.' ID | '.' lp= '(' mapExpr rp= ')' )*
            {
            pushFollow(FOLLOW_callExpr_in_memberExpr828);
            callExpr();

            state._fsp--;

            // STParser.g:261:3: ( '.' ID | '.' lp= '(' mapExpr rp= ')' )*
            loop25:
            do {
                int alt25=3;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==DOT) ) {
                    int LA25_2 = input.LA(2);

                    if ( (LA25_2==ID) ) {
                        alt25=1;
                    }
                    else if ( (LA25_2==LPAREN) ) {
                        alt25=2;
                    }


                }


                switch (alt25) {
            	case 1 :
            	    // STParser.g:261:5: '.' ID
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_memberExpr834); 
            	    ID8=(CommonToken)match(input,ID,FOLLOW_ID_in_memberExpr836); 
            	    gen.emit(Bytecode.INSTR_LOAD_PROP, (ID8!=null?ID8.getText():null),
            	    					                 ID8.getStartIndex(), ID8.getStopIndex());

            	    }
            	    break;
            	case 2 :
            	    // STParser.g:263:5: '.' lp= '(' mapExpr rp= ')'
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_memberExpr852); 
            	    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_memberExpr856); 
            	    pushFollow(FOLLOW_mapExpr_in_memberExpr858);
            	    mapExpr();

            	    state._fsp--;

            	    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_memberExpr862); 
            	    gen.emit(Bytecode.INSTR_LOAD_PROP_IND,
            	    						   		     lp.getStartIndex(),rp.getStartIndex());

            	    }
            	    break;

            	default :
            	    break loop25;
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
    // STParser.g:269:1: callExpr options {k=2; } : ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary );
    public final STParser.callExpr_return callExpr() throws RecognitionException {
        STParser.callExpr_return retval = new STParser.callExpr_return();
        retval.start = input.LT(1);

        CommonToken s=null;
        CommonToken rp=null;
        CommonToken ID9=null;
        CommonToken ID10=null;
        CommonToken ID11=null;

        try {
            // STParser.g:271:2: ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary )
            int alt29=4;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // STParser.g:271:4: {...}? ID '(' expr ')'
                    {
                    if ( !((Compiler.funcs.containsKey(input.LT(1).getText()))) ) {
                        throw new FailedPredicateException(input, "callExpr", "Compiler.funcs.containsKey(input.LT(1).getText())");
                    }
                    ID9=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr902); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr904); 
                    pushFollow(FOLLOW_expr_in_callExpr906);
                    expr();

                    state._fsp--;

                    match(input,RPAREN,FOLLOW_RPAREN_in_callExpr908); 
                    gen.func(ID9);

                    }
                    break;
                case 2 :
                    // STParser.g:273:4: (s= 'super' '.' )? ID '(' ( args )? ')'
                    {
                    // STParser.g:273:4: (s= 'super' '.' )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==SUPER) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // STParser.g:273:5: s= 'super' '.'
                            {
                            s=(CommonToken)match(input,SUPER,FOLLOW_SUPER_in_callExpr921); 
                            match(input,DOT,FOLLOW_DOT_in_callExpr923); 

                            }
                            break;

                    }

                    ID10=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr927); 
                    gen.emit(s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
                    								     gen.prefixedName((ID10!=null?ID10.getText():null)),
                    								     ((CommonToken)retval.start).getStartIndex(), ID10.getStopIndex());
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr942); 
                    // STParser.g:277:7: ( args )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==SUPER||LA27_0==ELLIPSIS||LA27_0==LPAREN||LA27_0==LBRACK||LA27_0==LCURLY||(LA27_0>=ID && LA27_0<=STRING)||LA27_0==AT) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // STParser.g:277:7: args
                            {
                            pushFollow(FOLLOW_args_in_callExpr944);
                            args();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_callExpr947); 

                    }
                    break;
                case 3 :
                    // STParser.g:278:4: '@' (s= 'super' '.' )? ID '(' rp= ')'
                    {
                    match(input,AT,FOLLOW_AT_in_callExpr952); 
                    // STParser.g:278:8: (s= 'super' '.' )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==SUPER) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // STParser.g:278:9: s= 'super' '.'
                            {
                            s=(CommonToken)match(input,SUPER,FOLLOW_SUPER_in_callExpr957); 
                            match(input,DOT,FOLLOW_DOT_in_callExpr959); 

                            }
                            break;

                    }

                    ID11=(CommonToken)match(input,ID,FOLLOW_ID_in_callExpr963); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_callExpr965); 
                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_callExpr969); 

                    						   gen.defineBlankRegion(enclosingTemplateName, (ID11!=null?ID11.getText():null));
                    						   String mangled = STGroup.getMangledRegionName(enclosingTemplateName, (ID11!=null?ID11.getText():null));
                    						   gen.emit(s!=null?Bytecode.INSTR_SUPER_NEW:Bytecode.INSTR_NEW,
                    							   	    gen.prefixedName(mangled),
                    								    ((CommonToken)retval.start).getStartIndex(), rp.getStartIndex());
                    						   

                    }
                    break;
                case 4 :
                    // STParser.g:286:4: primary
                    {
                    pushFollow(FOLLOW_primary_in_callExpr986);
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
    // STParser.g:289:1: primary : (o= ID | STRING | subtemplate | list | lp= '(' expr rp= ')' ( '(' ( args )? ')' )? );
    public final void primary() throws RecognitionException {
        CommonToken o=null;
        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken STRING12=null;
        STParser.subtemplate_return subtemplate13 = null;


        try {
            // STParser.g:290:2: (o= ID | STRING | subtemplate | list | lp= '(' expr rp= ')' ( '(' ( args )? ')' )? )
            int alt32=5;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt32=1;
                }
                break;
            case STRING:
                {
                alt32=2;
                }
                break;
            case LCURLY:
                {
                alt32=3;
                }
                break;
            case LBRACK:
                {
                alt32=4;
                }
                break;
            case LPAREN:
                {
                alt32=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // STParser.g:290:4: o= ID
                    {
                    o=(CommonToken)match(input,ID,FOLLOW_ID_in_primary999); 
                    gen.refAttr(o);

                    }
                    break;
                case 2 :
                    // STParser.g:291:4: STRING
                    {
                    STRING12=(CommonToken)match(input,STRING,FOLLOW_STRING_in_primary1017); 
                    gen.emit(Bytecode.INSTR_LOAD_STR,
                    									 Misc.strip((STRING12!=null?STRING12.getText():null),1),
                    							 		 STRING12.getStartIndex(), STRING12.getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:294:4: subtemplate
                    {
                    pushFollow(FOLLOW_subtemplate_in_primary1036);
                    subtemplate13=subtemplate();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_NEW, gen.prefixedName((subtemplate13!=null?subtemplate13.name:null)),
                    									 (subtemplate13!=null?((CommonToken)subtemplate13.start):null).getStartIndex(),
                    									 (subtemplate13!=null?((CommonToken)subtemplate13.stop):null).getStopIndex());

                    }
                    break;
                case 4 :
                    // STParser.g:298:4: list
                    {
                    pushFollow(FOLLOW_list_in_primary1064);
                    list();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // STParser.g:299:4: lp= '(' expr rp= ')' ( '(' ( args )? ')' )?
                    {
                    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_primary1071); 
                    pushFollow(FOLLOW_expr_in_primary1073);
                    expr();

                    state._fsp--;

                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_primary1077); 
                    gen.emit(Bytecode.INSTR_TOSTR,
                    									 lp.getStartIndex(),rp.getStartIndex());
                    // STParser.g:301:3: ( '(' ( args )? ')' )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==LPAREN) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // STParser.g:301:20: '(' ( args )? ')'
                            {
                            gen.emit(Bytecode.INSTR_NEW_IND,
                                                    		     lp.getStartIndex(),rp.getStartIndex());
                            match(input,LPAREN,FOLLOW_LPAREN_in_primary1105); 
                            // STParser.g:303:8: ( args )?
                            int alt30=2;
                            int LA30_0 = input.LA(1);

                            if ( (LA30_0==SUPER||LA30_0==ELLIPSIS||LA30_0==LPAREN||LA30_0==LBRACK||LA30_0==LCURLY||(LA30_0>=ID && LA30_0<=STRING)||LA30_0==AT) ) {
                                alt30=1;
                            }
                            switch (alt30) {
                                case 1 :
                                    // STParser.g:303:8: args
                                    {
                                    pushFollow(FOLLOW_args_in_primary1107);
                                    args();

                                    state._fsp--;


                                    }
                                    break;

                            }

                            match(input,RPAREN,FOLLOW_RPAREN_in_primary1110); 

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
    // STParser.g:307:1: args : arg ( ',' arg )* ;
    public final void args() throws RecognitionException {
        try {
            // STParser.g:307:5: ( arg ( ',' arg )* )
            // STParser.g:307:7: arg ( ',' arg )*
            {
            pushFollow(FOLLOW_arg_in_args1126);
            arg();

            state._fsp--;

            // STParser.g:307:11: ( ',' arg )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==COMMA) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // STParser.g:307:12: ',' arg
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args1129); 
            	    pushFollow(FOLLOW_arg_in_args1131);
            	    arg();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop33;
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
    // STParser.g:309:1: arg : ( ID '=' exprNoComma | exprNoComma | elip= '...' );
    public final void arg() throws RecognitionException {
        CommonToken elip=null;
        CommonToken ID14=null;
        STParser.exprNoComma_return exprNoComma15 = null;

        STParser.exprNoComma_return exprNoComma16 = null;


        try {
            // STParser.g:309:5: ( ID '=' exprNoComma | exprNoComma | elip= '...' )
            int alt34=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==EQUALS) ) {
                    alt34=1;
                }
                else if ( ((LA34_1>=COLON && LA34_1<=RPAREN)||(LA34_1>=COMMA && LA34_1<=DOT)) ) {
                    alt34=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 1, input);

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
                alt34=2;
                }
                break;
            case ELLIPSIS:
                {
                alt34=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // STParser.g:309:7: ID '=' exprNoComma
                    {
                    ID14=(CommonToken)match(input,ID,FOLLOW_ID_in_arg1142); 
                    match(input,EQUALS,FOLLOW_EQUALS_in_arg1144); 
                    pushFollow(FOLLOW_exprNoComma_in_arg1146);
                    exprNoComma15=exprNoComma();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_STORE_ATTR, (ID14!=null?ID14.getText():null),
                    					 				 ID14.getStartIndex(), (exprNoComma15!=null?((CommonToken)exprNoComma15.stop):null).getStopIndex());

                    }
                    break;
                case 2 :
                    // STParser.g:311:4: exprNoComma
                    {
                    pushFollow(FOLLOW_exprNoComma_in_arg1153);
                    exprNoComma16=exprNoComma();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_STORE_SOLE_ARG,
                    									 (exprNoComma16!=null?((CommonToken)exprNoComma16.start):null).getStartIndex(),
                    									 (exprNoComma16!=null?((CommonToken)exprNoComma16.stop):null).getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:314:4: elip= '...'
                    {
                    elip=(CommonToken)match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_arg1169); 
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
    // STParser.g:317:1: templateRef : ( ID '(' ')' | subtemplate | lp= '(' mapExpr rp= ')' '(' ')' );
    public final STParser.templateRef_return templateRef() throws RecognitionException {
        STParser.templateRef_return retval = new STParser.templateRef_return();
        retval.start = input.LT(1);

        CommonToken lp=null;
        CommonToken rp=null;
        CommonToken ID17=null;
        STParser.subtemplate_return subtemplate18 = null;


        try {
            // STParser.g:323:2: ( ID '(' ')' | subtemplate | lp= '(' mapExpr rp= ')' '(' ')' )
            int alt35=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt35=1;
                }
                break;
            case LCURLY:
                {
                alt35=2;
                }
                break;
            case LPAREN:
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
                    // STParser.g:323:4: ID '(' ')'
                    {
                    ID17=(CommonToken)match(input,ID,FOLLOW_ID_in_templateRef1188); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1191); 
                    match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1193); 
                    gen.emit(Bytecode.INSTR_LOAD_STR,gen.prefixedName((ID17!=null?ID17.getText():null)),
                                       		 		     ID17.getStartIndex(), ID17.getStopIndex());

                    }
                    break;
                case 2 :
                    // STParser.g:325:4: subtemplate
                    {
                    pushFollow(FOLLOW_subtemplate_in_templateRef1204);
                    subtemplate18=subtemplate();

                    state._fsp--;

                    gen.emit(Bytecode.INSTR_LOAD_STR,
                    	                                 gen.prefixedName((subtemplate18!=null?subtemplate18.name:null)),
                    									 (subtemplate18!=null?((CommonToken)subtemplate18.start):null).getStartIndex(),
                    									 (subtemplate18!=null?((CommonToken)subtemplate18.stop):null).getStopIndex());

                    }
                    break;
                case 3 :
                    // STParser.g:329:4: lp= '(' mapExpr rp= ')' '(' ')'
                    {
                    lp=(CommonToken)match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1220); 
                    pushFollow(FOLLOW_mapExpr_in_templateRef1222);
                    mapExpr();

                    state._fsp--;

                    rp=(CommonToken)match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1226); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_templateRef1228); 
                    match(input,RPAREN,FOLLOW_RPAREN_in_templateRef1230); 
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
    // STParser.g:334:1: list : ( '[' listElement ( ',' listElement )* ']' | '[' ']' );
    public final void list() throws RecognitionException {
        try {
            // STParser.g:334:5: ( '[' listElement ( ',' listElement )* ']' | '[' ']' )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==LBRACK) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==RBRACK) ) {
                    alt37=2;
                }
                else if ( (LA37_1==SUPER||LA37_1==LPAREN||LA37_1==LBRACK||LA37_1==LCURLY||(LA37_1>=ID && LA37_1<=STRING)||LA37_1==AT) ) {
                    alt37=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 37, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // STParser.g:334:7: '[' listElement ( ',' listElement )* ']'
                    {
                    gen.emit(Bytecode.INSTR_LIST);
                    match(input,LBRACK,FOLLOW_LBRACK_in_list1265); 
                    pushFollow(FOLLOW_listElement_in_list1267);
                    listElement();

                    state._fsp--;

                    // STParser.g:334:56: ( ',' listElement )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==COMMA) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // STParser.g:334:57: ',' listElement
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_list1270); 
                    	    pushFollow(FOLLOW_listElement_in_list1272);
                    	    listElement();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop36;
                        }
                    } while (true);

                    match(input,RBRACK,FOLLOW_RBRACK_in_list1276); 

                    }
                    break;
                case 2 :
                    // STParser.g:335:4: '[' ']'
                    {
                    gen.emit(Bytecode.INSTR_LIST);
                    match(input,LBRACK,FOLLOW_LBRACK_in_list1283); 
                    match(input,RBRACK,FOLLOW_RBRACK_in_list1285); 

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
    // STParser.g:338:1: listElement : exprNoComma ;
    public final void listElement() throws RecognitionException {
        STParser.exprNoComma_return exprNoComma19 = null;


        try {
            // STParser.g:339:5: ( exprNoComma )
            // STParser.g:339:9: exprNoComma
            {
            pushFollow(FOLLOW_exprNoComma_in_listElement1301);
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
    protected DFA29 dfa29 = new DFA29(this);
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
            return "80:1: element : ( (i= INDENT )? ifstat ({...}? NEWLINE )? | i= INDENT exprTag | exprTag | i= INDENT text | text | (i= INDENT )? region | i= INDENT NEWLINE | NEWLINE );";
        }
    }
    static final String DFA29_eotS =
        "\23\uffff";
    static final String DFA29_eofS =
        "\23\uffff";
    static final String DFA29_minS =
        "\1\10\1\11\6\uffff\1\0\12\uffff";
    static final String DFA29_maxS =
        "\1\41\1\36\6\uffff\1\0\12\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\1\3\1\4\15\uffff\1\1";
    static final String DFA29_specialS =
        "\10\uffff\1\0\12\uffff}>";
    static final String[] DFA29_transitionS = {
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

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "269:1: callExpr options {k=2; } : ({...}? ID '(' expr ')' | (s= 'super' '.' )? ID '(' ( args )? ')' | '@' (s= 'super' '.' )? ID '(' rp= ')' | primary );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_8 = input.LA(1);

                         
                        int index29_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((Compiler.funcs.containsKey(input.LT(1).getText()))) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_8);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
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
    public static final BitSet FOLLOW_LCURLY_in_subtemplate393 = new BitSet(new long[]{0x0000000002200000L});
    public static final BitSet FOLLOW_ID_in_subtemplate399 = new BitSet(new long[]{0x0000000010040000L});
    public static final BitSet FOLLOW_COMMA_in_subtemplate402 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_subtemplate406 = new BitSet(new long[]{0x0000000010040000L});
    public static final BitSet FOLLOW_PIPE_in_subtemplate410 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RCURLY_in_subtemplate427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_addTemplateEndTokensToFollowOfTemplateRule442 = new BitSet(new long[]{0x0000000000A00000L});
    public static final BitSet FOLLOW_RCURLY_in_addTemplateEndTokensToFollowOfTemplateRule445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_addTemplateEndTokensToFollowOfTemplateRule447 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_END_in_addTemplateEndTokensToFollowOfTemplateRule449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat465 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IF_in_ifstat467 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_ifstat469 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_conditional_in_ifstat471 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_ifstat473 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat475 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat483 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat489 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat492 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ELSEIF_in_ifstat494 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_ifstat504 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_conditional_in_ifstat506 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_ifstat508 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat510 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat520 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat531 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat534 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ELSE_in_ifstat536 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat538 = new BitSet(new long[]{0x0000000180C00000L});
    public static final BitSet FOLLOW_template_in_ifstat548 = new BitSet(new long[]{0x0000000080800000L});
    public static final BitSet FOLLOW_INDENT_in_ifstat557 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_LDELIM_in_ifstat562 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ENDIF_in_ifstat564 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_RDELIM_in_ifstat566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andConditional_in_conditional586 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_OR_in_conditional589 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_andConditional_in_conditional591 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_notConditional_in_andConditional607 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_AND_in_andConditional610 = new BitSet(new long[]{0x0000000206114500L});
    public static final BitSet FOLLOW_notConditional_in_andConditional612 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_BANG_in_notConditional627 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_memberExpr_in_notConditional629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_notConditional637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_option_in_exprOptions651 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_exprOptions654 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_option_in_exprOptions656 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_option669 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_EQUALS_in_option673 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_exprNoComma_in_option675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_exprNoComma695 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_COLON_in_exprNoComma701 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_exprNoComma703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapExpr_in_expr729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberExpr_in_mapExpr744 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpr749 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_memberExpr_in_mapExpr751 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COLON_in_mapExpr762 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_mapExpr764 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpr772 = new BitSet(new long[]{0x0000000002104000L});
    public static final BitSet FOLLOW_templateRef_in_mapExpr774 = new BitSet(new long[]{0x0000000000042002L});
    public static final BitSet FOLLOW_callExpr_in_memberExpr828 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_memberExpr834 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_memberExpr836 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_memberExpr852 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_memberExpr856 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_mapExpr_in_memberExpr858 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_memberExpr862 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_ID_in_callExpr902 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr904 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_expr_in_callExpr906 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_callExpr921 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_callExpr923 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_callExpr927 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr942 = new BitSet(new long[]{0x000000020611C900L});
    public static final BitSet FOLLOW_args_in_callExpr944 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_callExpr952 = new BitSet(new long[]{0x0000000002000100L});
    public static final BitSet FOLLOW_SUPER_in_callExpr957 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_DOT_in_callExpr959 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_ID_in_callExpr963 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_callExpr965 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_callExpr969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_callExpr986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_primary1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subtemplate_in_primary1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_primary1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_primary1071 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_expr_in_primary1073 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_primary1077 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_LPAREN_in_primary1105 = new BitSet(new long[]{0x000000020611C900L});
    public static final BitSet FOLLOW_args_in_primary1107 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_primary1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arg_in_args1126 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_args1129 = new BitSet(new long[]{0x0000000206114900L});
    public static final BitSet FOLLOW_arg_in_args1131 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ID_in_arg1142 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_EQUALS_in_arg1144 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_exprNoComma_in_arg1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprNoComma_in_arg1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELLIPSIS_in_arg1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_templateRef1188 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1191 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_subtemplate_in_templateRef1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1220 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_mapExpr_in_templateRef1222 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1226 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_LPAREN_in_templateRef1228 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_RPAREN_in_templateRef1230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_list1265 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_listElement_in_list1267 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_COMMA_in_list1270 = new BitSet(new long[]{0x0000000206114100L});
    public static final BitSet FOLLOW_listElement_in_list1272 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_RBRACK_in_list1276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACK_in_list1283 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACK_in_list1285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprNoComma_in_listElement1301 = new BitSet(new long[]{0x0000000000000002L});

}