package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class MixinBlockNode extends Node {
    @Override
    public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
        BlockNode blockNode = (BlockNode) model.get("block");
        if(blockNode != null)
            blockNode.execute(writer,model,template);
    }
}
