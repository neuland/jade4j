package de.neuland.jade4j.parser.node;

import java.util.*;

import de.neuland.jade4j.compiler.Utils;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AttrsNode extends Node {

	protected List<Attr> attributes = new LinkedList<Attr>();
	protected LinkedList<String> attributeBlocks = new LinkedList<String>();
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
//	protected Map<String, Object> mergeInheritedAttributes(JadeModel model) {
//		List<Attr> mergedAttributes = this.attributes;
//
//		if (inheritsAttributes) {
//			Object o = model.get("attributes");
//			if (o != null && o instanceof Map) {
//				@SuppressWarnings("unchecked")
//				Map<String, Object> inheritedAttributes = (Map<String, Object>) o;
//
//				for (Entry<String, Object> entry : inheritedAttributes.entrySet()) {
//					setAttribute(mergedAttributes, (String) entry.getKey(), entry.getValue());
//				}
//			}
//		}
//		return mergedAttributes;
//	}

	@Override
	public AttrsNode clone() throws CloneNotSupportedException {
		AttrsNode clone = (AttrsNode) super.clone();

        // shallow copy
		if (this.attributes != null) {
			clone.attributes = new ArrayList<Attr>(this.attributes);

		}
        if (this.attributes != null) {
            clone.attributeBlocks = new LinkedList<String>(this.attributeBlocks);
        }
		return clone;
	}
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

	protected String visitAttributes(JadeModel model, JadeTemplate template) {

        if(attributeBlocks.size()>0){
            //Todo: AttributesBlock needs to be evaluated
            for (String attributeBlock : attributeBlocks) {
                HashMap<String,String> o = null;
                try {
                    o = (HashMap<String,String>)template.getExpressionHandler().evaluateExpression(attributeBlock, model);
                } catch (ExpressionException e) {
                    e.printStackTrace();
                }
                if(o!=null) {
                    for (Map.Entry<String, String> entry : o.entrySet()) {
                        Attr attr = new Attr();
                        attr.setName(entry.getKey());
                        attr.setValue(entry.getValue());
                        attributes.add(attr);
                    }
                }
            }
            LinkedHashMap<String,String> attrs = attrs(model, template);
            return attrsToString(attrs);
        }else{
            LinkedHashMap<String,String> attrs = attrs(model, template);
            return attrsToString(attrs);
        }


    }

    private String attrsToString(LinkedHashMap<String, String> attrs) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
                sb.append(" ");
            sb.append(entry.getKey());
            if (entry.getValue() != null) {
                sb.append("=").append('"');
                sb.append(entry.getValue());
                sb.append('"');
            }
        }
        return sb.toString();
    }

    protected LinkedHashMap<String,String> attrs(JadeModel model, JadeTemplate template) {
        ArrayList<String> classes = new ArrayList<String>();
        LinkedHashMap<String,String> newAttributes = new LinkedHashMap<String,String>();
        for (Attr attribute : attributes) {
            try {
                addAttributesToMap(newAttributes,classes, attribute, model, template);
            } catch (ExpressionException e) {
                throw new JadeCompilerException(this, template.getTemplateLoader(), e);
            }
        }
        if(!classes.isEmpty()){
            newAttributes.put("class",String.join(" ",classes));
        }
        return newAttributes;
    }

    private void addAttributesToMap(HashMap<String, String> newAttributes, ArrayList<String> classes, Attr attribute, JadeModel model, JadeTemplate template) throws ExpressionException {
        String name = attribute.getName();
        String key = name;
        boolean escaped = false;
//        if ("class".equals(key)) {
//          classes.push(attr.val);
//          classEscaping.push(attr.escaped);
//        } else if (isConstant(attr.val)) {
//          if (buffer) {
//            this.buffer(runtime.attr(key, toConstant(attr.val), escaped, this.terse));
//          } else {
//            var val = toConstant(attr.val);
//            if (key === 'style') val = runtime.style(val);
//            if (escaped && !(key.indexOf('data') === 0 && typeof val !== 'string')) {
//              val = runtime.escape(val);
//            }
//            buf.push(utils.stringify(key) + ': ' + utils.stringify(val));
//          }
//        } else {
//          if (buffer) {
//            this.bufferExpression('jade.attr("' + key + '", ' + attr.val + ', ' + utils.stringify(escaped) + ', ' + utils.stringify(this.terse) + ')');
//          } else {
//            var val = attr.val;
//            if (key === 'style') {
//              val = 'jade.style(' + val + ')';
//            }
//            if (escaped && !(key.indexOf('data') === 0)) {
//              val = 'jade.escape(' + val + ')';
//            } else if (escaped) {
//              val = '(typeof (jade_interp = ' + val + ') == "string" ? jade.escape(jade_interp) : jade_interp)';
//            }
//            buf.push(utils.stringify(key) + ': ' + val);
//          }
//        }

        String value = null;
        Object attributeValue = attribute.getValue();
        if("class".equals(key)) {
            if (attributeValue instanceof String) {
                escaped = attribute.isEscaped();
                value = getInterpolatedAttributeValue(name, attributeValue,escaped, model, template);
            } else if (attributeValue instanceof ExpressionString) {
                escaped = ((ExpressionString) attributeValue).isEscape();
                Object expressionValue = evaluateExpression((ExpressionString) attributeValue, model,template.getExpressionHandler());
                if (expressionValue != null && expressionValue.getClass().isArray()) {
                    StringBuffer s = new StringBuffer("");
                    boolean first = true;
                    if (expressionValue instanceof int[]) {
                        for (int o : (int[]) expressionValue) {
                            if (!first)
                                s.append(" ");
                            s.append(o);
                            first = false;
                        }
                    } else {
                        for (Object o : (Object[]) expressionValue) {
                            if (!first)
                                s.append(" ");
                            s.append(o.toString());
                            first = false;
                        }
                    }
                    value = s.toString();
                }
            }
            if(!StringUtils.isBlank(value))
                classes.add(value);
            return;
//        }else if("id".equals(key)){
//            value = (String) attribute;
        }else if (attributeValue instanceof String) {
            escaped = attribute.isEscaped();
            value = getInterpolatedAttributeValue(name, attributeValue, escaped, model, template);
        } else if (attributeValue instanceof Boolean) {
            if ((Boolean) attributeValue) {
                value = name;
            } else {
                return;
            }
            if (template.isTerse()) {
                value = null;
            }
        } else if (attributeValue instanceof ExpressionString) {
            escaped = ((ExpressionString) attributeValue).isEscape();
            Object expressionValue = evaluateExpression((ExpressionString) attributeValue, model, template.getExpressionHandler());
            if (expressionValue == null) {
                return;
            }
            // TODO: refactor
            if (expressionValue instanceof Boolean) {
                if ((Boolean) expressionValue) {
                    value = name;
                } else {
                    return;
                }
                if (template.isTerse()) {
                    value = null;
                }
            }else{
                value = expressionValue.toString();
                value = StringEscapeUtils.escapeHtml4(value);
            }
        } else if (attributeValue instanceof String) {
            value = (String) attributeValue;
//        } else {
//            return "";
        }
        newAttributes.put(name,value);
    }

	private Object evaluateExpression(ExpressionString attribute, JadeModel model, ExpressionHandler expressionHandler) throws ExpressionException {
        String expression = ((ExpressionString) attribute).getValue();
        Object result = expressionHandler.evaluateExpression(expression, model);
        if (result instanceof ExpressionString) {
            return evaluateExpression((ExpressionString) result, model, expressionHandler);
        }
        return result;
    }

	private String getInterpolatedAttributeValue(String name, Object attribute, boolean escaped, JadeModel model, JadeTemplate template)
            throws JadeCompilerException {
//        if (!preparedAttributeValues.containsKey(name)) {
//            preparedAttributeValues.put(name, Utils.prepareInterpolate((String) attribute, escaped));
//        }
        List<Object> prepared = Utils.prepareInterpolate((String) attribute, escaped);
        try {
            return Utils.interpolate(prepared, model,template.getExpressionHandler());
        } catch (ExpressionException e) {
            throw new JadeCompilerException(this, template.getTemplateLoader(), e);
        }
    }
}
