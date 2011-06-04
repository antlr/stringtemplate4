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

public class TestTokensForDollarDelimiters extends BaseTest {
    @Test public void testSimpleAttr() throws Exception {
        String template = "hi $name$";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='$',<LDELIM>,1:3], [@2,4:7='name',<ID>,1:4], [@3,8:8='$',<RDELIM>,1:8]]";
        checkTokens(template, expected, '$', '$');
    }

    @Test public void testString() throws Exception {
        String template = "hi $foo(a=\"$\")$";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='$',<LDELIM>,1:3], [@2,4:6='foo',<ID>,1:4], [@3,7:7='(',<LPAREN>,1:7], [@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], [@6,10:12='\"$\"',<STRING>,1:10], [@7,13:13=')',<RPAREN>,1:13], [@8,14:14='$',<RDELIM>,1:14]]";
        checkTokens(template, expected, '$', '$');
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi $foo(a=\"$\\\"\")$"; // "hi $foo(a="$\"")$"
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='$',<LDELIM>,1:3], [@2,4:6='foo',<ID>,1:4], [@3,7:7='(',<LPAREN>,1:7], [@4,8:8='a',<ID>,1:8], [@5,9:9='=',<EQUALS>,1:9], [@6,10:14='\"$\"\"',<STRING>,1:10], [@7,15:15=')',<RPAREN>,1:15], [@8,16:16='$',<RDELIM>,1:16]]";
        checkTokens(template, expected, '$', '$');
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi $names:{n | $n$}$";
        String expected =
            "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='$',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], " +
            "[@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], " +
            "[@6,13:13='|',<PIPE>,1:13], [@7,15:15='$',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], " +
            "[@9,17:17='$',<RDELIM>,1:17], [@10,18:18='}',<RCURLY>,1:18], [@11,19:19='$',<RDELIM>,1:19]]";
        checkTokens(template, expected, '$', '$');
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi $names:{n | $n:{$it$}$}$";
        String expected = "[[@0,0:2='hi ',<TEXT>,1:0], [@1,3:3='$',<LDELIM>,1:3], [@2,4:8='names',<ID>,1:4], [@3,9:9=':',<COLON>,1:9], [@4,10:10='{',<LCURLY>,1:10], [@5,11:11='n',<ID>,1:11], [@6,13:13='|',<PIPE>,1:13], [@7,15:15='$',<LDELIM>,1:15], [@8,16:16='n',<ID>,1:16], [@9,17:17=':',<COLON>,1:17], [@10,18:18='{',<LCURLY>,1:18], [@11,19:19='$',<LDELIM>,1:19], [@12,20:21='it',<ID>,1:20], [@13,22:22='$',<RDELIM>,1:22], [@14,23:23='}',<RCURLY>,1:23], [@15,24:24='$',<RDELIM>,1:24], [@16,25:25='}',<RCURLY>,1:25], [@17,26:26='$',<RDELIM>,1:26]]";
        checkTokens(template, expected, '$', '$');
    }
}
