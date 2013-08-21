package de.neuland.jade4j.lexer.token;

import org.junit.Test;

public class IndentTest extends TokenTest {

    @Test
    public void shouldReturnIndentTag() throws Exception {
        lexer = initLexer("indent_1.jade");
        assertToken("head");
        assertToken("newline");
        assertToken("body");
        assertToken("newline");
        assertToken("div");
        assertToken("indent");
        assertToken("div");
        assertToken("newline");
        assertToken("table");
        assertToken("outdent");
        assertToken("tbody");
        assertToken("newline");
        assertToken("tr");
        assertToken("newline");
        assertToken("td");
        assertToken("newline");
        assertToken("eos");
    }

    @Test
    public void shouldCorrectlyIndent() throws Exception {
        lexer = initLexer("indent_2.jade");
        assertToken("head");
        assertToken("newline");
        assertToken("body");
        assertToken("indent");
        assertToken("div");
        assertToken("newline");
        assertToken("div");
        assertToken(":");
        assertToken("p");
        assertToken(":");
        assertToken("span");
        assertToken("indent");
        assertToken("ul");
        assertToken(":");
        assertToken("li");
        assertToken("indent");
        assertToken("span");
        assertToken("outdent");
        assertToken("div");
        assertToken("newline");
        assertToken("table");
        assertToken(":");
        assertToken("tbody");
        assertToken("indent");
        assertToken("tr");
        assertToken("indent");
        assertToken("td");
        assertToken("indent");
        assertToken("div");
        assertToken("indent");
        assertToken("span");
        assertToken("outdent");
        assertToken("tr");
        assertToken("indent");
        assertToken("td");
        assertToken("outdent");
        assertToken("div");
        assertToken("outdent");
        assertToken("div");
        assertToken("outdent");
        assertToken("eos");
    }

    @Test
    public void shouldReturnAnIndentTokenIfTooManyIndentationCharacters() throws Exception {
        lexer = initLexer("indent_error_1.jade");
        assertToken("head");
        assertToken("newline");
        assertToken("body");
        assertToken("newline");
        assertToken("div");
        assertToken("indent");
        assertToken("div");
    }

    @Test
    public void shouldReturnAnIndentTokenIfNotEnoughIndentationCharacters() throws Exception {
        lexer = initLexer("indent_error_2.jade");
        assertToken("head");
        assertToken("newline");
        assertToken("body");
        assertToken("newline");
        assertToken("div");
        assertToken("indent");
        assertToken("div");
        assertToken("newline");
        assertToken("table");
        assertToken("outdent");
        assertToken("tbody");
    }

}
