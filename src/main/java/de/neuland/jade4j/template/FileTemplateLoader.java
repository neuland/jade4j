package de.neuland.jade4j.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

public class FileTemplateLoader implements TemplateLoader {
	
	private String encoding = "UTF-8";
	private String suffix = ".jade";
	private final File basePath;
	
        public FileTemplateLoader(String basePath, String encoding) {
		this.basePath = basePath == null || "".equals(basePath) ? null : new File(basePath);
		this.encoding = encoding;
	}

        @Override
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
                if (!name.endsWith(suffix)) {
			name += suffix;
		}
		
		File file;
                if(basePath == null) {
                    file = new File(name);
                } else {
                    file = new File(basePath, name);
                }
		return file;
	}
}