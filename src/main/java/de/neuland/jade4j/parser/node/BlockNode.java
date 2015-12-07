package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.template.JadeTemplate;

import java.util.ArrayList;
import java.util.Collection;

public class BlockNode extends Node {

	private boolean yield = false;
	private BlockNode yieldBlock;
	private String mode;
	private Collection<? extends Node> prepended = new ArrayList<Node>();
	private Collection<? extends Node> appended = new ArrayList<Node>();;
	private Parser parser;
	private boolean subBlock;

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
		BlockNode ret = this;
		for (Node node : getNodes()) {
			if (node instanceof BlockNode && ((BlockNode) node).isYield()) {
				return (BlockNode) node;
			}
			else if (node instanceof TagNode && ((TagNode) node).isTextOnly()) {
				continue;
			}
			else if (node instanceof BlockNode && ((BlockNode) node).getIncludeBlock() != null) {
				ret =  ((BlockNode) node).getIncludeBlock();
			}
			else if (node.hasBlock()) {
				ret =  ((BlockNode) node.getBlock()).getIncludeBlock();
			}
			if(ret instanceof BlockNode && ((BlockNode) ret).isYield()){
				return ret;
			}
		}
		return ret;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Collection<? extends Node> getPrepended() {
		return prepended;
	}

	public void setPrepended(Collection<? extends Node> prepended) {
		this.prepended = prepended;
	}

	public Collection<? extends Node> getAppended() {
		return appended;
	}

	public void setAppended(Collection<? extends Node> appended) {
		this.appended = appended;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Parser getParser() {
		return parser;
	}

	public void setSubBlock(boolean subBlock) {
		this.subBlock = subBlock;
	}

	public boolean isSubBlock() {
		return subBlock;
	}
}
