package de.neuland.pug4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.compiler.Utils;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class TextNode extends Node {

	private String value = "";
	private List<Object> preparedValue = new LinkedList<Object>();
	boolean isHtml = false;
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
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		try {
			String string = Utils.interpolate(preparedValue, model,template.getExpressionHandler());
			writer.append(value);
		} catch (ExpressionException e) {
			throw new PugCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	public void addNode(Node node) {
		nodes.add(node);
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean html) {
		isHtml = html;
	}
}
