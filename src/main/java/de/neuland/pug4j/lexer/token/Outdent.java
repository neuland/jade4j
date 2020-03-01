package de.neuland.pug4j.lexer.token;


public class Outdent extends Token {
	public Outdent() {
	}

	public Outdent(int lineNumber) {
		super(null, lineNumber);
	}

}
