package de.neuland.jade4j.expression;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.util.Map;
import java.util.Set;

/**
 * Work In Progress - Using ScriptEngineManager
 */
public class JsExpressionHandler implements ExpressionHandler {
    @Override
    public Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    @Override
    public Object evaluateExpression(String expression, JadeModel model) throws ExpressionException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

        try{
            Bindings bindings = jsEngine.createBindings();
            bindings.putAll(model);
            Object eval = jsEngine.eval(expression, bindings);
            for (Map.Entry<String, Object> stringObjectEntry : bindings.entrySet()) {
                model.put(stringObjectEntry.getKey(),stringObjectEntry.getValue());
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
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("Jexl");
        try{
          jsEngine.eval("return ("+expression+")");
        }
        catch (ScriptException ex){
            throw new ExpressionException(expression, ex);
        }

    }

    @Override
    public void setCache(boolean cache) {

    }

    @Override
    public void clearCache() {

    }
}
