package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;
import java.util.LinkedList;
import org.apache.commons.lang3.ArrayUtils;

public class TagNode extends AttrsNode {
    private Node textNode;
    private static final String[] inlineTags = {"a", "abbr", "acronym", "b", "br", "code", "em", "font", "i", "img", "ins", "kbd", "map", "samp", "small", "span", "strong", "sub", "sup"};
    private boolean buffer = false;

    public TagNode() {
        this.block = new BlockNode();
    }

    public void setTextNode(Node textNode) {
        this.textNode = textNode;
    }

    public Node getTextNode() {
        return textNode;
    }

    public boolean hasTextNode() {
        return textNode != null;
    }

    public boolean isInline() {
        return ArrayUtils.indexOf(inlineTags, this.name) > -1;
    }

    private boolean isInline(Node node) {
        // Recurse if the node is a block
        if (node instanceof BlockNode && !((BlockNode) node).isYield()) {
            return everyIsInline(node.getNodes());
        }
        if (node instanceof BlockNode && ((BlockNode) node).isYield()) {
            return true;
        }
        boolean inline = false;
        if(node instanceof ExpressionNode){
            inline = ((ExpressionNode) node).isInline();
        }
        if(node instanceof TagNode){
            inline = ((TagNode) node).isInline();
        }
        return (node instanceof TextNode && !"\n".equals(node.getValue())) || inline;
    }

    private boolean everyIsInline(LinkedList<Node> nodes) {
        boolean multilineInlineOnlyTag = true;
        for (Node node : nodes) {
            if (!isInline(node)) {
                multilineInlineOnlyTag = false;
            }
        }
        return multilineInlineOnlyTag;
    }

    public boolean canInline() {
        Node block = this.getBlock();
        if (block == null) {
            return true;
        }
        LinkedList<Node> nodes = block.getNodes();


        // Empty tag
        if (nodes.size() == 0) {
            return true;
        }

        // Text-only or inline-only tag
        if (1 == nodes.size()) {
            return isInline(nodes.get(0));
        }

        // Multi-line inline-only tag
        if (everyIsInline(nodes)) {
            for (int i = 1, len = nodes.size(); i < len; ++i) {
                if (nodes.get(i - 1) instanceof TextNode && nodes.get(i) instanceof TextNode) {
                    return false;
                }
            }
            return true;
        }

        // Mixed tag
        return false;
    }

    @Override
    public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
        writer.increment();


        if ("pre".equals(this.name)) {
            writer.setEscape(true);
        }

        if (!writer.isCompiledTag()) {
            if (!writer.isCompiledDoctype() && "html".equals(name)) {
//              template.setDoctype(null);
            }
            writer.setCompiledTag(true);
        }

        // pretty print
        if (writer.isPp() && !isInline()) {
            writer.prettyIndent(0, true);
        }

        if (isSelfClosing() || (!template.isXml() && isBodyless())) {
            openTag(writer, model, template, !(template.isTerse() && !isSelfClosing()));
            if (hasBlock()) {
                handleIgnoredBlock();
            }
        } else {
            openTag(writer, model, template, false);

            if (hasCodeNode()) {
                codeNode.execute(writer, model, template);
            }
            if (hasBlock()) {
                block.execute(writer, model, template);
            }
            if (writer.isPp() && !isInline() && !"pre".equals(name) && !canInline()) {
                writer.prettyIndent(0, true);
            }
            writer.append("</");
            writer.append(bufferName(template, model));
            writer.append(">");
            if (hasBlock()) {
                handleIgnoredBlock();
            }
        }

        if ("pre".equals(this.name)) {
            writer.setEscape(false);
        }
        writer.decrement();
    }

    private void openTag(IndentWriter writer, PugModel model, PugTemplate template, boolean selfClosing) {
        writer.append("<")
            .append(bufferName(template, model))
            .append(visitAttributes(model, template));

        if (selfClosing) {
            writer.append("/");
        }
        writer.append(">");
    }

    private void handleIgnoredBlock() {
        // TODO Fehlerbehandlung
    }


    private String bufferName(PugTemplate template, PugModel model) {
        if (isBuffer()) {
            try {
                return template.getExpressionHandler().evaluateStringExpression(name, model);
            } catch (ExpressionException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return name;
        }
    }

    public boolean isBuffer() {
        return buffer;
    }

    public void setBuffer(boolean buffer) {
        this.buffer = buffer;
    }
}
