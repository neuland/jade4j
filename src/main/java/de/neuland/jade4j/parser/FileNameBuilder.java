package de.neuland.jade4j.parser;

public class FileNameBuilder {

    private String path;

    public FileNameBuilder(String path) {
        this.path = path;
    }

    public String build() {
        return path.endsWith(".jade") ? path : path + ".jade";
    }

}
