package de.neuland.jade4j.lexer.token;


import org.junit.Test;


public class TextTest extends TokenTest {


	@Test
	public void shouldReturnIndentTag() throws Exception {
        lexer = initLexer("text.jade");
		assertToken(Tag.class,     "p");
		assertToken(Indent.class,  "indent");
		assertToken(Text.class,    "Hello World");
		assertToken(Newline.class, "newline");
		assertToken(Text.class,    "Hello Berlin");
		assertToken(Newline.class, "newline");
		assertToken(Text.class,    "Hello Tokyo");
		assertToken(Outdent.class, "outdent");

		assertToken(Tag.class,     "div");
		assertToken(Indent.class,  "indent");
		assertToken(Tag.class,     "p");
		assertToken(Indent.class,  "indent");
		assertToken(Text.class,    "Hello World");
		assertToken(Newline.class, "newline");
		assertToken(Text.class,    "Hello Berlin");
		assertToken(Newline.class, "newline");
		assertToken(Text.class,    "Hello Tokyo");
		assertToken(Outdent.class, "outdent");

		
		assertToken(Tag.class,     "div");
		assertToken(Indent.class,  "indent");
		assertToken(Tag.class,     "div");
		assertToken(Indent.class,  "indent");
		assertToken(Tag.class,     "p");
		assertToken(Indent.class,  "indent");
		assertToken(Text.class,    "Hello World");
		assertToken(Outdent.class, "outdent");
		assertToken(Tag.class,     "p");
		assertToken(Indent.class,  "indent");
		assertToken(Text.class,    "Hello Berlin");
		assertToken(Newline.class, "newline");
		assertToken(Text.class,    "Hello Tokyo");
		assertToken(Newline.class, "newline");
		assertToken(Outdent.class, "outdent");
		assertToken(Eos.class,     "eos");
	}
}
