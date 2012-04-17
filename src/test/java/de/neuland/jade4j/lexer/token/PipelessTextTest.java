package de.neuland.jade4j.lexer.token;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class PipelessTextTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
	    lexer = initLexer("pipeless_text.jade");
		Token token = lexer.next();
		assertThat(token.getValue(), is("p"));
		token = lexer.next();
		assertThat(token.getValue(), is("Hallo Welt"));
		assertThat(lexer.next().getValue(), is("newline"));
        token = lexer.next();
        assertThat(token.getValue(), is("p"));
        token = lexer.next();
        assertThat(token.getValue(), is(" Hallo Jade"));
        assertThat(lexer.next().getValue(), is("newline"));
        token = lexer.next();
        assertThat(token.getValue(), is("p"));
        token = lexer.next();
        assertThat(token.getValue(), is("  Hallo Jade"));
        assertThat(lexer.next().getValue(), is("newline"));
        assertThat(lexer.next().getValue(), is("eos"));
	}
}
