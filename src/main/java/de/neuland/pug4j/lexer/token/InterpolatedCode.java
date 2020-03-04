package de.neuland.pug4j.lexer.token;

public class InterpolatedCode extends Token {
    private boolean mustEscape;

    public InterpolatedCode() {
    }

    public void setMustEscape(boolean mustEscape) {
        this.mustEscape = mustEscape;
    }

    public boolean isMustEscape() {
        return mustEscape;
    }
}
