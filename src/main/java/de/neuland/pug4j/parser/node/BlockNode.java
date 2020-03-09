package de.neuland.pug4j.parser.node;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.parser.Parser;
import de.neuland.pug4j.template.PugTemplate;

import java.util.ArrayList;
import java.util.Collection;

public class BlockNode extends Node {

	private boolean yield = false;
	private String mode;
	private Collection<? extends Node> prepended = new ArrayList<Node>();
	private Collection<? extends Node> appended = new ArrayList<Node>();;
	private Parser parser;
	private boolean subBlock;
	private boolean namedBlock;

	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {

		// Pretty print multi-line text
		if (writer.isPp() && getNodes().size() > 1 && !writer.isEscape() && getNodes().get(0) instanceof TextNode && getNodes().get(1) instanceof TextNode)
			writer.prettyIndent(1, true);

		for (int i = 0; i < getNodes().size(); ++i) {
			// Pretty print text
			if (writer.isPp() && i > 0 && !writer.isEscape() && getNodes().get(i) instanceof TextNode && getNodes().get(i - 1) instanceof TextNode && "\n".equals(getNodes().get(i - 1).getValue()))
				writer.prettyIndent(1, false);

			getNodes().get(i).execute(writer, model, template);
		}

	}

	public void setYield(boolean yield) {
		this.yield = yield;
	}

	public boolean isYield() {
		return yield;
	}

	public BlockNode getYieldBlock() {
		BlockNode ret = this;
		for (Node node : getNodes()) {
			if (node instanceof BlockNode && ((BlockNode) node).isYield()) {
				return (BlockNode) node;
			}
			else if (node instanceof TagNode && ((TagNode) node).isTextOnly()) {
				continue;
			}
			else if (node instanceof BlockNode && ((BlockNode) node).getYieldBlock() != null) {
				ret =  ((BlockNode) node).getYieldBlock();
			}
			else if (node.hasBlock()) {
				ret =  ((BlockNode) node.getBlock()).getYieldBlock();
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

	public boolean isNamedBlock() {
		return namedBlock;
	}

	public void setNamedBlock(boolean namedBlock) {
		this.namedBlock = namedBlock;
	}
}
