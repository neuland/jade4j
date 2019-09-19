package de.neuland.pug4j.parser.node;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class EachNode extends Node {

	private String key;
	private String code;
	private Node elseNode;

	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		Object result;
		try {
			result = template.getExpressionHandler().evaluateExpression(getCode(), model);
		} catch (ExpressionException e) {
			throw new PugCompilerException(this, template.getTemplateLoader(), e);
		}
		if (result == null) {
			throw new PugCompilerException(this, template.getTemplateLoader(), "[" + code + "] has to be iterable but was null");
		}
		model.pushScope();
		run(writer, model, result, template);
		model.popScope();
	}

	@SuppressWarnings("unchecked")
	private void run(IndentWriter writer, PugModel model, Object result, PugTemplate template) {
		if (result instanceof Iterable<?>) {
			runIterator(((Iterable<?>) result).iterator(), model, writer, template);
		} else if (result.getClass().isArray()) {
			Iterator<?> iterator = IteratorUtils.arrayIterator(result);
			runIterator(iterator, model, writer, template);
		} else if (result instanceof Map) {
			runMap((Map<Object, Object>) result, model, writer, template);
		}
	}

	private void runIterator(Iterator<?> iterator, PugModel model, IndentWriter writer, PugTemplate template) {
		int index = 0;

		if (!iterator.hasNext()) {
			executeElseNode(model, writer, template);
			return;
		}

		while (iterator.hasNext()) {
			model.put(getValue(), iterator.next());
			model.put(getKey(), index);
			getBlock().execute(writer, model, template);
			index++;
		}
	}

	private void runMap(Map<Object, Object> result, PugModel model, IndentWriter writer, PugTemplate template) {
		Set<Object> keys = result.keySet();
		if (keys.size() == 0) {
			executeElseNode(model, writer, template);
			return;
		}
		for (Object key : keys) {
			model.put(getValue(), result.get(key));
			model.put(getKey(), key);
			getBlock().execute(writer, model, template);
		}
	}

	private void executeElseNode(PugModel model, IndentWriter writer, PugTemplate template) {
		if (elseNode != null) {
			elseNode.execute(writer, model, template);
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
