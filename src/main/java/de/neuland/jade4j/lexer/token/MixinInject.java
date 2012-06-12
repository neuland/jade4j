package de.neuland.jade4j.lexer.token;


public class MixinInject extends Token {

	private String arguments;

    public MixinInject(String value, int lineNumber) {
		super(value, lineNumber);
	}

    public void setArguments(String args) {
        this.arguments = args;
    }
    
    public String getArguments() {
        return arguments;
    }

}
