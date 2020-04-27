package de.neuland.pug4j.template;

import de.neuland.pug4j.TestFileHelper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FileTemplateLoaderTest {
    private String RESOURCE_PATH = TestFileHelper.getLoaderResourcePath("");
    @Test
    public void shouldGetAbsoluteFile() throws IOException {
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader();
        fileTemplateLoader.getReader(RESOURCE_PATH+"pages/subdir/test.pug");
    }
    @Test
    public void shouldGetReleativeFile() throws IOException {
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(RESOURCE_PATH);
        fileTemplateLoader.getReader("pages/subdir/test.pug");
    }

}