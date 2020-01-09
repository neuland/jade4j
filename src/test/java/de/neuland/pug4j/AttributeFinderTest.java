package de.neuland.pug4j;

import de.neuland.pug4j.lexer.Scanner;
import de.neuland.pug4j.lexer.token.Attribute;
import de.neuland.pug4j.lexer.token.AttributeList;
import de.neuland.pug4j.parser.node.ExpressionString;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.apache.commons.jexl3.parser.ParserConstants.eq;
import static org.junit.Assert.assertThat;

public class AttributeFinderTest {

    private AttributeFinder attributeFinder;

    @Before
    public void setUp() throws Exception {

    }

    private List<Attribute> findAttributes(String attributeString) {
        attributeFinder = new AttributeFinder(new Scanner(new StringReader(attributeString)), 1);
        AttributeList attributeList = attributeFinder.find();
        return attributeList.getAttributes();
    }

    private void assertExpressionStringWithValue(Object expression, String expectedValue) {
        assertThat(expression, IsInstanceOf.instanceOf(ExpressionString.class));
        ExpressionString value = (ExpressionString) expression;
        assertThat(value.getValue(), IsEqual.equalTo(expectedValue));
    }

    @Test
    public void testAttributesValueWithQuote() {
        List<Attribute> attributes = findAttributes("(hello='worl\"d', data-a=\"b\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("worl\"d"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("data-a"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("b"));
    }

    @Test
    public void testAttributesValueWithSingleQuote() {
        List<Attribute> attributes = findAttributes("(hello=\"worl'd\", a=a.b)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("worl'd"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("a"));
        assertExpressionStringWithValue(attributes.get(1).getValue(), "a.b");
    }

    @Test
    public void testAttributeWithBooleanTrue() {
        List<Attribute> attributes = findAttributes("(checked=true)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("checked"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "true");
    }

    @Test
    public void testAttributeWithQuotedBooleanTrue() {
        List<Attribute> attributes = findAttributes("(checked=\"true\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("checked"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("true"));
    }

    @Test
    public void testAttributeBooleanFalse() {
        List<Attribute> attributes = findAttributes("(checked=false)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("checked"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "false");
    }

    @Test
    public void testAttributeWithQuotedBooleanFalse() {
        List<Attribute> attributes = findAttributes("(checked=\"false\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("checked"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("false"));
    }

    @Test
    public void testAttributeVariable() {
        List<Attribute> attributes = findAttributes("(value=hello)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("value"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "hello");
    }

    @Test
    public void testAttributeWithInterpolation() {
        List<Attribute> attributes = findAttributes("(value=\"#{hello}\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("value"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("#{hello}"));
    }

    @Test
    public void testClassAttribute() {
        List<Attribute> attributes = findAttributes("(class=\"welt\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("welt"));
    }

    @Test
    public void testClassAttributeWithVariable() {
        List<Attribute> attributes = findAttributes("(class=hello)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("class"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "hello");
    }

    @Test
    public void testClassAttributeWithExpression() {
        List<Attribute> attributes = findAttributes("(value=\"hello \" + hello)");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("value"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello \"+hello");
    }

    @Test
    public void testClassAttributeWithExpression2() {
        List<Attribute> attributes = findAttributes("(value=\"hello \" + hello + \"!\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("value"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello \"+hello+\"!\"");
    }

    @Test
    public void testAttributesValueWithQuoteWithoutSeparator() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\" world=\"world\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("world"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesValueWithQuoteAndTabsWithoutSeparator() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\" \tworld=\"world\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("world"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionAndClassWithoutSeparator() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\"+\"world\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello\"+\"world\"");
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionSpacesAndClassWithoutSeparator() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\" + \"world\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello\"+\"world\"");
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithQuoteAndExpressionTabsAndClassWithoutSeparator() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\"\t+\t\"world\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello\"+\"world\"");
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesValueWithNewline() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\"\n world=\"world\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("world"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("world"));
    }

    @Test
    public void testAttributesWithExpressionAndNewline() {
        List<Attribute> attributes = findAttributes("(hello=\"hello\"\n    +\t\"world\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertExpressionStringWithValue(attributes.get(0).getValue(), "\"hello\"+\"world\"");
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesWithQuotedKey() {
        List<Attribute> attributes = findAttributes("(\"hello\"=\"hello\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo("foo"));
    }

    @Test
    public void testAttributesWithQuotedKeyAndQuotedKeyWithoutValue() {
        List<Attribute> attributes = findAttributes("(\"hello\"=\"hello\" \"world\" class=\"foo\")");
        assertThat(attributes.get(0).getName(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(0).getValue(), IsEqual.equalTo("hello"));
        assertThat(attributes.get(1).getName(), IsEqual.equalTo("world"));
        assertThat(attributes.get(1).getValue(), IsEqual.equalTo(true));
        assertThat(attributes.get(2).getName(), IsEqual.equalTo("class"));
        assertThat(attributes.get(2).getValue(), IsEqual.equalTo("foo"));
    }

}