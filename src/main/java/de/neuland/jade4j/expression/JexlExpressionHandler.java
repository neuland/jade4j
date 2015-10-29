package de.neuland.jade4j.expression;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JadeJexlEngine;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JexlExpressionHandler implements ExpressionHandler {

	private static final int MAX_ENTRIES = 5000;
	public static Pattern plusplus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])\\+\\+");
	public static Pattern minusminus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])--");
	private JexlEngine jexl;

	public JexlExpressionHandler() {
		jexl = new JadeJexlEngine();
		jexl.setCache(MAX_ENTRIES);

	}

	public Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
		return BooleanUtil.convert(evaluateExpression(expression, model));
	}

	public Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
		try {
			if(expression.startsWith("{")) {
				return expression;
			}else{
			if(expression.startsWith("var")){
				expression = expression.substring(3);
			}
				Matcher matcher = plusplus.matcher(expression);
				if(matcher.find(0) && matcher.groupCount()==1) {
					String a = matcher.group(1);
					expression = a+" = "+a+" + 1";
				}
				matcher = minusminus.matcher(expression);
				if(matcher.find(0) && matcher.groupCount()==1) {
					String a = matcher.group(1);
					expression = a+" = "+a+" - 1";
				}
				Expression e = jexl.createExpression(expression);
				Object evaluate = e.evaluate(new MapContext(model));
				return evaluate;
			}

		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}
	public void assertExpression(String expression) throws ExpressionException {
		try {
			jexl.createExpression("return (" + expression + ")");
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	public String evaluateStringExpression(String expression, JadeModel model) throws ExpressionException {
		Object result = evaluateExpression(expression, model);
		return result == null ? "" : result.toString();
	}
	
	public void setCache(boolean cache) {
		jexl.setCache(cache ? MAX_ENTRIES : 0);
	}

    public void clearCache() {
        jexl.clearCache();
    }
}
