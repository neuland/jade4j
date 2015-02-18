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
    public static final int EXPECTED_NUMBER_OF_NODES = 7;

	@Test
	public void shouldReturnTagsWithTexts() {
		loadInParser("assignment.jade");
		block = (BlockNode) root;
		LinkedList<Node> nodes = block.getNodes();
		assertEquals(EXPECTED_NUMBER_OF_NODES, nodes.size());
		
		AssigmentNode assignment = (AssigmentNode) block.getNodes().get(0).getBlock().getNodes().get(0);
		assertEquals("hello", assignment.getName());
		assertEquals("\"world\"", assignment.getValue());

		TagNode tag = (TagNode) block.getNodes().get(0).getBlock().getNodes().get(1);
		assertNotNull(tag);

        AssigmentNode assignmentTwo = (AssigmentNode) block.getNodes().get(1).getBlock().getNodes().get(0);
        assertEquals("hello", assignmentTwo.getName());
        assertEquals("\"world\"", assignmentTwo.getValue());

        TagNode tagTwo = (TagNode) block.getNodes().get(1).getBlock().getNodes().get(1);
        assertNotNull(tagTwo);

        AssigmentNode assignmentThree = (AssigmentNode) block.getNodes().get(2);
        assertEquals("goodnight", assignmentThree.getName());
        assertEquals("\"moon\"", assignmentThree.getValue());

        TagNode tagThree = (TagNode) block.getNodes().get(3);
        assertNotNull(tagTwo);

        AssigmentNode assignmentFour = (AssigmentNode) block.getNodes().get(4);
        assertEquals("\"good\"", assignmentFour.getValue());

        TagNode tagFour = (TagNode) block.getNodes().get(5);
        assertNotNull(tagFour);
	}
}
