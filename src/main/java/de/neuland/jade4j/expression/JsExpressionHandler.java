package de.neuland.jade4j.expression;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

import javax.script.*;
import java.util.Map;

/**
 * Work In Progress - Using ScriptEngineManager
 */
public class JsExpressionHandler implements ExpressionHandler {
    JexlExpressionHandler jexlExpressionHandler = new JexlExpressionHandler();
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

    public JsExpressionHandler() {

    }

    @Override
    public Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    @Override
    public Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
        try{
            Bindings bindings = jsEngine.createBindings();
            bindings.putAll(model);
            Object eval = jsEngine.eval(expression, bindings);
            for (Map.Entry<String, Object> stringObjectEntry : bindings.entrySet()) {
                model.put(stringObjectEntry.getKey(),stringObjectEntry.getValue());
            }
            if(eval instanceof Double){
                String s = String.valueOf(eval);
                if(s.endsWith(".0")){
                    return Integer.valueOf(s.substring(0,s.length()-2));
                }
            }
            return eval;
        }
        catch (ScriptException ex){
//            return expression;
            throw new ExpressionException(expression, ex);
        }
    }

    @Override
    public String evaluateStringExpression(String expression, JadeModel model) throws ExpressionException {
        Object result = evaluateExpression(expression, model);
        return result == null ? "" : result.toString();

    }

    @Override
    public void assertExpression(String expression) throws ExpressionException {
        jexlExpressionHandler.assertExpression(expression);
    }

    @Override
    public void setCache(boolean cache) {

    }

    @Override
    public void clearCache() {

    }
}
