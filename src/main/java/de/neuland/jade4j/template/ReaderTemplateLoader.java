package de.neuland.jade4j.template;

import java.io.IOException;
import java.io.Reader;


public class ReaderTemplateLoader implements TemplateLoader {
	
	private final Reader reader;
	private final String name;
	private String extension = "jade";

	public ReaderTemplateLoader(Reader reader, String name) {
		this.reader = reader;
		this.name = name;
	}

	public ReaderTemplateLoader(Reader reader, String name, String extension) {
		this.reader = reader;
		this.name = name;
		this.extension = extension;
	}

	@Override
	public long getLastModified(String name) throws IOException {
		checkName(name);
		return -1;
	}

	@Override
	public Reader getReader(String name) throws IOException {
		checkName(name);
		return reader;
	}

	private void checkName(String name) {
	    String nameOfParamWithoutExtension = getNameWithoutExtension(name);
		String nameOfObjectWithoutExtension = getNameWithoutExtension(this.name);
		if (!nameOfObjectWithoutExtension.equals(nameOfParamWithoutExtension)) {
			throw new RuntimeException("This reader only responds to [" + this.name + "] template. " +
                                           "You should not reference other templates if using ReaderTemplateLoader, " +
                                           "because multiple template loaders are currently not supported. " +
                                           "Maybe you could use a FileTemplateLoader?");
		}
	}

    private String getNameWithoutExtension(String name) {
        return name.endsWith("." + extension)? name.substring(0, name.lastIndexOf("." + extension)) : name;
    }

    @Override
	public String getExtension() {
		return extension;
	}
}
