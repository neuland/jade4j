package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

public class TextParserTest extends ParserTest {

    private Node block;
    private TagNode tag1;
    private Node tag2;
    private Node tag;

    @Test
    public void shouldReturnTagsWithTexts() {
        loadInParser("tags_with_text.jade");
        block = (BlockNode) root;
        assertThat(block.getNodes(), notNullValue());
        assertThat(block.getNodes().size(), equalTo(2));

        tag1 = (TagNode)block.pollNode();
        assertThat(((TagNode) tag1).getAttribute("class"), equalTo("myclass"));
        assertThat(((TagNode) tag1).getTextNode(), notNullValue());
        assertThat(((TagNode) tag1).getTextNode().getValue(), equalTo("Hello World!"));
        assertThat(block.hasNodes(), equalTo(true));
        
        tag2 = block.pollNode();
        assertThat(((TagNode) tag2).getAttribute("id"), equalTo("myid2"));
        assertThat(((TagNode) tag2).getTextNode().getValue(), equalTo("without words"));
        assertThat(block.hasNodes(), equalTo(false));

        block = ((TagNode) tag1).getBlock();
        tag = block.pollNode();
        assertThat(((TagNode) tag).getAttribute("class"), equalTo("c1"));
        assertThat(((TagNode) tag).getTextNode().getValue(), equalTo("The quick brown fox"));
        assertThat(block.hasNodes(), equalTo(true));
        
        tag = block.pollNode();
        assertThat(((TagNode) tag).getAttribute("class"), equalTo("c2"));
        assertThat(((TagNode) tag).getAttribute("id"), equalTo("myid"));
        assertThat(((TagNode) tag).getTextNode().getValue(), equalTo("jumpes over the lazy dog"));
        assertThat(block.hasNodes(), equalTo(false));

        block = ((TagNode) tag2).getBlock();
        tag = block.pollNode();
        assertThat(((TagNode) tag).getAttribute("id"), equalTo("id1"));
        assertThat(((TagNode) tag).getTextNode().getValue(), equalTo("without music"));
        assertThat(block.hasNodes(), equalTo(false));
    }

}
