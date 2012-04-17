package de.neuland.jade4j.compiler;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;

public class IndentWriter {
    private int indent = 0;
    private boolean useIndent = false;
    private boolean empty = true;
    private Writer writer;

    public IndentWriter(Writer writer) {
        this.writer = writer;
    }
    
    public IndentWriter add(String string) {
    	return append(string);
    }
    
    public IndentWriter append(String string) {
        write(string);
        return this;
    }
    
    public void increment() {
        indent++;
    }
    
    public void decrement() {
        indent--;
    }
    
    private void write(String string) {
		try {
			writer.write(string);
			empty = false;
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    public String toString() {
        return writer.toString();
    }

	public void newline() {
		if (useIndent && !empty) {
			write("\n" + StringUtils.repeat("  ", indent));
		}
	}

	public void setUseIndent(boolean useIndent) {
		this.useIndent = useIndent;
	}
}
