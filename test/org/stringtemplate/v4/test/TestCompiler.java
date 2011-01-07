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

import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.ErrorManager;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestCompiler extends BaseTest {
    @Before
    public void setUp() { org.stringtemplate.v4.compiler.Compiler.subtemplateCount = 0; }

    @Test public void testAttr() throws Exception {
        String template = "hi <name>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, " +
            "write, " +
            "load_attr 1, " +
            "write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[hi , name]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

	@Test public void testInclude() throws Exception {
		String template = "hi <foo()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_str 0, write, new 1 0, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[hi , foo]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testSuperInclude() throws Exception {
		String template = "<super.foo()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"super_new 0 0, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[foo]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testSuperIncludeWithArgs() throws Exception {
		String template = "<super.foo(a,{b})>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, new 1 0, super_new 2 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[a, _sub1, foo]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testIncludeWithArgs() throws Exception {
		String template = "hi <foo(a,b)>";
		CompiledST code = new org.stringtemplate.v4.compiler.Compiler().compile(template);
		String asmExpected =
			"load_str 0, write, load_attr 1, load_attr 2, new 3 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[hi , a, b, foo]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testAnonIncludeArgs() throws Exception {
		String template = "<({ a, b | <a><b>})>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"new 0 0, tostr, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[_sub1]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testAnonIncludeArgMismatch() throws Exception {
		STErrorListener errors = new ErrorBuffer();
		String template = "<a:{foo}>";
		CompiledST code = new Compiler(new ErrorManager(errors)).compile(template);
		String expected = "1:3: anonymous template has 0 arg(s) but mapped across 1 value(s)"+newline;
		assertEquals(expected, errors.toString());
	}

	@Test public void testAnonIncludeArgMismatch2() throws Exception {
		STErrorListener errors = new ErrorBuffer();
		String template = "<a,b:{x|foo}>";
		CompiledST code = new Compiler(new ErrorManager(errors)).compile(template);
		String expected = "1:5: anonymous template has 1 arg(s) but mapped across 2 value(s)"+newline;
		assertEquals(expected, errors.toString());
	}

	@Test public void testAnonIncludeArgMismatch3() throws Exception {
		STErrorListener errors = new ErrorBuffer();
		String template = "<a:{x|foo},{bar}>";
		CompiledST code = new Compiler(new ErrorManager(errors)).compile(template);
		String expected = "1:11: anonymous template has 0 arg(s) but mapped across 1 value(s)"+newline;
		assertEquals(expected, errors.toString());
	}

	@Test public void testIndirectIncludeWitArgs() throws Exception {
		String template = "hi <(foo)(a,b)>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_str 0, write, load_attr 1, tostr, load_attr 2, load_attr 3, new_ind 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[hi , foo, a, b]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

    @Test public void testProp() throws Exception {
        String template = "hi <a.b>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, write, load_attr 1, load_prop 2, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[hi , a, b]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testProp2() throws Exception {
        String template = "<u.id>: <u.name>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_attr 0, load_prop 1, write, load_str 2, write, " +
            "load_attr 0, load_prop 3, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[u, id, : , name]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

	@Test public void testMap() throws Exception {
		String template = "<name:bold()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, new 1 1, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, bold]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testMapAsOption() throws Exception {
		String template = "<a; wrap=name:bold()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, options, load_attr 1, null, new 2 1, map, " +
			"store_option 4, write_opt";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[a, name, bold]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testMapArg() throws Exception {
		String template = "<name:bold(x)>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, load_attr 1, new 2 2, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, x, bold]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testIndirectMapArg() throws Exception {
		String template = "<name:(t)(x)>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, load_attr 1, tostr, null, load_attr 2, new_ind 2, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, t, x]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testRepeatedMap() throws Exception {
		String template = "<name:bold():italics()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, new 1 1, map, null, new 2 1, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, bold, italics]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testRepeatedMapArg() throws Exception {
		String template = "<name:bold(x):italics(x,y)>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, load_attr 1, new 2 2, map, " +
			"null, load_attr 1, load_attr 3, new 4 3, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, x, bold, y, italics]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testRotMap() throws Exception {
		String template = "<name:bold(),italics()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, new 1 1, null, new 2 1, rot_map 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, bold, italics]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testRotMapArg() throws Exception {
		String template = "<name:bold(x),italics()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, load_attr 1, new 2 2, null, new 3 1, rot_map 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, x, bold, italics]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testZipMap() throws Exception {
		String template = "<names,phones:bold()>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[names, phones, bold]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testZipMapArg() throws Exception {
		String template = "<names,phones:bold(x)>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, load_attr 1, null, null, load_attr 2, new 3 3, zip_map 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[names, phones, x, bold]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testAnonMap() throws Exception {
		String template = "<name:{n | <n>}>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, null, new 1 1, map, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[name, _sub1]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

	@Test public void testAnonZipMap() throws Exception {
		String template = "<a,b:{x,y | <x><y>}>";
		CompiledST code = new Compiler().compile(template);
		String asmExpected =
			"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
		String asmResult = code.instrs();
		assertEquals(asmExpected, asmResult);
		String stringsExpected = "[a, b, _sub1]";
		String stringsResult = Arrays.toString(code.strings);
		assertEquals(stringsExpected, stringsResult);
	}

    @Test public void testIf() throws Exception {
        String template = "go: <if(name)>hi, foo<endif>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, write, load_attr 1, brf 14, load_str 2, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[go: , name, hi, foo]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testIfElse() throws Exception {
        String template = "go: <if(name)>hi, foo<else>bye<endif>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, " +
            "write, " +
            "load_attr 1, " +
            "brf 17, " +
            "load_str 2, " +
            "write, " +
            "br 21, " +
            "load_str 3, " +
            "write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[go: , name, hi, foo, bye]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testElseIf() throws Exception {
        String template = "go: <if(name)>hi, foo<elseif(user)>a user<endif>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, " +
            "write, " +
            "load_attr 1, " +
            "brf 17, " +
            "load_str 2, " +
            "write, " +
            "br 27, " +
            "load_attr 3, " +
            "brf 27, " +
            "load_str 4, " +
            "write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[go: , name, hi, foo, user, a user]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testElseIfElse() throws Exception {
        String template = "go: <if(name)>hi, foo<elseif(user)>a user<else>bye<endif>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, " +
            "write, " +
            "load_attr 1, " +
            "brf 17, " +
            "load_str 2, " +
            "write, " +
            "br 34, " +
            "load_attr 3, " +
            "brf 30, " +
            "load_str 4, " +
            "write, " +
            "br 34, " +
            "load_str 5, " +
            "write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[go: , name, hi, foo, user, a user, bye]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testOption() throws Exception {
        String template = "hi <name; separator=\"x\">";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, write, load_attr 1, options, load_str 2, store_option 3, write_opt";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[hi , name, x]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testOptionAsTemplate() throws Exception {
        String template = "hi <name; separator={, }>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, write, load_attr 1, options, new 2 0, store_option 3, write_opt";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[hi , name, _sub1]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testOptions() throws Exception {
        String template = "hi <name; anchor, wrap=foo(), separator=\", \">";
        CompiledST code = new Compiler().compile(template);
        String asmExpected =
            "load_str 0, " +
            "write, " +
            "load_attr 1, " +
            "options, " +
            "load_str 2, " +
            "store_option 0, " +
            "new 3 0, " +
            "store_option 4, " +
            "load_str 4, " +
            "store_option 3, " +
            "write_opt";
        String stringsExpected = // the ", , ," is the ", " separator string
            "[hi , name, true, foo, , ]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
    }

    @Test public void testEmptyList() throws Exception {
        String template = "<[]>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected = "list, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testList() throws Exception {
        String template = "<[a,b]>";
        CompiledST code = new Compiler().compile(template);
        String asmExpected = "list, load_attr 0, add, load_attr 1, add, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[a, b]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testEmbeddedRegion() throws Exception {
        String template = "<@r>foo<@end>";
        // compile as if in root dir and in template 'a'
        CompiledST code = new Compiler('<', '>').compile("a", template);
        String asmExpected =
            "new 0 0, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[region__a__r]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }

    @Test public void testRegion() throws Exception {
        String template = "x:<@r()>";
        // compile as if in root dir and in template 'a'
        CompiledST code = new Compiler('<', '>').compile("a", template);
        String asmExpected =
            "load_str 0, write, new 1 0, write";
        String asmResult = code.instrs();
        assertEquals(asmExpected, asmResult);
        String stringsExpected = "[x:, region__a__r]";
        String stringsResult = Arrays.toString(code.strings);
        assertEquals(stringsExpected, stringsResult);
    }
}
