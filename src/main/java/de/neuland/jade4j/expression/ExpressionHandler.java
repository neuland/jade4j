package de.neuland.jade4j.expression;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JadeJexlEngine;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

public class ExpressionHandler {

	private static final int MAX_ENTRIES = 5000;

	private static JexlEngine jexl;

	static {
		jexl = new JadeJexlEngine();
		jexl.setCache(MAX_ENTRIES);
	}

	public static Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
		return BooleanUtil.convert(evaluateExpression(expression, model));
	}

	public static Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
		try {
			Expression e = jexl.createExpression(expression);
			return e.evaluate(new MapContext(model));
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	public static String evaluateStringExpression(String expression, JadeModel model) throws ExpressionException {
		Object result = evaluateExpression(expression, model);
		return result == null ? "" : result.toString();
	}
	
	public static void setCache(boolean cache) {
		jexl.setCache(cache ? MAX_ENTRIES : 0);
	}

    public static void clearCache() {
        jexl.clearCache();
    }
}
