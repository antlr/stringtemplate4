package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;
import org.stringtemplate.STGroup;
import org.stringtemplate.Misc;
import org.stringtemplate.STErrorListener;

import java.util.ArrayList;

public class TestLists extends BaseTest {
	@Test public void testJustCat() throws Exception {
		ST e = new ST(
				"<[names,phones]>"
			);
		e.add("names", "Ter");
		e.add("names", "Tom");
		e.add("phones", "1");
		e.add("phones", "2");
		String expecting = "TerTom12";
		assertEquals(expecting, e.render());
	}

	@Test public void testCat2Attributes() throws Exception {
		ST e = new ST(
				"<[names,phones]; separator=\", \">"
			);
		e.add("names", "Ter");
		e.add("names", "Tom");
		e.add("phones", "1");
		e.add("phones", "2");
		String expecting = "Ter, Tom, 1, 2";
		assertEquals(expecting, e.render());
	}

	@Test public void testCat2AttributesWithApply() throws Exception {
		ST e = new ST(
				"<[names,phones]:{a|<a>.}>"
			);
		e.add("names", "Ter");
		e.add("names", "Tom");
		e.add("phones", "1");
		e.add("phones", "2");
		String expecting = "Ter.Tom.1.2.";
		assertEquals(expecting, e.render());
	}

	@Test public void testCat3Attributes() throws Exception {
		ST e = new ST(
				"<[names,phones,salaries]; separator=\", \">"
			);
		e.add("names", "Ter");
		e.add("names", "Tom");
		e.add("phones", "1");
		e.add("phones", "2");
		e.add("salaries", "big");
		e.add("salaries", "huge");
		String expecting = "Ter, Tom, 1, 2, big, huge";
		assertEquals(expecting, e.render());
	}

    @Test public void testCatWithTemplateApplicationAsElement() throws Exception {
        ST e = new ST(
                "<[names:{<it>!},phones]; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones" , "1");
        e.add("phones", "2");
        String expecting = "Ter!, Tom!, 1, 2";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatWithIFAsElement() throws Exception {
        ST e = new ST(
                "<[{<if(names)>doh<endif>},phones]; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones" , "1");
        e.add("phones", "2");
        String expecting = "doh, 1, 2";
        assertEquals(expecting, e.render());
    }

    @Test public void testCatWithNullTemplateApplicationAsElement() throws Exception {
        ST e = new ST(
                "<[names:{<it>!},\"foo\"]:{x}; separator=\", \">"
            );
        e.add("phones", "1");
        e.add("phones", "2");
        String expecting = "x";  // only one since template application gives nothing
        assertEquals(expecting, e.render());
    }

    @Test public void testCatWithNestedTemplateApplicationAsElement() throws Exception {
        ST e = new ST(
                "<[names, [\"foo\",\"bar\"]:{<it>!},phones]; separator=\", \">"
            );
        e.add("names", "Ter");
        e.add("names", "Tom");
        e.add("phones", "1");
        e.add("phones", "2");
        String expecting = "Ter, Tom, foo!, bar!, 1, 2";
        assertEquals(expecting, e.render());
    }

    @Test public void testListAsTemplateArgument() throws Exception {
		String templates =
				"test(names,phones) ::= \"<foo([names,phones])>\""+newline+
				"foo(items) ::= \"<items:{a | *<a>*}>\""+newline
				;
        Misc.writeFile(tmpdir, "t.stg", templates);
        STGroup group = STGroup.loadGroup(tmpdir+"/"+"t.stg");
		ST e = group.getInstanceOf("test");
		e.add("names", "Ter");
		e.add("names", "Tom");
		e.add("phones", "1");
		e.add("phones", "2");
		String expecting = "*Ter**Tom**1**2*";
		String result = e.render();
		assertEquals(expecting, result);
	}

    public void testNullListGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n" +
            "$users:{name: $it$}; separator=\", \"$\n" +
            "end\n");
        //t.setAttribute("users", new Duh());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    public void testEmptyListGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n" +
            "$users:{name: $it$}; separator=\", \"$\n" +
            "end\n");
        t.add("users", new ArrayList());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }

    public void testEmptyListNoIteratorGetsNoOutput() throws Exception {
        STGroup group =
                new STGroup();
        STErrorListener errors = new ErrorBuffer();
        group.setErrorListener(errors);
        ST t = new ST(group,
            "begin\n" +
            "$users; separator=\", \"$\n" +
            "end\n");
        t.add("users", new ArrayList());
        String expecting="begin\nend\n";
        String result = t.render();
        assertEquals(expecting, result);
    }    
}