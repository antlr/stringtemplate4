package org.stringtemplate;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;

import java.util.List;
import java.util.ArrayList;

public class Chunkifier {
    CharStream input;
    int start; // where we start lexing within input 
    //String temp late;
    List<Chunk> chunks = new ArrayList<Chunk>();
    int n;
    int exprStart = start;
    int exprStop = start;
    int strStart;
    char delimiterStartChar;
    char delimiterStopChar;
    int c;

    int line;
    int charPositionInLine; // not used at moment, might be in future for errors

    public Chunkifier(char delimiterStartChar, char delimiterStopChar) {
        this.delimiterStartChar = delimiterStartChar;
        this.delimiterStopChar = delimiterStopChar;
        charPositionInLine=0;
        line = 1;
    }

    public Chunkifier(String template, char delimiterStartChar, char delimiterStopChar) {
        this(delimiterStartChar, delimiterStopChar);
        input = new ANTLRStringStream(template);
        //this.template = template;
        n = template.length();
        c = input.LA(1);
        start = 0;
        //i = start;
        exprStart = start;
        exprStop = start;
    }

    public Chunkifier(CharStream input, char delimiterStartChar, char delimiterStopChar) {
        this(delimiterStartChar, delimiterStopChar);
        this.input = input;
        n = input.size();
        c = input.LA(1);
        start = input.index();
        //i = start;
        exprStart = start;
        exprStop = start;
    }

    public List<Chunk> chunkify() {
        while ( input.index() < n ) {
            if ( c=='\\' ) { consume(); consume(); continue; }
            if ( c==delimiterStartChar) {       // match everything inside delimiters
                exprStart = input.index()+1;
                if ( input.index()>strStart ) {
                    String text = input.substring(strStart,input.index()-1);
                    chunks.add(new Chunk(text, line, strStart));
                }
                matchExpr();
                String expr = input.substring(exprStart, exprStop+1-1);
                chunks.add(new ExprChunk(expr,line,exprStart-1));
                strStart = input.index(); // string starts again after stop delimiter
                continue;
            }
            consume();
        }
        if ( strStart < n ) {
            String expr = input.substring(strStart, n-1);
            chunks.add(new Chunk(expr, line, strStart));
        }
        return chunks;
    }

    protected void matchExpr() {
        consume();                // skip over start delimiter
        while ( c!=delimiterStopChar ) {   // scan for stop delimiter
            if ( c=='\\' ) { consume(); consume(); continue; }
            if ( c=='"' ) { matchString(); continue; }
            if ( c=='{' ) { consume(); matchBlock(); continue; }
            consume();
        }
        exprStop=input.index()-1;
        if ( input.index() >= n ) {
            throw new IllegalArgumentException("missing terminating delimiter expression; i="+input.index());
        }
        consume(); // skip final > or $ delimiter char
    }

    protected void matchString() {
        consume(); // jump over first "
        while ( c!='"' ) {   // scan for stop quote
            if ( c=='\\' ) { consume(); consume(); continue; }
            if ( c=='"' ) { consume(); break; }
            consume();
        }
        if ( input.index()>=n ) throw new IllegalArgumentException("unterminated string");
        consume();
    }

    protected void matchBlock() {
        // don't jump over first { since ST.g lexer matches '{' then calls us
        while ( c!='}' ) {   // scan for stop block '}'
            if ( c=='\\' ) { consume(); consume(); continue; }
            if ( c== delimiterStartChar) { matchExpr(); continue; }
            consume();
        }
        if ( input.index()>=n ) throw new IllegalArgumentException("unterminated block");
        consume();
    }

    protected void consume() {
        input.consume();
        if ( input.index()<n ) {
            charPositionInLine++;
            c = input.LA(1);
            if ( c=='\n' ) { line++; charPositionInLine=0; }
        }
        // TODO: else ERROR
    }
}
