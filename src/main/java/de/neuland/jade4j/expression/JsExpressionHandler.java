package de.neuland.jade4j.expression;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.util.*;


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
            Object eval;
            if(expression.startsWith("{")){
                eval = ((Map)jsEngine.eval("["+expression+"]", bindings)).get("0");
            }else{
                eval = jsEngine.eval(expression, bindings);
            }

            for (Map.Entry<String, Object> stringObjectEntry : bindings.entrySet()) {
                model.put(stringObjectEntry.getKey(),convertToJadeModelValue(stringObjectEntry.getValue()));
            }
            return convertToJadeModelValue(eval);
        }
        catch (ScriptException ex){
//            return expression;
            throw new ExpressionException(expression, ex);
        }
    }

//    public static Object[] toArray(ScriptObjectMirror scriptObjectMirror)
//    {
//        if (!scriptObjectMirror.isArray())
//        {
//            throw new IllegalArgumentException("ScriptObjectMirror is no array");
//        }
//
//        if (scriptObjectMirror.isEmpty())
//        {
//            return new Object[0];
//        }
//
//        Object[] array = new Object[scriptObjectMirror.size()];
//
//        int i = 0;
//
//        for (Map.Entry<String, Object> entry : scriptObjectMirror.entrySet())
//        {
//            Object result = entry.getValue();
//
//            System.err.println(result.getClass());
//
//            if (result instanceof ScriptObjectMirror && scriptObjectMirror.isArray())
//            {
//                array[i] = toArray((ScriptObjectMirror) result);
//            }
//            else
//            {
//                array[i] = result;
//            }
//
//            i++;
//        }
//
//        return array;
//    }
//    private static Object convert(final Object obj) {
//        if (obj instanceof Bindings) {
//            try {
//                final Class<?> cls = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
//                if (cls.isAssignableFrom(obj.getClass())) {
//                    final Method isArray = cls.getMethod("isArray");
//                    final Object result = isArray.invoke(obj);
//                    if (result != null && result.equals(true)) {
//                        final Method values = cls.getMethod("values");
//                        final Object vals = values.invoke(obj);
//                        if (vals instanceof Collection<?>) {
//                            final Collection<?> coll = (Collection<?>) vals;
//                            return toArray((ScriptObjectMirror) obj);
//                        }
//                    }
//                }
//            } catch(ClassNotFoundException | NoSuchMethodException | SecurityException
//                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//
//            }
//        }
//        if (obj instanceof List<?>) {
//            final List<?> list = (List<?>) obj;
//            return list.toArray(new Object[0]);
//        }
//        return obj;
//    }
    private Object convertToJadeModelValue(Object eval) {
        if(eval instanceof Double){
            String s = String.valueOf(eval);
            if(s.endsWith(".0")){
                return Integer.valueOf(s.substring(0,s.length()-2));
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
