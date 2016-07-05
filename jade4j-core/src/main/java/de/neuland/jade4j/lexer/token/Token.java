package de.neuland.jade4j.lexer.token;

import java.util.ArrayList;

public abstract class Token {

	private String value;
    private ArrayList<String> values;
    private int lineNumber;
    private boolean buffer = false;
    private String mode;
    private String name;
    private int indents;
    private boolean selfClosing = false;

    public Token(String value, int lineNumber) {
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public Token(String value, int lineNumber, boolean buffer) {
        this.value = value;
        this.lineNumber = lineNumber;
        this.buffer = buffer;
    }

	public String getValue() {
		return this.value;
	}
	
	public int getLineNumber() {
        return lineNumber;
    }
	
	public void setBuffer(boolean buffer) {
        this.buffer = buffer;
    }
	
	public boolean isBuffer() {
        return buffer;
    }
	
	public void setMode(String mode) {
        this.mode = mode;
    }
	
	public String getMode() {
        return mode;
    }
	
	@Override
	public String toString() {
		return value;
	}
	
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIndents(int indents) {
        this.indents = indents;
    }
    
    public int getIndents() {
        return indents;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelfClosing() {
        return selfClosing;
    }

    public void setSelfClosing(boolean selfClosing) {
        this.selfClosing = selfClosing;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
