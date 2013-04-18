package de.neuland.jade4j.expression;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JadeJexlEngine;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

public class ExpressionHandler {

    public static Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
		return BooleanUtil.convert(evaluateExpression(expression, model));
	}

	public static Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
		try {
            final JexlEngine jexlEngine = JadeJexlEngine.getInstance();
            Expression e = jexlEngine.createExpression(expression);
			return e.evaluate(new MapContext(model));
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	public static String evaluateStringExpression(String expression, JadeModel model) throws ExpressionException {
		Object result = evaluateExpression(expression, model);
		return result == null ? "" : result.toString();
	}
}
