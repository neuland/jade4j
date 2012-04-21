package de.neuland.jade4j.parser.expression;

import java.util.LinkedHashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

public class ExpressionHandler {

	private static final int MAX_ENTRIES = 5000;

	private static Map<String, Object> cache = new LinkedHashMap<String, Object>(MAX_ENTRIES + 1, .75F, true) {
		private static final long serialVersionUID = -5019455452524450589L;

		public boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public static Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
		try {
			Object tree = getCompiledExpression(expression);
			return (Boolean) Ognl.getValue(tree, (Object) model, Boolean.class);
		} catch (OgnlException e) {
			throw new ExpressionException(e);
		}
	}

	public static Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
		try {
			Object tree = getCompiledExpression(expression);
			return Ognl.getValue(tree, model);
		} catch (OgnlException e) {
			throw new ExpressionException(e);
		}
	}

	protected static Object getCompiledExpression(String expression) throws OgnlException {
		if (!cache.containsKey(expression)) {
			cache.put(expression, compileExpression(expression));
		}
		return cache.get(expression);
	}

	protected static Object compileExpression(String expression) throws OgnlException {
		return Ognl.parseExpression(expression);
	}
}
