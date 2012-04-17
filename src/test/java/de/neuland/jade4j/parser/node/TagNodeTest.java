package de.neuland.jade4j.parser.node;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.neuland.jade4j.parser.node.TagNode;
import de.neuland.jade4j.template.JadeTemplate;

public class TagNodeTest {

    private TagNode tagNode;
    private JadeTemplate template;
    private String[] selfClosing = { "meta", "img", "link", "input", "area", "base", "col", "br", "hr", "source" };
    private String[] notSelfClosing = { "div", "table", "span" };

    @Before
    public void setup() {
        tagNode = new TagNode();
        template = new JadeTemplate();
        template.setTerse(false);
        template.setXml(false);
    }
    
    @Test
    public void testThatTagNodeIsTerse() {
        template.setTerse(true);
        for (int i = 0; i < selfClosing.length; i++) {
            tagNode.setName(selfClosing[i]);
            assertTrue(tagNode.isTerse(template));
        }
    }
    
    @Test
    public void testThatTagNodeIsNotSelfclosingIfTheTagIsNotSelfclosing() {
        template.setTerse(true);
        for (int i = 0; i < notSelfClosing.length; i++) {
            tagNode.setName(notSelfClosing[i]);
            assertFalse(tagNode.isTerse(template));
        }
    }
    
    @Test
    public void testThatTagNodeIsNotTerseIfTempalteSettingIsNotTerse() {
        template.setTerse(false);
        for (int i = 0; i < selfClosing.length; i++) {
            tagNode.setName(selfClosing[i]);
            assertFalse(tagNode.isTerse(template));
        }
    }
    
    
    @Test
    public void testThatTagNodeIsSelfClosing() {
        for (int i = 0; i < selfClosing.length; i++) {
            tagNode.setName(selfClosing[i]);
            assertTrue(tagNode.isSelfClosing(template));
        }
    }

    @Test
    public void testThatTagNodeIsNotSelfClosingIfXmlDoctype() {
        template.setXml(true);
        for (int i = 0; i < selfClosing.length; i++) {
            tagNode.setName(selfClosing[i]);
            assertFalse(tagNode.isSelfClosing(template));
        }
    }

    @Test
    public void testThatTagNodeIsNotSelfClosingIfNotSelfClosingtag() {
        for (int i = 0; i < notSelfClosing.length; i++) {
            tagNode.setName(notSelfClosing[i]);
            assertFalse(tagNode.isSelfClosing(template));
        }
    }

}
