package de.neuland.jade4j.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;

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
    
    @Test
    public void testLastModified() throws IOException {
    	String fileBaseName = getParserResourcePath("assignment");
    	File f = new File(fileBaseName + ".jade");
    	
    	JadeConfiguration config = new JadeConfiguration();
    	JadeTemplate template = config.getTemplate(fileBaseName);
    	
    	assertEquals(f.lastModified(), template.getLastmodified().longValue());
    	
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
