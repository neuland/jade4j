package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.lexer.token.Doctypes;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;
import org.apache.commons.lang3.ArrayUtils;

import java.util.LinkedList;

public class TagNode extends AttrsNode {
    private Node textNode;
    private static final String[] inlineTags = { "a", "abbr", "acronym", "b", "br", "code", "em", "font", "i", "img", "ins", "kbd", "map", "samp", "small", "span", "strong", "sub", "sup"};
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

    public boolean isInline(){
        return ArrayUtils.indexOf(inlineTags,this.name) > -1;
    }
    private boolean isInline(Node node){
      // Recurse if the node is a block
      if (node instanceof BlockNode) return everyIsInline(node.getNodes());
      return node instanceof TextNode || (ArrayUtils.indexOf(inlineTags,node.getName()) > -1);
    }
    private boolean everyIsInline(LinkedList<Node> nodes){
        boolean multilineInlineOnlyTag = true;
          for (Node node : nodes) {
              if(!isInline(node))
                  multilineInlineOnlyTag = false;
          }
        return multilineInlineOnlyTag;
    }
    public boolean canInline (){
        Node block = this.getBlock();
        if(block==null)
            return true;
        LinkedList<Node> nodes = block.getNodes();


      // Empty tag
      if (nodes.size()==0) return true;

      // Text-only or inline-only tag
      if (1 == nodes.size()) return isInline(nodes.get(0));

      // Multi-line inline-only tag
      if (everyIsInline(nodes)) {
        for (int i = 1, len = nodes.size(); i < len; ++i) {
          if (nodes.get(i-1) instanceof TextNode && nodes.get(i) instanceof TextNode)
            return false;
        }
        return true;
      }

      // Mixed tag
      return false;
    };
    @Override
    public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
        writer.increment();

        if (!writer.isCompiledTag()) {
          if (!writer.isCompiledDoctype() && "html".equals(name)) {
//              template.setDoctype(null);
          }
          writer.setCompiledTag(true);
        }

        if ("pre".equals(this.name)) writer.setEscape(true);
        if(writer.isPp() && !isInline()){
            writer.prettyIndent(0,true);
        }
        if (isSelfClosing() || isSelfClosing(template)) {
            writer.append("<");
            writer.append(bufferName(template, model));
            writer.append(visitAttributes(model, template));
            if (isTerse(template)) {
                writer.append(">");
            }else {
                writer.append("/>");
            }
            if (hasBlock()) {
                //Fehlerbehandlung
            }

        }else {
            writer.append("<");
            writer.append(bufferName(template, model));
            writer.append(visitAttributes(model, template));
            writer.append(">");
            if (hasCodeNode()) {
                codeNode.execute(writer, model, template);
            }
            if (hasBlock()) {
                block.execute(writer, model, template);
            }
            // pretty print
            if (writer.isPp() && !isInline() && !"pre".equals(name) && !canInline()){
                writer.prettyIndent(0, true);
            }
            writer.append("</");
            writer.append(bufferName(template,model));
            writer.append(">");

        }
        if ("pre".equals(this.name)) writer.setEscape(false);
        writer.decrement();
    }

    private boolean isEmpty() {
        return !hasBlock() && !hasTextNode() && !hasCodeNode();
    }

    private String bufferName(JadeTemplate template, JadeModel model) {
          if (isBuffer()) {
              try {
                  return template.getExpressionHandler().evaluateStringExpression(name, model);
              } catch (ExpressionException e) {
                  e.printStackTrace();
                  return null;
              }
          }else {
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
