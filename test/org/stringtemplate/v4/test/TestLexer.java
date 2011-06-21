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
package org.stringtemplate.v4.test;

import org.junit.Test;

public class TestLexer extends BaseTest {

    @Test public void testOneExpr() throws Exception {
        String template = "<name>";
        String expected = "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:4='name',<ID>,1:1], " +
                          "[@2,5:5='>',<RDELIM>,1:5]]";
		checkTokens(template, expected);
    }

    @Test public void testOneExprSurrounded() throws Exception {
        String template = "hi <name> mom";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:7='name',<ID>,1:4], [@3,8:8='>',<RDELIM>,1:8], " +
                          "[@4,9:12=' mom',<TEXT>,1:9]]";
		checkTokens(template, expected);
    }

    @Test public void testEscDelim() throws Exception {
        String template = "hi \\<name>";
        String expected = "[[@0,0:9='hi <name>',<TEXT>,1:0]]";
		checkTokens(template, expected);
    }

    @Test public void testEscEsc() throws Exception {
        String template = "hi \\\\ foo";
        String expected = "[[@0,0:8='hi \\ foo',<TEXT>,1:0]]";
		checkTokens(template, expected);
    }

    @Test public void testEscDelimHasCorrectStartChar() throws Exception {
        String template = "<a>\\<dog";
        String expected =
            "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:1='a',<ID>,1:1], [@2,2:2='>',<RDELIM>,1:2], " +
            "[@3,3:7='<dog',<TEXT>,1:3]]";
		checkTokens(template, expected);
    }

    @Test public void testEscChar() throws Exception {
        String template = "hi \\x";
        String expected = "[[@0,0:4='hi \\x',<TEXT>,1:0]]";
		checkTokens(template, expected);
    }

    @Test public void testString() throws Exception {
        String template = "hi <foo(a=\">\")>";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], " +
                          "[@2,4:6='foo',<ID>,1:4], [@3,7:7='(',<LPAREN>,1:7], " +
                          "[@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
                          "[@6,10:12='\">\"',<STRING>,1:10], [@7,13:13=')',<RPAREN>,1:13], " +
                          "[@8,14:14='>',<RDELIM>,1:14]]";
		checkTokens(template, expected);
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi <foo(a=\">\\\"\")>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:6='foo',<ID>,1:4], " +
            "[@3,7:7='(',<LPAREN>,1:7], [@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], " +
            "[@6,10:14='\">\"\"',<STRING>,1:10], [@7,15:15=')',<RPAREN>,1:15], " +
            "[@8,16:16='>',<RDELIM>,1:16]]";
		checkTokens(template, expected);
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi <names:{n | <n>}>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='<',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17='>',<RDELIM>,1:17], [@10,18:18='}',<RCURLY>,1:18], [@11,19:19='>',<RDELIM>,1:19]]";
		checkTokens(template, expected);
    }

    @Test public void testSubtemplateNoArg() throws Exception {
        String template = "hi <names:{n | <n>}>";
		String expected =
			"[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='<',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17='>',<RDELIM>,1:17], [@10,18:18='}',<RCURLY>,1:18], [@11,19:19='>',<RDELIM>,1:19]]";
		checkTokens(template, expected);
	}

    @Test public void testSubtemplateMultiArgs() throws Exception {
        String template = "hi <names:{x,y | <x><y>}>"; // semantically bogus
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='x',<ID>,1:11], [@6,12:12=',',<COMMA>,1:12], [@7,13:13='y',<ID>,1:13], [@8,15:15='|',<PIPE>,1:15], [@9,17:17='<',<LDELIM>,1:17], [@10,18:18='x',<ID>,1:18], [@11,19:19='>',<RDELIM>,1:19], [@12,20:20='<',<LDELIM>,1:20], [@13,21:21='y',<ID>,1:21], [@14,22:22='>',<RDELIM>,1:22], [@15,23:23='}',<RCURLY>,1:23], [@16,24:24='>',<RDELIM>,1:24]]";
		checkTokens(template, expected);
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi <names:{n | <n:{x|<x>}>}>";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='<',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='<',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17=':',<COLON>,1:17], [@10,18:18='{',<LCURLY>,1:18], [@11,19:19='x',<ID>,1:19], [@12,20:20='|',<PIPE>,1:20], [@13,21:21='<',<LDELIM>,1:21], [@14,22:22='x',<ID>,1:22], [@15,23:23='>',<RDELIM>,1:23], [@16,24:24='}',<RCURLY>,1:24], [@17,25:25='>',<RDELIM>,1:25], [@18,26:26='}',<RCURLY>,1:26], [@19,27:27='>',<RDELIM>,1:27]]";
		checkTokens(template, expected);
    }

    @Test public void testNestedList() throws Exception {
        String template =
            "*<[names, [\"foo\",\"bar\"]:{x|<x>!},phones]; separator=\", \">*";
		   //01234567890123456
        String expected =
            "[[@0,0:0='*',<TEXT>,1:0], [@1,1:1='<',<LDELIM>,1:1], [@2,2:2='[',<LBRACK>,1:2], [@3,3:7='names',<ID>,1:3], [@4,8:8=',',<COMMA>,1:8], [@5,10:10='[',<LBRACK>,1:10], [@6,11:15='\"foo\"',<STRING>,1:11], [@7,16:16=',',<COMMA>,1:16], [@8,17:21='\"bar\"',<STRING>,1:17], [@9,22:22=']',<RBRACK>,1:22], [@10,23:23=':',<COLON>,1:23], [@11,24:24='{',<LCURLY>,1:24], [@12,25:25='x',<ID>,1:25], [@13,26:26='|',<PIPE>,1:26], [@14,27:27='<',<LDELIM>,1:27], [@15,28:28='x',<ID>,1:28], [@16,29:29='>',<RDELIM>,1:29], [@17,30:30='!',<TEXT>,1:30], [@18,31:31='}',<RCURLY>,1:31], [@19,32:32=',',<COMMA>,1:32], [@20,33:38='phones',<ID>,1:33], [@21,39:39=']',<RBRACK>,1:39], [@22,40:40=';',<SEMI>,1:40], [@23,42:50='separator',<ID>,1:42], [@24,51:51='=',<EQUALS>,1:51], [@25,52:55='\", \"',<STRING>,1:52], [@26,56:56='>',<RDELIM>,1:56], [@27,57:57='*',<TEXT>,1:57]]";
		checkTokens(template, expected);
    }

	@Test public void testIF() throws Exception {
		String template = "<if(!name)>works<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:4='!',<BANG>,1:4], [@4,5:8='name',<ID>,1:5], [@5,9:9=')',<RPAREN>,1:9], " +
			"[@6,10:10='>',<RDELIM>,1:10], [@7,11:15='works',<TEXT>,1:11], " +
			"[@8,16:16='<',<LDELIM>,1:16], [@9,17:21='endif',<ENDIF>,1:17], " +
			"[@10,22:22='>',<RDELIM>,1:22]]";
		checkTokens(template, expected);
	}

	@Test public void testIFNot() throws Exception {
		String template = "<if(!name)>works<endif>";
		String expected =
			"[[@0,0:0='<',<LDELIM>,1:0], [@1,1:2='if',<IF>,1:1], [@2,3:3='(',<LPAREN>,1:3], " +
			"[@3,4:4='!',<BANG>,1:4], [@4,5:8='name',<ID>,1:5], [@5,9:9=')',<RPAREN>,1:9], " +
			"[@6,10:10='>',<RDELIM>,1:10], [@7,11:15='works',<TEXT>,1:11], " +
			"[@8,16:16='<',<LDELIM>,1:16], [@9,17:21='endif',<ENDIF>,1:17], " +
			"[@10,22:22='>',<RDELIM>,1:22]]";
		checkTokens(template, expected);
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
		checkTokens(template, expected);
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
		checkTokens(template, expected);
	}

    @Test public void testEmbeddedRegion() throws Exception {
        String template = "<@r>foo<@end>";
        String expected =
            "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:1='@',<AT>,1:1], [@2,2:2='r',<ID>,1:2], " +
            "[@3,3:3='>',<RDELIM>,1:3], [@4,4:6='foo',<TEXT>,1:4], [@5,7:7='<',<LDELIM>,1:7], " +
            "[@6,8:11='@end',<END>,1:8], [@7,12:12='>',<RDELIM>,1:12]]";
		checkTokens(template, expected);
    }

    @Test public void testRegion() throws Exception {
        String template = "<@r()>";
        String expected =
            "[[@0,0:0='<',<LDELIM>,1:0], [@1,1:1='@',<AT>,1:1], [@2,2:2='r',<ID>,1:2], " +
             "[@3,3:3='(',<LPAREN>,1:3], [@4,4:4=')',<RPAREN>,1:4], [@5,5:5='>',<RDELIM>,1:5]]";
		checkTokens(template, expected);
    }
}
