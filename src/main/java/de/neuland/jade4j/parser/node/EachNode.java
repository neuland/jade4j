package de.neuland.jade4j.parser.node;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.expression.JexlExpressionHandler;
import org.apache.commons.collections.IteratorUtils;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class EachNode extends Node {

	private String key;
	private String code;
	private Node elseNode;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template, ExpressionHandler expressionHandler) throws JadeCompilerException {
		Object result;
		try {
			result = expressionHandler.evaluateExpression(getCode(), model);
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), e);
		}
		if (result == null) {
			throw new JadeCompilerException(this, template.getTemplateLoader(), "[" + code + "] has to be iterable but was null");
		}
		model.pushScope();
		run(writer, model, result, template,expressionHandler);
		model.popScope();
	}

	@SuppressWarnings("unchecked")
	private void run(IndentWriter writer, JadeModel model, Object result, JadeTemplate template, ExpressionHandler expressionHandler) {
		if (result instanceof Iterable<?>) {
			runIterator(((Iterable<?>) result).iterator(), model, writer, template,expressionHandler);
		} else if (result.getClass().isArray()) {
			Iterator<?> iterator = IteratorUtils.arrayIterator(result);
			runIterator(iterator, model, writer, template, expressionHandler);
		} else if (result instanceof Map) {
			runMap((Map<String, Object>) result, model, writer, template,expressionHandler);
		}
	}

	private void runIterator(Iterator<?> iterator, JadeModel model, IndentWriter writer, JadeTemplate template, ExpressionHandler expressionHandler) {
		int index = 0;

		if (!iterator.hasNext()) {
			executeElseNode(model, writer, template, expressionHandler);
			return;
		}

		while (iterator.hasNext()) {
			model.put(getValue(), iterator.next());
			model.put(getKey(), index);
			getBlock().execute(writer, model, template, expressionHandler);
			index++;
		}
	}

	private void runMap(Map<String, Object> result, JadeModel model, IndentWriter writer, JadeTemplate template, ExpressionHandler expressionHandler) {
		Set<String> keys = result.keySet();
		if (keys.size() == 0) {
			executeElseNode(model, writer, template,expressionHandler);
			return;
		}
		for (String key : keys) {
			model.put(getValue(), result.get(key));
			model.put(getKey(), key);
			getBlock().execute(writer, model, template, expressionHandler);
		}
	}

	private void executeElseNode(JadeModel model, IndentWriter writer, JadeTemplate template, ExpressionHandler expressionHandler) {
		if (elseNode != null) {
			elseNode.execute(writer, model, template, expressionHandler);
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKey() {
		return key == null ? "$index" : key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Node getElseNode() {
		return elseNode;
	}

	public void setElseNode(Node elseNode) {
		this.elseNode = elseNode;
	}

}
