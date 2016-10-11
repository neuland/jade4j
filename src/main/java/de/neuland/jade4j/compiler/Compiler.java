package de.neuland.jade4j.compiler;

import java.io.StringWriter;
import java.io.Writer;

import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.JadeTemplate;

public class Compiler {

	private final Node rootNode;
	private boolean prettyPrint;
	private JadeTemplate template = new JadeTemplate();
	private ExpressionHandler expressionHandler;

	public Compiler(Node rootNode) {
		this.rootNode = rootNode;
	}

	public String compileToString(JadeModel model) throws JadeCompilerException {
		StringWriter writer = new StringWriter();
		compile(model, writer);
		return writer.toString();
	}

	public void compile(JadeModel model, Writer w) throws JadeCompilerException {
		IndentWriter writer = new IndentWriter(w);
		writer.setUseIndent(prettyPrint);
		rootNode.execute(writer, model, template);
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public void setTemplate(JadeTemplate jadeTemplate) {
		this.template = jadeTemplate;
	}

	public void setExpressionHandler(ExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

}