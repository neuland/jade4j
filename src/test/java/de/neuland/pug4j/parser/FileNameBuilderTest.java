package de.neuland.pug4j.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileNameBuilderTest {

    private FileNameBuilder fileNameBuilder;

    @Test
    public void testIncludeFileName() {
        fileNameBuilder = new FileNameBuilder("includes/layout");
        assertEquals("includes/layout.pug", fileNameBuilder.build());
    }

}
