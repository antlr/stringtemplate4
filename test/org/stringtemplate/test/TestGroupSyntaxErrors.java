package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.STGroupFile;
import org.stringtemplate.ErrorManager;
import org.stringtemplate.misc.ErrorBuffer;

public class TestGroupSyntaxErrors extends BaseTest {
    @Test public void testMissingTemplate() throws Exception {
        String templates =
            "foo() ::= \n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "t.stg 2:0: missing template at '<EOF>'"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testParen() throws Exception {
        String templates =
            "foo( ::= << >>\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "t.stg 1:5: missing ')' at '::='"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testNewlineInString() throws Exception {
        String templates =
            "foo() ::= \"\nfoo\"\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "t.stg 1:11: \\n in string"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testParen2() throws Exception {
        String templates =
            "foo) ::= << >>\n" +
            "bar() ::= <<bar>>\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "t.stg 1:0: garbled template definition starting at 'foo'"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testArg() throws Exception {
        String templates =
            "foo(a,) ::= << >>\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "t.stg 1:6: missing ID at ')'"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testArg2() throws Exception {
        String templates =
            "foo(a,,) ::= << >>\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:6: missing ID at ',', "+
                          "t.stg 1:7: missing ID at ')']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testArg3() throws Exception {
        String templates =
            "foo(a b) ::= << >>\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:6: extraneous input 'b' expecting ')']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testErrorWithinTemplate() throws Exception {
        String templates =
            "foo(a) ::= \"<a b>\"\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[1:15: 'b' came as a complete surprise to me]";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testMap() throws Exception {
        String templates =
            "d ::= []\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:7: missing dictionary entry at ']']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testMap2() throws Exception {
        String templates =
            "d ::= [\"k\":]\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:11: missing value for key at ']']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testMap3() throws Exception {
        String templates =
            "d ::= [\"k\":{dfkj}}]\n"; // extra }
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:17: invalid character '}']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testUnterminatedString() throws Exception {
        String templates =
            "f() ::= \""; // extra }
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		ErrorBuffer errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:9: unterminated string, t.stg 1:9: missing template at '<EOF>']";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}
}
