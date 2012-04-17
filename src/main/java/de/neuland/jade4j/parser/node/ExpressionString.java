package de.neuland.jade4j.parser.node;

public class ExpressionString {
	private String value = null;
	private boolean escape = false;

	public boolean isEscape() {
		return escape;
	}

	public ExpressionString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setEscape(boolean escape) {
		this.escape  = escape;
	}
}
