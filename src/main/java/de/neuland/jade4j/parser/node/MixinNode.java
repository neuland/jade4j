package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import java.util.ArrayList;

public class MixinNode extends MixinInjectNode {

        public MixinNode() {
        }

        public MixinNode(MixinNode node) {
            this.block = node.block;
            this.fileName = node.fileName;
            this.lineNumber = node.lineNumber;
            this.name = node.name;
            this.value = node.value;
            this.nodes.addAll(node.nodes);
            this.arguments.addAll(node.arguments);
        }
        
	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		if (hasBlock()) {
			model.setMixin(getName(), this);
		} else {
			super.execute(writer, model, template);
		}
	}
	
}
