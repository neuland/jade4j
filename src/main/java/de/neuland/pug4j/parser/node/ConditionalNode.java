package de.neuland.pug4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class ConditionalNode extends Node {

	private List<IfConditionNode> conditions = new LinkedList<IfConditionNode>();

	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		for (IfConditionNode conditionNode : this.conditions) {
			try {
				if (conditionNode.isDefault() || checkCondition(model, conditionNode.getValue(),template.getExpressionHandler()) ^ conditionNode.isInverse()) {
					conditionNode.getBlock().execute(writer, model, template);
					return;
				}
			} catch (ExpressionException e) {
				throw new PugCompilerException(conditionNode, template.getTemplateLoader(), e);
			}
		}
	}

	private boolean checkCondition(PugModel model, String condition, ExpressionHandler expressionHandler) throws ExpressionException {
		Boolean value = expressionHandler.evaluateBooleanExpression(condition, model);
		return (value == null) ? false : value;
	}

	public List<IfConditionNode> getConditions() {
		return conditions;
	}

	public void setConditions(List<IfConditionNode> conditions) {
		this.conditions = conditions;
	}

    @Override
    public ConditionalNode clone() throws CloneNotSupportedException {
        ConditionalNode clone = (ConditionalNode) super.clone();

        clone.conditions = new LinkedList<IfConditionNode>();
        for(IfConditionNode condition : conditions) {
            clone.conditions.add((IfConditionNode) condition.clone());
        }

        return clone;
    }
}
