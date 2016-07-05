package de.neuland.jade4j.exceptions;

import de.neuland.jade4j.template.TemplateLoader;

public class JadeLexerException extends JadeException {

	private static final long serialVersionUID = -4390591022593362563L;

	public JadeLexerException(String message, String filename, int lineNumber, TemplateLoader templateLoader) {
		super(message, filename, lineNumber, templateLoader, null);
	}
}
