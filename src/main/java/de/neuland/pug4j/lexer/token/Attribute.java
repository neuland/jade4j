package de.neuland.pug4j.lexer.token;

/**
 * Created by christoph on 04.03.16.
 */
public class Attribute extends Token {
    String name;
    Object attributeValue;
    boolean escaped;

    public Attribute() {
    }

    public Attribute(String name, Object attributeValue, boolean escape) {
        this.name = name;
        this.attributeValue = attributeValue;
        this.escaped = escape;
    }

    public String getName() {
        return name;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public boolean isEscaped() {
        return escaped;
    }

    public void setEscaped(boolean escaped) {
        this.escaped = escaped;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setAttributeValue(Object attributeValue) {
        this.attributeValue = attributeValue;
    }
}
