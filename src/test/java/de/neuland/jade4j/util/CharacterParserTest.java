package de.neuland.jade4j.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by christoph on 14.10.15.
 */
public class CharacterParserTest {

    CharacterParser characterParser;
    @Before
    public void setUp() throws Exception {
        characterParser = new CharacterParser();
    }

    @Test
    public void testWorksOutHowMuchDepthChanges() throws Exception {
        CharacterParser.State state = characterParser.parse("foo(arg1, arg2, {\n  foo: [a, b\n");
        assertEquals(1,state.getRoundDepth());
        assertEquals(1,state.getCurlyDepth());
        assertEquals(1,state.getSquareDepth());

        state = characterParser.parse("    c, d]\n  })", state);
        assertEquals(0,state.getSquareDepth());
        assertEquals(0,state.getCurlyDepth());
        assertEquals(0,state.getRoundDepth());

    }

    @Test
    public void testFindsContentsOfBracketedExpressions() throws Exception {
        CharacterParser.Match section = characterParser.parseMax("foo=\"(\", bar=\"}\") bing bong");
        assertEquals(0,section.getStart());
        assertEquals(16,section.getEnd());//exclusive end of string
        assertEquals("foo=\"(\", bar=\"}\"",section.getSrc());

        Options options = new Options();
        options.setStart(1);
        section = characterParser.parseMax("{foo=\"(\", bar=\"}\"} bing bong", options);
        assertEquals(1,section.getStart());
        assertEquals(17,section.getEnd());//exclusive end of string
        assertEquals("foo=\"(\", bar=\"}\"",section.getSrc());
    }

    @Test
    public void testFindsContentsOfBracketedExpressionsWithSpecifiedBracket() throws Exception {
        CharacterParser.Match section = characterParser.parseMaxBracket("foo=\"(\", bar=\"}\")] bing bong",']');
        assertEquals(0,section.getStart());
        assertEquals(17,section.getEnd());//exclusive end of string
        assertEquals("foo=\"(\", bar=\"}\")",section.getSrc());

        section = characterParser.parseMaxBracket("foo=\"(\", bar=\"}\")] bing bong", ')');
        assertEquals(0,section.getStart());
        assertEquals(16,section.getEnd());//exclusive end of string
        assertEquals("foo=\"(\", bar=\"}\"",section.getSrc());
    }

    @Test
    public void testFindsCodeUpToACustomDelimiter() throws Exception {
        CharacterParser.Match section = characterParser.parseUntil("foo.bar(\"%>\").baz%> bing bong", "%>");
        assertEquals(0,section.getStart());
        assertEquals(17,section.getEnd());//exclusive end of string
        assertEquals("foo.bar(\"%>\").baz",section.getSrc());

        Options options = new Options();
        options.setStart(2);
        section = characterParser.parseUntil("<%foo.bar(\"%>\").baz%> bing bong", "%>",options);
        assertEquals(2,section.getStart());
        assertEquals(19,section.getEnd());//exclusive end of string
        assertEquals("foo.bar(\"%>\").baz",section.getSrc());
    }

    @Test
    public void testRegressionsNr1ParsesRegularExpressions() throws Exception {
        CharacterParser.Match section = characterParser.parseMax("foo=/\\\\/g, bar=\"}\") bing bong");
        assertEquals(0,section.getStart());
        assertEquals(18,section.getEnd());//exclusive end of string
        assertEquals("foo=/\\\\/g, bar=\"}\"",section.getSrc());

        section = characterParser.parseMax("foo = typeof /\\\\/g, bar=\"}\") bing bong");
        assertEquals(0,section.getStart());
        assertEquals(27,section.getEnd());//exclusive end of string
        assertEquals("foo = typeof /\\\\/g, bar=\"}\"",section.getSrc());
    }

    @Test
    public void testRegressionNr6ParsesBlockComments() throws Exception {
        CharacterParser.Match section = characterParser.parseMax("/* ) */) bing bong");
        assertEquals(0,section.getStart());
        assertEquals(7,section.getEnd());//exclusive end of string
        assertEquals("/* ) */",section.getSrc());

        section = characterParser.parseMax("/* /) */) bing bong");
        assertEquals(0,section.getStart());
        assertEquals(8,section.getEnd());//exclusive end of string
        assertEquals("/* /) */",section.getSrc());
    }

}