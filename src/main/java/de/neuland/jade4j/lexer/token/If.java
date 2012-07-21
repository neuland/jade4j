package de.neuland.jade4j.lexer.token;

public class If extends Token {

	private boolean inverseCondition = false;
	private boolean alternativeCondition = false;
	
	public If(String value, int lineNumber) {
        super(value, lineNumber);
    }

	public boolean isInverseCondition() {
		return inverseCondition;
	}

	public void setInverseCondition(boolean inverseCondition) {
		this.inverseCondition = inverseCondition;
	}

	public boolean isAlternativeCondition() {
		return alternativeCondition;
	}

	public void setAlternativeCondition(boolean alternativeCondition) {
		this.alternativeCondition = alternativeCondition;
	}

	
	
}
