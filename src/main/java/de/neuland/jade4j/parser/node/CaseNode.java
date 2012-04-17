package de.neuland.jade4j.parser.node;

import java.util.List;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.expression.ExpressionHandler;
import de.neuland.jade4j.template.JadeTemplate;

public class CaseNode extends Node {

	private List<Node> caseConditionNodes;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
	    try {
    		for (Node caseConditionNode : caseConditionNodes) {
                if (((CaseConditionNode)caseConditionNode).isDefault() || checkCondition(model, caseConditionNode)) {
                	caseConditionNode.execute(writer, model, template);
                	break;
                }
    		}
	    } catch (ExpressionException e) {
	        throw new JadeCompilerException(this, e);
	    }
	}

	private Boolean checkCondition(JadeModel model, Node caseConditionNode) throws ExpressionException {
		return ExpressionHandler.evaluateBooleanExpression(value + " == " + caseConditionNode.getValue(), model);
	}

	public void setConditions(List<Node> caseConditionNodes) {
		this.caseConditionNodes = caseConditionNodes;		
	}

}
