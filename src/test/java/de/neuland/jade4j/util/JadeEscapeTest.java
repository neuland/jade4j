package de.neuland.jade4j.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class JadeEscapeTest {

    @Test
    public void testEscape() throws Exception {
        assertEquals("\\\\s", JadeEscape.escape("\\\\s"));
    }
}