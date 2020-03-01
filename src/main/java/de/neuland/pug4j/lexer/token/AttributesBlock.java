package de.neuland.pug4j.lexer.token;

public class AttributesBlock extends Token {
    public AttributesBlock() {
    }

    public AttributesBlock(String value) {
        super(value);
    }

    public AttributesBlock(String value, int lineNumber) {
        super(value, lineNumber);
    }
}
