package de.neuland.jade4j.lexer.token;

import org.junit.Test;

public class ForTagTest extends TokenTest {

    @Test
    public void shouldRecognizeForToken() throws Exception {
        lexer = initLexer("for_each.jade");
//        token(ForTag.class, "for");
        assert(true);
    }
}
