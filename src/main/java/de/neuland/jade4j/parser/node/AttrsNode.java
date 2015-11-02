package de.neuland.jade4j.parser.node;

import java.util.*;
import java.util.Map.Entry;

import de.neuland.jade4j.exceptions.JadeParserException;
import de.neuland.jade4j.model.JadeModel;

public abstract class AttrsNode extends Node {

	protected List<Attr> attributes = new LinkedList<Attr>();
	protected List<String> attributeBlocks = new LinkedList<String>();
	protected List<String> attributeNames = new LinkedList<String>();
	protected boolean selfClosing = false;
	protected Node codeNode;
	private boolean textOnly;

	public void setAttribute(String key, Object value, boolean escaped) {
		if (!"class".equals(key) && this.attributeNames.indexOf(key) != -1) {
			throw new Error("Duplicate attribute '" + key + "' is not allowed.");
		} else {
			this.attributeNames.add(key);
			Attr attr = new Attr();
			attr.setName(key);
			attr.setValue(value);
			attr.setEscaped(escaped);
			this.attributes.add(attr);
		}
	}

	public String getAttribute(String key) {
		for (int i = 0, len = this.attributes.size(); i < len; ++i) {
			if (this.attributes.get(i) != null && this.attributes.get(i).getName().equals(name)) {
				return attributeValueToString(this.attributes.get(i).getValue());
			}
		}
		return null;
	}

	private String attributeValueToString(Object value) {
		if (value instanceof ExpressionString) {
			String expression = ((ExpressionString) value).getValue();
			return "#{" + expression + "}";
		}
		return value.toString();
	}

//	@Override
//	public AttrsNode clone() throws CloneNotSupportedException {
//		AttrsNode clone = (AttrsNode) super.clone();
//
//        // shallow copy
//		if (this.attributes != null) {
//			clone.attributes = new LinkedHashMap<String, Object>(this.attributes);
//		}
//
//		return clone;
//	}
	public void addAttributes(String src){
		this.attributeBlocks.add(src);
	}

	public void setSelfClosing(boolean selfClosing) {
        this.selfClosing = selfClosing;
    }

	public boolean isSelfClosing() {
        return selfClosing;
    }

	public void setTextOnly(boolean textOnly) {
        this.textOnly = textOnly;

    }

	public boolean isTextOnly() {
        return this.textOnly;
    }

	public void setCodeNode(Node codeNode) {
        this.codeNode = codeNode;
    }

	public boolean hasCodeNode() {
        return codeNode != null;
    }
}
