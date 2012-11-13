package de.neuland.jade4j.parser.node;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.compiler.Utils;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.expression.ExpressionHandler;
import de.neuland.jade4j.template.JadeTemplate;

public class TagNode extends AttributedNode {
	private boolean textOnly;
	private Node textNode;
	private Node codeNode;
	private static final String[] selfClosing = { "meta", "img", "link", "input", "area", "base", "col", "br", "hr", "source" };

	public void setTextOnly(boolean textOnly) {
		this.textOnly = textOnly;

	}

	public void setTextNode(Node textNode) {
		this.textNode = textNode;
	}

	public void setCodeNode(Node codeNode) {
		this.codeNode = codeNode;
	}

	public boolean isTextOnly() {
		return this.textOnly;
	}

	public Node getTextNode() {
		return textNode;
	}

	public boolean hasTextNode() {
		return textNode != null;
	}

	public boolean hasCodeNode() {
		return codeNode != null;
	}

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		writer.newline();
		writer.append("<");
		writer.append(name);
		writer.append(attributes(model, template));
		if (isTerse(template)) {
			writer.append(">");
			return;
		}
		if (isSelfClosing(template)) {
			writer.append("/>");
			return;
		}
		writer.append(">");
		if (hasTextNode()) {
			textNode.execute(writer, model, template);
		}
		if (hasBlock()) {
			writer.increment();
			block.execute(writer, model, template);
			writer.decrement();
			writer.newline();
		}
		if (hasCodeNode()) {
			codeNode.execute(writer, model, template);
		}
		writer.append("</");
		writer.append(name);
		writer.append(">");
	}

	public boolean isTerse(JadeTemplate template) {
		return isSelfClosing(template) && template.isTerse();
	}

	public boolean isSelfClosing(JadeTemplate template) {
		return !template.isXml() && ArrayUtils.contains(selfClosing, name);
	}

	private String attributes(JadeModel model, JadeTemplate template) {
		StringBuilder sb = new StringBuilder();

		Map<String, Object> mergedAttributes = mergeInheritedAttributes(model);

		for (Map.Entry<String, Object> entry : mergedAttributes.entrySet()) {
			try {
				sb.append(getAttributeString(entry.getKey(), entry.getValue(), model, template));
			} catch (ExpressionException e) {
				throw new JadeCompilerException(this, template.getTemplateLoader(), e);
			}
		}

		return sb.toString();
	}

	private String getAttributeString(String name, Object attribute, JadeModel model, JadeTemplate template) throws ExpressionException {
		String value = null;
		if (attribute instanceof String) {
			value = getInterpolatedAttributeValue(name, attribute, model, template);
		} else if (attribute instanceof Boolean) {
			if ((Boolean) attribute) {
				value = name;
			} else {
				return "";
			}
			if (template.isTerse()) {
				value = null;
			}
		} else if (attribute instanceof ExpressionString) {
			String expression = ((ExpressionString) attribute).getValue();
			Object expressionValue = ExpressionHandler.evaluateExpression(expression, model);
			if (expressionValue == null) {
				return "";
			}
			// TODO: refactor
			if (expressionValue instanceof Boolean) {
				if ((Boolean) expressionValue) {
					value = name;
				} else {
					return "";
				}
				if (template.isTerse()) {
					value = null;
				}
			} else {
				value = expressionValue.toString();
				value = StringEscapeUtils.escapeHtml4(value);
			}
		} else {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(" ").append(name);
			if (value != null) {
				sb.append("=").append('"');
				sb.append(value);
				sb.append('"');
			}
		}
		return sb.toString();
	}

	private String getInterpolatedAttributeValue(String name, Object attribute, JadeModel model, JadeTemplate template)
			throws JadeCompilerException {
		if (!preparedAttributeValues.containsKey(name)) {
			preparedAttributeValues.put(name, Utils.prepareInterpolate((String) attribute, true));
		}
		List<Object> prepared = preparedAttributeValues.get(name);
		try {
			return Utils.interpolate(prepared, model);
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
	}
}
