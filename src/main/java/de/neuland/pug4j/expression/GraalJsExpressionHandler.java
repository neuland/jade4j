package de.neuland.pug4j.expression;

import de.neuland.pug4j.AbstractExpressionHandler;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.Proxy;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.graalvm.polyglot.HostAccess.newBuilder;

public class GraalJsExpressionHandler extends AbstractExpressionHandler {
    JexlExpressionHandler jexlExpressionHandler = new JexlExpressionHandler();
    Context jsContext;

    {
        HostAccess all = newBuilder().allowPublicAccess(true).allowAllImplementations(true).allowArrayAccess(true).allowListAccess(true).build();
        jsContext = Context.newBuilder("js").allowHostAccess(all).allowAllAccess(true).allowExperimentalOptions(true)
                    .allowHostClassLookup(s -> true).allowPolyglotAccess(PolyglotAccess.ALL).build();
    }

    @Override
    public Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    @Override
    public Object evaluateExpression(String expression, PugModel model) throws ExpressionException {
        try{
            saveNonLocalVarAssignmentInModel(expression, model);
            Value jsContextBindings = jsContext.getBindings("js");
            for (Map.Entry<String, Object> objectEntry : model.entrySet()) {
                String key = objectEntry.getKey();
                if(!"locals".equals(key)&&!"nonLocalVars".equals(key)) {
                    Object value = objectEntry.getValue();
                    if(value instanceof Map)
                        value = ProxyObject.fromMap((Map)value);
                    if(value instanceof List)
                        value = ProxyArray.fromList((List)value);
                    jsContextBindings.putMember(key, value);
                }
            }

            Value eval;
            Source js;
            if(expression.startsWith("{")){
                 js = Source.create("js", "(" + expression + ")");
            }else{
                 js = Source.create("js", expression);
            }
            eval = jsContext.eval(js);


            Set<String> memberKeys = jsContextBindings.getMemberKeys();
            for (String memberKey : memberKeys) {
                Value member = jsContextBindings.getMember(memberKey);
                model.put(memberKey, convertToPugModelValue(member));
            }
            return convertToPugModelValue(eval);
        }
        catch (Exception ex){
            throw new ExpressionException(expression, ex);
        }
    }
    private Object convertToPugModelValue(Value eval) {
        if(eval.isNull()) {
            return null;
        }
        if(eval.hasArrayElements()) {
            return eval.as(List.class);
        }
        if(eval.fitsInInt()){
            return eval.asInt();
        }
        if(eval.hasMembers()){
            return eval.as(Map.class);
        }
        if(eval.fitsInDouble() && !eval.fitsInInt()){
            return eval.asDouble();
        }
        if(eval.isString()){
            return eval.asString();
        }
        if(eval.isBoolean()){
            return eval.asBoolean();
        }


        return eval;
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

