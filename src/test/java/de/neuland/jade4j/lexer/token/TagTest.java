package de.neuland.jade4j.lexer.token;

import org.junit.Test;

public class TagTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
        lexer = initLexer("tag.jade");
		assertToken("div");
		assertToken(":");
		assertToken("p");
		assertToken(":");
		assertToken("strong");
		assertToken("indent");
		assertToken("div");
		assertToken(":");
		assertToken("strong");
		assertToken("indent");
		assertToken("p");
		assertToken("outdent");
		assertToken("eos");
	}
}
