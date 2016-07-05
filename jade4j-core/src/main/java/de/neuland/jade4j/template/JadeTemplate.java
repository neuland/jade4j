package de.neuland.jade4j.template;

import java.io.Writer;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.Jade4J.Mode;
import de.neuland.jade4j.compiler.Compiler;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.lexer.token.Doctypes;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.node.Node;

public class JadeTemplate {

	private boolean prettyPrint = false;
	private Node rootNode;
	private boolean terse = false;
	private boolean xml = false;
	private TemplateLoader templateLoader;
	private ExpressionHandler expressionHandler;
	private String doctypeLine;

	public void process(JadeModel model, Writer writer) throws JadeCompilerException {
		Compiler compiler = new Compiler(rootNode);
		compiler.setPrettyPrint(prettyPrint);
		compiler.setTemplate(this);
		compiler.setExpressionHandler(expressionHandler);
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

	public boolean isTerse() {
		return terse;
	}

	public boolean isXml() {
		return xml;
	}

	public void setTemplateLoader(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	public TemplateLoader getTemplateLoader() {
		return templateLoader;
	}

	public void setDoctype(String name){
		if (name == null) {
			name = "default";
		}
		doctypeLine = Doctypes.get(name);
		if (doctypeLine == null) {
			doctypeLine = "<!DOCTYPE " + name + ">";
		}

		this.terse = "<!doctype html>".equals(this.doctypeLine.toLowerCase());
		this.xml = doctypeLine.startsWith("<?xml");
 	}

	public String getDoctypeLine() {
		return doctypeLine;
	}

	public void setMode(Mode mode) {
		xml = false;
		terse = false;
		switch (mode) {
		case HTML:
			terse = true;
			break;
		case XML:
			xml = true;
			break;
		}
	}

	public void setExpressionHandler(ExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	public ExpressionHandler getExpressionHandler() {
		return expressionHandler;
	}
}
