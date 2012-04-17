package de.neuland.jade4j.lexer.token;


public class Mixin extends Token {

	private String arguments;

    public Mixin(String value, int lineNumber) {
		super(value, lineNumber);
	}

    public void setArguments(String args) {
        this.arguments = args;
    }
    
    public String getArguments() {
        return arguments;
    }

}
