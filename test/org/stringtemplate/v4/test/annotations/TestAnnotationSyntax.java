/*
  [The "BSD license"]
  Copyright (c) 2009-today Terence Parr
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

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.stringtemplate.v4.test.BaseTest;

public class TestAnnotationSyntax extends BaseTest {
	@Test public void testAnnotatedTemplate() throws Exception {
		String templates =
			"@FactoryImpl(\"^.* uses a (.*) delegate to create a new strategy\")\n" +
			"t() ::= <<foo>>" + Misc.newline;

		writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		group.errMgr = buildErrorManager();
		String expected =
			"@FactoryImpl(\"^.* uses a (.*) delegate to create a new strategy\")\n" +
			"t() ::= <<" + Misc.newline+
			"foo" + Misc.newline+
			">>"+ Misc.newline;
		String result = group.show();
		assertEquals(expected, result);
	}
}
