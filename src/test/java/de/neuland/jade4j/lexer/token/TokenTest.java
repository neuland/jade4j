package de.neuland.jade4j.lexer.token;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.template.FileTemplateLoader;

public class TokenTest {

    protected Lexer lexer;
    
    protected Lexer initLexer(String fileName) {
        FileTemplateLoader loader;
        try {
            loader = new FileTemplateLoader(TestFileHelper.getLexerResourcePath(""), "UTF-8");
            return new Lexer(fileName, loader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    protected void assertToken(int expectedLineNumber, Class expectedClazz, String expectedValue) {
        final Token token = lexer.next();
        assertThat(token.getValue(), is(expectedValue));
        assertThat(token.getLineNumber(), is(expectedLineNumber));
        assertTrue(token.getClass() == expectedClazz);
    }

    @SuppressWarnings("rawtypes")
    protected void assertToken(Class clazz, String value) {
        final Token token = lexer.next();
        assertThat(token.getValue(), is(value));
        assertTrue("Expected: " + clazz + " got " + token.getClass(), token.getClass() == clazz);      
    }

    protected void assertToken(String value) {
        final Token token = lexer.next();
        assertThat(token.getValue(), is(value));
    }
}
