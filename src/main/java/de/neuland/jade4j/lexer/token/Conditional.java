package de.neuland.jade4j.lexer.token;

public class Conditional extends Token {

	private boolean inverseCondition = false;
	private boolean conditionActive = false;
	private boolean alternativeCondition = false;
	
	public Conditional(String value, int lineNumber) {
        super(value, lineNumber);
    }

	public boolean isInverseCondition() {
		return inverseCondition;
	}

	public void setInverseCondition(boolean inverseCondition) {
		this.inverseCondition = inverseCondition;
	}

	public boolean isConditionActive() {
		return conditionActive;
	}

	public void setConditionActive(boolean conditionActive) {
		this.conditionActive = conditionActive;
	}

	public boolean isAlternativeCondition() {
		return alternativeCondition;
	}

	public void setAlternativeCondition(boolean alternativeCondition) {
		this.alternativeCondition = alternativeCondition;
	}

	
	
}
