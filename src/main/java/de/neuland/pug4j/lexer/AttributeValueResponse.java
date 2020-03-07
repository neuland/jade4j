package de.neuland.pug4j.lexer;

public class AttributeValueResponse {
    Object value;
    boolean mustEscape;
    String remainingSource;

    public AttributeValueResponse(Object value, boolean mustEscape, String remainingSource) {
        this.value = value;
        this.mustEscape = mustEscape;
        this.remainingSource = remainingSource;
    }

    public Object getValue() {
        return value;
    }

    public boolean isMustEscape() {
        return mustEscape;
    }

    public String getRemainingSource() {
        return remainingSource;
    }
}
