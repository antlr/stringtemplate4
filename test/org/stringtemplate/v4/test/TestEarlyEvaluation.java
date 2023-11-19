package org.stringtemplate.v4.test;

import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.gui.STViz;

import java.awt.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEarlyEvaluation extends BaseTest {
    /**
     * @return true if at least one Window is visible
     */
    public static boolean isAnyWindowVisible() {
        for (Window w : Window.getWindows()) {
            if (w.isVisible())
                return true;
        }
        return false;
    }

    public static void waitUntilAnyWindowIsVisible(long maxWaitMillis) {
        long startMillis = System.currentTimeMillis();
        while (!isAnyWindowVisible()) {
            if (System.currentTimeMillis() - startMillis > maxWaitMillis) {
                throw new RuntimeException("Timeout");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    public static void waitUntilAllWindowsAreClosed() {
        while (isAnyWindowVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * see
     * https://www.antlr3.org/pipermail/stringtemplate-interest/2011-May/003476.html
     *
     * @throws Exception
     */
    @Test
    public void testEarlyEval() throws Exception {
        String templates = "main() ::= <<\n<f(p=\"x\")>*<f(p=\"y\")>\n>>\n\n"
                + "f(p,q={<({a<p>})>}) ::= <<\n-<q>-\n>>";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        String s = st.render();
        assertEquals("-ax-*-ay-", s);

        // Calling inspect led to an java.lang.ArrayIndexOutOfBoundsException in
        // 4.0.2
        STViz viz = st.inspect();
        if (interactive) {
            viz.waitForClose();
        }
        else {
            waitUntilAnyWindowIsVisible(4000);
            viz.viewFrame.dispose();
            waitUntilAllWindowsAreClosed();
        }
    }

    /**
     * see
     * https://www.antlr.org/pipermail/stringtemplate-interest/2011-May/003476.html
     *
     * @throws Exception
     */
    @Test
    public void testEarlyEval2() throws Exception {
        String templates = "main() ::= <<\n<f(p=\"x\")>*\n>>\n\n"
                + "f(p,q={<({a<p>})>}) ::= <<\n-<q>-\n>>";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        String s = st.render();
        assertEquals("-ax-*", s);

        // When <f(...)> is invoked only once inspect throws no Exception in
        // 4.0.2
        STViz viz = st.inspect();
        if (interactive) {
            viz.waitForClose();
        }
        else {
            waitUntilAnyWindowIsVisible(4000);
            viz.viewFrame.dispose();
            waitUntilAllWindowsAreClosed();
        }
    }


    /**
     * see https://www.antlr3.org/pipermail/stringtemplate-interest/2011-August/003758.html
     * @throws Exception
     */
    @Test
    public void testBugArrayIndexOutOfBoundsExceptionInSTRuntimeMessage_getSourceLocation()
            throws Exception {
        String templates = "main(doit = true) ::= "
                + "\"<if(doit || other)><t(...)><endif>\"\n"
                + "t2() ::= \"Hello\"\n" //
                + "t(x={<(t2())>}) ::= \"<x>\"";

        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        String s = st.render();
        assertEquals("Hello", s);

        // Inspecting this template threw an ArrayIndexOutOfBoundsException
        // in 4.0.2.
        // With the default for x changed to {<t2()>} (i.e. lazy eval) inspect
        // works fine. Also removing the " || other" and keeping the early eval
        // works fine with inspect.

        STViz viz = st.inspect();
        if (interactive) {
            viz.waitForClose();
        }
        else {
            waitUntilAnyWindowIsVisible(4000);
            viz.viewFrame.dispose();
            waitUntilAllWindowsAreClosed();
        }
    }

    @Test
    public void testEarlyEvalInIfExpr() throws Exception {
        String templates = "main(x) ::= << <if((x))>foo<else>bar<endif> >>";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        String s = st.render();
        assertEquals(" bar ", s);

        st.add("x", "true");
        s = st.render();
        assertEquals(" foo ", s);
    }

    @Test
    public void testEarlyEvalOfSubtemplateInIfExpr() throws Exception {
        String templates = "main(x) ::= << <if(({a<x>b}))>foo<else>bar<endif> >>";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        String s = st.render();
        assertEquals(" foo ", s);
    }

    @Test
    public void testEarlyEvalOfMapInIfExpr() throws Exception {
        String templates =
            "m ::= [\n"+
            "   \"parrt\": \"value\",\n"+
            "   default: \"other\"\n"+
            "]\n" +
            "main(x) ::= << p<x>t: <m.({p<x>t})>, <if(m.({p<x>t}))>if<else>else<endif> >>\n";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");

        st.add("x", null);
        String s = st.render();
        assertEquals(" pt: other, if ", s);

        st.add("x", "arr");
        s = st.render();
        assertEquals(" parrt: value, if ", s);
    }

    @Test
    public void testEarlyEvalOfMapInIfExprPassInHashMap() throws Exception {
        String templates =
            "main(m,x) ::= << p<x>t: <m.({p<x>t})>, <if(m.({p<x>t}))>if<else>else<endif> >>\n";
        writeFile(tmpdir, "t.stg", templates);

        STGroup group = new STGroupFile(tmpdir + "/t.stg");

        ST st = group.getInstanceOf("main");
        st.add("m", new HashMap<String, String>() {{put("parrt","value");}});

        st.add("x", null);
        String s = st.render();
        assertEquals(" pt: , else ", s); // m[null] has no default value so else clause

        st.add("x", "arr");
        s = st.render();
        assertEquals(" parrt: value, if ", s);
    }

}
