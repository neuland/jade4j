package de.neuland.pug4j;

import de.neuland.pug4j.lexer.Lexer;
import de.neuland.pug4j.lexer.token.Attribute;
import de.neuland.pug4j.lexer.token.Token;
import de.neuland.pug4j.parser.node.ExpressionString;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AttributeLexerTest {

    private Lexer lexer;

    @Before
    public void setUp() throws Exception {

    }

    private List<Token> findAttributes(String attributeString) {
        PugConfiguration pugConfiguration = new PugConfiguration();
        try {
            lexer = new Lexer(attributeString,"test.pug",pugConfiguration.getTemplateLoader(),pugConfiguration.getExpressionHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<Token> tokens = lexer.getTokens();
        return tokens;
    }

    private void assertExpressionStringWithValue(Object expression, String expectedValue) {
        assertThat(expression, IsInstanceOf.instanceOf(ExpressionString.class));
        ExpressionString value = (ExpressionString) expression;
        assertThat(value.getValue(), IsEqual.equalTo(expectedValue));
    }

    @Test
    public void testAttributesValueWithQuote() {
        List<Token> tokens = findAttributes("(hello='worl\"d', data-a=\"b\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1),  IsEqual.equalTo("worl\"d"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("data-a"));
        assertAttributeTokenValue(tokens.get(2),  IsEqual.equalTo("b"));
    }

    @Test
    public void testAttributesValueWithSingleQuote() {
        List<Token> tokens = findAttributes("(hello=\"worl'd\", a=a.b)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1),  IsEqual.equalTo("worl'd"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("a"));
        assertAttributeTokenExpressionValue(tokens.get(2), IsEqual.equalTo( "a.b"));
    }

    @Test
    public void testAttributeWithBooleanTrue() {
        List<Token> tokens = findAttributes("(checked=true)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("checked"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "true"));
    }

    @Test
    public void testAttributeWithQuotedBooleanTrue() {
        List<Token> tokens = findAttributes("(checked=\"true\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("checked"));
        assertAttributeTokenValue(tokens.get(1),  IsEqual.equalTo("true"));
    }

    @Test
    public void testAttributeBooleanFalse() {
        List<Token> tokens = findAttributes("(checked=false)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("checked"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "false"));
    }

    @Test
    public void testAttributeWithQuotedBooleanFalse() {
        List<Token> tokens = findAttributes("(checked=\"false\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("checked"));
        assertAttributeTokenValue(tokens.get(1),  IsEqual.equalTo("false"));
    }

    @Test
    public void testAttributeVariable() {
        List<Token> tokens = findAttributes("(value=hello)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("value"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "hello"));
    }

    @Test
    public void testAttributeWithInterpolation() {
        List<Token> tokens = findAttributes("(value=\"#{hello}\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("value"));
        assertAttributeTokenValue(tokens.get(1),  IsEqual.equalTo("#{hello}"));
    }

    @Test
    public void testClassAttribute() {
        List<Token> tokens = findAttributes("(class=\"welt\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("welt"));
    }

    @Test
    public void testClassAttributeWithVariable() {
        List<Token> tokens = findAttributes("(class=hello)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "hello"));
    }

    @Test
    public void testClassAttributeWithExpression() {
        List<Token> tokens = findAttributes("(value=\"hello \" + hello)");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("value"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo("\"hello \" + hello"));
    }

    @Test
    public void testClassAttributeWithExpression2() {
        List<Token> tokens = findAttributes("(value=\"hello \" + hello + \"!\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("value"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "\"hello \" + hello + \"!\""));
    }

    @Test
    public void testAttributesValueWithQuoteWithoutSeparator() {
        List<Token> tokens = findAttributes("(hello=\"hello\" world=\"world\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("hello"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("world"));
        assertAttributeTokenValue(tokens.get(2),IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesValueWithQuoteAndTabsWithoutSeparator() {
        List<Token> tokens = findAttributes("(hello=\"hello\" \tworld=\"world\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("hello"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("world"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionAndClassWithoutSeparator() {
        List<Token> attributes = findAttributes("(hello=\"hello\"+\"world\" class=\"foo\")");
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenExpressionValue(attributes.get(1), IsEqual.equalTo("\"hello\"+\"world\""));
        assertThat(attributes.get(2).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(attributes.get(2), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionSpacesAndClassWithoutSeparator() {
        List<Token> tokens = findAttributes("(hello=\"hello\" + \"world\" class=\"foo\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "\"hello\" + \"world\""));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionTabsAndClassWithoutSeparator() {
        List<Token> tokens = findAttributes("(hello=\"hello\"\t+\t\"world\" class=\"foo\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo( "\"hello\"\t+\t\"world\""));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithNewline() {
        List<Token> tokens = findAttributes("(hello=\"hello\"\n world=\"world\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("hello"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("world"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesWithExpressionAndNewline() {
        List<Token> tokens = findAttributes("(hello=\"hello\"\n    +\t\"world\" class=\"foo\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenExpressionValue(tokens.get(1), IsEqual.equalTo("\"hello\"\n    +\t\"world\""));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesWithQuotedKey() {
        List<Token> tokens = findAttributes("(\"hello\"=\"hello\" class=\"foo\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("hello"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesWithQuotedKeyAndQuotedKeyWithoutValue() {
        List<Token> tokens = findAttributes("(\"hello\"=\"hello\" \"world\" class=\"foo\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("hello"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("hello"));
        assertThat(tokens.get(2).getName(), IsEqual.equalTo("world"));
        assertAttributeTokenValue(tokens.get(2), IsEqual.equalTo(true));
        assertThat(tokens.get(3).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(3), IsEqual.equalTo("foo"));
    }

    private void assertAttributeTokenValue(Token actualToken, Matcher<Object> expected) {
        if(actualToken instanceof Attribute)
            assertThat(((Attribute) actualToken).getAttributeValue(), expected);
        else
            fail("Not an attribute!");
    }
    private void assertAttributeTokenExpressionValue(Token actualToken, Matcher<Object> expected) {

        if(actualToken instanceof Attribute) {
            Object expression = ((Attribute) actualToken).getAttributeValue();
            assertThat(expression, IsInstanceOf.instanceOf(ExpressionString.class));
            ExpressionString value = (ExpressionString) expression;
            assertThat(value.getValue(), expected);
        } else
            fail("Not an attribute!");
    }

    @Test
    public void testAttributesWithClass() {
        List<Token> tokens = findAttributes("(class=\"ap-input--ok2\")");
        assertThat(tokens.get(1).getName(), IsEqual.equalTo("class"));
        assertAttributeTokenValue(tokens.get(1), IsEqual.equalTo("ap-input--ok2"));
    }

//    @Test
//    public void testAttributesTernaryExpression() {
//        List<Attribute> attributes = findAttributes("(class=error ? \"ap-input--error1\" : \"\")");
//        assertThat(attributes.get(0).getName(), IsEqual.equalTo("class"));
//        assertExpressionStringWithValue(attributes.get(0).getAttributeValue(), "error ? \"ap-input--error1\" : \"\")");
//    }

}