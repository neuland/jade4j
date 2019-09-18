package de.neuland.pug4j.parser.node;

import java.util.*;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.compiler.Utils;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.filter.Filter;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;
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
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
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
			throw new PugCompilerException(this, template.getTemplateLoader(), e);
		}
		writer.append(result);
	}

	public void setAttributes(List<Attr> attributes) {
		this.attributes = attributes;

	}

}
