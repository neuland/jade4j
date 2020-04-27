package de.neuland.pug4j.template;

import de.neuland.pug4j.exceptions.PugTemplateLoaderException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Loads a Pug template from Classpath
 * It is useful when Pug templates are in the same JAR or WAR
 * 
 * @author emiguel
 *
 */
public class ClasspathTemplateLoader implements TemplateLoader {

    private FileTemplateLoader fileTemplateLoader;

    public ClasspathTemplateLoader() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        fileTemplateLoader = new FileTemplateLoader(path);
    }

    public ClasspathTemplateLoader(Charset encoding) {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        fileTemplateLoader = new FileTemplateLoader(path,encoding);
    }

    public ClasspathTemplateLoader(Charset encoding, String extension) {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        fileTemplateLoader = new FileTemplateLoader(path,encoding,extension);
    }
    public ClasspathTemplateLoader(String basePath) {
        String path = Thread.currentThread().getContextClassLoader().getResource(basePath).getPath();
        fileTemplateLoader = new FileTemplateLoader(path);
    }

    public ClasspathTemplateLoader(String basePath, Charset encoding) {
        String path = Thread.currentThread().getContextClassLoader().getResource(basePath).getPath();
        fileTemplateLoader = new FileTemplateLoader(path,encoding);
    }

    public ClasspathTemplateLoader(String basePath, String extension) {
        String path = Thread.currentThread().getContextClassLoader().getResource(basePath).getPath();
        fileTemplateLoader = new FileTemplateLoader(path,extension);
    }

    public ClasspathTemplateLoader(String basePath, Charset encoding, String extension) {
        String path = Thread.currentThread().getContextClassLoader().getResource(basePath).getPath();
        fileTemplateLoader = new FileTemplateLoader(path,encoding,extension);
    }

    public long getLastModified(String name) {
        return -1;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        return fileTemplateLoader.getReader(name);
    }

    @Override
    public String getExtension() {
        return fileTemplateLoader.getExtension();
    }

    @Override
    public String getBasePath() {
        return fileTemplateLoader.getBasePath();
    }

}
