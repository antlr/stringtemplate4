/*
 [The "BSD license"]
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
package org.stringtemplate.v4.compiler;

import antlr.MismatchedCharException;
import org.antlr.runtime.*;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.Misc;
import org.stringtemplate.v4.compiler.STException;

import java.util.ArrayList;
import java.util.List;

/** This class represents the tokenizer for templates. It operates in two modes:
 *  inside and outside of expressions. It behaves like an ANTLR TokenSource,
 *  implementing nextToken().  Outside of expressions, we can return these
 *  token types: TEXT, INDENT, LDELIM (start of expr), RCURLY (end of subtemplate),
 *  and NEWLINE.  Inside of an expression, this lexer returns all of the tokens
 *  needed by the STParser. From the parser's point of view, it can treat a
 *  template as a simple stream of elements.
 *
 *  This class defines the token types and communicates these values to STParser.g
 *  via STLexer.tokens file (which must remain consistent).
 */
public class STLexer implements TokenSource {
    public static final char EOF = (char)-1;            // EOF char
    public static final int EOF_TYPE = CharStream.EOF;  // EOF token type

    /** We build STToken tokens instead of relying on CommonToken so we
     *  can override toString(). It just converts token types to
     *  token names like 23 to LDELIM.
     */
    public static class STToken extends CommonToken {
        public STToken(CharStream input, int type, int start, int stop) {
            super(input, type, DEFAULT_CHANNEL, start, stop);
        }
        public STToken(int type, String text) { super(type, text); }

        public String toString() {
            String channelStr = "";
            if ( channel>0 ) {
                channelStr=",channel="+channel;
            }
            String txt = getText();
            if ( txt!=null ) txt = Misc.replaceEscapes(txt);
            else txt = "<no text>";
            return "[@"+getTokenIndex()+","+start+":"+stop+"='"+txt+"',<"+ STParser.tokenNames[type]+">"+channelStr+","+line+":"+getCharPositionInLine()+"]";
        }
    }

    public static final Token SKIP = new STToken(-1, "<skip>");

    // must follow STLexer.tokens file that STParser.g loads
    public static final int RBRACK=17;
    public static final int LBRACK=16;
    public static final int ELSE=5;
    public static final int ELLIPSIS=11;
    public static final int LCURLY=20;
    public static final int BANG=10;
    public static final int EQUALS=12;
    public static final int TEXT=22;
    public static final int ID=25;
    public static final int SEMI=9;
    public static final int LPAREN=14;
    public static final int IF=4;
    public static final int ELSEIF=6;
    public static final int COLON=13;
    public static final int RPAREN=15;
    public static final int COMMA=18;
    public static final int RCURLY=21;
    public static final int ENDIF=7;
    public static final int RDELIM=24;
    public static final int SUPER=8;
    public static final int DOT=19;
    public static final int LDELIM=23;
    public static final int STRING=26;
	public static final int PIPE=28;
	public static final int OR=29;
	public static final int AND=30;
	public static final int INDENT=31;
    public static final int NEWLINE=32;
    public static final int AT=33;
    public static final int REGION_END=34;

    /** What char starts an expression? */
    char delimiterStartChar = '<';
    char delimiterStopChar = '>';

    /** This keep track of the mode of the lexer. Are we inside or outside
     *  an ST expression?
     */
    boolean scanningInsideExpr = false;

    /** To be able to properly track the inside/outside mode, we need to
     *  track how deeply nested we are in some templates. Otherwise, we
     *  know whether a '}' and the outermost subtemplate to send this back to
     *  outside mode.
     */
	public int subtemplateDepth = 0; // start out *not* in a {...} subtemplate

    CharStream input;
    char c;        // current character

    /** When we started token, track initial coordinates so we can properly
     *  build token objects.
     */
    int startCharIndex;
    int startLine;
    int startCharPositionInLine;

    /** Our lexer routines might have to emit more than a single token. We
     *  buffer everything through this list.
     */
    List<Token> tokens = new ArrayList<Token>();

    public Token nextToken() {
        if ( tokens.size()>0 ) { return tokens.remove(0); }
        return _nextToken();
    }

    public STLexer(CharStream input) { this(input, '<', '>'); }

