package de.neuland.pug4j;

import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.neuland.pug4j.model.PugModel.NON_LOCAL_VARS;

public abstract class AbstractExpressionHandler implements ExpressionHandler {

    public static Pattern isAssignment = Pattern.compile("^([a-zA-Z0-9-_]+)[\\s]?={1}[\\s]?[^=]+$");

    protected void saveNonLocalVarAssignmentInModel(String expression, PugModel model) {
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
}
