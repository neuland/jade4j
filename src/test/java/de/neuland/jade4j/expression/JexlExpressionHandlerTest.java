package de.neuland.jade4j.expression;

import de.neuland.jade4j.model.JadeModel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class JexlExpressionHandlerTest {

    private JexlExpressionHandler jexlExpressionHandler;

    @Before
    public void setUp() throws Exception {
        jexlExpressionHandler = new JexlExpressionHandler();
    }

    @Test
    public void evaluateBooleanExpression() throws Exception {
        JadeModel jadeModel = new JadeModel(new HashMap<String, Object>());
        Boolean aBoolean = jexlExpressionHandler.evaluateBooleanExpression("1<5", jadeModel);
        assertTrue(aBoolean);
    }

    @Test
    public void evaluateExpression() throws Exception {
        JadeModel jadeModel = new JadeModel(new HashMap<String, Object>());
        Object object = jexlExpressionHandler.evaluateExpression("1<5", jadeModel);
        assertTrue((Boolean) object);
    }

    @Test
    public void assertExpression() throws Exception {
    }

    @Test
    public void evaluateStringExpression() throws Exception {
    }

}