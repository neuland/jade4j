package de.neuland.jade4j.parser.node;

/**
 * Created by christoph on 22.10.15.
 */
public class ValueString {
    private String value;
    private boolean escape;

    public ValueString(String value) {
        this.value = value;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public boolean isEscape() {
        return escape;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
