package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class IfConditionNode extends Node {

	private boolean defaultNode = false;
	private boolean isInverse = false;
	
	public IfConditionNode(String condition, int lineNumber) {
		this.value = condition;
		this.lineNumber = lineNumber;
	}

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

	public boolean isInverse() {
		return isInverse;
	}

	public void setInverse(boolean isInverse) {
		this.isInverse = isInverse;
	}
}
