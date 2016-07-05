package de.neuland.jade4j.lexer.token;


public class Expression extends Token {
    
    private boolean escape;

	public Expression(String value, int lineNumber) {
		super(value, lineNumber);
	}

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
	
}
