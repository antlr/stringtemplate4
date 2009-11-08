package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.Misc;
import org.stringtemplate.STGroup;
import org.stringtemplate.STGroupDir;
import org.stringtemplate.ST;

import java.io.File;

public class TestGroupDir extends BaseTest {
    @Test public void testSimpleGroup() throws Exception {
        String randomDir = tmpdir+"/dir"+String.valueOf(Math.random());
        File f = new File(randomDir);
        f.mkdirs();
        String a =
            "a(x) ::= <<"+newline+
            "foo"+newline+
            ">>"+newline;
        Misc.writeFile(randomDir, "a.st", a);
        STGroup group = new STGroupDir(randomDir);
        ST st = group.getInstanceOf("a");
        String expected = "foo"+newline;
        String result = st.render();
        assertEquals(expected, result);
    }
}
