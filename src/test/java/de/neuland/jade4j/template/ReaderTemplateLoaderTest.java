package de.neuland.jade4j.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReaderTemplateLoaderTest {
    
    @Test
    public void getReaderShouldReturnReaderIfNameExactlyMatches() throws IOException {
        Reader reader = new StringReader("Hello #{foo}!");
        ReaderTemplateLoader templateLoader = new ReaderTemplateLoader(reader, "template.jade");

        Reader readerResult = templateLoader.getReader("template.jade");

        assertEquals(reader, readerResult);
    }

    @Test
    public void getReaderShouldReturnReaderIfNameOfParameterIsWithoutExtension() throws IOException {
        Reader reader = new StringReader("Hello #{foo}!");
        ReaderTemplateLoader templateLoader = new ReaderTemplateLoader(reader, "template.jade");

        Reader readerResult = templateLoader.getReader("template");

        assertEquals(reader, readerResult);
    }

    @Test
    public void getReaderShouldReturnReaderIfNameOfObjectIsWithoutExtension() throws IOException {
        Reader reader = new StringReader("Hello #{foo}!");
        ReaderTemplateLoader templateLoader = new ReaderTemplateLoader(reader, "template");

        Reader readerResult = templateLoader.getReader("template.jade");

        assertEquals(reader, readerResult);
    }

    @Test
    public void getReaderShouldReturnReaderIfNameIsCompletelyWithoutExtension() throws IOException {
        Reader reader = new StringReader("Hello #{foo}!");
        ReaderTemplateLoader templateLoader = new ReaderTemplateLoader(reader, "template");

        Reader readerResult = templateLoader.getReader("template");

        assertEquals(reader, readerResult);
    }

}