package de.neuland.jade4j.parser.node;

import java.util.HashMap;
import java.util.Map;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.compiler.Utils;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class FilterNode extends Node {

	private Node textBlock;
	private Map<String, Object> attributes = new HashMap<String, Object>();

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
        String result = textBlock.getValue();
		if (filter != null) {
            result = filter.convert(result, attributes, model);
		}
		try {
			result = Utils.interpolate(result, model, false);
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
		writer.append(result);
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;

	}

}
