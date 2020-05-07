package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import static org.junit.Assert.assertEquals;

public class TestScopes extends BaseTest {
    @Test public void testSeesEnclosingAttr()
    {
        String templates =
            "t(x,y) ::= \"<u()>\"\n" +
            "u() ::= \"<x><y>\"";
        ErrorBuffer errors = new ErrorBuffer();
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("x", "x");
        st.add("y", "y");
        String result = st.render();

        String expectedError = "";
        assertEquals(expectedError, errors.toString());

        String expected = "xy";
        assertEquals(expected, result);
    }

    @Test public void testMissingArg()
    {
        String templates =
            "t() ::= \"<u()>\"\n" +
            "u(z) ::= \"\"";
        ErrorBuffer errors = new ErrorBuffer();
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        String result = st.render();

        String expectedError = "context [/t] 1:1 passed 0 arg(s) to template /u with 1 declared arg(s)"+newline;
        assertEquals(expectedError, errors.toString());
    }

    @Test public void testUnknownAttr()
    {
        String templates =
            "t() ::= \"<x>\"\n";
        ErrorBuffer errors = new ErrorBuffer();
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        String result = st.render();

        String expectedError = "context [/t] 1:1 attribute x isn't defined"+newline;
        assertEquals(expectedError, errors.toString());
    }

    @Test public void testArgWithSameNameAsEnclosing()
    {
        String templates =
            "t(x,y) ::= \"<u(x)>\"\n" +
            "u(y) ::= \"<x><y>\"";
        ErrorBuffer errors = new ErrorBuffer();
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("x", "x");
        st.add("y", "y");
        String result = st.render();

        String expectedError = "";
        assertEquals(expectedError, errors.toString());

        String expected = "xx";
        assertEquals(expected, result);
        group.setListener(ErrorManager.DEFAULT_ERROR_LISTENER);
    }

    @Test public void testIndexAttrVisibleLocallyOnly()
    {
        String templates =
            "t(names) ::= \"<names:{n | <u(n)>}>\"\n" +
            "u(x) ::= \"<i>:<x>\"";
        ErrorBuffer errors = new ErrorBuffer();
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setListener(errors);
        ST st = group.getInstanceOf("t");
        st.add("names", "Ter");
        String result = st.render();
        group.getInstanceOf("u").impl.dump();

        String expectedError = "t.stg 2:11: implicitly-defined attribute i not visible"+newline;
        assertEquals(expectedError, errors.toString());

        String expected = ":Ter";
        assertEquals(expected, result);
        group.setListener(ErrorManager.DEFAULT_ERROR_LISTENER);
    }

}
