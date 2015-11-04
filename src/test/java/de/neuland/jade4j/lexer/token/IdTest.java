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
		assertToken(11, Tag.class, "p");
		assertToken(14, Indent.class, "indent");
		assertToken(14, Text.class, "Hello World");
		assertToken(16, Newline.class, "newline");
		assertToken(16, Text.class, "Hello Berlin");
		assertToken(18, Newline.class, "newline");
		assertToken(18, Text.class, "Hello Tokyo");
		assertToken(22, Outdent.class, "outdent");
//		assertToken(22, Outdent.class, "outdent"); //Original Jade has an outdent here !!!
		assertToken(22, Tag.class, "div");
		assertToken(22, CssId.class, "footer");
		assertToken(25, Indent.class, "indent");
		assertToken(25, Tag.class, "div");
		assertToken(25, CssId.class, "left");
		assertToken(26, Indent.class, "indent");
		assertToken(26, Tag.class, "p");
		assertToken(26, CssId.class, "red");
		assertToken(27, Indent.class, "indent");
		assertToken(27, Text.class, "Hello World");
		assertToken(28, Outdent.class, "outdent");
		assertToken(28, Tag.class, "p");
		assertToken(28, CssId.class, "green");
		assertToken(29, Indent.class, "indent");
		assertToken(29, Text.class, "Hello Berlin");
		assertToken(30, Newline.class, "newline");
		assertToken(30, Text.class, "Hello Tokyo");
		assertToken(31, Outdent.class, "outdent");
		assertToken(31, Outdent.class, "outdent");
		assertToken(31, Outdent.class, "outdent");
		assertToken(31, Eos.class, "eos");
	}
}