	public STLexer(CharStream input, char delimiterStartChar, char delimiterStopChar) {
		this.input = input;
		c = (char)input.LA(1); // prime lookahead
		this.delimiterStartChar = delimiterStartChar;
		this.delimiterStopChar = delimiterStopChar;
	}

    /** Ensure x is next character on the input stream */
    public void match(char x) {
        if ( c == x) consume();
        else throw new Error(getErrorHeader()+": expecting "+x+"; found "+c);
    }

    protected void consume() {
        input.consume();
        c = (char)input.LA(1);
    }

    public void emit(Token token) { tokens.add(token); }

    public Token _nextToken() {
		//System.out.println("nextToken: c="+(char)c+"@"+input.index());
        while ( true ) { // lets us avoid recursion when skipping stuff
            startCharIndex = input.index();
            startLine = input.getLine();
            startCharPositionInLine = input.getCharPositionInLine();

            if ( c==EOF ) return newToken(EOF_TYPE);
            Token t;
            if ( scanningInsideExpr ) t = inside();
            else t = outside();
            if ( t!=SKIP ) return t;
        }
    }

    protected Token outside() {
        if ( input.getCharPositionInLine()==0 && (c==' '||c=='\t') ) {
            while ( c==' ' || c=='\t' ) consume(); // scarf indent
            if ( c!=EOF ) return newToken(INDENT);
            return newToken(TEXT);
        }
        if ( c==delimiterStartChar ) {
            consume();
            if ( c=='!' ) { COMMENT(); return SKIP; }
            if ( c=='\\' ) return ESCAPE(); // <\\> <\uFFFF> <\n> etc...
            scanningInsideExpr = true;
            return newToken(LDELIM);
        }
        if ( c=='\r' ) { consume(); consume(); return newToken(NEWLINE); } // \r\n -> \n
        if ( c=='\n') {	consume(); return newToken(NEWLINE); }
        if ( c=='}' && subtemplateDepth>0 ) {
            scanningInsideExpr = true;
            subtemplateDepth--;
            consume();
            return newTokenFromPreviousChar(RCURLY);
        }
        return mTEXT();
    }

    protected Token inside() {
        while ( true ) {
            switch ( c ) {
                case ' ': case '\t': case '\n': case '\r': consume(); continue;
                case '.' :
					consume();
					if ( input.LA(1)=='.' && input.LA(2)=='.' ) {
						consume();
						match('.');
						return newToken(ELLIPSIS);
					}
					return newToken(DOT);
                case ',' : consume(); return newToken(COMMA);
				case ':' : consume(); return newToken(COLON);
				case ';' : consume(); return newToken(SEMI);
                case '(' : consume(); return newToken(LPAREN);
                case ')' : consume(); return newToken(RPAREN);
                case '[' : consume(); return newToken(LBRACK);
                case ']' : consume(); return newToken(RBRACK);
				case '=' : consume(); return newToken(EQUALS);
                case '!' : consume(); return newToken(BANG);
                case '@' :
                    consume();
                    if ( c=='e' && input.LA(2)=='n' && input.LA(3)=='d' ) {
                        consume(); consume(); consume();
                        return newToken(REGION_END);
                    }
                    return newToken(AT);
                case '"' : return mSTRING();
                case '&' : consume(); match('&'); return newToken(AND); // &&
                case '|' : consume(); match('|'); return newToken(OR); // ||
				case '{' : return subTemplate();
				default:
					if ( c==delimiterStopChar ) {
						consume();
						scanningInsideExpr =false;
						return newToken(RDELIM);
					}
                    if ( isIDStartLetter(c) ) {
						Token id = mID();
						String name = id.getText();
						if ( name.equals("if") ) return newToken(IF);
						else if ( name.equals("endif") ) return newToken(ENDIF);
						else if ( name.equals("else") ) return newToken(ELSE);
						else if ( name.equals("elseif") ) return newToken(ELSEIF);
                        else if ( name.equals("super") ) return newToken(SUPER);
						return id;
					}
					RecognitionException re =
						new NoViableAltException("",0,0,input);
                    re.line = startLine;
                    re.charPositionInLine = startCharPositionInLine;
					if ( c==EOF ) {
						throw new STException("EOF inside ST expression at "+
							re.line+":"+re.charPositionInLine, re);
					}
                    throw new STException("invalid character '"+c+"' at "+
							re.line+":"+re.charPositionInLine, re);
            }
        }
    }

