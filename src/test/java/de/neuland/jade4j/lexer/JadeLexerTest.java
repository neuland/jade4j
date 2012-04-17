package de.neuland.jade4j.lexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.lexer.token.Eos;
import de.neuland.jade4j.lexer.token.Indent;
import de.neuland.jade4j.lexer.token.Outdent;
import de.neuland.jade4j.lexer.token.Tag;
import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.template.FileTemplateLoader;


public class JadeLexerTest {

	private Lexer lexer1;
	private Lexer lexer2;

	@Before
	public void setUp() throws Exception {
        FileTemplateLoader loader1 = new FileTemplateLoader(TestFileHelper.getLexerResourcePath(""), "UTF-8");
        lexer1 = new Lexer("test_file1.jade", loader1);

        FileTemplateLoader loader2 = new FileTemplateLoader(TestFileHelper.getLexerResourcePath(""), "UTF-8");
        lexer2 = new Lexer("empty_file.jade", loader2);
	
	}
	
    @Test
    public void shouldReturnALookaheadToken() throws Exception {
        Token token = lexer1.next();
        assertThat(token.toString(), is("div"));
        assertEquals(token.getClass(), Tag.class);
        
        token = lexer1.next();
        assertThat(token.toString(), is("indent"));
        assertEquals(token.getClass(), Indent.class);

        token = lexer1.next();
        assertThat(token.toString(), is("h1"));
        assertEquals(token.getClass(), Tag.class);

        token = lexer1.next();
        assertThat(token.toString(), is("outdent"));
        assertEquals(token.getClass(), Outdent.class);

        token = lexer1.next();
        assertEquals(token.getClass(), Eos.class);
    }

	@Test
	public void shouldReturnATagToken() throws Exception {
		final Token token = lexer1.next();
		assertThat(token, notNullValue());
		assertThat(token.getValue(), is("div"));
	}

	@Test
	public void shouldReturnAnEOFTagIfEmptyFile() throws Exception {
		assertThat(lexer2.next().getValue(), is("eos"));
	}
	
}
