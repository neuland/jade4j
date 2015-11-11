package de.neuland.jade4j.lexer.token;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class PipelessTextTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
	    lexer = initLexer("pipeless_text.jade");
		assertToken("p");
		assertToken("Hallo Welt");
		assertToken("newline");
		assertToken("p");
		assertToken(" Hallo Jade");
		assertToken("newline");
		assertToken("p");
		assertToken("  Hallo Jade");
//		assertToken("newline"); // Jade File Reader Puts an /n at the end of file.
		assertToken("eos");
	}
}
