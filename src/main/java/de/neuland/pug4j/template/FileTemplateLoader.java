package de.neuland.pug4j.template;

import de.neuland.pug4j.exceptions.PugTemplateLoaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileTemplateLoader implements TemplateLoader {

    private Charset encoding = StandardCharsets.UTF_8;
	private String basePath = "";
	private String extension = "pug";
	
	public FileTemplateLoader() {
	}

	public FileTemplateLoader(Charset encoding) {
		this.encoding = encoding;
	}

	public FileTemplateLoader(Charset encoding, String extension) {
		this(encoding);
		this.extension = extension;
	}

	public FileTemplateLoader(String basePath) {
		if(!Files.isDirectory(Paths.get(basePath))){
			throw new PugTemplateLoaderException("Directory '"+basePath+"' does not exist.");
		}
		this.basePath = basePath;
	}

	public FileTemplateLoader(String basePath, Charset encoding) {
		this(basePath);
		this.encoding = encoding;
	}

	public FileTemplateLoader(String basePath, String extension) {
		this(basePath);
		this.extension = extension;
	}

	public FileTemplateLoader(String basePath, Charset encoding, String extension) {
		this(basePath,extension);
		this.encoding = encoding;
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
		if(Paths.get(name).isAbsolute())
        	return Paths.get(name).toFile();
		else
			return Paths.get(basePath).resolve(name).toFile();
	}

	public String getExtension() {
		return extension;
	}

	@Override
	public String getBasePath() {
		return basePath;
	}
}