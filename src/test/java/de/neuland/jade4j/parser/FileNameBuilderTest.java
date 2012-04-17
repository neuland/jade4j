package de.neuland.jade4j.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileNameBuilderTest {

    private FileNameBuilder fileNameBuilder;

    @Test
    public void testIncludeFileName() {
        fileNameBuilder = new FileNameBuilder("includes/layout");
        assertEquals("includes/layout.jade", fileNameBuilder.build());
    }

}
