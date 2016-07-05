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
    
    @Test
	public void testGetTemplate() throws IOException {
    	JadeConfiguration config = new JadeConfiguration();
    	JadeTemplate template = config.getTemplate(getParserResourcePath("assignment"));
    	assertNotNull(template);
    }

    @Test
    public void testCache() throws IOException {
        JadeConfiguration config = new JadeConfiguration();
        config.setCaching(true);
        JadeTemplate template = config.getTemplate(getParserResourcePath("assignment"));
        assertNotNull(template);
        JadeTemplate template2 = config.getTemplate(getParserResourcePath("assignment"));
        assertNotNull(template2);
        assertSame(template, template2);
    }

    @Test
	public void testExceptionOnUnknowwTemplate() throws IOException {
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
    	JadeTemplate template = config.getTemplate(getParserResourcePath("assignment"));
    	assertTrue(template.isPrettyPrint());
    }

    @Test
    public void testRootNode() throws IOException {
    	JadeConfiguration config = new JadeConfiguration();
    	JadeTemplate template = config.getTemplate(getParserResourcePath("assignment"));
    	assertNotNull(template.getRootNode());
    }
    
    public String getParserResourcePath(String fileName) {
    	try {
			return TestFileHelper.getRootResourcePath() + "/parser/" + fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
