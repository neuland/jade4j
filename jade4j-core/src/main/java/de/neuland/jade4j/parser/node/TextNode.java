package de.neuland.jade4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.compiler.Utils;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class TextNode extends Node {

	private String value = "";
	private List<Object> preparedValue = new LinkedList<Object>();

	public void appendText(String txt) {
		value += txt;
		prepare();
	}

	public void setValue(String value) {
		this.value = value;
		prepare();
	}

	public String getValue() {
		return value;
	}

	private void prepare() {
		preparedValue = Utils.prepareInterpolate(value, false);
	}

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		try {
			String string = Utils.interpolate(preparedValue, model,template.getExpressionHandler());
			writer.append(string);
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	public void addNode(Node node) {
		nodes.add(node);
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}
}
