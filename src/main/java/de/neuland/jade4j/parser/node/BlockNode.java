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
		for (Node node : getNodes()) {
			node.execute(writer, model, template);
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
