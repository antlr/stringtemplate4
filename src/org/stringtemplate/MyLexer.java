package org.stringtemplate;

import org.antlr.runtime.*;

public class MyLexer implements TokenSource {
    public static final char EOF = (char)-1;            // EOF char
    public static final int EOF_TYPE = CharStream.EOF;  // EOF token type

    // pasted from STParser
    public static final int RBRACK=17;
    public static final int LBRACK=16;
    public static final int ELSE=5;
    public static final int ELLIPSIS=11;
    public static final int BANG=10;
    public static final int EQUALS=12;
    public static final int ANONYMOUS_TEMPLATE=24;
    public static final int TEXT=20;
    public static final int ID=23;
    public static final int SEMI=9;
    public static final int LPAREN=14;
    public static final int IF=4;
    public static final int ELSEIF=6;
    public static final int COLON=13;
    public static final int RPAREN=15;
    public static final int WS=26;
    public static final int COMMA=18;
    public static final int ENDIF=7;
    public static final int RDELIM=22;
    public static final int SUPER=8;
    public static final int DOT=19;
    public static final int LDELIM=21;
    public static final int STRING=25;

    char delimiterStartChar = '<';
    char delimiterStopChar = '>';

    boolean insideExpr = false;

    ANTLRStringStream input;
    char c;        // current character
    int startChar;
    int startLine;
    int startCharPositionInLine;

    public MyLexer(ANTLRStringStream input) {
        this.input = input;
        c = (char)input.LA(1); // prime lookahead
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

    public Token nextToken() {
        while ( c!=EOF ) {
            startChar = input.index();
            startLine = input.getLine();
            startCharPositionInLine = input.getCharPositionInLine();
            if ( !insideExpr ) {
                if ( c==delimiterStartChar ) {
                    consume();
                    insideExpr = true;
                    return newToken(LDELIM);
                }
                return mTEXT();
            }
            switch ( c ) {
                case ' ': case '\t': case '\n': case '\r': WS(); continue;
                case ',' : consume(); return newToken(COMMA);
                case '[' : consume(); return newToken(LBRACK);
                case ']' : consume(); return newToken(RBRACK);
                case '=' : consume(); return newToken(EQUALS);
                case '>' :
                    consume();
                    insideExpr=false;
                    return newToken(RDELIM);
                case '"' : consume(); return mSTRING();
                default:
                    if ( isIDStartLetter(c) ) { return mID(); }
                    throw new Error("invalid character: "+(char)c);
            }
        }
        return newToken(EOF_TYPE);
    }

    Token mTEXT() {
        StringBuilder buf = new StringBuilder();
        while ( c != EOF && c != delimiterStartChar ) {
            if ( c=='\\' ) { buf.append(c); consume(); buf.append(c); consume(); continue; }
            buf.append(c);
            consume();
        }
        return newToken(TEXT);
    }

    /** ID  :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')* ; */
    Token mID() {
        StringBuilder buf = new StringBuilder();
        buf.append(c);
        consume();
        while ( isIDLetter(c) ) {
            buf.append(c);
            consume();
        }
        return newToken(ID);
    }

    boolean isIDStartLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z'; }
    boolean isIDLetter(char c) { return c>='a'&&c<='z' || c>='A'&&c<='Z' || c>='0'&&c<='9'; }

    /** STRING : '"' ( '\\' '"' | '\\' ~'"' | ~('\\'|'"') )* '"' ; */
    Token mSTRING() {
    	//{setText(getText().substring(1, getText().length()-1));}
        StringBuilder buf = new StringBuilder();
        while ( c != '"' ) {
            if ( c=='\\' ) { buf.append(c); consume(); buf.append(c); consume(); continue; }
            buf.append(c);
            consume();
        }
        consume();
        return newToken(STRING);
    }

    Token ANONYMOUS_TEMPLATE() {
        match('{');
        insideExpr = false;
        return newToken(ANONYMOUS_TEMPLATE);
    }

    /** WS : (' '|'\t'|'\n'|'\r')* ; // ignore any whitespace */
    void WS() {
        while ( c==' ' || c=='\t' || c=='\n' || c=='\r' ) consume();
    }

    public Token newToken(int ttype) {
        CommonToken t = new CommonToken(input, ttype, Lexer.DEFAULT_TOKEN_CHANNEL,
                                        startChar, input.index()-1);

        t.setLine(startLine);
        t.setCharPositionInLine(startCharPositionInLine);
        return t;
    }

    public String getSourceName() {
        return "no idea";
    }
}
