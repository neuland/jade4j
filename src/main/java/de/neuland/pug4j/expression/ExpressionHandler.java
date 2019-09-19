package de.neuland.pug4j.expression;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;

/**
 * Created by christoph on 27.10.15.
 */
public interface ExpressionHandler {
    Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException;

    Object evaluateExpression(String expression, PugModel model) throws ExpressionException;

    String evaluateStringExpression(String expression, PugModel model) throws ExpressionException;

    void assertExpression(String expression) throws ExpressionException;

    void setCache(boolean cache);

    void clearCache();
}

