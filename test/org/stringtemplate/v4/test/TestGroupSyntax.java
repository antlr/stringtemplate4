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
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import static org.junit.Assert.assertEquals;

import java.io.File;

public class TestGroupSyntax extends BaseTest {
    @Test public void testSimpleGroup() throws Exception {
        String templates =
            "t() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t() ::= <<" + Misc.newline+
            "foo" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

	@Test public void testEscapedQuote() throws Exception {
		// setTest(ranges) ::= "<ranges; separator=\"||\">"
		// has to unescape the strings.
		String templates =
			"setTest(ranges) ::= \"<ranges; separator=\\\"||\\\">\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"setTest(ranges) ::= <<"+Misc.newline+
			"<ranges; separator=\"||\">" +Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testMultiTemplates() throws Exception {
		String templates =
			"ta(x) ::= \"[<x>]\"" + Misc.newline +
			"duh() ::= <<hi there>>" + Misc.newline +
			"wow() ::= <<last>>" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"ta(x) ::= <<" +Misc.newline+
			"[<x>]" +Misc.newline+
			">>" +Misc.newline+
			"duh() ::= <<" +Misc.newline+
			"hi there" +Misc.newline+
			">>" +Misc.newline+
			"wow() ::= <<" +Misc.newline+
			"last" +Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testSetDefaultDelimiters() throws Exception {
		String templates =
			"delimiters \"<\", \">\"" + Misc.newline +
			"ta(x) ::= \"[<x>]\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("ta");
		st.add("x", "hi");
		String expected = "[hi]";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testSetNonDefaultDelimiters() throws Exception {
		String templates =
			"delimiters \"%\", \"%\"" + Misc.newline +
			"ta(x) ::= \"[%x%]\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("ta");
		st.add("x", "hi");
		String expected = "[hi]";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testSingleTemplateWithArgs() throws Exception {
        String templates =
            "t(a,b) ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a,b) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

	@Test public void testDefaultValues() throws Exception {
		String templates =
			"t(a={def1},b=\"def2\") ::= \"[<a>]\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"t(a={def1},b=\"def2\") ::= <<" + Misc.newline+
			"[<a>]" + Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testDefaultValues2() throws Exception {
		String templates =
			"t(x, y, a={def1}, b=\"def2\") ::= \"[<a>]\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"t(x,y,a={def1},b=\"def2\") ::= <<" + Misc.newline+
			"[<a>]" + Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

    @Test public void testDefaultValueTemplateWithArg() throws Exception {
        String templates =
            "t(a={x | 2*<x>}) ::= \"[<a>]\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        String expected =
            "t(a={x | 2*<x>}) ::= <<" + Misc.newline+
            "[<a>]" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

	@Test
	public void testDefaultValueBehaviorTrue() throws Exception {
		String templates =
			"t(a=true) ::= <<\n" +
			"<a><if(a)>+<else>-<endif>\n" +
			">>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		String expected = "true+";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test
	public void testDefaultValueBehaviorFalse() throws Exception {
		String templates =
			"t(a=false) ::= <<\n" +
			"<a><if(a)>+<else>-<endif>\n" +
			">>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		String expected = "false-";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test
	public void testDefaultValueBehaviorEmptyTemplate() throws Exception {
		String templates =
			"t(a={}) ::= <<\n" +
			"<a><if(a)>+<else>-<endif>\n" +
			">>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		String expected = "+";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test
	public void testDefaultValueBehaviorEmptyList() throws Exception {
		String templates =
			"t(a=[]) ::= <<\n" +
			"<a><if(a)>+<else>-<endif>\n" +
			">>\n";

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir + File.separatorChar + "t.stg");
		ST st = group.getInstanceOf("t");
		String expected = "-";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testNestedTemplateInGroupFile() throws Exception {
		String templates =
			"t(a) ::= \"<a:{x | <x:{y | <y>}>}>\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"t(a) ::= <<" + newline +
			"<a:{x | <x:{y | <y>}>}>" + newline +
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testNestedDefaultValueTemplate() throws Exception {
		String templates =
			"t(a={x | <x:{y|<y>}>}) ::= \"ick\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		group.load();
		String expected =
			"t(a={x | <x:{y|<y>}>}) ::= <<" + newline +
			"ick" + newline +
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testNestedDefaultValueTemplateWithEscapes() throws Exception {
		String templates =
			"t(a={x | \\< <x:{y|<y>\\}}>}) ::= \"[<a>]\"" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		String expected =
			"t(a={x | \\< <x:{y|<y>\\}}>}) ::= <<" + Misc.newline+
			"[<a>]" + Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}

	@Test public void testMessedUpTemplateDoesntCauseRuntimeError() throws Exception {
		String templates =
			"main(p) ::= <<\n" +
			"<f(x=\"abc\")>\n" +
			">>\n" +
			"\n" +
			"f() ::= <<\n" +
			"<x>\n" +
			">>\n";
		writeFile(tmpdir, "t.stg", templates);

		STGroupFile group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		group.setListener(errors);
		ST st = group.getInstanceOf("main");
		st.render();

		String expected = "[context [/main] 1:1 passed 1 arg(s) to template /f with 0 declared arg(s)," +
						  " context [/main] 1:1 attribute x isn't defined," +
						  " context [/main /f] 1:1 attribute x isn't defined]";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}
}
