package de.neuland.pug4j.parser;

public class FileNameBuilder {

    private String path;

    public FileNameBuilder(String path) {
        this.path = path;
    }

    public String build() {
        return path.endsWith(".pug") ? path : path + ".pug";
    }

}
