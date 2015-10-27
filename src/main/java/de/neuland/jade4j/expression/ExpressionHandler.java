package de.neuland.jade4j.expression;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;

/**
 * Created by christoph on 27.10.15.
 */
public interface ExpressionHandler {
    Boolean evaluateBooleanExpression(String expression, JadeModel model) throws ExpressionException;

    Object evaluateExpression(String expression, JadeModel model) throws ExpressionException;

    String evaluateStringExpression(String expression, JadeModel model) throws ExpressionException;

    void assertExpression(String expression) throws ExpressionException;

    void setCache(boolean cache);

    void clearCache();
}

