package de.neuland.jade4j.parser;


import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class SelfClosingTagParserTest extends ParserTest {

	private BlockNode block;


	@Test
	public void shouldReturnTagsSelfClosed() {
		loadInParser("selfClosingTag.jade");
		block = (BlockNode) root;
		LinkedList<Node> nodes = block.getNodes();
		assertEquals(4, nodes.size());
		
		TagNode tag = (TagNode)block.getNodes().get(0);

        assertTrue(tag.isTrailingSlashSelfClosing());
        assertEquals(tag.getAttribute("bar"),"baz");

        tag = (TagNode)block.getNodes().get(1);

        assertTrue(tag.isTrailingSlashSelfClosing());
        assertEquals(tag.getAttributes().size(),0);

        tag = (TagNode)block.getNodes().get(2);

        assertFalse(tag.isTrailingSlashSelfClosing());
        assertEquals(tag.getAttribute("bar"),"baz");

        tag = (TagNode)block.getNodes().get(3);

        assertFalse(tag.isTrailingSlashSelfClosing());
        assertEquals(tag.getAttributes().size(),0);

	}
}
