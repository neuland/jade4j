package de.neuland.pug4j.expression;

import de.neuland.pug4j.model.PugModel;
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
        PugModel pugModel = new PugModel(new HashMap<String, Object>());
        Boolean aBoolean = jexlExpressionHandler.evaluateBooleanExpression("1<5", pugModel);
        assertTrue(aBoolean);
    }

    @Test
    public void evaluateExpression() throws Exception {
        PugModel pugModel = new PugModel(new HashMap<String, Object>());
        Object object = jexlExpressionHandler.evaluateExpression("1<5", pugModel);
        assertTrue((Boolean) object);
    }

    @Test
    public void assertExpression() throws Exception {
    }

    @Test
    public void evaluateStringExpression() throws Exception {
    }

}