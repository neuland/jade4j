package de.neuland.jade4j.parser.node;

import java.util.LinkedList;
import java.util.List;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public abstract class Node implements Cloneable {

	protected LinkedList<Node> nodes = new LinkedList<Node>();
    protected int lineNumber;
    protected String name;
    protected String value;
    protected Node block;
    protected String fileName;

    public abstract void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException;
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void push(Node node) {
    	if (node == null) {
    		throw new RuntimeException();
    	}
        nodes.add(node);
    }

    public LinkedList<Node> getNodes() {
        return nodes;
    }
    
    public void setNodes(LinkedList<Node> nodes) {
		this.nodes = nodes;
    }

    public Node pollNode() {
        return nodes.isEmpty() ? null : nodes.poll();
    }
    
    public boolean hasNodes() {
        return !nodes.isEmpty();
    }
    
    public boolean hasBlock() {
        return block != null;
    }

    public Node getBlock() {
        return block;
    }

    public void setBlock(Node block) {
        this.block = block;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
	@Override
	public Node clone() throws CloneNotSupportedException {
		Node clone = (Node) super.clone();

		// Deep copy block
		if (this.block != null) {
			clone.block = this.block.clone();
		}

        clone.nodes = new LinkedList<Node>();
        for (Node node : this.nodes) {
            clone.nodes.add(node.clone());
        }

		return clone;
	}
}
