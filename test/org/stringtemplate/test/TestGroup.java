package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

public class TestGroup {
    @Test public void testSimpleGroup() throws Exception {
        String templates =
            "group t;" + Misc.newline+
            "t() ::= <<foo>>" + Misc.newline;

        String tmpdir = System.getProperty("java.io.tmpdir");
        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.load(tmpdir+"/"+"t.stg");
        String expected =
            "group t;" + Misc.newline+
            "t()::= <<" + Misc.newline+
            "foo" + Misc.newline+
            ">>"+ Misc.newline;
        String result = group.show();
        assertEquals(expected, result);
    }

}
