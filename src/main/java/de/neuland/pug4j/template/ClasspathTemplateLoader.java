package de.neuland.pug4j.template;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Loads a Pug template from Classpath
 * It is useful when Pug templates are in the same JAR or WAR
 * 
 * @author emiguel
 *
 */
public class ClasspathTemplateLoader implements TemplateLoader {

    private String encoding = "UTF-8";
    private String extension = "pug";

    public ClasspathTemplateLoader() {
    }

    public ClasspathTemplateLoader(String encoding) {
        this.encoding = encoding;
    }

    public ClasspathTemplateLoader(String encoding, String extension) {
        this.encoding = encoding;
        this.extension = extension;
    }

    public long getLastModified(String name) {
        return -1;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(name), getEncoding());
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
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
