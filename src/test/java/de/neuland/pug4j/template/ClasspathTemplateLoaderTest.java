package de.neuland.pug4j.template;

import org.junit.Test;

import java.io.IOException;

public class ClasspathTemplateLoaderTest {
    @Test
    public void shouldGetFileFromClassPath() throws IOException {
        ClasspathTemplateLoader templateLoader = new ClasspathTemplateLoader();
        templateLoader.getReader("loader/pages/subdir/test.pug");
    }
    @Test
    public void shouldGetFileFromClassPathWithBasePath() throws IOException {
        ClasspathTemplateLoader templateLoader = new ClasspathTemplateLoader("loader");
        templateLoader.getReader("pages/subdir/test.pug");
    }

}