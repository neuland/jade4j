package de.neuland.jade4j.lexer.token;


import org.junit.Test;

public class AttributeTest extends TokenTest {

    @Test
	public void shouldReturnAnAttributesToken() throws Exception {
        lexer = initLexer("attribute_1.jade");
        assertToken(Tag.class,        "img");
        assertToken(Attribute.class, "src='http://example.com/spacer.gif', title='u cant c me'");
	}
	
	@Test
    public void shouldScanTagsWithAttributes() throws Exception {
	    lexer = initLexer("attribute_2.jade");
        assertToken(Tag.class,        "p");
        assertToken(CssId.class,      "red");
        assertToken(Attribute.class,  "title='my special title', alt='some alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Text.class,       "Hello World");
        assertToken(Newline.class,    "newline");
        assertToken(Text.class,       "Hello Berlin");
        assertToken(Newline.class,    "newline");
        assertToken(Text.class,       "Hello Tokyo");
        assertToken(Outdent.class,    "outdent");
        assertToken(Tag.class,        "div");
        assertToken(CssId.class,      "content");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Tag.class,        "p");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Text.class,       "Hello World");
        assertToken(Newline.class,    "newline");
        assertToken(Text.class,       "Hello Berlin");
        assertToken(Newline.class,    "newline");
        assertToken(Text.class,       "Hello Tokyo");
        assertToken(Outdent.class,    "outdent");
        assertToken(Tag.class,        "div");
        assertToken(CssId.class,      "footer");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Tag.class,        "div");
        assertToken(CssId.class,      "left");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Tag.class,        "p");
        assertToken(CssId.class,      "red");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Text.class,       "Hello World");
        assertToken(Outdent.class,    "outdent");
        assertToken(Tag.class,        "p");
        assertToken(CssId.class,      "green");
        assertToken(Attribute.class,  "title='test title', alt = 'alt text'");
        assertToken(Indent.class,     "indent");
        assertToken(Text.class,       "Hello Berlin");
        assertToken(Newline.class,    "newline");
        assertToken(Text.class,       "Hello Tokyo");
        assertToken(Outdent.class,    "outdent");
        assertToken(Eos.class,        "eos");
    }
}
