package de.neuland.jade4j.lexer.token;


public class Call extends Token {

	private String arguments;

    public Call(String value, int lineNumber) {
		super(value, lineNumber);
	}

    public void setArguments(String args) {
        this.arguments = args;
    }
    
    public String getArguments() {
        return arguments;
    }

}
