package de.neuland.jade4j.template;

import java.io.Writer;

import de.neuland.jade4j.compiler.Compiler;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.node.Node;

public class JadeTemplate {

	private boolean prettyPrint = false;
	private Node rootNode;
	private Long lastmodified;
	private boolean terse = true;
	private boolean xml = false;

	public void process(JadeModel model, Writer writer) throws JadeCompilerException {
		Compiler compiler = new Compiler(rootNode);
		compiler.setPrettyPrint(prettyPrint);
		compiler.setTemplate(this);
		compiler.compile(model, writer);
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public Long getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(Long lastmodified) {
		this.lastmodified = lastmodified;
	}

	public void setTerse(boolean terse) {
		this.terse = terse;
	}

	public boolean isTerse() {
		return terse;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
	}

	public boolean isXml() {
		return xml;
	}
}
