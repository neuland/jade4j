package de.neuland.jade4j.template;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

public class FileTemplateLoader implements TemplateLoader {

    private String encoding = "UTF-8";
	private String suffix = ".jade";
	private String basePath = "";
	
	public FileTemplateLoader(String basePath, String encoding) {
		this.basePath = basePath;
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
		// TODO Security
		String filename = basePath + name;
		if (hasNoExtension(filename)) {
        	filename += suffix;
        }

		return new File(filename);
	}

    private boolean hasNoExtension(String filename) {
        return "".equals(FilenameUtils.getExtension(filename));
    }
}