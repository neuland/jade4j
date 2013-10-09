package de.neuland.jade4j.parser.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.neuland.jade4j.model.JadeModel;

public abstract class AttributedNode extends Node {

	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Map<String, List<Object>> preparedAttributeValues = new HashMap<String, List<Object>>();
	protected boolean inheritsAttributes = false;

	public void addAttribute(String key, Object value) {
		if ("attributes".equals(key)) {
			inheritsAttributes = true;
		} else {
			addAttribute(attributes, key, value);
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

	protected Map<String, Object> mergeInheritedAttributes(JadeModel model) {
		Map<String, Object> mergedAttributes = this.attributes;

		if (inheritsAttributes) {
			Object o = model.get("attributes");
			if (o != null && o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> inheritedAttributes = (Map<String, Object>) o;

				for (Entry<String, Object> entry : inheritedAttributes.entrySet()) {
					addAttribute(mergedAttributes, (String) entry.getKey(), entry.getValue());
				}
			}
		}
		return mergedAttributes;
	}

	/**
	 * Puts the specified key-value pair in the specified map. Provides special
	 * processing in the case of the "class" attribute.
	 */
	private void addAttribute(Map<String, Object> map, String key, Object newValue) {
		if ("class".equals(key) && attributes.containsKey(key)) {
			String value1 = attributeValueToString(attributes.get(key));
			String value2 = attributeValueToString(newValue);
			attributes.put(key, value1 + " " + value2);

		} else {
			attributes.put(key, newValue);
		}
	}

	private String attributeValueToString(Object value) {
		if (value instanceof ExpressionString) {
			String expression = ((ExpressionString) value).getValue();
			return "#{" + expression + "}";
		}
		return value.toString();
	}

	@Override
	public AttributedNode clone() throws CloneNotSupportedException {
		AttributedNode clone = (AttributedNode) super.clone();

        // shallow copy
		if (this.attributes != null) {
			clone.attributes = new LinkedHashMap<String, Object>(this.attributes);
		}

		// clear prepared attribute values, will be rebuilt on execute
		clone.preparedAttributeValues = new HashMap<String, List<Object>>();

		return clone;
	}

}
