package de.neuland.jade4j.parser.node;

/**
 * Created by christoph on 29.10.15.
 */
public class Attr {
    private String name;
    private Object value;
    private boolean escaped;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setEscaped(boolean escaped) {
        this.escaped = escaped;
    }

    public boolean isEscaped() {
        return escaped;
    }
}
