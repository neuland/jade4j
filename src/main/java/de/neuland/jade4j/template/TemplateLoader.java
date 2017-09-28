package de.neuland.jade4j.template;

import java.io.IOException;
import java.io.Reader;

public interface TemplateLoader {
    public long getLastModified(String name) throws IOException;
    public Reader getReader(String name) throws IOException;
    public String getExtension();
}

