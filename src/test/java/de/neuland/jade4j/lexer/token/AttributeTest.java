package de.neuland.jade4j.lexer.token;


import org.junit.Test;

public class AttributeTest extends TokenTest {

    @Test
	public void shouldReturnAnAttributesToken() throws Exception {
        lexer = initLexer("attribute_1.jade");
        assertToken(Tag.class,        "img");
        assertToken(AttributeList.class, "src='http://example.com/spacer.gif', title='u cant c me'");
	}
	
	@Test
    public void shouldScanTagsWithAttributes() throws Exception {
	    lexer = initLexer("attribute_2.jade");
	    assertToken(2, Newline.class,    "newline");
	    assertToken(3, Newline.class,    "newline");
        assertToken(3, Tag.class,        "p");
        assertToken(3, CssId.class,      "red");
        assertToken(3, AttributeList.class,  "title='my special title', alt='some alt text'");
        assertToken(6, Indent.class,     "indent");
        assertToken(6, Text.class,       "Hello World");
        assertToken(8, Newline.class,    "newline");
        assertToken(8, Text.class,       "Hello Berlin");
        assertToken(9, Newline.class,    "newline");
        assertToken(9, Text.class,       "Hello Tokyo");
        assertToken(12, Outdent.class,    "outdent");
        assertToken(12, Tag.class,        "div");
        assertToken(12, CssId.class,      "content");
        assertToken(12, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(14, Indent.class,     "indent");
        assertToken(14, Tag.class,        "p");
        assertToken(14, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(15, Indent.class,     "indent");
        assertToken(15, Text.class,       "Hello World");
        assertToken(16, Newline.class,    "newline");
        assertToken(16, Text.class,       "Hello Berlin");
        assertToken(17, Newline.class,    "newline");
        assertToken(17, Text.class,       "Hello Tokyo");
        assertToken(20, Outdent.class,    "outdent");
        assertToken(20, Tag.class,        "div");
        assertToken(20, CssId.class,      "footer");
        assertToken(20, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(23, Indent.class,     "indent");
        assertToken(23, Tag.class,        "div");
        assertToken(23, CssId.class,      "left");
        assertToken(23, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(24, Indent.class,     "indent");
        assertToken(24, Tag.class,        "p");
        assertToken(24, CssId.class,      "red");
        assertToken(24, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(25, Indent.class,     "indent");
        assertToken(25, Text.class,       "Hello World");
        assertToken(27, Outdent.class,    "outdent");
        assertToken(27, Tag.class,        "p");
        assertToken(27, CssId.class,      "green");
        assertToken(27, AttributeList.class,  "title='test title', alt = 'alt text'");
        assertToken(28, Indent.class,     "indent");
        assertToken(28, Text.class,       "Hello Berlin");
        assertToken(29, Newline.class,    "newline");
        assertToken(29, Text.class,       "Hello Tokyo");
        assertToken(30, Outdent.class,    "outdent");
        assertToken(30, Eos.class,        "eos");
    }

    @Test
    public void testattrinterpolation() throws Exception {
        lexer = initLexer("../originalTests20150515/cases/attrs.interpolation.jade");
        assertToken(1,Expression.class,"var id = 5");
        assertToken(2,Newline.class,"newline");
        assertToken(2,Tag.class,"a");
        assertToken(2,AttributeList.class,"attrs");
        assertToken(3,Newline.class,"newline");
        assertToken(3,Tag.class,"foo");
        assertToken(3,AttributeList.class,"attrs");
        assertToken(3, Eos.class,        "eos");

    }
}
