package de.neuland.jade4j.lexer.token;


import org.junit.Test;

public class DotTextTest extends TokenTest {

	@Test
    public void shouldScanTagsWithAttributes() throws Exception {
        lexer = initLexer("large_body_text_without_pipes.jade");
//        assertToken(1,Tag.class,        "p");
//        assertToken(1,Dot.class,        ".");
//        assertToken(4,PipelessText.class,     "pipeless-text");
//        assertToken(4,Eos.class,        "eos");
        assertToken(Tag.class,        "p");
        assertToken(Dot.class,        ".");
        assertToken(PipelessText.class,     "pipeless-text");
        assertToken(Eos.class,        "eos");
    }
}
