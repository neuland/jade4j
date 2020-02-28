package de.neuland.jade4j.expression;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.jexl2.JadeJexlEngine;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;

import static de.neuland.jade4j.model.JadeModel.NON_LOCAL_VARS;

public class JexlExpressionHandler implements ExpressionHandler {

	private static final int MAX_ENTRIES = 5000;
	public static Pattern plusplus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])\\+\\+\\s*;{0,1}\\s*$");
	public static Pattern isplusplus = Pattern.compile("\\+\\+\\s*;{0,1}\\s*$");
	public static Pattern minusminus = Pattern.compile("([a-zA-Z0-9-_]*[a-zA-Z0-9])--\\s*;{0,1}\\s*$");
	public static Pattern isminusminus = Pattern.compile("--\\s*;{0,1}\\s*$");
	public static Pattern isAssignment = Pattern.compile("^([a-zA-Z0-9-_]+)[\\s]?={1}[\\s]?[^=]+$");
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
			saveNonLocalVarAssignmentInModel(expression, model);
			expression = removeVar(expression);
			if (isplusplus.matcher(expression).find()) {
				expression = convertPlusPlusExpression(expression);
			}
			if (isminusminus.matcher(expression).find()) {
				expression = convertMinusMinusExpression(expression);
			}
			Script e = jexl.createScript(expression);
			Object evaluate = e.execute(new MapContext(model));
			return evaluate;
		} catch (Exception e) {
			throw new ExpressionException(expression, e);
		}
	}

	private void saveNonLocalVarAssignmentInModel(String expression, JadeModel model) {
		if (expression.startsWith("var ")) {
			return;
		}
		Matcher matcher = isAssignment.matcher(expression);
		if (matcher.matches()) {
			Set<String> nonLocalVars;
			if (model.containsKey(NON_LOCAL_VARS)) {
				nonLocalVars = (HashSet<String>) model.get(NON_LOCAL_VARS);
			} else {
				nonLocalVars = new HashSet<String>();
			}
			model.put(NON_LOCAL_VARS, nonLocalVars);
			nonLocalVars.add(matcher.group(1));
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
