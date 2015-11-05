package de.neuland.jade4j.parser.node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.sun.deploy.util.StringUtils;
import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.util.ArgumentSplitter;

public class CallNode extends AttrsNode {

	protected List<String> arguments = new ArrayList<String>();
	boolean call = false;
	private boolean dynamicMixins = false;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		boolean dynamic = getName().charAt(0)=='#';
  		if (dynamic)
			this.dynamicMixins = true;
		String newname = (dynamic ? getName().substring(2,getName().length()-1):'"'+getName()+'"');
		try {
			newname = (String) template.getExpressionHandler().evaluateExpression(newname,model);
		} catch (ExpressionException e) {
			e.printStackTrace();
		}

		MixinNode mixin;
		if(dynamic)
			mixin = model.getMixin(newname);
		else
			mixin = model.getMixin(getName());

		if (mixin == null) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), "mixin " + getName() + " is not defined");
		}

		// Clone mixin
		try {
			mixin = (MixinNode) mixin.clone();
		} catch (CloneNotSupportedException e) {
			// Can't happen
			throw new IllegalStateException(e);
		}

		if (hasBlock()) {
			List<MixinBlockNode> injectionPoints = getInjectionPoints(mixin.getBlock());
            for (MixinBlockNode point : injectionPoints) {
				point.getNodes().add(block);
            }
		}

		if (this.isCall()) {
			model.pushScope();
			model.put("block", block);
			writeVariables(model, mixin, template);
			writeAttributes(model, mixin, template);
			mixin.getBlock().execute(writer, model, template);
			model.put("block",null);
			model.popScope();

		}else{

		}




	}

	private List<MixinBlockNode> getInjectionPoints(Node block) {
        List<MixinBlockNode> result = new ArrayList<MixinBlockNode>();
		for (Node node : block.getNodes()) {
			if (node instanceof MixinBlockNode && !node.hasNodes()) {
                result.add((MixinBlockNode) node);
			} else if(node instanceof ConditionalNode){
                for (IfConditionNode condition : ((ConditionalNode) node).getConditions()) {
                    result.addAll(getInjectionPoints(condition.getBlock()));
                }
//            } else if(node instanceof CaseNode.When){
//                for (CaseConditionNode condition : ((CaseNode) node).getCaseConditionNodes()) {
//                    result.addAll(getInjectionPoints(condition.getBlock()));
//                }
            } else if (node.hasBlock()) {
                result.addAll(getInjectionPoints(node.getBlock()));
            }
		}
		return result;
	}

	private void writeVariables(JadeModel model, MixinNode mixin, JadeTemplate template) {
		List<String> names = mixin.getArguments();
		List<String> values = arguments;
		if (names == null) {
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			String key = names.get(i);
			Object value = null;
			if (i < values.size()) {
				value = values.get(i);
			}
			if (value != null) {
				try {
					value = template.getExpressionHandler().evaluateExpression(values.get(i), model);
				} catch (Throwable e) {
					throw new JadeCompilerException(this, template.getTemplateLoader(), e);
				}
			}
			if (key != null) {
				model.put(key, value);
			}
		}
	}

	private void writeAttributes(JadeModel model, MixinNode mixin, JadeTemplate template) {
//		model.put("attributes", mergeInheritedAttributes(model));
//		model.put("attributes", getArguments());
		if (attributeBlocks.size()>0) {
    		if (attributes.size()>0) {
				LinkedHashMap<String,String> attrs = attrs(model, template);
    		}
			model.put("attributes", StringUtils.join(attributeBlocks, ","));
  		} else if (attributes.size()>0) {
			LinkedHashMap<String,String> attrs = attrs(model, template);
			model.put("attributes", attrs);
  		}

	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public void setArguments(String arguments) {
		this.arguments.clear();
		this.arguments = ArgumentSplitter.split(arguments);
	}

	public boolean isCall() {
		return call;
	}

	public void setCall(boolean call) {
		this.call = call;
	}
}
