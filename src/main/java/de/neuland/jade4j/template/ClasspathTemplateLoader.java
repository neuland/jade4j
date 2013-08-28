package de.neuland.jade4j.template;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Loads a Jade template from Classpath
 * It is useful when Jade templates are in the same JAR or WAR
 * 
 * @author emiguel
 *
 */
public class ClasspathTemplateLoader implements TemplateLoader {

    private static final String suffix = ".jade";
    private String encoding = "UTF-8";

    public long getLastModified(String name) {
        return -1;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        if(!name.endsWith(suffix))name = name + suffix;
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(name), getEncoding());
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
