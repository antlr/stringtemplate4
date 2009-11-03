package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.MyLexer;
import org.stringtemplate.ST;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

import java.util.List;

public class TestLexer extends BaseTest {
	public void check(String template, String expected) {
		MyLexer lexer = new MyLexer(new ANTLRStringStream(template));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		String result = tokens.getTokens().toString();
		assertEquals(expected, result);
	}

    @Test public void testOneExpr() throws Exception {
        String template = "<name>";
        String expected = "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:4='name',<ID>,1:1], " +
                          "[@2,5:5='>',<RDELIM>,1:5]]";
		check(template, expected);
    }

    @Test public void testOneExprSurrounded() throws Exception {
        String template = "hi <name> mom";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:7='name',<ID>,1:4], [@3,8:8='>',<RDELIM>,1:8], " +
                          "[@4,9:12=' mom',<TEXT>,1:9]]";
		check(template, expected);
    }

    @Test public void testEscDelim() throws Exception {
        String template = "hi \\<name>";
        String expected = "[[@0,0:0='hi <name>',<TEXT>,1:0]]";
		check(template, expected);
    }

    @Test public void testEscDelimHasCorrectStartChar() throws Exception {
        String template = "<a>\\<dog";
        String expected =
            "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:1='a',<ID>,1:1], [@2,2:2='>',<RDELIM>,1:2], " +
            "[@3,3:0='<dog',<TEXT>,1:3]]";
		check(template, expected);
    }

    @Test public void testEscChar() throws Exception {
        String template = "hi \\x";
        String expected = "[[@0,0:4='hi \\x',<TEXT>,1:0]]";
		check(template, expected);
    }

    @Test public void testString() throws Exception {
        String template = "hi <foo(a=\">\")>";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:6='foo',<ID>,1:4], [@3,7:7='(',<LPAREN>,1:7], " +
                          "[@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
                          "[@6,10:12='\">\"',<STRING>,1:10], [@7,13:13=')',<RPAREN>,1:13], " +
                          "[@8,14:14='>',<RDELIM>,1:14]]";
		check(template, expected);
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi <foo(a=\">\\\"\")>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:6='foo',<ID>,1:4], " +
            "[@3,7:7='(',<LPAREN>,1:7], [@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
            "[@6,10:0='\">\"\"',<STRING>,1:10], [@7,15:15=')',<RPAREN>,1:15], " +
            "[@8,16:16='>',<RDELIM>,1:16]]";
		check(template, expected);
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi <names:{n | <n>}>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='<',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17='>',<RDELIM>,1:17], [@10,18:18='}',<RCURLY>,1:18], [@11,19:19='>',<RDELIM>,1:19]]";
		check(template, expected);
    }

    @Test public void testSubtemplateNoArg() throws Exception {
        String template = "hi <names:{ <it>}>";
		String expected =
			"[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
				"[@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], " +
				"[@4,10:10='{',<LCURLY>,1:10], [@5,11:11=' ',<TEXT>,1:11], " +
				"[@6,12:12='<',<LDELIM>,1:12], [@7,13:14='it',<ID>,1:13], " +
				"[@8,15:15='>',<RDELIM>,1:15], [@9,16:16='}',<RCURLY>,1:16], " +
				"[@10,17:17='>',<RDELIM>,1:17]]";
		check(template, expected);
	}

    @Test public void testSubtemplateMultiArgs() throws Exception {
        String template = "hi <names:{x,y | <x><y>}>"; // semantically bogus
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='x',<ID>,1:11], [@6,12:12=',',<COMMA>,1:12], [@7,13:13='y',<ID>,1:13], [@8,15:15='|',<PIPE>,1:15], [@9,17:17='<',<LDELIM>,1:17], [@10,18:18='x',<ID>,1:18], [@11,19:19='>',<RDELIM>,1:19], [@12,20:20='<',<LDELIM>,1:20], [@13,21:21='y',<ID>,1:21], [@14,22:22='>',<RDELIM>,1:22], [@15,23:23='}',<RCURLY>,1:23], [@16,24:24='>',<RDELIM>,1:24]]";
		check(template, expected);
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi <names:{n | <n:{<it>}>}>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='<',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17=':',<COLON>,1:17], [@10,18:18='{',<LCURLY>,1:18], [@11,19:19='<',<LDELIM>,1:19], [@12,20:21='it',<ID>,1:20], [@13,22:22='>',<RDELIM>,1:22], [@14,23:23='}',<RCURLY>,1:23], [@15,24:24='>',<RDELIM>,1:24], [@16,25:25='}',<RCURLY>,1:25], [@17,26:26='>',<RDELIM>,1:26]]";
		check(template, expected);
    }

    @Test public void testNestedList() throws Exception {
        String template =
            "*<[names, [\"foo\",\"bar\"]:{<it>!},phones]; separator=\", \">*";
        String expected =
            "[[@0,0:0='*',<TEXT>,1:0], [@1,1:1='<',<LDELIM>,1:1], [@2,2:2='[',<LBRACK>,1:2], " +
			"[@3,3:7='names',<ID>,1:3], [@4,8:8=',',<COMMA>,1:8], [@5,9:10=' [',<LBRACK>,1:9], " +
			"[@6,11:15='\"foo\"',<STRING>,1:11], [@7,16:16=',',<COMMA>,1:16], " +
			"[@8,17:21='\"bar\"',<STRING>,1:17], [@9,22:22=']',<RBRACK>,1:22], " +
			"[@10,23:23=':',<COLON>,1:23], [@11,24:24='{',<LCURLY>,1:24], " +
			"[@12,25:25='<',<LDELIM>,1:25], [@13,26:27='it',<ID>,1:26], " +
			"[@14,28:28='>',<RDELIM>,1:28], [@15,29:29='!',<TEXT>,1:29], " +
			"[@16,30:30='}',<RCURLY>,1:30], [@17,31:31=',',<COMMA>,1:31], " +
			"[@18,32:37='phones',<ID>,1:32], [@19,38:38=']',<RBRACK>,1:38], " +
			"[@20,39:39=';',<SEMI>,1:39], [@21,41:49='separator',<ID>,1:41], " +
			"[@22,50:50='=',<EQUALS>,1:50], [@23,51:54='\", \"',<STRING>,1:51], " +
			"[@24,55:55='>',<RDELIM>,1:55], [@25,56:56='*',<TEXT>,1:56]]";
		check(template, expected);
    }

	@Test public void testIF() throws Exception {
		String template = "<if(!name)>works<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:4='!',<BANG>,1:4], [@4,5:8='name',<ID>,1:5], [@5,9:9=')',<RPAREN>,1:9], " +
			"[@6,10:10='>',<RDELIM>,1:10], [@7,11:15='works',<TEXT>,1:11], " +
			"[@8,16:16='<',<LDELIM>,1:16], [@9,17:21='endif',<ENDIF>,1:17], " +
			"[@10,22:22='>',<RDELIM>,1:22]]";
		check(template, expected);
	}

	@Test public void testIFNot() throws Exception {
		String template = "<if(!name)>works<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:4='!',<BANG>,1:4], [@4,5:8='name',<ID>,1:5], [@5,9:9=')',<RPAREN>,1:9], " +
			"[@6,10:10='>',<RDELIM>,1:10], [@7,11:15='works',<TEXT>,1:11], " +
			"[@8,16:16='<',<LDELIM>,1:16], [@9,17:21='endif',<ENDIF>,1:17], " +
			"[@10,22:22='>',<RDELIM>,1:22]]";
		check(template, expected);
	}

	@Test public void testIFELSE() throws Exception {
		String template = "<if(name)>works<else>fail<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:7='name',<ID>,1:4], [@4,8:8=')',<RPAREN>,1:8], [@5,9:9='>',<RDELIM>,1:9], " +
			"[@6,10:14='works',<TEXT>,1:10], [@7,15:15='<',<LDELIM>,1:15], " +
			"[@8,16:19='else',<ELSE>,1:16], [@9,20:20='>',<RDELIM>,1:20], " +
			"[@10,21:24='fail',<TEXT>,1:21], [@11,25:25='<',<LDELIM>,1:25], " +
			"[@12,26:30='endif',<ENDIF>,1:26], [@13,31:31='>',<RDELIM>,1:31]]";
		check(template, expected);
	}

	@Test public void testELSEIF() throws Exception {
		String template = "<if(name)>fail<elseif(id)>works<else>fail<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:7='name',<ID>,1:4], [@4,8:8=')',<RPAREN>,1:8], [@5,9:9='>',<RDELIM>,1:9], " +
			"[@6,10:13='fail',<TEXT>,1:10], [@7,14:14='<',<LDELIM>,1:14], " +
			"[@8,15:20='elseif',<ELSEIF>,1:15], [@9,21:21='(',<LPAREN>,1:21], " +
			"[@10,22:23='id',<ID>,1:22], [@11,24:24=')',<RPAREN>,1:24], " +
			"[@12,25:25='>',<RDELIM>,1:25], [@13,26:30='works',<TEXT>,1:26], " +
			"[@14,31:31='<',<LDELIM>,1:31], [@15,32:35='else',<ELSE>,1:32], " +
			"[@16,36:36='>',<RDELIM>,1:36], [@17,37:40='fail',<TEXT>,1:37], " +
			"[@18,41:41='<',<LDELIM>,1:41], [@19,42:46='endif',<ENDIF>,1:42], " +
			"[@20,47:47='>',<RDELIM>,1:47]]";
		check(template, expected);
	}
}
