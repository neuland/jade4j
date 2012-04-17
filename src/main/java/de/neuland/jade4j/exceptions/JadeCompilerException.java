package de.neuland.jade4j.exceptions;

import de.neuland.jade4j.parser.node.Node;

public class JadeCompilerException extends JadeException {

	private static final long serialVersionUID = -126617495230190225L;

	public JadeCompilerException(Node node, Throwable e) {
		super(e.getMessage(), e);
		setFilename(node.getFileName());
		setLineNumber(node.getLineNumber());
	}

	public JadeCompilerException(Node node, String message) {
		super(message);
		setFilename(node.getFileName());
		setLineNumber(node.getLineNumber());
	}
	
}
