package de.neuland.jade4j.exceptions;

import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.TemplateLoader;

public class JadeCompilerException extends JadeException {

	private static final long serialVersionUID = -126617495230190225L;

	public JadeCompilerException(Node node, TemplateLoader templateLoader, Throwable e) {
		super(e.getMessage(), node.getFileName(), node.getLineNumber(), templateLoader, e);
	}

	public JadeCompilerException(Node node, TemplateLoader templateLoader, String message) {
		super(message, node.getFileName(), node.getLineNumber(), templateLoader, null);
	}

}
