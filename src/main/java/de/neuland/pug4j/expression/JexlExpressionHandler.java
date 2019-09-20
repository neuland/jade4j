package de.neuland.pug4j.expression;

import de.neuland.pug4j.AbstractExpressionHandler;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.internal.PugJexlEngine;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static de.neuland.pug4j.model.PugModel.NON_LOCAL_VARS;

public class JexlExpressionHandler extends AbstractExpressionHandler {

	private static final int MAX_ENTRIES = 5000;
	public static Pattern plusplus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])\\+\\+\\s*;{0,1}\\s*$");
	public static Pattern isplusplus = Pattern.compile("\\+\\+\\s*;{0,1}\\s*$");
	public static Pattern minusminus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])--\\s*;{0,1}\\s*$");
	public static Pattern isminusminus = Pattern.compile("--\\s*;{0,1}\\s*$");
	private JexlEngine jexl;

	public JexlExpressionHandler() {
		jexl = new PugJexlEngine(MAX_ENTRIES);

	}

	public Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException {
		return BooleanUtil.convert(evaluateExpression(expression, model));
	}

	public Object evaluateExpression(String expression, PugModel model) throws ExpressionException {
		try {
			saveNonLocalVarAssignmentInModel(expression, model);
			expression = removeVar(expression);
			if (isplusplus.matcher(expression).find()) {
				expression = convertPlusPlusExpression(expression);
			}
			if (isminusminus.matcher(expression).find()) {
				expression = convertMinusMinusExpression(expression);
			}
			JexlScript e = jexl.createScript(expression);
            MapContext jexlContext = new MapContext(model);
			Object evaluate = e.execute(jexlContext);
			return evaluate;
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	private String convertMinusMinusExpression(String expression) {
		Matcher matcher = minusminus.matcher(expression);
		if (matcher.find(0) && matcher.groupCount() == 1) {
            String a = matcher.group(1);
            expression = a + " = " + a + " - 1";
        }
		return expression;
	}

	private String convertPlusPlusExpression(String expression) {
		Matcher matcher = plusplus.matcher(expression);
		if (matcher.find(0) && matcher.groupCount() == 1) {
            String a = matcher.group(1);
            expression = a + " = " + a + " + 1";
        }
		return expression;
	}

	private String removeVar(String expression) {
		expression = expression.replace("var ",";");
		return expression;
	}

	public void assertExpression(String expression) throws ExpressionException {
		try {
			jexl.createExpression(expression);
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	public String evaluateStringExpression(String expression, PugModel model) throws ExpressionException {
		Object result = evaluateExpression(expression, model);
		return result == null ? "" : result.toString();
	}
	
	public void setCache(boolean cache) {
		jexl = new PugJexlEngine(cache ? MAX_ENTRIES : 0);
	}

    public void clearCache() {
        jexl.clearCache();
    }
}
