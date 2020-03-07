package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class IncludeFilterNode extends AttrsNode {
    public IncludeFilterNode() {
    }

    @Override
    public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        //TODO: implement IncludeFilterNode
    }
}
