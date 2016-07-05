package de.neuland.jade4j.parser.node;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.neuland.jade4j.Jade4J;
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
		template.setMode(Jade4J.Mode.XHTML);
	}

	@Test
	public void testThatTagNodeIsTerse() {
		template.setMode(Jade4J.Mode.HTML);
		for (int i = 0; i < selfClosing.length; i++) {
			tagNode.setName(selfClosing[i]);
			assertTrue(tagNode.isTerse(template));
		}
	}

	@Test
	public void testThatTagNodeIsNotSelfclosingIfTheTagIsNotSelfclosing() {
		template.setMode(Jade4J.Mode.HTML);
		for (int i = 0; i < notSelfClosing.length; i++) {
			tagNode.setName(notSelfClosing[i]);
			assertFalse(tagNode.isTerse(template));
		}
	}

	@Test
	public void testThatTagNodeIsNotTerseIfTempalteSettingIsNotTerse() {
		template.setMode(Jade4J.Mode.XHTML);
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
		template.setMode(Jade4J.Mode.XML);
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
