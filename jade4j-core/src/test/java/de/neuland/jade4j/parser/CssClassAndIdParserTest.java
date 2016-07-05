package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

@Ignore("Attribute Handling Changed, This is not working anymore")
public class CssClassAndIdParserTest extends ParserTest {

    private Node tag1;
    private Node tag2;
    private Node tag3;
    private Node block;

    @Test
    public void shouldReturnABlockWithTokens2() {
        loadInParser("css_class_and_id.jade");
        block = (BlockNode) root;
        assertThat(block.getNodes(), notNullValue());

        // .myclass
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getAttribute("class"), equalTo("myclass"));
        assertThat(block.hasNodes(), equalTo(true));

        // #myid.c1.c2.c3
        tag2 = block.pollNode();
        assertThat(((TagNode) tag2).getAttribute("id"), equalTo("myid"));
        assertThat(((TagNode) tag2).getAttribute("class"), equalTo("c1 c2 c3"));
        assertThat(block.hasNodes(), equalTo(false));

        // .c1.c2.c3
        // #myid
        block = ((TagNode) tag1).getBlock();
        tag3 = block.pollNode();
        assertThat(((TagNode) tag3).getAttribute("class"), equalTo("c1 c2 c3"));
        assertThat(block.hasNodes(), equalTo(true));
        tag3 = block.pollNode();
        assertThat(((TagNode) tag3).getAttribute("id"), equalTo("myid"));
        assertThat(block.hasNodes(), equalTo(false));

        // div#id1
        block = ((TagNode) tag2).getBlock();
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getAttribute("id"), equalTo("id1"));
        assertThat(block.hasNodes(), equalTo(false));

        // span#id2.c1.c2.c3.c4
        block = ((TagNode) tag1).getBlock();
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getValue(), equalTo("span"));
        assertThat(((TagNode) tag1).getAttribute("id"), equalTo("id2"));
        assertThat(((TagNode) tag1).getAttribute("class"), equalTo("c1 c2 c3 c4"));
        assertThat(block.hasNodes(), equalTo(false));
    }
}
