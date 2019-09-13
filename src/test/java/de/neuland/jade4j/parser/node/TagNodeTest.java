package de.neuland.jade4j.parser.node;


import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.JadeTemplate;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagNodeTest {

    private static final String TEXT = "dummytext";


    private String[] bodylessTags = { "meta", "img", "link", "input", "area", "base", "col", "br", "hr", "source" };
    private String[] normalTags = { "div", "table", "span" };
    private JadeConfiguration jade;


    @Before
    public void init() {
        jade = new JadeConfiguration();
    }

    @Test
    public void shouldCloseBodylessTagsWithSlashAndIgnoreBlockWhenCompilingToXhtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XHTML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseBodylessTagsWithoutSlashAndIgnoreBlockWhenCompilingToHtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.HTML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + ">");
        }
    }

    @Test
    public void shouldCloseBodylessTagsWithEndTagWhenCompilingToXml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + ">" + TEXT + "</" + tagName + ">");
        }
    }

    @Test
    public void shouldCloseSelfClosingBodylessTagsWithSlashAndIgnoreBlockWhenCompilingToXhtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XHTML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseSelfClosingBodylessTagsWithSlashAndIgnoreBlockWhenCompilingToHtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.HTML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseSelfClosingBodylessTagsWithSlashAndIgnoreBlockWhenCompilingToXml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XML);

        TagNode tagNode = new TagNode();

        for (String tagName : bodylessTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseNormalTagsWithEndTagWhenCompilingToXhtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XHTML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + ">" + TEXT + "</" + tagName + ">");
        }
    }


    @Test
    public void shouldCloseNormalTagsWithEndTagWhenCompilingToHtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.HTML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + ">" + TEXT + "</" + tagName + ">");
        }
    }


    @Test
    public void shouldCloseNormalTagsWithEndTagWhenCompilingToXml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + ">" + TEXT + "</" + tagName + ">");
        }
    }



    @Test
    public void shouldCloseSelfClosingNormalTagsWithSlashAndIgnoreBlockWhenCompilingToXhtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XHTML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseSelfClosingNormalTagsWithSlashAndIgnoreBlockWhenCompilingToHtml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.HTML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    @Test
    public void shouldCloseSelfClosingNormalTagsWithSlashAndIgnoreBlockWhenCompilingToXml() {
        JadeTemplate template = new JadeTemplate();
        template.setMode(Jade4J.Mode.XML);

        TagNode tagNode = new TagNode();

        for (String tagName : normalTags) {
            tagNode.setName(tagName);
            withTextBlock(tagNode);
            tagNode.setSelfClosing(true);
            template.setRootNode(tagNode);

            String result = jade.renderTemplate(template, Collections.<String, Object>emptyMap());

            assertThat(result).isEqualTo("<" + tagName + "/>");
        }
    }

    private void withTextBlock(TagNode tagNode) {
        TextNode textNode = new TextNode();
        textNode.setValue(TEXT);

        BlockNode blockNode = new BlockNode();

        LinkedList<Node> list = new LinkedList<Node>();
        list.add(textNode);
        blockNode.setNodes(list);

        tagNode.setBlock(blockNode);
    }


}