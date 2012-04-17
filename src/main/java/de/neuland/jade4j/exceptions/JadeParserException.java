package de.neuland.jade4j.exceptions;

import de.neuland.jade4j.lexer.token.Token;

public class JadeParserException extends JadeException {

	private static final long serialVersionUID = 2022663314591205451L;

	@SuppressWarnings("rawtypes")
	public JadeParserException(String filename, int lineNumber, Class expected, Class got) {
		super("expected " + expected + " but got " + got);
		setFilename(filename);
		setLineNumber(lineNumber);
	}

	public JadeParserException(String filename, int lineNumber, Token token) {
		super("unknown token " + token);
		setFilename(filename);
		setLineNumber(lineNumber);
	}

	public JadeParserException(String filename, int lineNumber, String message) {
		super(message);
		setFilename(filename);
		setLineNumber(lineNumber);
	}

}
