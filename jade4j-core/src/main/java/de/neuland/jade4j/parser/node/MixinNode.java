package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MixinNode extends CallNode {
	private String rest;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		if (isCall()) {
			super.execute(writer, model, template);
		} else {
			model.setMixin(getName(), this);
		}
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public String getRest() {
		return rest;
	}
}
