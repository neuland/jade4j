package de.neuland.jade4j.lexer.token;

import org.junit.Test;


public class IdTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
	    lexer = initLexer("tag_with_id.jade");
		assertToken(1, Tag.class, "p");
		assertToken(1, CssId.class, "red");
		assertToken(3, Indent.class, "indent");
		assertToken(3, Text.class, "Hello World");
		assertToken(5, Newline.class, "newline");
		assertToken(5, Text.class, "Hello Berlin");
		assertToken(7, Newline.class, "newline");
		assertToken(7, Text.class, "Hello Tokyo");
		assertToken(8, Outdent.class, "outdent");

		assertToken(8, Tag.class, "div");
		assertToken(8, CssId.class, "content");
		assertToken(11, Indent.class, "indent");
		assertToken("p");
		assertToken("indent");
		assertToken("Hello World");
		assertToken("newline");
		assertToken("Hello Berlin");
		assertToken("newline");
		assertToken("Hello Tokyo");
		assertToken("outdent");

		
		assertToken("div");
		assertToken("footer");
		assertToken("indent");
		assertToken("div");
		assertToken("left");
		assertToken("indent");
		assertToken("p");
		assertToken("red");
		assertToken("indent");
		assertToken("Hello World");
		assertToken("outdent");
		assertToken("p");
		assertToken("green");
		assertToken("indent");
		assertToken("Hello Berlin");
		assertToken("newline");
		assertToken("Hello Tokyo");
		assertToken("newline");
		assertToken("outdent");
		assertToken("eos");
	}
}
