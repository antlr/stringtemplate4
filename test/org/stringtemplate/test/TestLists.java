package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.ST;

public class TestLists {
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
}