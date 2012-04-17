package de.neuland.jade4j.lexer.token;

import org.junit.Before;
import org.junit.Test;

public class CommentTest extends TokenTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void shouldScanBufferedComments() throws Exception {
        lexer = initLexer("buffered_comment.jade");
        assertToken(1, Comment.class, "this is my first comment");
        assertToken(2, Newline.class,         "newline");
	    assertToken(2, Tag.class,             "div");
	    assertToken(3, Indent.class,          "indent");
	    assertToken(3, Comment.class, "this is a comment");
	    assertToken(4, Newline.class,         "newline");
	    assertToken(4, Tag.class,             "div");
	    assertToken(5, Indent.class,          "indent");
	    assertToken(5, Comment.class, "so is this");
        assertToken(6, Newline.class,         "newline");
        assertToken(6, Tag.class,             "p");
        assertToken(7, Indent.class,          "indent");
        assertToken(7, Text.class,            "my text");
        assertToken(8, Outdent.class,         "outdent");
        assertToken(8, Comment.class, "another comment comes here");
	}
	
    @Test
    public void shouldScanUnbufferedComments() throws Exception {
        lexer = initLexer("unbuffered_comment.jade");
        assertToken(1, Comment.class,           "this is my first comment");
        assertToken(2, Newline.class,           "newline");
        assertToken(2, Tag.class,               "div");
        assertToken(3, Indent.class,            "indent");
        assertToken(3, Comment.class,           "this is an unbuffered comment");
        assertToken(4, Newline.class,           "newline");
        assertToken(4, Tag.class,               "div");
        assertToken(5, Indent.class,            "indent");
        assertToken(5, Comment.class,           "so is this");
        assertToken(6, Newline.class,           "newline");
        assertToken(6, Tag.class,               "p");
        assertToken(7, Indent.class,            "indent");
        assertToken(7, Text.class,              "my text");
        assertToken(8, Outdent.class,           "outdent");
        assertToken(8, Comment.class,           "another comment comes here");
    }
    
    @Test
    public void shouldScanBlockComment() throws Exception {
        lexer = initLexer("buffered_block_comment.jade");
        assertToken(1, Comment.class,         "this is my first comment");
        assertToken(2, Newline.class,         "newline");
        assertToken(2, Tag.class,             "div");
        assertToken(3, Indent.class,          "indent");
        assertToken(3, Comment.class,         "");
        assertToken(4, Indent.class,          "indent");
        assertToken(4, Tag.class,             "div");
        assertToken(5, Indent.class,          "indent");
        assertToken(5, Text.class,            "so is this");
        assertToken(6, Newline.class,         "newline");
        assertToken(6, Tag.class,             "p");
        assertToken(7, Indent.class,          "indent");
        assertToken(7, Text.class,            "my text");
        assertToken(8, Outdent.class,         "outdent");
        assertToken(8, Comment.class,         "another comment comes here");
    }    
}
