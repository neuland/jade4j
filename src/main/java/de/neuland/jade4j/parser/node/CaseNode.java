package de.neuland.jade4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class CaseNode extends Node {

    private List<CaseConditionNode> caseConditionNodes = new LinkedList<CaseConditionNode>();
	public static class When extends Node {
		@Override
		public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
			block.execute(writer, model, template);
		}
	}
	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		try {
			boolean skip = false;
			for (Node when : block.getNodes()) {
				if (skip || "default".equals(when.getValue()) || checkCondition(model, when,template.getExpressionHandler())) {
					skip = false;
					if(when.getBlock()!=null) {
						when.execute(writer, model, template);
						break;
					}else {
						skip = true;
					}
				}
			}
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	private Boolean checkCondition(JadeModel model, Node caseConditionNode, ExpressionHandler expressionHandler) throws ExpressionException {
		return expressionHandler.evaluateBooleanExpression(value + " == " + caseConditionNode.getValue(), model);
	}

	public void setConditions(List<CaseConditionNode> caseConditionNodes) {
		this.caseConditionNodes = caseConditionNodes;
	}

    public List<CaseConditionNode> getCaseConditionNodes() {
        return caseConditionNodes;
    }

    @Override
    public CaseNode clone() throws CloneNotSupportedException {
        CaseNode clone = (CaseNode) super.clone();

        clone.caseConditionNodes = new LinkedList<CaseConditionNode>();
        for(CaseConditionNode condition : caseConditionNodes) {
            clone.caseConditionNodes.add((CaseConditionNode) condition.clone());
        }

        return clone;
    }
}
