package de.neuland.jade4j.lexer.token;

import java.util.HashMap;
import java.util.Map;

import de.neuland.jade4j.parser.node.ExpressionString;

public class Attribute extends Token {

	Map<String, Object> attributes = new HashMap<String, Object>();

	public Attribute(String value, int lineNumber) {
		super(value, lineNumber);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public void addExpressionAttribute(String name, String expression) {
		attributes.put(name, new ExpressionString(expression));
	}

	public void addBooleanAttribute(String name, Boolean value) {
		attributes.put(name, value);
	}

}
