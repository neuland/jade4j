package de.neuland.jade4j.lexer.token;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CssClassTest extends TokenTest {

	@Test
	public void shouldReturnCssClassAndCssIdTags() throws Exception {
        lexer = initLexer("tag_with_css_class.jade");
		assertToken(1, Tag.class, "p");
		assertToken(1, CssId.class, "firstdiv");
		assertToken(1, CssClass.class, "red");
		assertToken(1, CssClass.class, "bold");
		assertToken(2, Indent.class, "indent");
		assertToken(2, Text.class, "Hello World");
		assertToken(3, Newline.class, "newline");
		assertToken(3, Text.class, "Hello Berlin");
		assertToken(4, Newline.class, "newline");
		assertToken(4, Text.class, "Hello Tokyo");
		assertToken(5, Outdent.class, "outdent");
		assertToken(5, Tag.class, "div");
		assertToken(5, CssClass.class, "content");
		assertToken(6, Indent.class, "indent");
		assertToken(6, Tag.class, "p");
		assertToken(7, Indent.class, "indent");
		assertToken(7, Text.class, "Hello World");
		assertToken(8, Newline.class, "newline");
		assertToken(8, Text.class, "Hello Berlin");
		assertToken(9, Newline.class, "newline");
		assertToken(9, Text.class, "Hello Tokyo");
		assertToken(10, Outdent.class, "outdent");
		assertToken(10, Outdent.class, "outdent");
		assertToken(10, Tag.class, "div");
		assertToken(10, CssClass.class, "footer");
		assertToken(11, Indent.class, "indent");
		assertToken(11, Tag.class, "div");
		assertToken(11, CssId.class, "leftdiv");
		assertToken(11, CssClass.class, "left");
		assertToken(11, CssClass.class, "happy");
		assertToken(12, Indent.class, "indent");
		assertToken(12, Tag.class, "p");
		assertToken(12, CssClass.class, "red");
		assertToken(13, Indent.class, "indent");
		assertToken(13, Text.class, "Hello World");
		assertToken(14, Outdent.class, "outdent");
		assertToken(14, Tag.class, "p");
		assertToken(14, CssClass.class, "green");
		assertToken(15, Indent.class, "indent");
		assertToken(15, Text.class, "Hello Berlin");
		assertToken(16, Newline.class, "newline");
		assertToken(16, Text.class, "Hello Tokyo");
		assertToken(17, Newline.class, "newline");
		assertToken(18, Outdent.class, "outdent");
		assertToken(18, Outdent.class, "outdent");
		assertToken(18, Outdent.class, "outdent");
		assertToken(18, Eos.class, "eos");
	}

	@SuppressWarnings("rawtypes")
	protected void assertToken(int expectedLineNumber, Class expectedClazz,
			String expectedValue) {
		final Token token = lexer.advance();
		assertThat(token.getValue(), is(expectedValue));
		assertThat(token.getLineNumber(), is(expectedLineNumber));
		// assertTrue(token.getClass() == expectedClazz);
	}
}
