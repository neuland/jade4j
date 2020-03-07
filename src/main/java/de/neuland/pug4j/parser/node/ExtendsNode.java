package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class ExtendsNode extends Node {
    FileReference file;
    @Override
    public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        writer.append(value);
        //Todo implement Extends
    }

    public FileReference getFile() {
        return file;
    }

    public void setFile(FileReference file) {
        this.file = file;
    }
}
