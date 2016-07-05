package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;

public class DoctypeParserTest extends ParserTest {
    
    private BlockNode blockNode;

    @Test
    public void shouldReturnDoctype() {
        loadInParser("doctype.jade");
        blockNode = (BlockNode)root;
        assertThat(blockNode.getNodes(), notNullValue());
        
        Node node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("strict"));
        assertThat(blockNode.hasNodes(), equalTo(false));
    }

}
