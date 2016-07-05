package de.neuland.jade4j.template;

import java.io.IOException;
import java.io.Reader;


public class ReaderTemplateLoader implements TemplateLoader {
	
	private final Reader reader;
	private final String name;

	public ReaderTemplateLoader(Reader reader, String name) {
		this.reader = reader;
		this.name = name;
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
		if (!name.equals(this.name)) {
			throw new RuntimeException("this reader only responds to [" + name + "] templates");
		}
	}
	
	

}
