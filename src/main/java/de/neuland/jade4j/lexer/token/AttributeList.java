package de.neuland.jade4j.lexer.token;

import de.neuland.jade4j.parser.node.ExpressionString;
import de.neuland.jade4j.parser.node.ValueString;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttributeList extends Token {
	private LinkedList<Attribute> attributes = new LinkedList<Attribute>();
//	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	public AttributeList(int lineNumber) {
		super(null, lineNumber);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(String name, String value, boolean escapedAttr) {
		attributes.add(new Attribute(name,value,escapedAttr));
	}

	public void addExpressionAttribute(String name, String expression, boolean escapedAttr) {
		ExpressionString value = new ExpressionString(expression);
		value.setEscape(escapedAttr);
		attributes.add(new Attribute(name,value,escapedAttr));
	}

	public void addBooleanAttribute(String name, Boolean value) {
		attributes.add(new Attribute(name,value,false));
	}

}
