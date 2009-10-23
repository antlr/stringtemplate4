package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.STGroup;
import org.stringtemplate.STErrorListener;
import org.stringtemplate.Misc;

public class TestSyntaxErrors extends BaseTest {
    @Test public void testEmptyExpr() throws Exception {
        String template = "<>";
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.listener = errors;
        group.defineTemplate("test", template);
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }    

    @Test public void testWeirdChar() throws Exception {
        String template = "<*>";
        STGroup group = new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.listener = errors;
        group.defineTemplate("test", template);
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }

    @Test public void testValidButOutOfPlaceChar() throws Exception {
        String templates =
            "group t;\n" +
            "foo() ::= \"hi <.> mom\"\n";
        Misc.writeFile(tmpdir, "t.stg", templates);

        STGroup group = null;
        STErrorListener errors = new ErrorBuffer();
        try {
            group = new STGroup(tmpdir+"/"+"t.stg");
            group.listener = errors;
            group.load(); // force load
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }
        String expected = "";
        String result = errors.toString();
        assertEquals(expected, result);
    }
}
