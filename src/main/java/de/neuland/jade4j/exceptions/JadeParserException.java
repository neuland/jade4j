package de.neuland.jade4j.exceptions;

import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.template.TemplateLoader;

public class JadeParserException extends JadeException {

	private static final long serialVersionUID = 2022663314591205451L;

	@SuppressWarnings("rawtypes")
	public JadeParserException(String filename, int lineNumber, TemplateLoader templateLoader, Class expected, Class got) {
		super("expected " + expected + " but got " + got, filename, lineNumber, templateLoader, null);
	}

	public JadeParserException(String filename, int lineNumber, TemplateLoader templateLoader, Token token) {
		super("unknown token " + token, filename, lineNumber, templateLoader, null);
	}

	public JadeParserException(String filename, int lineNumber, TemplateLoader templateLoader, String message) {
		super(message, filename, lineNumber, templateLoader, null);
	}

}
