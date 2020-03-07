package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

import java.util.LinkedList;

public class IncludeNode extends Node {

    FileReference file;
    boolean raw = false;
    private LinkedList<Node> filters;

    @Override
    public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        //TODO:implement IncludeNode
    }

    public void setFile(FileReference file) {
        this.file = file;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public void setFilters(LinkedList<Node> filters) {
        this.filters = filters;
    }
}
