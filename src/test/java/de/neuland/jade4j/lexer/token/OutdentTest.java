package de.neuland.jade4j.lexer.token;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class OutdentTest extends TokenTest {

	@Test
	public void shouldReturnCssClassAndCssIdTags() throws Exception {
        lexer = initLexer("outdent1.jade");
		assertToken(1, Tag.class,      "div");
		assertToken(1, CssClass.class, "footer");
		assertToken(2, Indent.class,   "indent");
		assertToken(2, Tag.class,      "div");
		assertToken(2, CssId.class,    "leftdiv");
		assertToken(2, CssClass.class, "left");
		assertToken(2, CssClass.class, "happy");
        assertToken(3, Indent.class,   "indent");
		assertToken(3, Tag.class,      "p");
		assertToken(3, CssClass.class, "red");
		assertToken(4, Indent.class,   "indent");
		assertToken(4, Text.class,     "Hello World");
		assertToken(5, Outdent.class,  "outdent");
		assertToken(5, Tag.class,      "p");
		assertToken(5, CssClass.class, "green");
		assertToken(6, Indent.class,   "indent");
		assertToken(6, Text.class,     "Hello Berlin");
		assertToken(7, Newline.class,  "newline");
		assertToken(7, Text.class,     "Hello Tokyo");
		assertToken(8, Newline.class,  "newline");
		assertToken(9, Outdent.class,  "outdent");
		assertToken(9, Eos.class,      "eos");
	}
	
    @SuppressWarnings("rawtypes")
    protected void assertToken(int expectedLineNumber, Class expectedClazz, String expectedValue) {
        final Token token = lexer.next();
        assertThat(token.getValue(), is(expectedValue));
        assertThat(token.getLineNumber(), is(expectedLineNumber));
//        assertTrue(token.getClass() == expectedClazz);
    }
}
