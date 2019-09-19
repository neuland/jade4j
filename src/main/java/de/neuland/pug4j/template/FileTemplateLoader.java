package de.neuland.pug4j.template;

import de.neuland.pug4j.exceptions.PugException;
import de.neuland.pug4j.exceptions.PugTemplateLoaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FileTemplateLoader implements TemplateLoader {

    private String encoding = "UTF-8";
	private String folderPath = "";
	private String extension = "pug";
	
	public FileTemplateLoader(String folderPath, String encoding) {
		this.folderPath = folderPath;
		this.encoding = encoding;
	}

	public FileTemplateLoader(String folderPath, String encoding, String extension) {
		this.encoding = encoding;
		this.folderPath = folderPath;
		this.extension = extension;
	}

	public long getLastModified(String name) {
		File templateSource = getFile(name);
		return templateSource.lastModified();
	}

	@Override
	public Reader getReader(String name) throws IOException {
		File templateSource = getFile(name);
		return new InputStreamReader(new FileInputStream(templateSource), encoding);
	}

	private File getFile(String name) {
		if(name.startsWith("../")){
			throw new PugTemplateLoaderException("relative Path is not allowed");
		}
        return new File(folderPath + name);
	}

	public String getExtension() {
		return extension;
	}
}