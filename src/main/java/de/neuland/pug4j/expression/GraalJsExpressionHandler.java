package de.neuland.pug4j.expression;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.script.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GraalJsExpressionHandler implements ExpressionHandler {
    JexlExpressionHandler jexlExpressionHandler = new JexlExpressionHandler();
    Context jsContext = Context.create("js");

    @Override
    public Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    @Override
    public Object evaluateExpression(String expression, PugModel model) throws ExpressionException {
        try{

            Value jsContextBindings = jsContext.getBindings("js");
            for (Map.Entry<String, Object> objectEntry : model.entrySet()) {
                jsContextBindings.putMember(objectEntry.getKey(),objectEntry.getValue());
            }

            Value eval;
            if(expression.startsWith("{")){
                eval = jsContext.eval("js", "["+expression+"];");
                eval = jsContext.asValue(eval.as(List.class).get(0));
            }else{
                eval = jsContext.eval("js", expression);
            }


            Set<String> memberKeys = jsContextBindings.getMemberKeys();
            for (String memberKey : memberKeys) {
                if(!"locals".equals(memberKey)) {
                    Value member = jsContextBindings.getMember(memberKey);
                    model.put(memberKey, convertToPugModelValue(member));
                }
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

