package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.MyLexer;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

public class TestLexer {
    @Test public void testOneExpr() throws Exception {
        String template = "<name>";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "[[@0,0:0='<',<21>,1:0], [@1,1:4='name',<23>,1:1], [@2,5:5='>',<22>,1:5]]";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }

    @Test public void testOneExprSurrounded() throws Exception {
        String template = "hi <name> mom";
        MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        String expected = "";
        String result = tokens.getTokens().toString();
        assertEquals(expected, result);
    }
}
