package de.neuland.pug4j.template;

import de.neuland.pug4j.exceptions.PugException;
import de.neuland.pug4j.exceptions.PugTemplateLoaderException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

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
//		if(name.startsWith("../")){
//			throw new PugTemplateLoaderException("relative Path is not allowed");
//		}
        return new File(folderPath + name);
	}

	public String getExtension() {
		return extension;
	}

	public String resolvePath(String parentName, String templateName, String extension) {
//		Path basePath = Paths.get(folderPath);
//		Path parentPath = basePath.resolve(Paths.get(parentName)).getParent();
//		Path templatePath = parentPath.resolve(Paths.get(templateName));
//		templatePath = basePath.relativize(templatePath);
//		String filePath = templatePath.toString();
//
//		filePath = FilenameUtils.normalize(filePath);
        String filePath;
		if (templateName.startsWith("/")) {
			//ignore parentName
			filePath = templateName.substring(1);
		} else {
			if (FilenameUtils.indexOfLastSeparator(parentName) == -1)
				filePath = templateName;
			else {
				//            String currentDir = FilenameUtils.getFullPath(parentName);
				String currentDir = parentName.substring(0, FilenameUtils.indexOfLastSeparator(parentName) + 1);
				filePath = currentDir + templateName;
			}
		}
		if (StringUtils.lastIndexOf(filePath, "/") >= StringUtils.lastIndexOf(filePath, "."))
			filePath += "." + extension;
        filePath = FilenameUtils.normalize(filePath);
		return filePath;
	}
}