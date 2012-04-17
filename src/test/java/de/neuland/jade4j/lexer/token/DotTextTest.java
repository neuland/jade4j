package de.neuland.jade4j.lexer.token;


import org.junit.Test;

public class DotTextTest extends TokenTest {

	@Test
    public void shouldScanTagsWithAttributes() throws Exception {
        lexer = initLexer("large_body_text_without_pipes.jade");
        assertToken(Tag.class,        "p");
        assertToken(Dot.class,        ".");
        assertToken(Indent.class,     "indent");
        assertToken(Tag.class,        "Hello");
        assertToken(Text.class,       "World!");
        assertToken(Newline.class,    "newline");
        assertToken(Tag.class,        "Here");
        assertToken(Text.class,       "comes the Message!");
        assertToken(Outdent.class,    "outdent");
        assertToken(Eos.class,        "eos");
    }
}
