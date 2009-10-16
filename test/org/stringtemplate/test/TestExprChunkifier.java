package org.stringtemplate.test;

import org.stringtemplate.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class TestExprChunkifier {
    @Test public void testEsc() throws Exception {
        String template = "hi \\<name>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi \\<name>]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testEsc2() throws Exception {
        String template = "hi \\x";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi \\x]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testID() throws Exception {
        String template = "hi <name>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:name]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testExpr2x() throws Exception {
        String template = "hi <name> <id>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:name, 1:9: , 1:10:id]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testString() throws Exception {
        String template = "hi <foo(a=\">\")>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:foo(a=\">\")]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testEscInString() throws Exception {
        String template = "hi <foo(a=\">\\\"\")>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:foo(a=\">\\\"\")]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testSubtemplate() throws Exception {
        String template = "hi <names:{n | <n>}>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:names:{n | <n>}]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testNestedSubtemplate() throws Exception {
        String template = "hi <names:{n | <n:{<it>}>}>";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected = "[1:0:hi , 1:3:names:{n | <n:{<it>}>}]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }

    @Test public void testNestedList() throws Exception {
        String template =
            "*<[names, [\"foo\",\"bar\"]:{<it>!},phones]; separator=\", \">*";
        List<Chunk> chunks = new Chunkifier(template, '<', '>').chunkify();
        String expected =
            "[1:0:*, " +
            "1:1:[names, [\"foo\",\"bar\"]:{<it>!},phones]; separator=\", \", " +
            "1:56:*]";
        String result = chunks.toString();
        assertEquals(expected, result);
    }


}
