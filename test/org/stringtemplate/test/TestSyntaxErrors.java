package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;
import org.stringtemplate.misc.ErrorBuffer;
import org.stringtemplate.compiler.STException;
import org.antlr.runtime.RecognitionException;

public class TestSyntaxErrors extends BaseTest {
    @Test public void testEmptyExpr() throws Exception {
        String template = " <> ";
        STGroup group = new STGroup();
		String result = null;
		try {
        	group.defineTemplate("test", template);
		}
		catch (STException se) {
            RecognitionException re = (RecognitionException)se.getCause();
            result = new STCompiletimeMessage(ErrorType.SYNTAX_ERROR,re.token,re,se.getMessage()).toString();
		}
        String expected = "1:0: this doesn't look like a template: \" <> \"";
        assertEquals(expected, result);
    }

    @Test public void testEmptyExpr2() throws Exception {
        String template = "hi <> ";
        STGroup group = new STGroup();
		String result = null;
		try {
        	group.defineTemplate("test", template);
		}
		catch (STException se) {
            RecognitionException re = (RecognitionException)se.getCause();
            result = new STCompiletimeMessage(ErrorType.SYNTAX_ERROR,re.token,re,se.getMessage()).toString();
		}
        String expected = "1:3: doesn't look like an expression";
        assertEquals(expected, result);
    }

    @Test public void testWeirdChar() throws Exception {
        String template = "   <*>";
        STGroup group = new STGroup();
		String result = null;
		try {
        	group.defineTemplate("test", template);
		}
		catch (STException se) {
            RecognitionException re = (RecognitionException)se.getCause();
            result = new STCompiletimeMessage(ErrorType.SYNTAX_ERROR,re.token,re,se.getMessage()).toString();
		}
        String expected = "1:4: invalid character: *";
        assertEquals(expected, result);
    }

    @Test public void testValidButOutOfPlaceChar() throws Exception {
        String templates =
            "foo() ::= <<hi <.> mom>>\n";
        writeFile(tmpdir, "t.stg", templates);

		STErrorListener errors = new ErrorBuffer();
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
        String expected = "1:15: doesn't look like an expression"+newline;
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testValidButOutOfPlaceCharOnDifferentLine() throws Exception {
        String templates =
				"foo() ::= \"hi <\n" +
				".> mom\"\n";
		writeFile(tmpdir, "t.stg", templates);

		ErrorBuffer errors = new ErrorBuffer();
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "[t.stg 1:15: \\n in string, 1:14: doesn't look like an expression]";
		String result = errors.errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testErrorInNestedTemplate() throws Exception {
        String templates =
            "foo() ::= \"hi <name:{[<aaa.bb!>]}> mom\"\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "1:29: '!' came as a complete surprise to me"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testEOFInExpr() throws Exception {
        String templates =
            "foo() ::= \"hi <name:{[<aaa.bb>]}\"\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "1:32: premature EOF"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testMissingRPAREN() throws Exception {
        String templates =
            "foo() ::= \"hi <foo(>\"\n";
        writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroupFile(tmpdir+"/"+"t.stg");
		ErrorManager.setErrorListener(errors);
		group.load(); // force load
		String expected = "1:19: mismatched input '>' expecting RPAREN"+newline;
		String result = errors.toString();
		assertEquals(expected, result);
	}

}
