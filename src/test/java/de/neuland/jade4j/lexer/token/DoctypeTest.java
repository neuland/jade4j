package de.neuland.jade4j.lexer.token;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DoctypeTest extends TokenTest {

	@Test
	public void shouldReturnIndentTag() throws Exception {
        lexer = initLexer("doctype.jade");
		Token nextToken = lexer.next();
		assertThat(nextToken.getValue(), is("strict"));
		assertTrue(nextToken.getClass() == Doctype.class);

		nextToken = lexer.next();
		assertThat(nextToken.getValue(), is("newline"));
		assertTrue(nextToken.getClass() == Newline.class);

		nextToken = lexer.next();
		assertThat(nextToken.getValue(), is("eos"));
		assertTrue(nextToken.getClass() == Eos.class);
	}
}
