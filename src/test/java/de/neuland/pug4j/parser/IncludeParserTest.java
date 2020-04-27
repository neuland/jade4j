package de.neuland.pug4j.parser;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;

import de.neuland.pug4j.parser.node.BlockNode;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.parser.node.TagNode;
import de.neuland.pug4j.parser.node.TextNode;


public class IncludeParserTest extends ParserTest {

    private Node textNode;
    private TagNode tagNode;
    private BlockNode yieldNode;
    private BlockNode includeNode;
    private BlockNode pNode;

    @Test
    public void testJade() throws FileNotFoundException {
        loadInParser("include_1.jade");
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("Before Include"));

        //includeNode = (BlockNode) root.pollNode();
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("span"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("Hello Include"));

        yieldNode = (BlockNode) root.pollNode();
        assertThat(yieldNode, notNullValue());

        tagNode = (TagNode) yieldNode.pollNode().pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("After Include"));

        assertThat(yieldNode.hasNodes(), equalTo(false));
        assertThat(tagNode.hasNodes(), equalTo(false));
        assertThat(root.hasNodes(), equalTo(false));

    }
    @Test
    public void testPug() throws FileNotFoundException {
        loadInParser("include_1.pug","pug");
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("Before Include"));

        //includeNode = (BlockNode) root.pollNode();
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("span"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("Hello Include"));

        yieldNode = (BlockNode) root.pollNode();
        assertThat(yieldNode, notNullValue());

        tagNode = (TagNode) yieldNode.pollNode().pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        pNode = (BlockNode) tagNode.getBlock();
        textNode = (TextNode) pNode.getNodes().get(0);
        assertThat(textNode.getValue(), equalTo("After Include"));

        assertThat(yieldNode.hasNodes(), equalTo(false));
        assertThat(tagNode.hasNodes(), equalTo(false));
        assertThat(root.hasNodes(), equalTo(false));
    }
}
