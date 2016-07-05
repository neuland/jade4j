package de.neuland.jade4j.lexer.token;


public class Comment extends Token {

    public Comment(String value, int lineNumber) {
        super(value, lineNumber);
    }

    public Comment(String value, int lineNumber, boolean buffer) {
        super(value, lineNumber, buffer);
    }

}
