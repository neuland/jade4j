package de.neuland.jade4j.exceptions;


public class JadeLexerException extends JadeException {

	private static final long serialVersionUID = -4390591022593362563L;
	private final String input;

	public JadeLexerException(String message, String filename, int lineNumber, String input) {
		super(message + "\n" + input);
		this.input = input;
		setFilename(filename);
		setLineNumber(lineNumber);
	}

	public String getInput() {
		return input;
	}
}
