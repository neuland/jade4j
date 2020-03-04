package de.neuland.pug4j.lexer.token;

import java.util.ArrayList;

public abstract class Token implements Cloneable{

	private String value;
    private ArrayList<String> values;
    private int startLineNumber;
    private int startColumn;
    private int endLineNumber;
    private int endColumn;
    private String fileName;
    private boolean buffer = false;
    private String mode;
    private String name;
    private int indents;
    private boolean selfClosing = false;
    private String type = this.getType();
    public Token(){
    }

    public Token(String value){
        this.value = value;
    }
    public Token(String value, boolean buffer){
        this.value = value;
        this.buffer = buffer;
    }
    public Token(String value, int startLineNumber) {
        this.value = value;
        this.startLineNumber = startLineNumber;
    }

    public Token(String value, int startLineNumber, int startColumn) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.startColumn = startColumn;
    }

    public Token(String value, int startLineNumber, boolean buffer) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.buffer = buffer;
    }

    public Token(String value, int startLineNumber, int startColumn, boolean buffer) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.startColumn = startColumn;
        this.buffer = buffer;
    }
    public Token(String value, int startLineNumber, String filename) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.fileName = filename;
    }

    public Token(String value, int startLineNumber, int startColumn, String filename) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.startColumn = startColumn;
        this.fileName = filename;
    }

    public Token(String value, int startLineNumber, String filename, boolean buffer) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.fileName = filename;
        this.buffer = buffer;
    }

    public Token(String value, int startLineNumber, int startColumn, String filename, boolean buffer) {
        this.value = value;
        this.startLineNumber = startLineNumber;
        this.startColumn = startColumn;
        this.fileName = filename;
        this.buffer = buffer;
    }
	public String getValue() {
		return this.value;
	}
	
	public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getType(){
        return this.getClass().getSimpleName();
    }
    public Token clone() throws CloneNotSupportedException {
        return (Token) super.clone();
    }
}
