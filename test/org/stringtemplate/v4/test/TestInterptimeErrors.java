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

public class TestInterptimeErrors extends BaseTest {
    public static class UserHiddenName {
        protected String name;
        public UserHiddenName(String name) { this.name = name; }
        protected String getName() { return name; }
    }

    public static class UserHiddenNameField {
        protected String name;
        public UserHiddenNameField(String name) { this.name = name; }
    }

    @Test public void testMissingEmbeddedTemplate() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t() ::= \"<foo()>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "context [/t] 1:1 no such template: /foo"+newline;
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testMissingSuperTemplate() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t() ::= \"<super.t()>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        String templates2 =
            "u() ::= \"blech\"" + Misc.newline;

        writeFile(tmpdir, "t2.stg", templates2);
        STGroup group2 = new STGroupFile(tmpdir+"/"+"t2.stg");
        group.importTemplates(group2);
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "context [/t] 1:1 no such template: super.t"+newline;
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testNoPropertyNotError() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t(u) ::= \"<u.x>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("u", new User(32, "parrt"));
        st.render();
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testHiddenPropertyNotError() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t(u) ::= \"<u.name>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("u", new UserHiddenName("parrt"));
        st.render();
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testHiddenFieldNotError() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t(u) ::= \"<u.name>\"" + Misc.newline;

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("u", new UserHiddenNameField("parrt"));
        st.render();
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testSoleArg() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t() ::= \"<u({9})>\"\n"+
            "u(x,y) ::= \"<x>\"\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "context [/t] 1:1 passed 1 arg(s) to template /u with 2 declared arg(s)"+newline;
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testSoleArgUsingApplySyntax() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t() ::= \"<{9}:u()>\"\n"+
            "u(x,y) ::= \"<x>\"\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        String expected = "9";
        String result = st.render();
        assertEquals(expected, result);

        expected = "context [/t] 1:5 passed 1 arg(s) to template /u with 2 declared arg(s)"+newline;
        result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testUndefinedAttr() {
        ErrorBuffer errors = new ErrorBuffer();

        String templates =
            "t() ::= \"<u()>\"\n"+
            "u() ::= \"<x>\"\n";

        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.render();
        String expected = "context [/t /u] 1:1 attribute x isn't defined"+newline;
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testParallelAttributeIterationWithMissingArgs() {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroup();
        group.setListener(errors);
        ST e = new ST(group,
                "<names,phones,salaries:{n,p | <n>@<p>}; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        e.add("salaries", "big");
        e.render();
        String errorExpecting =
            "1:23: anonymous template has 2 arg(s) but mapped across 3 value(s)" + newline +
            "context [anonymous] 1:23 passed 3 arg(s) to template /_sub1 with 2 declared arg(s)" + newline +
            "context [anonymous] 1:1 iterating through 3 values in zip map but template has 2 declared arguments" + newline;
        assertEquals(errorExpecting, errors.toString());
        String expecting = "Ter@1, Tom@2";
        assertEquals(expecting, e.render());
    }

    @Test public void testStringTypeMismatch()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroup();
        group.setListener(errors);
        ST e = new ST(group, "<trim(s)>");
        e.add("s", 34);
        e.render(); // generate the error
        String errorExpecting = "context [anonymous] 1:1 function trim expects a string not java.lang.Integer"+newline;
        assertEquals(errorExpecting, errors.toString());
    }

    @Test public void testStringTypeMismatch2()
    {
        ErrorBuffer errors = new ErrorBuffer();
        STGroup group = new STGroup();
        group.setListener(errors);
        ST e = new ST(group, "<strlen(s)>");
        e.add("s", 34);
        e.render(); // generate the error
        String errorExpecting = "context [anonymous] 1:1 function strlen expects a string not java.lang.Integer"+newline;
        assertEquals(errorExpecting, errors.toString());
    }
}
