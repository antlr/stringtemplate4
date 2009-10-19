package org.stringtemplate.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.stringtemplate.*;

import java.util.List;

public class TestDollarDelimiters extends BaseTest {
    @Test public void testSimpleAttr() throws Exception {
        String template = "hi $name$";
        List<Chunk> chunks = new Chunkifier(template, '$', '$').chunkify();
        String expected = "[1:0:hi , 1:3:name]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }
    
    @Test public void testString() throws Exception {
        String template = "hi $foo(a=\"$\")$";
        List<Chunk> chunks = new Chunkifier(template, '$', '$').chunkify();
        String expected = "[1:0:hi , 1:3:foo(a=\"$\")]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi $foo(a=\"$\\\"\")$";
        List<Chunk> chunks = new Chunkifier(template, '$', '$').chunkify();
        String expected = "[1:0:hi , 1:3:foo(a=\"$\\\"\")]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi $names:{n | $n$}$";
        List<Chunk> chunks = new Chunkifier(template, '$', '$').chunkify();
        String expected = "[1:0:hi , 1:3:names:{n | $n$}]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi $names:{n | $n:{$it$}$}$";
        List<Chunk> chunks = new Chunkifier(template, '$', '$').chunkify();
        String expected = "[1:0:hi , 1:3:names:{n | $n:{$it$}$}]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }
}
