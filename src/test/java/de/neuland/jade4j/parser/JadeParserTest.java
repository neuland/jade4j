package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

public class JadeParserTest extends ParserTest {

    private Node blockNode;


    @Test
    public void shouldReturnABlockWithTokens2() {
        loadInParser("two_blocks_with_an_outdent.jade");
        blockNode = (BlockNode) root;
        assertThat(blockNode.getNodes(), notNullValue());

        Node node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("html"));

        node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("div"));
        assertThat(blockNode.hasNodes(), equalTo(false));

        blockNode = ((TagNode) node).getBlock();
        node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("p"));
        Node node2 = blockNode.pollNode();
        assertThat(node2.getValue(), equalTo("h1"));
        assertThat(blockNode.hasNodes(), equalTo(false));

        blockNode = ((TagNode) node).getBlock();
        node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("span"));
        assertThat(blockNode.hasNodes(), equalTo(false));
    }

    @Test
    public void shouldReturnABlockWithTokens() {
        loadInParser("two_blocks_with_a_tag.jade");
        assertThat(((BlockNode) root).getNodes(), notNullValue());
        Node node = ((BlockNode) root).pollNode();
        assertThat(node.getValue(), equalTo("div"));
        Node blockNode = ((TagNode) node).getBlock();
        node = blockNode.pollNode();
        assertThat(node.getValue(), equalTo("h1"));
        assertThat(blockNode.hasNodes(), equalTo(false));
    }

}
