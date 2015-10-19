package de.neuland.jade4j.lexer.token;


public class Include extends Token {
	String filter;
	public Include(String value, int lineNumber) {
		super(value, lineNumber);
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
