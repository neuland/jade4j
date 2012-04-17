package de.neuland.jade4j.parser.node;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.expression.ExpressionHandler;
import de.neuland.jade4j.template.JadeTemplate;

public class EachNode extends Node {

	private String key;
	private String code;

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		Object result;
		try {
			result = ExpressionHandler.evaluateExpression(getCode(), model);
		} catch (ExpressionException e) {
			throw new JadeCompilerException(this, e);
		}
		model.pushScope();
		run(writer, model, result, template);
		model.popScope();
	}

	@SuppressWarnings("unchecked")
	private void run(IndentWriter writer, JadeModel model, Object result, JadeTemplate template) {
		if (result instanceof Collection) {
			runCollection((Collection<Object>) result, model, writer, template);
		}
		if (result.getClass().isArray()) {
			runArray((Object[]) result, model, writer, template);
		}
		if (result instanceof Map) {
			runMap((Map<String, Object>) result, model, writer, template);
		}
	}

	private void runCollection(Collection<Object> result, JadeModel model, IndentWriter writer, JadeTemplate template) {
		int index = 0;
		for (Object entry : result) {
			model.put(getValue(), entry);
			model.put(getKey(), index);
			getBlock().execute(writer, model, template);
			index++;
		}
	}

	private void runArray(Object[] result, JadeModel model, IndentWriter writer, JadeTemplate template) {
		int index = 0;
		for (Object entry : result) {
			model.put(getValue(), entry);
			model.put(getKey(), index);
			getBlock().execute(writer, model, template);
			index++;
		}
	}

	private void runMap(Map<String, Object> result, JadeModel model, IndentWriter writer, JadeTemplate template) {
		Set<String> keys = result.keySet();
		for (String key : keys) {
			model.put(getValue(), result.get(key));
			model.put(getKey(), key);
			getBlock().execute(writer, model, template);
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

}
