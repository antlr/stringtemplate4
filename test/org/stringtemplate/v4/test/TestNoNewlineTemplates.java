/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;

import static org.junit.Assert.assertEquals;

public class TestNoNewlineTemplates extends BaseTest {
		@Test
		public void testNoNewlineTemplate() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"[  <if(!x)>" +
			"<else>" +
			"<x>\n" +
			"<endif>" +
			"\n" +
			"\n" +
			"]\n" +
			"\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "[  99]";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testWSNoNewlineTemplate() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testEmptyNoNewlineTemplate() throws Exception {
		String template =
			"t(x) ::= <%%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testIgnoreIndent() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"	foo\n" +
			"	<x>\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "foo99";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testIgnoreIndentInIF() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"	<if(x)>\n" +
			"		foo\n" +
			"	<endif>\n" +
			"	<x>\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "foo99";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testKeepWS() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"	<x> <x> hi\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "99 99 hi";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testRegion() throws Exception {
		String template =
			"t(x) ::= <%\n" +
			"<@r>\n" +
			"	Ignore\n" +
			"	newlines and indents\n" +
			"<x>\n\n\n" +
			"<@end>\n" +
			"%>\n";
		STGroup g = new STGroupString(template);
		ST st = g.getInstanceOf("t");
		st.add("x", 99);
		String expected = "Ignorenewlines and indents99";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testDefineRegionInSubgroup() throws Exception {
		String dir = getRandomDir();
		String g1 = "a() ::= <<[<@r()>]>>\n";
		writeFile(dir, "g1.stg", g1);
		String g2 = "@a.r() ::= <%\n" +
		"	foo\n\n\n" +
		"%>\n";
		writeFile(dir, "g2.stg", g2);

		STGroup group1 = new STGroupFile(dir+"/g1.stg");
		STGroup group2 = new STGroupFile(dir+"/g2.stg");
		group2.importTemplates(group1); // define r in g2
		ST st = group2.getInstanceOf("a");
		String expected = "[foo]";
		String result = st.render();
		assertEquals(expected, result);
	}

}
