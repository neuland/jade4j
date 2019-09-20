package de.neuland.pug4j.expression;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.util.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;


/**
 * Work In Progress - Using ScriptEngineManager
 */
public class NashornExpressionHandler implements ExpressionHandler {
    JexlExpressionHandler jexlExpressionHandler = new JexlExpressionHandler();
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine jsEngine = mgr.getEngineByName("Nashorn");

    public NashornExpressionHandler() {

    }

    @Override
    public Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    @Override
    public Object evaluateExpression(String expression, PugModel model) throws ExpressionException {
        try{
            Bindings bindings = jsEngine.createBindings();
            bindings.putAll(model);
            Object eval;
            if(expression.startsWith("{")) {
                eval = ((Map) jsEngine.eval("[" + expression + "]", bindings)).get("0");
            } else {
                eval = jsEngine.eval(expression, bindings);

            }


            for (Map.Entry<String, Object> stringObjectEntry : bindings.entrySet()) {
                model.put(stringObjectEntry.getKey(), convertToPugModelValue(stringObjectEntry.getValue()));
            }
            return convertToPugModelValue(eval);
        }
        catch (ScriptException ex){
            throw new ExpressionException(expression, ex);
        }
    }

    private Object convertToPugModelValue(Object eval) {
        if(eval == null) {
            return null;
        }

        if(eval instanceof Double){
            String s = String.valueOf(eval);
            if(s.endsWith(".0")){
                return Integer.valueOf(s.substring(0,s.length()-2));
            }
        }

        if(eval instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) eval;

            if(mirror.isArray()) {
                return toArray(mirror);
            }
        }

//        eval = convert(eval);
        //        if(eval instanceof NativeArray){
//            NativeArray n = (NativeArray) eval;
//            for(int i=0;i<n.getLength();i++){
//                n.get(0);
//            }
//        }
        return eval;
    }

    private Object toArray(ScriptObjectMirror mirror) {
        Object[] array = new Object[mirror.size()];

        if(mirror.isEmpty()) {
            return array;
        }

        boolean multiDimensional = false;
        if(mirror.get("0") instanceof ScriptObjectMirror) {
            ScriptObjectMirror innerMirror = (ScriptObjectMirror) mirror.get("0");
            if(innerMirror.isArray()) {
                multiDimensional = true;
            }
        }

        for (int i = 0; i < mirror.size(); i++) {
            Object value = mirror.get(i + "");
            if(multiDimensional) {
                array[i] = toArray((ScriptObjectMirror) value);
            } else {
                array[i] = value;
            }
        }
        return array;
    }

    @Override
    public String evaluateStringExpression(String expression, PugModel model) throws ExpressionException {
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
