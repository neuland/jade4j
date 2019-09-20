package de.neuland.pug4j.expression;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GraalJsExpressionHandlerTest {

    private GraalJsExpressionHandler graalJsExpressionHandler;
    private PugModel pugModel;

    @Before
    public void setUp() throws Exception {
        graalJsExpressionHandler = new GraalJsExpressionHandler();
        pugModel = new PugModel(new HashMap<>());
    }

    @Test
    public void testArrayList() throws ExpressionException {
        graalJsExpressionHandler.evaluateExpression("var list = [1,2,3]", pugModel);
        List list = (List) pugModel.get("list");
        assertEquals(1,list.get(0));
    }

    @Test
    public void testNull() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var list;", pugModel);
        Object list = pugModel.get("list");
        assertNull(list);
    }

    @Test
    public void testMap() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var map = {'foo':'bar'}", pugModel);
        Map map = (Map) pugModel.get("map");
        assertEquals("bar",map.get("foo"));

    }
    @Test
    public void testMapMulti() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var map = {" +
                "  'text': 'text'," +
                "  'image': 'image.jpg'," +
                "  'button' : {" +
                "  'text': 'textbutton'" +
                "  }," +
                "  \"list\": [1,2,3,4]" +
                "}", pugModel);
        Map map = (Map) pugModel.get("map");
        assertEquals("textbutton",((Map)map.get("button")).get("text"));

    }
    @Test
    public void testReturn() throws ExpressionException  {
        Object value = graalJsExpressionHandler.evaluateExpression("{" +
                "  'text': 'text'," +
                "  'image': 'image.jpg'," +
                "  'button' : {" +
                "  'text': 'textbutton'" +
                "  }," +
                "  \"list\": [1,2,3,4]" +
                "}", pugModel);

        assertEquals("textbutton",((Map)((Map)value).get("button")).get("text"));

    }

    @Test
    public void testInt() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var count = 5", pugModel);
        int count = (int) pugModel.get("count");
        assertEquals(5,count);

    }
    @Test
    public void testDouble() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var price = 5.50", pugModel);
        double price = (double) pugModel.get("price");
        assertEquals(5.5,price,0.0001);
    }
    @Test
    public void testString() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var moin = 'Hallo Welt!'", pugModel);
        String moin = (String) pugModel.get("moin");
        assertEquals("Hallo Welt!",moin);
    }
    @Test
    public void testBoolean() throws ExpressionException  {
        graalJsExpressionHandler.evaluateExpression("var what = true", pugModel);
        boolean what = (boolean) pugModel.get("what");
        assertTrue(what);
    }

}