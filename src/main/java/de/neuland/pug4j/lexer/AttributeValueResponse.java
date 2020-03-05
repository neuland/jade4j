package de.neuland.pug4j.lexer;

public class AttributeValueResponse {
    String value;
    boolean mustEscape;
    String remainingSource;

    public AttributeValueResponse(String value, boolean mustEscape, String remainingSource) {
        this.value = value;
        this.mustEscape = mustEscape;
        this.remainingSource = remainingSource;
    }

    public String getValue() {
        return value;
    }

    public boolean isMustEscape() {
        return mustEscape;
    }

    public String getRemainingSource() {
        return remainingSource;
    }
}
