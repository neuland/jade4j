package de.neuland.pug4j.parser.node;


import de.neuland.pug4j.Pug4J;
import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.template.PugTemplate;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagNodeTest {

    private static final String TEXT = "dummytext";


    private String[] bodylessTags = { "meta", "img", "link", "input", "area", "base", "col", "br", "hr", "source" };
    private String[] normalTags = { "div", "table", "span" };
    private PugConfiguration jade;


    @Before
    public void init() {
        jade = new PugConfiguration();
    }

    @Test
    public void shouldCloseBodylessTagsWithSlashAndIgnoreBlockWhenCompilingToXhtml() {
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XHTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.HTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XHTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.HTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XHTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.HTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XHTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.HTML);

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
        PugTemplate template = new PugTemplate();
        template.setMode(Pug4J.Mode.XML);

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