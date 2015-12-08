package de.neuland.jade4j.lexer.token;

import de.neuland.jade4j.parser.node.ExpressionString;
import de.neuland.jade4j.parser.node.ValueString;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeList extends Token {
	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	public AttributeList(int lineNumber) {
		super(null, lineNumber);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void addAttribute(String name, String value, boolean escapedAttr) {
		ValueString valueString = new ValueString(value);
		valueString.setEscape(escapedAttr);
		attributes.put(name, valueString);
	}

	public void addExpressionAttribute(String name, String expression, boolean escapedAttr) {
		ExpressionString value = new ExpressionString(expression);
		value.setEscape(escapedAttr);
		attributes.put(name, value);
	}

	public void addBooleanAttribute(String name, Boolean value) {
		attributes.put(name, value);
	}

}
