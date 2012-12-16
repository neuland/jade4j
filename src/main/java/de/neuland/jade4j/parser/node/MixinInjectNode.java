package de.neuland.jade4j.parser.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class MixinInjectNode extends AttributedNode {

	protected List<String> arguments = new ArrayList<String>();

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		MixinNode mixin = model.getMixin(getName());
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
			Node injectionPoint = getInjectionPoint(mixin.getBlock());
			if (injectionPoint != null) {
				injectionPoint.getNodes().add(block);
			}
		}

		model.pushScope();

		writeVariables(model, mixin, template);
		writeAttributes(model, mixin, template);
		mixin.getBlock().execute(writer, model, template);

		model.popScope();

	}

	private Node getInjectionPoint(Node block) {
		for (Node node : block.getNodes()) {
			if (node instanceof BlockNode && !node.hasNodes()) {
				return node;
			}

			if (node.hasBlock()) {
				if (!node.getBlock().hasNodes()) {
					return node.getBlock();
				}

				Node nodeFromTree = getInjectionPoint(node.getBlock());
				if (nodeFromTree != null) {
					return nodeFromTree;
				}
			}

			Node nodeFromTree = getInjectionPoint(node);
			if (nodeFromTree != null) {
				return nodeFromTree;
			}
		}
		return null;
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
					value = ExpressionHandler.evaluateExpression(values.get(i), model);
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
		model.put("attributes", mergeInheritedAttributes(model));
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public void setArguments(String arguments) {
		this.arguments.clear();
		for (String argument : arguments.split(",")) {
			this.arguments.add(argument.trim());
		}
	}
}
