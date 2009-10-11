package org.stringtemplate;

import java.util.List;
import java.util.ArrayList;

public class Chunkifier {
    String template;
    List<Chunk> chunks = new ArrayList<Chunk>();
    int i;
    int n;
    int exprStart = i;
    int exprStop = i;
    int strStart;
    char start;
    char stop;
    char c;

    public Chunkifier(String template, char start, char stop) {
        this.template = template;
        this.start = start;
        this.stop = stop;
        n = template.length();
    }

    public List<Chunk> chunkify() {
        while ( i < n ) {
            c = template.charAt(i);
            if ( c=='\\' ) { i+=2; continue; }
            if ( c==start ) {       // match everything inside delimiters
                exprStart = i+1;
                if ( i>strStart ) {
                    String text = template.substring(strStart,i);
                    chunks.add(new Chunk(text));
                }
                matchExpr();
                String expr = template.substring(exprStart, exprStop+1);
                chunks.add(new ExprChunk(expr));
                strStart = i+1; // string starts again after stop delimiter
            }
            i++;
        }
        if ( strStart < n ) {
            String expr = template.substring(strStart, n);
            chunks.add(new Chunk(expr));
        }
        return chunks;
    }

    protected void matchExpr() {
        i++;                // skip over start delimiter
        c = template.charAt(i);
        while ( i < n ) {   // scan for stop delimiter
            if ( c=='\\' ) { i+=2; continue; }
            if ( c=='"' ) { matchString(); continue; }
            if ( c=='{' ) { matchBlock(); continue; }
            if ( c==stop ) { exprStop=i-1; break; }
            i++;
            c = template.charAt(i);
        }
        if ( i >= n ) {
            throw new IllegalArgumentException("missing terminating delimiter expression; i="+i);
        }
    }

    protected void matchString() {
        i++; // jump over first "
        c = template.charAt(i);
        while ( i < n ) {   // scan for stop delimiter
            if ( c=='\\' ) { i+=2; c = template.charAt(i); continue; }
            if ( c=='"' ) { i++; break; }
            i++;
            c = template.charAt(i);
        }
        if ( i>=n ) throw new IllegalArgumentException("unterminated string");
        c = template.charAt(i);
    }

    protected void matchBlock() {
        i++; // jump over first {
        c = template.charAt(i);
        while ( i < n ) {   // scan for stop delimiter
            if ( c=='\\' ) { i+=2; c = template.charAt(i); continue; }
            if ( c==start ) { matchExpr(); continue; }
            if ( c=='}' ) { i++; break; }
            i++;
            c = template.charAt(i);
        }
        if ( i>=n ) throw new IllegalArgumentException("unterminated block");
        c = template.charAt(i);
    }
    
}
