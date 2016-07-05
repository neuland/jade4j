package de.neuland.jade4j.util;

/**
 * Created by christoph on 14.10.15.
 */
public class Options {
    private int start = 0;
    private int end = 0;
    boolean includeLineComment = false;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isIncludeLineComment() {
        return includeLineComment;
    }

    public void setIncludeLineComment(boolean includeLineComment) {
        this.includeLineComment = includeLineComment;
    }

}
