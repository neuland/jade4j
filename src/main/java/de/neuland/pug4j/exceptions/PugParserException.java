package de.neuland.pug4j.exceptions;

import de.neuland.pug4j.lexer.token.Token;
import de.neuland.pug4j.template.TemplateLoader;

public class PugParserException extends PugException {

	private static final long serialVersionUID = 2022663314591205451L;

	@SuppressWarnings("rawtypes")
	public PugParserException(String filename, int lineNumber, TemplateLoader templateLoader, Class expected, Class got) {
		super("expected " + expected + " but got " + got, filename, lineNumber, templateLoader, null);
	}

	public PugParserException(String filename, int lineNumber, TemplateLoader templateLoader, Token token) {
		super("unknown token " + token, filename, lineNumber, templateLoader, null);
	}

	public PugParserException(String filename, int lineNumber, TemplateLoader templateLoader, String message) {
		super(message, filename, lineNumber, templateLoader, null);
	}

}
