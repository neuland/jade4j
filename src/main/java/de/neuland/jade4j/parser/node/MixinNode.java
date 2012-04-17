package de.neuland.jade4j.parser.node;

import java.util.ArrayList;
import java.util.List;

import ognl.Ognl;
import ognl.OgnlException;
import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class MixinNode extends Node {

	private List<String> arguments = new ArrayList<String>();

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		if (hasBlock()) {
			model.setMixin(getName(), this);
		} else {
			MixinNode mixin = model.getMixin(getName());
			if (mixin == null) {
				throw new JadeCompilerException(this, "mixin " + getName() + " is not defined");
			}
			model.pushScope();
			writeVariables(model, mixin);
			mixin.getBlock().execute(writer, model, template);
			model.popScope();
		}
	}

	private void writeVariables(JadeModel model, MixinNode mixin) {
		List<String> names = mixin.getArguments();
		List<String> values = arguments;
		if (names == null) {
			return;
		}
		for (int i = 0; i < values.size(); i++) {
			String key = names.get(i);
			Object value = null;
			try {
				value = Ognl.getValue(values.get(i), model);
			} catch (OgnlException e) {
				throw new JadeCompilerException(this, e);
			}
			if (key != null) {
				model.put(key, value);
			}
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
		for (String argument : arguments.split(",")) {
			this.arguments.add(argument.trim());
		}
	}

}
