package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class CaseConditionNode extends Node {

	private boolean defaultNode = false;

	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		block.execute(writer, model, template);
	}

	public void setDefault(boolean defaultNode) {
		this.defaultNode = defaultNode;		
	}
	
	public boolean isDefault() {
		return defaultNode;
	}
}
