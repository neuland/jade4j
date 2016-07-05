package de.neuland.jade4j.lexer;

import de.neuland.jade4j.lexer.token.Token;

public class Each extends Token {

	private String code;
	private String key;

	public Each(String value, int lineNumber) {
		super(value, lineNumber);
	}

	public void setCode(String code) {
		this.code = code;		
	}
	
	public String getCode() {
		return code;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