    Token subTemplate() {
        // look for "{ args ID (',' ID)* '|' ..."
		subtemplateDepth++;
        int m = input.mark();
        int curlyStartChar = startCharIndex;
        int curlyLine = startLine;
        int curlyPos = startCharPositionInLine;
        List<Token> argTokens = new ArrayList<Token>();
        consume();
		Token curly = newTokenFromPreviousChar(LCURLY);
        WS();
        argTokens.add( mID() );
        WS();
        while ( c==',' ) {
			consume();
            argTokens.add( newTokenFromPreviousChar(COMMA) );
            WS();
            argTokens.add( mID() );
            WS();
        }
        WS();
        if ( c=='|' ) {
			consume();
            argTokens.add( newTokenFromPreviousChar(PIPE) );
            if ( isWS(c) ) consume(); // ignore a single whitespace after |
            //System.out.println("matched args: "+argTokens);
            for (Token t : argTokens) emit(t);
			input.release(m);
			scanningInsideExpr = false;
			startCharIndex = curlyStartChar; // reset state
			startLine = curlyLine;
			startCharPositionInLine = curlyPos;
			return curly;
		}
		input.rewind(m);
		startCharIndex = curlyStartChar; // reset state
		startLine = curlyLine;
        startCharPositionInLine = curlyPos;
		consume();
		scanningInsideExpr = false;
        return curly;
    }

    Token ESCAPE() {
        consume(); // kill \\
        Token t = null;
        switch ( c ) {
            case '\\' : LINEBREAK(); return SKIP;
            case 'n'  :
                t = newToken(TEXT, "\n", input.getCharPositionInLine()-2);
                break;
            case 't'  :
                t = newToken(TEXT, "\t", input.getCharPositionInLine()-2);
                break;
            case ' '  :
                t = newToken(TEXT, " ", input.getCharPositionInLine()-2);
                break;
            case 'u' : t = UNICODE(); break;
            default :
                NoViableAltException e = new NoViableAltException("",0,0,input);
                ErrorManager.lexerError(ErrorType.LEXER_ERROR, e, c);
        }
        consume();
        match(delimiterStopChar);
        return t;
    }

    Token UNICODE() {
        consume();
        char[] chars = new char[4];
        if ( !isUnicodeLetter(c) ) {
            NoViableAltException e = new NoViableAltException("",0,0,input);
            ErrorManager.lexerError(ErrorType.LEXER_ERROR, e, c);
        }
        chars[0] = c;
        consume();
        if ( !isUnicodeLetter(c) ) {
            NoViableAltException e = new NoViableAltException("",0,0,input);
            ErrorManager.lexerError(ErrorType.LEXER_ERROR, e, c);
        }
        chars[1] = c;
        consume();
        if ( !isUnicodeLetter(c) ) {
            NoViableAltException e = new NoViableAltException("",0,0,input);
            ErrorManager.lexerError(ErrorType.LEXER_ERROR, e, c);
        }
        chars[2] = c;
        consume();
        if ( !isUnicodeLetter(c) ) {
            NoViableAltException e = new NoViableAltException("",0,0,input);
            ErrorManager.lexerError(ErrorType.LEXER_ERROR, e, c);
        }
        chars[3] = c;
        // ESCAPE kills final char and >
        char uc = (char)Integer.parseInt(new String(chars), 16);
        return newToken(TEXT, String.valueOf(uc), input.getCharPositionInLine()-6);
    }

    Token mTEXT() {
		boolean modifiedText = false;
        StringBuilder buf = new StringBuilder();
        while ( c != EOF && c != delimiterStartChar ) {
			if ( c=='\r' || c=='\n') break;
			if ( c=='}' && subtemplateDepth>0 ) break;
            if ( c=='\\' ) {
                if ( input.LA(2)=='\\' ) { // convert \\ to \
                    consume(); consume(); buf.append('\\');
                    modifiedText = true;
                    continue;
                }
                if ( input.LA(2)==delimiterStartChar ||
					 input.LA(2)=='}' )
				{
                    modifiedText = true;
                    consume(); // toss out \ char
                    buf.append(c); consume();
                }
                else {
                    buf.append(c);
                    consume();
                }
                continue;
            }
            buf.append(c);
            consume();
        }
        if ( modifiedText )	return newToken(TEXT, buf.toString());
        else return newToken(TEXT);
    }

