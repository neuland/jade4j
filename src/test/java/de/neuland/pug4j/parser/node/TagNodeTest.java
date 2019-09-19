package de.neuland.pug4j.parser.node;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.neuland.pug4j.Pug4J;
import de.neuland.pug4j.template.PugTemplate;

public class TagNodeTest {

	private TagNode tagNode;
	private PugTemplate template;
	private String[] selfClosing = { "meta", "img", "link", "input", "area", "base", "col", "br", "hr", "source" };
	private String[] notSelfClosing = { "div", "table", "span" };

	@Before
	public void setup() {
		tagNode = new TagNode();
		template = new PugTemplate();
		template.setMode(Pug4J.Mode.XHTML);
	}

	@Test
	public void testThatTagNodeIsTerse() {
		template.setMode(Pug4J.Mode.HTML);
		for (int i = 0; i < selfClosing.length; i++) {
			tagNode.setName(selfClosing[i]);
			assertTrue(tagNode.isTerse(template));
		}
	}

	@Test
	public void testThatTagNodeIsNotSelfclosingIfTheTagIsNotSelfclosing() {
		template.setMode(Pug4J.Mode.HTML);
		for (int i = 0; i < notSelfClosing.length; i++) {
			tagNode.setName(notSelfClosing[i]);
			assertFalse(tagNode.isTerse(template));
		}
	}

	@Test
	public void testThatTagNodeIsNotTerseIfTempalteSettingIsNotTerse() {
		template.setMode(Pug4J.Mode.XHTML);
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
		template.setMode(Pug4J.Mode.XML);
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
