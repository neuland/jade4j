package de.neuland.pug4j.template;

import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.parser.Parser;
import de.neuland.pug4j.parser.node.Node;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;

import static org.junit.Assert.*;

public class PugConfigurationTest {

    protected Parser parser;
    protected Node root;

    private final static String BASE_PATH = TestFileHelper.getParserResourcePath("");
    private final static String TEMPLATE_PATH = TestFileHelper.getParserResourcePath("assignment.jade");

    @Test
    public void testGetTemplate() throws IOException {
        PugConfiguration config = new PugConfiguration();
        PugTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template);
    }

    @Test
    public void testGetTemplateWithBasepath() throws IOException {
        PugConfiguration config = new PugConfiguration();
        config.setTemplateLoader(new FileTemplateLoader(TestFileHelper.getRootResourcePath() + "/parser/", "UTF-8","jade"));
        PugTemplate template = config.getTemplate("assignment");
        assertNotNull(template);
    }


    @Test
    public void testCache() throws IOException {
        PugConfiguration config = new PugConfiguration();
        config.setCaching(true);
        PugTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template);
        PugTemplate template2 = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template2);
        assertSame(template, template2);
    }

    @Test
	public void testExceptionOnUnknowwTemplate() throws IOException {
    	PugConfiguration config = new PugConfiguration();
    	PugTemplate template = null;
    	try {
    		template = config.getTemplate("UNKNOWN_PATH");
    		fail("Did expect TemplatException!");
    	} catch (IOException | UncheckedIOException ignore) {
    		
    	}
    	assertNull(template);
    }

    @Test
    public void testPrettyPrint() throws IOException {
        PugConfiguration config = new PugConfiguration();
        config.setPrettyPrint(true);
        PugTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertTrue(template.isPrettyPrint());
    }

    @Test
    public void testRootNode() throws IOException {
        PugConfiguration config = new PugConfiguration();
        PugTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template.getRootNode());
    }

}