    /** ID  :   ('a'..'z'|'A'..'Z'|'_'|'/') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')* ; */
    Token mID() {
        // called from subTemplate; so keep resetting position during speculation
        startCharIndex = input.index();
        startLine = input.getLine();
        startCharPositionInLine = input.getCharPositionInLine();
        consume();
        while ( isIDLetter(c) ) {
            consume();
        }
        return newToken(ID);
    }

    /** STRING : '"' ( '\\' '"' | '\\' ~'"' | ~('\\'|'"') )* '"' ; */
    Token mSTRING() {
    	//{setText(getText().substring(1, getText().length()-1));}
        boolean sawEscape = false;
        StringBuilder buf = new StringBuilder();
        buf.append(c); consume();
        while ( c != '"' ) {
            if ( c=='\\' ) {
                sawEscape = true;
                consume();
				switch ( c ) {
					case 'n' : buf.append('\n'); break;
					case 'r' : buf.append('\r'); break;
					case 't' : buf.append('\t'); break;
                	default : buf.append(c); break;
				}
				consume();
                continue;
            }
            buf.append(c);
            consume();
			if ( c==EOF ) {
				RecognitionException re =
					new MismatchedTokenException((int)'"', input);
				re.line = input.getLine();
				re.charPositionInLine = input.getCharPositionInLine();
				throw new STException("EOF inside string/template at "+
					startLine+":"+startCharPositionInLine, re);
			}
        }
        buf.append(c);
        consume();
        if ( sawEscape ) return newToken(STRING, buf.toString());
        else return newToken(STRING);
    }

    void WS() {
        while ( c==' ' || c=='\t' || c=='\n' || c=='\r' ) consume();
    }

    void COMMENT() {
        match('!');
        while ( !(c=='!' && input.LA(2)==delimiterStopChar) ) consume();
        consume(); consume(); // kill !>
    }

    void LINEBREAK() {
        match('\\'); // only kill 2nd \ as outside() kills first one
        match(delimiterStopChar);
        while ( c==' ' || c=='\t' ) consume(); // scarf WS after <\\>
        if ( c=='\r' ) consume();
        match('\n');
        while ( c==' ' || c=='\t' ) consume(); // scarf any indent
    }

    public static boolean isIDStartLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z' || c=='_'; }
	public static boolean isIDLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z' || c>='0'&&c<='9' || c=='_' || c=='/'; }
    public static boolean isWS(char c) { return c==' ' || c=='\t' || c=='\n' || c=='\r'; }
    public static boolean isUnicodeLetter(char c) { return c>='a'&&c<='f' || c>='A'&&c<='F' || c>='0'&&c<='9'; }

    public Token newToken(int ttype) {
        STToken t = new STToken(input, ttype, startCharIndex, input.index()-1);
        t.setLine(startLine);
        t.setCharPositionInLine(startCharPositionInLine);
		return t;
	}

    public Token newTokenFromPreviousChar(int ttype) {
        STToken t = new STToken(input, ttype, input.index()-1, input.index()-1);
        t.setLine(input.getLine());
        t.setCharPositionInLine(input.getCharPositionInLine()-1);
        return t;
    }

    public Token newToken(int ttype, String text, int pos) {
        STToken t = new STToken(ttype, text);
        t.setLine(input.getLine());
        t.setCharPositionInLine(pos);
        return t;
    }

	public Token newToken(int ttype, String text) {
		STToken t = new STToken(ttype, text);
        t.setStartIndex(startCharIndex);
        t.setStopIndex(input.index()-1);
		t.setLine(startLine);
		t.setCharPositionInLine(startCharPositionInLine);
		return t;
	}

    public String getErrorHeader() {
        return startLine+":"+startCharPositionInLine;
    }

    public String getSourceName() {
        return "no idea";
    }
}
