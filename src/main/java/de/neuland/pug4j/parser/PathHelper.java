package de.neuland.pug4j.parser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHelper {
    public String resolvePath(String parentName, String templateName, String extension) {
        Path currentPath = Paths.get(parentName);
        Path templatePath = Paths.get(templateName);
        Path parent = currentPath.getParent();
        String filePath = templatePath.toString();
        if(parent!=null)
            filePath = currentPath.resolve(templatePath).toString();
//        String filePath;
        if(templateName.startsWith("/")) {
            //ignore parentName
            filePath = templateName.substring(1);
        }else {
            if (FilenameUtils.indexOfLastSeparator(parentName) == -1)
                filePath = templateName;
            else {
                //            String currentDir = FilenameUtils.getFullPath(parentName);
                String currentDir = parentName.substring(0, FilenameUtils.indexOfLastSeparator(parentName) + 1);
                filePath = currentDir + templateName;
            }
        }
        if(StringUtils.lastIndexOf(filePath,"/") >= StringUtils.lastIndexOf(filePath,"."))
            filePath += "."+extension;
        filePath = FilenameUtils.normalize(filePath);
        return filePath;
    }
}
