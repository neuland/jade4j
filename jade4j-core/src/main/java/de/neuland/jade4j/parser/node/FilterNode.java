package de.neuland.jade4j.parser.node;

import java.util.*;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.compiler.Utils;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.lexer.token.Attribute;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import org.apache.commons.lang3.StringUtils;

public class FilterNode extends Node {

	private Node textBlock;
	private List<Attr> attributes = new LinkedList<Attr>();

	public boolean hasTextBlock() {
		return textBlock != null;
	}

	public void setTextBlock(Node textBlock) {
		this.textBlock = textBlock;
	}

	public Node getTextBlock() {
		return textBlock;
	}

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		Filter filter = model.getFilter(getValue());
		ArrayList<String> values = new ArrayList<String>();
		LinkedList<Node> nodes = textBlock.getNodes();
		for (Node node : nodes) {
				values.add(node.getValue());
		}

		String result = StringUtils.join(values, "\n");
		if (filter != null) {
            result = filter.convert(result, attributes, model);
		}
		try {
			result = Utils.interpolate(result, model, false,template.getExpressionHandler());
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
		writer.append(result);
	}

	public void setAttributes(List<Attr> attributes) {
		this.attributes = attributes;

	}

}
