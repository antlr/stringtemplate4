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
package org.stringtemplate.v4.test.annotations;

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.Misc;
import org.stringtemplate.v4.misc.STMessage;
import org.stringtemplate.v4.STAnnotatedGroup;
import org.stringtemplate.v4.STAnnotation;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.List;

import org.stringtemplate.v4.test.BaseTest;

/**
 * Tests for {@link STAnnotatedGroup}.
 */
public class TestSTAnnotatedGroup extends BaseTest {

    @Test public void testListsAllAnnotatedTemplates() throws Exception {
        
        String templates =
            "@Main(\"the main template\")\n" +
            "f() ::= <<foo>>" + Misc.newline +
            "@Unused(\"this is unused\")\n" +
            "b() ::= <<bar>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup stGroup = new STGroupFile(tmpdir+"/"+"t.stg");
        STAnnotatedGroup group = new STAnnotatedGroup(stGroup);
        stGroup.errMgr = buildErrorManager();

        group.show();
        List<STAnnotation> annotations = group.getAllAnnotations();
        Assert.assertNotNull(annotations);
        Assert.assertEquals(2, annotations.size());
        STAnnotation main = annotations.get(0);
        Assert.assertNotNull(main);
        Assert.assertNotNull(main.getName());
        Assert.assertEquals("Main", main.getName());
        Assert.assertNotNull(main.getValue());
        Assert.assertEquals("\"the main template\"", main.getValue());
        STAnnotation unused = annotations.get(1);
        Assert.assertNotNull(unused);
        Assert.assertNotNull(unused.getName());
        Assert.assertEquals("Unused", unused.getName());
        Assert.assertNotNull(unused.getValue());
        Assert.assertEquals("\"this is unused\"", unused.getValue());
    }

    @Test public void testAnnotationMatches() {
        STAnnotation annotation = new STAnnotation("Main", "\"^the (.*) template$\"");
        Assert.assertTrue(annotation.matches("the main template"));
    }

    @Test public void testAnnotationRemovesSurroundingQuotes() {
        Assert.assertEquals(
            "^the (.*) template$",
            new STAnnotation("Main", "\"^the (.*) template$\"") {
                @Override
                public String removeSurroundingQuotes(String value) {
                    return super.removeSurroundingQuotes(value);
                }
            }.removeSurroundingQuotes("\"^the (.*) template$\""));
    }

    @Test public void testMatchingAnnotations() throws Exception {
        
        String templates =
            "@Main(\"^The (.*) template$\")\n" +
            "f() ::= <<foo>>" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup stGroup = new STGroupFile(tmpdir+"/"+"t.stg");
        STAnnotatedGroup group = new STAnnotatedGroup(stGroup);
        stGroup.errMgr = buildErrorManager();

        group.show();
        List<STAnnotation> annotations = group.getMatchingAnnotations("The main template");
        Assert.assertNotNull(annotations);
        Assert.assertEquals(1, annotations.size());
        STAnnotation main = annotations.get(0);
        Assert.assertNotNull(main);
        Assert.assertNotNull(main.getName());
        Assert.assertEquals("Main", main.getName());
        Assert.assertNotNull(main.getValue());
        List<String> parameters = main.getParameters("The main template");
        Assert.assertNotNull(parameters);
        Assert.assertEquals(1, parameters.size());
        String parameter = parameters.get(0);
        Assert.assertNotNull(parameter);
        Assert.assertEquals("main", parameter);
    }
}

