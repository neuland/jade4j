package de.neuland.jade4j.parser;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;

import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;
import de.neuland.jade4j.parser.node.TextNode;

@Ignore("Not working after Parser changes")
public class LargeBodyTextWithoutPipesParserTest extends ParserTest {

    private Node block;
    private Node textNode;
    private TagNode tagNode;
    

    @Test
    public void test() throws FileNotFoundException {
        loadInParser("large_body_text_without_pipes.jade");
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("p"));

        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), notNullValue());
        assertThat(textNode.getValue(), equalTo("Hello World!\nHere comes the Message!"));
        assertThat(textNode.hasNodes(), equalTo(false));
        
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("div"));
        block = tagNode.getBlock();
        assertThat(block, notNullValue());

        tagNode = (TagNode) block.pollNode();
        assertThat(tagNode.getName(), equalTo("h1"));

        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), notNullValue());
        assertThat(textNode.getValue(), equalTo("Hello World!\nHere comes the second Message!"));
        assertThat(textNode.hasNodes(), equalTo(false));

        tagNode = (TagNode) block.pollNode();
        assertThat(tagNode.getName(), equalTo("h2"));
    
        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), notNullValue());
        assertThat(textNode.getValue(), equalTo("Hello World!\nHere comes the third Message!"));
        assertThat(textNode.hasNodes(), equalTo(false));

        assertThat(block.hasNodes(), equalTo(false));
        assertThat(root.hasNodes(), equalTo(false));
    }
}
