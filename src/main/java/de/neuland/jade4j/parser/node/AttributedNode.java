package de.neuland.jade4j.parser.node;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AttributedNode extends Node {

	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Map<String, List<Object>> preparedAttributeValues = new HashMap<String, List<Object>>();

	public void addAttribute(String key, Object value) {
		if ("class".equals(key) && attributes.containsKey(key)) {
			attributes.put(key, new StringBuilder((String) attributes.get(key)).append(" ").append(value).toString());
		} else {
			attributes.put(key, value);
		}
	}

	public String getAttribute(String key) {
		return (String) attributes.get(key);
	}

	public void addAttributes(Map<String, Object> attributeMap) {
		for (String key : attributeMap.keySet()) {
			addAttribute(key, attributeMap.get(key));
		}
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public AttributedNode clone() throws CloneNotSupportedException {
		AttributedNode clone = (AttributedNode) super.clone();

		if (clone.attributes != null) {
			// shallow copy
			clone.attributes = new LinkedHashMap<String, Object>(clone.attributes);
		}

		// clear prepared attribute values, will be rebuilt on execute
		preparedAttributeValues = new HashMap<String, List<Object>>();

		return clone;
	}

}
