package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

import java.util.LinkedList;

public class MixinBlockNode extends Node {
    @Override
    public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
        LinkedList<Node> nodes = getNodes();
        if(nodes.size()==1) {
            Node node = nodes.get(0);
            if (node != null)
                node.execute(writer, model, template);
        }
    }
}
