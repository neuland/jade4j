package de.neuland.jade4j.parser;

import static org.junit.Assert.*;

import java.util.LinkedList;

import de.neuland.jade4j.parser.node.*;
import org.junit.Test;

public class AssignmentParserTest extends ParserTest {

	private BlockNode block;

	@Test
	public void shouldReturnTagsWithTexts() {
		loadInParser("assignment.jade");
		block = (BlockNode) root;
		LinkedList<Node> nodes = block.getNodes();
		assertEquals(2, nodes.size());
		
		ExpressionNode assignment = (ExpressionNode) block.getNodes().get(0);
		assertEquals("var hello = \"world\"", assignment.getValue());

		TagNode tag = (TagNode) block.getNodes().get(1);
		assertNotNull(tag);
	}
}
