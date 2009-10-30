package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.MyLexer;
import org.stringtemplate.Chunk;
import org.stringtemplate.Chunkifier;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

import java.util.List;

public class TestLexer {
    @Test public void testOneExpr() throws Exception {
        String template = "<name>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:4='name',<ID>,1:1], " +
                          "[@2,5:5='>',<RDELIM>,1:5]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testOneExprSurrounded() throws Exception {
        String template = "hi <name> mom";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:7='name',<ID>,1:4], [@3,8:8='>',<RDELIM>,1:8], " +
                          "[@4,9:12=' mom',<TEXT>,1:9]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testEscDelim() throws Exception {
        String template = "hi \\<name>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:0='hi <name>',<TEXT>,1:0]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testEscDelimHasCorrectStartChar() throws Exception {
        String template = "<a>\\<dog";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected =
            "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:1='a',<ID>,1:1], [@2,2:2='>',<RDELIM>,1:2], " +
            "[@3,3:0='<dog',<TEXT>,1:3]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testEscChar() throws Exception {
        String template = "hi \\x";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:4='hi \\x',<TEXT>,1:0]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testString() throws Exception {
        String template = "hi <foo(a=\">\")>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:6='foo',<ID>,1:4], [@3,7:7='(',<LPAREN>,1:7], " +
                          "[@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
                          "[@6,10:12='\">\"',<STRING>,1:10], [@7,13:13=')',<RPAREN>,1:13], " +
                          "[@8,14:14='>',<RDELIM>,1:14]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi <foo(a=\">\\\"\")>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:6='foo',<ID>,1:4], " +
            "[@3,7:7='(',<LPAREN>,1:7], [@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
            "[@6,10:0='\">\"\"',<STRING>,1:10], [@7,15:15=')',<RPAREN>,1:15], " +
            "[@8,16:16='>',<RDELIM>,1:16]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi <names:{n | <n>}>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], " +
            "[@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], args " +
            "[@6,15:15='<',<LDELIM>,1:15], [@7,16:16='n',<ID>,1:16], [@8,17:17='>',<RDELIM>,1:17], " +
            "[@9,18:18='}',<RCURLY>,1:18], [@10,19:19='>',<RDELIM>,1:19]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi <names:{n | <n:{<it>}>}>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], " +
            "[@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:14='n | ',<TEXT>,1:11], " +
            "[@6,15:15='<',<LDELIM>,1:15], [@7,16:16='n',<ID>,1:16], [@8,17:17=':',<COLON>,1:17], " +
            "[@9,18:18='{',<LCURLY>,1:18], [@10,19:19='<',<LDELIM>,1:19], [@11,20:21='it',<ID>,1:20], " +
            "[@12,22:22='>',<RDELIM>,1:22], [@13,23:23='}',<RCURLY>,1:23], [@14,24:24='>',<RDELIM>,1:24], " +
            "[@15,25:25='}',<RCURLY>,1:25], [@16,26:26='>',<RDELIM>,1:26]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testNestedList() throws Exception {
        String template =
            "*<[names, [\"foo\",\"bar\"]:{<it>!},phones]; separator=\", \">*";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected =
            "[[@0,0:0='*',<TEXT>,1:0], [@1,1:1='<',<LDELIM>,1:1], [@2,2:2='[',<LBRACK>,1:2], " +
            "[@3,3:7='names',<ID>,1:3], [@4,8:8=',',<COMMA>,1:8], [@5,9:10=' [',<LBRACK>,1:9], " +
            "[@6,11:15='\"foo\"',<STRING>,1:11], [@7,16:16=',',<COMMA>,1:16], " +
            "[@8,17:21='\"bar\"',<STRING>,1:17], [@9,22:22=']',<RBRACK>,1:22], " +
            "[@10,23:23=':',<COLON>,1:23], [@11,24:24='{',<LCURLY>,1:24], " +
            "[@12,25:25='<',<LDELIM>,1:25], [@13,26:27='it',<ID>,1:26], " +
            "[@14,28:28='>',<RDELIM>,1:28], [@15,29:56='!},phones]; separator=\", \">*',<TEXT>,1:29]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }
}
