package de.neuland.jade4j.template;

import de.neuland.jade4j.exceptions.JadeTemplateLoaderException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FileTemplateLoader implements TemplateLoader {

    private String encoding = "UTF-8";
	private String folderPath = "";
	private String extension = "jade";
	
	public FileTemplateLoader(String folderPath, String encoding) {
		validateFolderPath(folderPath);
		this.folderPath = folderPath;
		this.encoding = encoding;
	}

	public FileTemplateLoader(String folderPath, String encoding, String extension) {
		validateFolderPath(folderPath);
		this.encoding = encoding;
		this.folderPath = folderPath;
		this.extension = extension;
	}

	private void validateFolderPath(String folderPath) {
		if(StringUtils.isNotEmpty(folderPath)) {
            File file = new File(folderPath);
            if (!file.exists() || !file.isDirectory()) {
                throw new IllegalArgumentException("The folder path '" + folderPath + "' does not exist");
            }
        }
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
			throw new JadeTemplateLoaderException("relative Path is not allowed");
		}
        return new File(folderPath + name);
	}

	public String getExtension() {
		return extension;
	}
}