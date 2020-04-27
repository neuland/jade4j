package de.neuland.pug4j.parser;

import de.neuland.pug4j.exceptions.PugTemplateLoaderException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHelper {
    public String resolvePath(String parentFileName, String templateName, String basePathString) {
        if(!Paths.get(parentFileName).isAbsolute()){
            parentFileName = resolveAbsolutePath(parentFileName,basePathString);
        }
        if(Paths.get(basePathString).isAbsolute()) {
            Path basePath = Paths.get(basePathString);

            if (Paths.get(templateName).isAbsolute()) {
                templateName = basePath.toString() + templateName;
            }

            Path parentPath = basePath.resolve(Paths.get(parentFileName)).getParent().normalize();

            Path templatePath = parentPath.resolve(Paths.get(templateName)).normalize();
            templatePath = templatePath.normalize();
            return templatePath.toString();
        }else{
            Path basePath = Paths.get(parentFileName).getParent();
            if (Paths.get(templateName).isAbsolute()) {
                templateName = basePath.toString() + templateName;
            }
            if(basePath == null) {
                Path templatePath = Paths.get(templateName);
                return templatePath.toString();
            }
            Path templatePath = basePath.resolve(Paths.get(templateName)).normalize();
            templatePath = templatePath.normalize();
            return templatePath.toString();
        }
    }
    private String resolveAbsolutePath(String filename,String basePath) {
        if(Paths.get(filename).isAbsolute()){
            return filename;
        }else{
            if(!Paths.get(basePath).isAbsolute()){
                throw new PugTemplateLoaderException("Can't resolve absolute path for '"+filename+"' if basePath has not been set.");
            }
            return Paths.get(basePath).resolve(filename).normalize().toString();
        }
    }

}
