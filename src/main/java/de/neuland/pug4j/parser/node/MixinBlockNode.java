package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

import java.util.LinkedList;

public class MixinBlockNode extends Node {
    @Override
    public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        LinkedList<Node> nodes = getNodes();
        if(nodes.size()==1) {
            Node node = nodes.get(0);
            if (node != null)
                node.execute(writer, model, template);
        }
    }
}
