package de.neuland.pug4j.lexer.token;


public class Newline extends Token {
	public Newline() {
	}

	public Newline(int lineNumber) {
		super(null, lineNumber);
	}

}
