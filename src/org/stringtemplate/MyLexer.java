package org.stringtemplate;

import org.antlr.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class MyLexer implements TokenSource {
    public static final char EOF = (char)-1;            // EOF char
    public static final int EOF_TYPE = CharStream.EOF;  // EOF token type

    public static class MyToken extends CommonToken {
        public MyToken(CharStream input, int type, int channel, int start, int stop) {
            super(input, type, channel, start, stop);
        }

        public MyToken(int type, String text) { super(type, text); }

        public String toString() {
            String channelStr = "";
            if ( channel>0 ) {
                channelStr=",channel="+channel;
            }
            String txt = getText();
            if ( txt!=null ) {
                txt = txt.replaceAll("\n","\\\\n");
                txt = txt.replaceAll("\r","\\\\r");
                txt = txt.replaceAll("\t","\\\\t");
            }
            else {
                txt = "<no text>";
            }
            return "[@"+getTokenIndex()+","+start+":"+stop+"='"+txt+"',<"+STParser.tokenNames[type]+">"+channelStr+","+line+":"+getCharPositionInLine()+"]";
        }
    }

    // pasted from STParser
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
    public static final int WS=27;
    public static final int COMMA=18;
    public static final int RCURLY=21;
    public static final int ENDIF=7;
    public static final int RDELIM=24;
    public static final int SUPER=8;
    public static final int DOT=19;
    public static final int LDELIM=23;
    public static final int STRING=26;
    public static final int PIPE=28;

    char delimiterStartChar = '<';
    char delimiterStopChar = '>';

    boolean scanningInsideExpr = false;
	int subtemplateDepth = 0; // start out *not* in a {...} subtemplate 

    CharStream input;
    char c;        // current character
    int startCharIndex;
    int startLine;
    int startCharPositionInLine;

    List<Token> tokens = new ArrayList<Token>();

    public Token nextToken() {
        if ( tokens.size()>0 ) { return tokens.remove(0); }
        return _nextToken();
    }

    public MyLexer(ANTLRStringStream input) {
		this(input, '<', '>');
    }

	public MyLexer(CharStream input, char delimiterStartChar, char delimiterStopChar) {
		this.input = input;
		c = (char)input.LA(1); // prime lookahead
		this.delimiterStartChar = delimiterStartChar;
		this.delimiterStopChar = delimiterStopChar;
	}

    /** Ensure x is next character on the input stream */
    public void match(char x) {
        if ( c == x) consume();
        else throw new Error("expecting "+x+"; found "+c);
    }

    protected void consume() {
        input.consume();
        c = (char)input.LA(1);
    }

    public void emit(Token token) { tokens.add(token); }

    public Token _nextToken() {
        startCharIndex = input.index();
        startLine = input.getLine();
        startCharPositionInLine = input.getCharPositionInLine();

        if ( c==EOF ) return newToken(EOF_TYPE);
        if ( scanningInsideExpr ) return inside();
        return outside();
    }

    protected Token outside() {
        if ( c==delimiterStartChar ) {
            consume();
            scanningInsideExpr = true;
            return newToken(LDELIM);
        }
        if ( c=='}' ) {
            consume();
            scanningInsideExpr = true;
            return newToken(RCURLY);
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
                case '"' : return mSTRING();
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
					RecognitionException re = new NoViableAltException("", 0, 0, input);
					if ( c==EOF ) {
						throw new STRecognitionException("EOF inside ST expression", re);						
					}
                    throw new STRecognitionException("invalid character: "+c, re);
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
		Token curly = newSingleCharToken(LCURLY);
        WS();
        argTokens.add( mID() );
        WS();
        while ( c==',' ) {
			consume();
            argTokens.add( newSingleCharToken(COMMA) );
            WS();
            argTokens.add( mID() );
            WS();
        }
        WS();
        if ( c=='|' ) {
			consume();
            argTokens.add( newSingleCharToken(PIPE) );
			WS(); // ignore any whitespace after |
            //System.out.println("matched args: "+argTokens);
            for (Token t : argTokens) emit(t);
			input.release(m);
			scanningInsideExpr = false;
			startCharIndex = curlyStartChar; // reset state
			startLine = curlyLine;
			startCharPositionInLine = curlyPos;
			return curly;
		}
		//System.out.println("no match rewind");
		input.rewind(m);
		startCharIndex = curlyStartChar; // reset state
		startLine = curlyLine;
        startCharPositionInLine = curlyPos;
		consume();
		scanningInsideExpr = false;
        return curly;
    }

    Token mTEXT() {
		boolean modifiedText = false;
        StringBuilder buf = new StringBuilder();
        while ( c != EOF && c != delimiterStartChar ) {
			if ( c=='}' && subtemplateDepth>0 ) {
				subtemplateDepth++;
				int p = buf.length()-1;
				while ( p>=0 && isWS(buf.charAt(p)) ) {p--;}
				if ( p < buf.length()-1 ) { // trim any whitespace off
					modifiedText = true;
					buf.setLength(p+1);
				}
				break;
			}
            if ( c=='\\' ) {
                if ( input.LA(2)==delimiterStartChar ||
					 input.LA(2)=='}' )
				{
                    modifiedText = true;
                    consume(); // toss out \ char
                    buf.append(c); consume();
                }
                else {
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

    /** ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')* ; */
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
                buf.append(c); consume();
                continue;
            }
            buf.append(c);
            consume();
        }
        buf.append(c);
        consume();
        if ( sawEscape ) return newToken(STRING, buf.toString());
        else return newToken(STRING);
    }

    void WS() {
        while ( c==' ' || c=='\t' || c=='\n' || c=='\r' ) consume();
    }
    
    boolean isIDStartLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z'; }
	boolean isIDLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z' || c>='0'&&c<='9'; }
	boolean isWS(char c) { return c==' ' || c=='\t' || c=='\n' || c=='\r'; }

    public Token newToken(int ttype) {
        MyToken t = new MyToken(input, ttype, Lexer.DEFAULT_TOKEN_CHANNEL,
                startCharIndex, input.index()-1);
        t.setLine(startLine);
        t.setCharPositionInLine(startCharPositionInLine);
		return t;
	}

	public Token newSingleCharToken(int ttype) {
		MyToken t =
			new MyToken(input, ttype, Lexer.DEFAULT_TOKEN_CHANNEL,
				input.index()-1, input.index()-1);
		t.setStartIndex(input.index()-1);
		t.setLine(input.getLine());
		t.setCharPositionInLine(input.getCharPositionInLine()-1);
		return t;
	}

	public Token newToken(int ttype, String text) {
		MyToken t = new MyToken(ttype, text);
		t.setStartIndex(startCharIndex);
		t.setLine(startLine);
		t.setCharPositionInLine(startCharPositionInLine);
		return t;
	}

    public String getSourceName() {
        return "no idea";
    }
}
