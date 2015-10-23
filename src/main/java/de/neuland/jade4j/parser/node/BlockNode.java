package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class BlockNode extends Node {

	private boolean yield = false;
	private BlockNode yieldBlock;
	private String mode;

	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {

		// Pretty print multi-line text
		if (writer.isPp() && getNodes().size() > 1 && !writer.isEscape() && getNodes().get(0) instanceof TextNode && getNodes().get(1) instanceof TextNode)
			writer.prettyIndent(1, true);

		for (int i = 0; i < getNodes().size(); ++i) {
			// Pretty print text
			if (writer.isPp() && i > 0 && !writer.isEscape() && getNodes().get(i) instanceof TextNode && getNodes().get(i - 1) instanceof TextNode)
				writer.prettyIndent(1, false);

			getNodes().get(i).execute(writer, model, template);
			// Multiple text nodes are separated by newlines
			Node nextNode = null;
			if(i+1 < getNodes().size())
				nextNode = getNodes().get(i + 1);
			if (nextNode !=null && getNodes().get(i) instanceof TextNode && nextNode instanceof TextNode)
				writer.append("\n");
		}

	}

	public void setYield(boolean yield) {
		this.yield = yield;
	}

	public boolean isYield() {
		return yield;
	}

	public void setYieldBlock(BlockNode yieldBlock) {
		this.yieldBlock = yieldBlock;
	}

	public BlockNode getYieldBlock() {
		return yieldBlock;
	}

	public BlockNode getIncludeBlock() {
		for (Node node : getNodes()) {
			if (node instanceof BlockNode && ((BlockNode) node).isYield()) {
				return (BlockNode) node;
			}
			if (node instanceof TagNode && ((TagNode) node).isTextOnly()) {
				continue;
			}
			if (node instanceof BlockNode && ((BlockNode) node).getIncludeBlock() != null) {
				return ((BlockNode) node).getIncludeBlock();
			}
			if (node.hasBlock() && node.getBlock() instanceof BlockNode) {
				return ((BlockNode) node.getBlock()).getIncludeBlock();
			}
		}
		return this;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
