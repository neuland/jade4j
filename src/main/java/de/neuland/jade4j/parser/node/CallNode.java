package de.neuland.jade4j.parser.node;

import java.util.*;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.util.ArgumentSplitter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
		if(mixin.getRest()!=null) {
			ArrayList<Object> restArguments = new ArrayList<Object>();
			for (int i = names.size(); i < arguments.size(); i++) {
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
				restArguments.add(value);
			}
			model.put(mixin.getRest(), restArguments);
		}
	}

	private void writeAttributes(JadeModel model, MixinNode mixin, JadeTemplate template) {
//		model.put("attributes", mergeInheritedAttributes(model));
//		model.put("attributes", getArguments());
		LinkedList<Attr> newAttributes = new LinkedList<Attr>(attributes);
		if (attributeBlocks.size()>0) {
			//Todo: AttributesBlock needs to be evaluated

			for (String attributeBlock : attributeBlocks) {
			   Object o = null;
			   try {
				   o = template.getExpressionHandler().evaluateExpression(attributeBlock, model);
			   } catch (ExpressionException e) {
				   e.printStackTrace();
			   }
			   if(o!=null) {
				   if(o instanceof Map) {
					   for (Map.Entry<String, String> entry : ((Map<String,String>) o).entrySet()) {
						   Attr attr = new Attr(entry.getKey(),entry.getValue(),false);
						   newAttributes.add(attr);
					   }
				   }else if(o instanceof String){
					   System.out.print(o);
				   }

			   }
		   }
  		}

		if (newAttributes.size()>0) {
			LinkedHashMap<String,String> attrs = attrs(model, template, newAttributes);
			model.put("attributes", attrs);
  		}else{
			model.put("attributes", null);
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
