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

public class FilterNode extends AttrsNode {

	private List<Attr> attributes = new LinkedList<Attr>();
	private LinkedList<Node> filters = new LinkedList<>();

	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		ArrayList<String> values = new ArrayList<String>();
		LinkedList<Node> nodes = block.getNodes();
		for (Node node : nodes) {
			values.add(node.getValue());
		}

		String result = StringUtils.join(values, "");
		Filter filter = model.getFilter(getValue());
		if (filter != null) {
			result = filter.convert(result, attributes, model);
		}
		for (Node filterValue : filters) {
			filter = model.getFilter(filterValue.getValue());
			if (filter != null) {
				result = filter.convert(result, attributes, model);
			}
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

	public void setFilter(LinkedList<Node> filters) {
		this.filters = filters;
	}
}
