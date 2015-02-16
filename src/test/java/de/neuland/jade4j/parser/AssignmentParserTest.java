package de.neuland.jade4j.parser;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import de.neuland.jade4j.parser.node.AssigmentNode;
import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

public class AssignmentParserTest extends ParserTest {

	private BlockNode block;

	@Test
	public void shouldReturnTagsWithTexts() {
		loadInParser("assignment.jade");
		block = (BlockNode) root;
		LinkedList<Node> nodes = block.getNodes();
		assertEquals(6, nodes.size());
		
		AssigmentNode assignment = (AssigmentNode) block.getNodes().get(0).getBlock().getNodes().get(0);
		assertEquals("hello", assignment.getName());
		assertEquals("\"world\"", assignment.getValue());

		TagNode tag = (TagNode) block.getNodes().get(0).getBlock().getNodes().get(1);
		assertNotNull(tag);

        AssigmentNode assignmentTwo = (AssigmentNode) block.getNodes().get(1);
        assertEquals("goodnight", assignmentTwo.getName());
        assertEquals("\"moon\"", assignmentTwo.getValue());

        TagNode tagTwo = (TagNode) block.getNodes().get(2);
        assertNotNull(tagTwo);

        AssigmentNode assignmentThree = (AssigmentNode) block.getNodes().get(3);
        assertEquals("\"good\"", assignmentThree.getValue());

        TagNode tagThree = (TagNode) block.getNodes().get(4);
        assertNotNull(tagThree);
	}
}
