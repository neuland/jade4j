package de.neuland.pug4j.exceptions;

import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.TemplateLoader;

public class PugCompilerException extends PugException {

	private static final long serialVersionUID = -126617495230190225L;

	public PugCompilerException(Node node, TemplateLoader templateLoader, Throwable e) {
		super(e.getMessage(), node.getFileName(), node.getLineNumber(), templateLoader, e);
	}

	public PugCompilerException(Node node, TemplateLoader templateLoader, String message) {
		super(message, node.getFileName(), node.getLineNumber(), templateLoader, null);
	}

}
