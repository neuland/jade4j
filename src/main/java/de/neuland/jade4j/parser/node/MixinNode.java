package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class MixinNode extends MixinInjectNode {

    @Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		if (hasBlock()) {
			model.setMixin(getName(), this);
		} else {
			super.execute(writer, model, template);
		}
	}
	
}
