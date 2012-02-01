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
import org.stringtemplate.v4.STGroupString;

import static org.junit.Assert.assertEquals;

/** */
public class TestAggregates extends BaseTest {
	@Test public void testApplyAnonymousTemplateToAggregateAttribute() throws Exception {
		ST st =
			new ST("<items:{it|<it.id>: <it.lastName>, <it.firstName>\n}>");
		// also testing wacky spaces in aggregate spec
		st.addAggr("items.{ firstName ,lastName, id }", "Ter", "Parr", 99);
		st.addAggr("items.{firstName, lastName ,id}", "Tom", "Burns", 34);
		String expecting =
			"99: Parr, Ter"+newline +
			"34: Burns, Tom"+newline;
		assertEquals(expecting, st.render());
	}

	public static class Decl {
		String name;
		String type;
		public Decl(String name, String type) {this.name=name; this.type=type;}
		public String getName() {return name;}
		public String getType() {return type;}
	}

	@Test public void testComplicatedIndirectTemplateApplication() throws Exception {
		String templates =
			"group Java;"+newline +
			""+newline +
		    "file(variables) ::= <<\n" +
			"<variables:{ v | <v.decl:(v.format)()>}; separator=\"\\n\">"+newline +
			">>"+newline+
			"intdecl(decl) ::= \"int <decl.name> = 0;\""+newline +
			"intarray(decl) ::= \"int[] <decl.name> = null;\""+newline
			;
		STGroup group = new STGroupString(templates);
		ST f = group.getInstanceOf("file");
		f.addAggr("variables.{ decl,format }", new Decl("i", "int"), "intdecl");
		f.addAggr("variables.{decl ,  format}", new Decl("a", "int-array"), "intarray");
		//System.out.println("f='"+f+"'");
		String expecting = "int i = 0;" +newline+
						   "int[] a = null;";
		assertEquals(expecting, f.render());
	}

}
