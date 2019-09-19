package de.neuland.pug4j.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PugEscapeTest {

    @Test
    public void testEscape() throws Exception {
        assertEquals("\\\\s", PugEscape.escape("\\\\s"));
    }
}