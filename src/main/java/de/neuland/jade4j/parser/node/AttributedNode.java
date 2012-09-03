package de.neuland.jade4j.parser.node;


import de.neuland.jade4j.model.JadeModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AttributedNode extends Node {

	protected Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	protected Map<String, List<Object>> preparedAttributeValues = new HashMap<String, List<Object>>();
	protected boolean inheritsAttributes = false;

	public void addAttribute(String key, Object value) {
		if ("attributes".equals(key)) {
			inheritsAttributes = true;
		}
		else {
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
				Map<?,?> inheritedAttributes = (Map)o;

				for (Map.Entry entry : inheritedAttributes.entrySet()) {
					addAttribute(mergedAttributes, (String)entry.getKey(), entry.getValue());
				}
			}
		}
		return mergedAttributes;
	}

	/**
	 * Puts the specified key-value pair in the specified map.  Provides special processing in the case of the
	 * "class" attribute.
	 */
	private void addAttribute(Map<String, Object> map, String key, Object value) {
        if ("class".equals(key) && attributes.containsKey(key)) {
           if(value instanceof ExpressionString) {
                attributes.put(key, new ExpressionString(new StringBuilder("\"").append((String) attributes.get(key)).append(" \" + ").append(((ExpressionString) value).getValue()).toString()));
            } else {
                attributes.put(key, new StringBuilder((String) attributes.get(key)).append(" ").append(value).toString());
            }
        } else {
            attributes.put(key, value);
        }
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
