package de.neuland.jade4j.parser;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;
import de.neuland.jade4j.parser.node.TextNode;

public class IncludeParserTest extends ParserTest {

    private Node textNode;
    private TagNode tagNode;
    private BlockNode yieldNode;
    private BlockNode includeNode;
    

    @Test
    public void test() throws FileNotFoundException {
        loadInParser("include_1.jade");
        tagNode = (TagNode) root.pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), equalTo("Before Include"));
        
        includeNode = (BlockNode) root.pollNode();
        tagNode = (TagNode) includeNode.pollNode();
        assertThat(tagNode.getName(), equalTo("span"));
        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), equalTo("Hello Include"));
        
        yieldNode = (BlockNode) includeNode.pollNode();
        assertThat(yieldNode, notNullValue());
        /*
        tagNode = (TagNode) yieldNode.pollNode();
        assertThat(tagNode.getName(), equalTo("p"));
        textNode = (TextNode) tagNode.getTextNode();
        assertThat(textNode.getValue(), equalTo("After Include"));

        assertThat(yieldNode.hasNodes(), equalTo(false));
        assertThat(includeNode.hasNodes(), equalTo(false));
        assertThat(root.hasNodes(), equalTo(false));
        */
    }
}
