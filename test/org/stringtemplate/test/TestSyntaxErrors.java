package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.Misc;
import org.stringtemplate.STException;
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
			result = 1+":"+re.charPositionInLine+": "+se.getMessage();
		}
        String expected = "1:1: mismatched input '<' expecting EOF";
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
			result = 1+":"+re.charPositionInLine+": "+se.getMessage();
		}
        String expected = "1:4: invalid character: *";
        assertEquals(expected, result);
    }

    @Test public void testValidButOutOfPlaceChar() throws Exception {
        String templates =
            "group t;\n" +
            "foo() ::= <<hi <.> mom>>\n";
        Misc.writeFile(tmpdir, "t.stg", templates);

		STErrorListener errors = new ErrorBuffer();
		STGroup group = new STGroup(tmpdir+"/"+"t.stg");
		group.listener = errors;
		group.load(); // force load
        String expected = "2:15: mismatched input '<' expecting EOF";
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testValidButOutOfPlaceCharOnDifferentLine() throws Exception {
        String templates =
			"group t;\n" +
				"foo() ::= \"hi <\n" +
				".> mom\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);

		STErrorListener errors = new ErrorBuffer();
		STGroup group = new STGroup(tmpdir+"/"+"t.stg");
		group.listener = errors;
		group.load(); // force load
		String expected = "2:14: mismatched input '<' expecting EOF"; // TODO: terrible err message
		String result = errors.toString();
		assertEquals(expected, result);
	}

    @Test public void testErrorInNestedTemplate() throws Exception {
        String templates =
            "group t;\n" +
            "foo() ::= \"hi <name:{[<aaa.bb!>]}> mom\"\n";
        Misc.writeFile(tmpdir, "t.stg", templates);

		STGroup group = null;
		STErrorListener errors = new ErrorBuffer();
		group = new STGroup(tmpdir+"/"+"t.stg");
		group.listener = errors;
		group.load(); // force load
		String expected = "2:29: no viable alternative at input '!'";
		String result = errors.toString();
		assertEquals(expected, result);
	}
}
