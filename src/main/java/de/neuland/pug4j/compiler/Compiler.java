package de.neuland.pug4j.compiler;

import java.io.StringWriter;
import java.io.Writer;

import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.PugTemplate;

public class Compiler {

	private final Node rootNode;
	private boolean prettyPrint;
	private PugTemplate template = new PugTemplate();
	private ExpressionHandler expressionHandler;

	public Compiler(Node rootNode) {
		this.rootNode = rootNode;
	}

	public String compileToString(PugModel model) throws PugCompilerException {
		StringWriter writer = new StringWriter();
		compile(model, writer);
		return writer.toString();
	}

	public void compile(PugModel model, Writer w) throws PugCompilerException {
		IndentWriter writer = new IndentWriter(w);
		writer.setUseIndent(prettyPrint);
		rootNode.execute(writer, model, template);
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public void setTemplate(PugTemplate pugTemplate) {
		this.template = pugTemplate;
	}

	public void setExpressionHandler(ExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

}