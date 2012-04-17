package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.neuland.jade4j.parser.node.BlockNode;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.parser.node.TagNode;

public class ComplexIndentOutdentParserTest extends ParserTest {

    private Node head;
    private Node body;
    private Node div1;
    private Node div2;
    private Node div3;
    private Node div4;
    private Node div5;
    private Node div6;
    private Node div7;
    private Node div8;
    private Node ul1;
    private Node ul2;
    private Node span;
    private Node em;
    private Node block;

    @Test
    public void shouldReturnABlockWithTokens2() {
        loadInParser("complex_indent_outdent_file.jade");
        block = (BlockNode) root;
        assertThat(block.getNodes(), notNullValue());

        head = block.pollNode();
        body = block.pollNode();
        assertThat(head.getValue(), equalTo("head"));
        assertThat(body.getValue(), equalTo("body"));
        assertThat(block.hasNodes(), equalTo(false));

        block = ((TagNode) head).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("meta"));
        assertThat(block.pollNode().getValue(), equalTo("meta"));
        assertThat(block.hasNodes(), equalTo(false));

        block = ((TagNode) body).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("div0"));
        
        div1 = block.pollNode();
        assertThat(div1.getValue(), equalTo("div1"));
        
        div2 = block.pollNode();
        assertThat(div2.getValue(), equalTo("div2"));

        div3 = block.pollNode();
        assertThat(div3.getValue(), equalTo("div3"));
        
        div4 = block.pollNode();
        assertThat(div4.getValue(), equalTo("div4"));
        
        div5 = block.pollNode();
        assertThat(div5.getValue(), equalTo("div5"));

        assertThat(block.hasNodes(), equalTo(false));

        // ===============================================
        
        block = ((TagNode) div1).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("span"));
        assertThat(block.pollNode().getValue(), equalTo("span"));
        assertThat(block.hasNodes(), equalTo(false));
        
        // ===============================================
        
        block = ((TagNode) div2).getBlock();
        ul1 = block.pollNode();
        assertThat(ul1.getValue(), equalTo("ul1"));
        ul2 = block.pollNode();
        assertThat(ul2.getValue(), equalTo("ul2"));
        assertThat(block.hasNodes(), equalTo(false));
        
        // ===============================================
        
        block = ((TagNode) div3).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("span"));
        assertThat(block.hasNodes(), equalTo(false));

        // ===============================================
        
        block = ((TagNode) div4).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("h1"));
        assertThat(block.hasNodes(), equalTo(false));

        // ===============================================
        
        block = ((TagNode) div5).getBlock();
        div6 = block.pollNode();
        assertThat(div6.getValue(), equalTo("div6"));
        div7 = block.pollNode();
        assertThat(div7.getValue(), equalTo("div7"));
        assertThat(block.hasNodes(), equalTo(false));

        // ===============================================
        
        block = ((TagNode) div6).getBlock();
        div8 = block.pollNode();
        assertThat(div8.getValue(), equalTo("div8"));
        assertThat(block.hasNodes(), equalTo(false));

        // ===============================================
        
        block = ((TagNode) div8).getBlock();
        span = block.pollNode();
        assertThat(span.getValue(), equalTo("span"));
        assertThat(block.hasNodes(), equalTo(false));
        
        // ===============================================
        
        block = ((TagNode) span).getBlock();
        em = block.pollNode();
        assertThat(em.getValue(), equalTo("em"));
        assertThat(block.hasNodes(), equalTo(false));
        
        // ===============================================
        
        block = ((TagNode) div7).getBlock();
        assertThat(block.pollNode().getValue(), equalTo("span"));
        assertThat(block.hasNodes(), equalTo(false));
    }

}
