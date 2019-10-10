package de.neuland.jade4j.template;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class JadeConfigurationTest {

    protected Parser parser;
    protected Node root;

    private final static String BASE_PATH = TestFileHelper.getParserResourcePath("");
    private final static String TEMPLATE_PATH = TestFileHelper.getParserResourcePath("assignment.jade");

    @Test
    public void testGetTemplate() throws IOException {
        JadeConfiguration config = new JadeConfiguration();
        JadeTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template);
    }

    @Test
    public void testGetTemplateWithBasepath() throws IOException {
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(TestFileHelper.getRootResourcePath() + "/parser/", "UTF-8");
        JadeConfiguration config = new JadeConfiguration();
        config.setTemplateLoader(fileTemplateLoader);
        JadeTemplate template = config.getTemplate("assignment");
        assertNotNull(template);
    }
    @Test
    public void testConfigurationWithNonExistingBasePath() throws FileNotFoundException {
        try {
            FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(TestFileHelper.getRootResourcePath() + "/does/not/exist", "UTF-8");
            JadeConfiguration config = new JadeConfiguration();
            config.setTemplateLoader(fileTemplateLoader);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    public void testCache() throws IOException {
        JadeConfiguration config = new JadeConfiguration();
        config.setCaching(true);
        JadeTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template);
        JadeTemplate template2 = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template2);
        assertSame(template, template2);
    }

    @Test
    public void testExceptionOnUnknowwTemplate() {
        JadeConfiguration config = new JadeConfiguration();
        JadeTemplate template = null;
        try {
            template = config.getTemplate("UNKNOWN_PATH");
            fail("Did expect TemplatException!");
        } catch (IOException ignore) {

        }
        assertNull(template);
    }

    @Test
    public void testPrettyPrint() throws IOException {
        JadeConfiguration config = new JadeConfiguration();
        config.setPrettyPrint(true);
        JadeTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertTrue(template.isPrettyPrint());
    }

    @Test
    public void testRootNode() throws IOException {
        JadeConfiguration config = new JadeConfiguration();
        JadeTemplate template = config.getTemplate(TEMPLATE_PATH);
        assertNotNull(template.getRootNode());
    }

}
