package de.neuland.pug4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class CaseNode extends Node {

    private List<CaseConditionNode> caseConditionNodes = new LinkedList<CaseConditionNode>();
	public static class When extends Node {
		@Override
		public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
			block.execute(writer, model, template);
		}
	}
	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
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
			throw new PugCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	private Boolean checkCondition(PugModel model, Node caseConditionNode, ExpressionHandler expressionHandler) throws ExpressionException {
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
