package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

@Ignore("Note working with new AttributeHandling")
public class TagsWithAttributesParserTest extends ParserTest {

    private Node tag1;
    private Node tag2;
    private Node tag3;
    private Node block;

    @Test
    public void shouldReturnABlockWithTokens2() {
        loadInParser("tags_with_attributes.jade");
        block = (BlockNode) root;
        assertThat(block.getNodes(), notNullValue());

        // .myclass(title="my first div" alt="alt does not fit here")
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getAttribute("class"), equalTo("myclass"));
        assertThat(((TagNode) tag1).getAttribute("title"), equalTo("my first div"));
        assertThat(((TagNode) tag1).getAttribute("alt"), equalTo("alt does not fit here"));
        assertThat(block.hasNodes(), equalTo(true));

        // #myid.c1.c2.c3(title="the third div with attribute")
        tag2 = block.pollNode();
        assertThat(((TagNode) tag2).getAttribute("id"), equalTo("myid"));
        assertThat(((TagNode) tag2).getAttribute("class"), equalTo("c1 c2 c3"));
        assertThat(((TagNode) tag2).getAttribute("title"), equalTo("the third div with attribute"));
        assertThat(block.hasNodes(), equalTo(false));

        // .c1.c2.c3(title="the second div with attribute")
        block = ((TagNode) tag1).getBlock();
        tag3 = block.pollNode();
        assertThat(((TagNode) tag3).getAttribute("class"), equalTo("c1 c2 c3"));
        assertThat(((TagNode) tag3).getAttribute("title"), equalTo("the second div with attribute"));
        assertThat(block.hasNodes(), equalTo(true));
        
        // #myid        
        tag3 = block.pollNode();
        assertThat(((TagNode) tag3).getAttribute("id"), equalTo("myid"));
        assertThat(block.hasNodes(), equalTo(false));

        // div#id1
        block = ((TagNode) tag2).getBlock();
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getAttribute("id"), equalTo("id1"));
        assertThat(block.hasNodes(), equalTo(false));

        // span#id2.c1.c2.c3.c4(alt="alt")
        block = ((TagNode) tag1).getBlock();
        tag1 = block.pollNode();
        assertThat(((TagNode) tag1).getValue(), equalTo("span"));
        assertThat(((TagNode) tag1).getAttribute("id"), equalTo("id2"));
        assertThat(((TagNode) tag1).getAttribute("class"), equalTo("c1 c2 c3 c4"));
        assertThat(((TagNode) tag1).getAttribute("alt"), equalTo("alt"));
        assertThat(block.hasNodes(), equalTo(false));
    }
}
