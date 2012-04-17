package de.neuland.jade4j.lexer.token;

import org.junit.Test;


public class IdTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
	    lexer = initLexer("tag_with_id.jade");
		assertToken("p");
		assertToken("red");
		assertToken("indent");
		assertToken("Hello World");
		assertToken("newline");
		assertToken("Hello Berlin");
		assertToken("newline");
		assertToken("Hello Tokyo");
		assertToken("outdent");

		assertToken("div");
		assertToken("content");
		assertToken("indent");
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
		assertToken("outdent");
		assertToken("eos");
	}
}
