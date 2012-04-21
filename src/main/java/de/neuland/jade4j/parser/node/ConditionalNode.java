package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.expression.ExpressionHandler;
import de.neuland.jade4j.template.JadeTemplate;

public class ConditionalNode extends Node {

	private boolean inverseCondition = false;
	private boolean conditionActive = false;
	protected Node elseNode;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		try {
			if (!conditionActive && this.block != null) {
				this.block.execute(writer, model, template);
			} else {
				Boolean conditionCheck;
				conditionCheck = checkCondition(model) ^ inverseCondition;
				if (conditionCheck && this.block != null) {
					this.block.execute(writer, model, template);
				} else if (elseNode != null) {
					this.elseNode.execute(writer, model, template);
				}
			}
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	private boolean checkCondition(JadeModel model) throws ExpressionException {
		Boolean value = ExpressionHandler.evaluateBooleanExpression(getValue(), model);
		return (value == null) ? false : value;
	}

	public boolean isInverseCondition() {
		return inverseCondition;
	}

	public void setInverseCondition(boolean inverseCondition) {
		this.inverseCondition = inverseCondition;
	}

	public boolean isConditionActive() {
		return conditionActive;
	}

	public void setConditionActive(boolean conditionActive) {
		this.conditionActive = conditionActive;
	}

	public Node getElseNode() {
		return elseNode;
	}

	public void setElseNode(Node elseNode) {
		this.elseNode = elseNode;
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}

}
