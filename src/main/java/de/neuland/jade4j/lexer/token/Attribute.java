package de.neuland.jade4j.lexer.token;

/**
 * Created by christoph on 04.03.16.
 */
public class Attribute {
    String name;
    Object value;
    boolean escaped;

    public Attribute(String name, Object value, boolean escape) {
        this.name = name;
        this.value = value;
        this.escaped = escape;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEscaped() {
        return escaped;
    }
}
