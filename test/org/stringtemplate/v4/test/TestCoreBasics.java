/*
 [The "BSD licence"]
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

import org.antlr.runtime.CommonToken;
import org.junit.Test;

import static org.junit.Assert.*;
import org.stringtemplate.*;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.compiler.GroupParser;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.ErrorManager;

import java.util.*;
import java.io.StringWriter;

public class TestCoreBasics extends BaseTest {
    @Test public void testNullAttr() throws Exception {
        String template = "hi <name>!";
        ST st = new ST(template);
        String expected =
            "hi !";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testAttr() throws Exception {
        String template = "hi <name>!";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "hi Ter!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testSetUnknownAttr() throws Exception {
        String templates =
            "t() ::= <<hi <name>!>>\n";
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.add("name", "Ter");
        String expected = "hi Ter!";
        String result = st.render();
        assertEquals(expected, result);

        // check error now
        expected = "[context [t] can't set attribute name; template t has no such attribute]";
        result = errors.errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testMultiAttr() throws Exception {
        String template = "hi <name>!";
        ST st = new ST(template);
        st.add("name", "Ter");
        st.add("name", "Tom");
        String expected =
            "hi TerTom!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testAttrIsList() throws Exception {
        String template = "hi <name>!";
        ST st = new ST(template);
        List names = new ArrayList() {{add("Ter"); add("Tom");}};
        st.add("name", names);
        st.add("name", "Sumana"); // shouldn't alter my version of names list!
        String expected =
            "hi TerTomSumana!";  // ST sees 3 names
        String result = st.render();
        assertEquals(expected, result);

        assertTrue(names.size()==2); // my names list is still just 2
    }

    @Test public void testAttrIsArray() throws Exception {
        String template = "hi <name>!";
        ST st = new ST(template);
        String[] names = new String[] {"Ter", "Tom"};
        st.add("name", names);
        st.add("name", "Sumana"); // shouldn't alter my version of names list!
        String expected =
            "hi TerTomSumana!";  // ST sees 3 names
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testProp() throws Exception {
        String template = "<u.id>: <u.name>";
        ST st = new ST(template);
        st.add("u", new User(1, "parrt"));
        String expected = "1: parrt";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testInclude() throws Exception {
        String template = "load <box()>;";
        ST st = new ST(template);
        st.impl.nativeGroup.defineTemplate("box",
                                "kewl\n" +
                                "daddy"
                                );
        st.add("name", "Ter");
        String expected =
            "load kewl\n" +
            "daddy;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeWithArg() throws Exception {
        String template = "load <box(x=\"arg\")>;";
        ST st = new ST(template);
        st.impl.nativeGroup.defineTemplate("box", "kewl <x> daddy");
        st.add("name", "Ter");
        String expected = "load kewl arg daddy;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeWithSingleUnnamedArg() throws Exception {
        String template = "load <box(\"arg\")>;";
        ST st = new ST(template);
        LinkedHashMap<String,FormalArgument> args =
            new LinkedHashMap<String,FormalArgument>();
        args.put("x", new FormalArgument("x"));
        st.impl.nativeGroup.defineTemplate("/",
                                           new CommonToken(GroupParser.ID, "box"),
                                           args, "kewl <x> daddy");
        st.add("name", "Ter");
        String expected = "load kewl arg daddy;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeWithArg2() throws Exception {
        String template = "load <box(x=\"arg\", y=foo())>;";
        ST st = new ST(template);
        st.impl.nativeGroup.defineTemplate("box", "kewl <x> <y> daddy");
        st.impl.nativeGroup.defineTemplate("foo", "blech");
        st.add("name", "Ter");
        String expected = "load kewl arg blech daddy;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testIncludeWithNestedArgs() throws Exception {
        String template = "load <box(y=foo(x=\"arg\"))>;";
        ST st = new ST(template);
        st.impl.nativeGroup.defineTemplate("box", "kewl <y> daddy");
        st.impl.nativeGroup.defineTemplate("foo", "blech <x>");
        st.add("name", "Ter");
        String expected = "load kewl blech arg daddy;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testDefineTemplate() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("inc", "<it>+1");
        group.defineTemplate("test", "hi <name>!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected =
            "hi TerTomSumana!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("inc", "[<it>]");
        group.defineTemplate("test", "hi <name:inc()>!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected =
            "hi [Ter][Tom][Sumana]!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMapWithExprAsTemplateName() throws Exception {
        String templates =
            "d ::= [\"foo\":\"bold\"]\n" +
            "test() ::= \"<name:(d.foo)()>\"\n" +
            "bold() ::= <<*<it>*>>\n";
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected = "*Ter**Tom**Sumana*";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testParallelMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <names,phones:{n,p | <n>:<p>;}>");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        st.add("phones", "x5001");
        st.add("phones", "x5002");
        st.add("phones", "x5003");
        String expected =
            "hi Ter:x5001;Tom:x5002;Sumana:x5003;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testParallelMapWith3Versus2Elements() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("test", "hi <names,phones:{n,p | <n>:<p>;}>");
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        st.add("names", "Sumana");
        st.add("phones", "x5001");
        st.add("phones", "x5002");
        String expected =
            "hi Ter:x5001;Tom:x5002;Sumana:;";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMapIndexes() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("inc", "<i>:<it>");
        group.defineTemplate("test", "<name:inc(); separator=\", \">");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", null); // don't count this one
        st.add("name", "Sumana");
        String expected =
            "1:Ter, 2:Tom, 3:Sumana";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testMapSingleValue() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("a", "[<it>]");
        group.defineTemplate("test", "hi <name:a()>!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        String expected = "hi [Ter]!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRepeatedMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("a", "[<it>]");
        group.defineTemplate("b", "(<it>)");
        group.defineTemplate("test", "hi <name:a():b()>!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected =
            "hi ([Ter])([Tom])([Sumana])!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testRoundRobinMap() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("a", "[<it>]");
        group.defineTemplate("b", "(<it>)");
        group.defineTemplate("test", "hi <name:a(),b()>!");
        ST st = group.getInstanceOf("test");
        st.add("name", "Ter");
        st.add("name", "Tom");
        st.add("name", "Sumana");
        String expected =
            "hi [Ter](Tom)[Sumana]!";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrueCond() throws Exception {
        String template = "<if(name)>works<endif>";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFalseCond() throws Exception {
        String template = "<if(name)>works<endif>";
        ST st = new ST(template);
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNotTrueCond() throws Exception {
        String template = "<if(!name)>works<endif>";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testNotFalseCond() throws Exception {
        String template = "<if(!name)>works<endif>";
        ST st = new ST(template);
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testTrueCondWithElse() throws Exception {
        String template = "<if(name)>works<else>fail<endif>";
        ST st = new ST(template);
        st.add("name", "Ter");
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testFalseCondWithElse() throws Exception {
        String template = "<if(name)>fail<else>works<endif>";
        ST st = new ST(template);
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testElseIf() throws Exception {
        String template = "<if(name)>fail<elseif(id)>works<else>fail<endif>";
        ST st = new ST(template);
        st.add("id", "2DF3DF");
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testElseIfNoElseAllFalse() throws Exception {
        String template = "<if(name)>fail<elseif(id)>fail<endif>";
        ST st = new ST(template);
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testElseIfAllExprFalse() throws Exception {
        String template = "<if(name)>fail<elseif(id)>fail<else>works<endif>";
        ST st = new ST(template);
        String expected = "works";
        String result = st.render();
        assertEquals(expected, result);
    }

	@Test public void testOr() throws Exception {
		String template = "<if(name||notThere)>works<else>fail<endif>";
		ST st = new ST(template);
		st.add("name", "Ter");
		String expected = "works";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testMapConditionAndEscapeInside() throws Exception {
        String template = "<if(m.name)>works \\\\<endif>";
        ST st = new ST(template);
        Map m = new HashMap();
        m.put("name", "Ter");
        st.add("m", m);
        String expected = "works \\";
        String result = st.render();
        assertEquals(expected, result);
    }

	@Test public void testAnd() throws Exception {
		String template = "<if(name&&notThere)>fail<else>works<endif>";
		ST st = new ST(template);
		st.add("name", "Ter");
		String expected = "works";
		String result = st.render();
		assertEquals(expected, result);
	}

	@Test public void testAndNot() throws Exception {
		String template = "<if(name&&!notThere)>works<else>fail<endif>";
		ST st = new ST(template);
		st.add("name", "Ter");
		String expected = "works";
		String result = st.render();
		assertEquals(expected, result);
	}

    @Test public void testITDoesntPropagate() throws Exception {
        STGroup group = new STGroup();
        group.defineTemplate("foo", "<it>");   // <it> not visible
        String template = "<names:{<foo()>}>"; // <it> visible only to {...} here
        group.defineTemplate("test", template);
        ST st = group.getInstanceOf("test");
        st.add("names", "Ter");
        st.add("names", "Tom");
        String expected = "";
        String result = st.render();
        assertEquals(expected, result);
    }

    @Test public void testCharLiterals() throws Exception {
        ST st = new ST(
                "Foo <\\n><\\n><\\t> bar\n"
                );
        StringWriter sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        String result = sw.toString();
        String expecting ="Foo \n\n\t bar\n";     // expect \n in output
        assertEquals(expecting, result);

        st = new ST(
                "Foo <\\n><\\t> bar" +newline);
        sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        expecting ="Foo \n\t bar\n";     // expect \n in output
        result = sw.toString();
        assertEquals(expecting, result);

        st = new ST(
                "Foo<\\ >bar<\\n>");
        sw = new StringWriter();
        st.write(new AutoIndentWriter(sw,"\n")); // force \n as newline
        result = sw.toString();
        expecting ="Foo bar\n"; // forced \n
        assertEquals(expecting, result);
    }

    @Test public void testUnicodeLiterals() throws Exception {
        ST st = new ST(
                "Foo <\\uFEA5><\\n><\\u00C2> bar\n"
                );
        String expecting ="Foo \ufea5"+newline+"\u00C2 bar"+newline;
        String result = st.render();
        assertEquals(expecting, result);

        st = new ST(
                "Foo <\\uFEA5><\\n><\\u00C2> bar" +newline);
        expecting ="Foo \ufea5"+newline+"\u00C2 bar"+newline;
        result = st.render();
        assertEquals(expecting, result);

        st = new ST(
                "Foo<\\ >bar<\\n>");
        expecting ="Foo bar"+newline;
        result = st.render();
        assertEquals(expecting, result);
    }

    @Test public void testSubtemplateExpr() throws Exception {
        String template = "<{name\n}>";
        ST st = new ST(template);
        String expected =
            "name"+newline;
        String result = st.render();
        assertEquals(expected, result);
    }

}
