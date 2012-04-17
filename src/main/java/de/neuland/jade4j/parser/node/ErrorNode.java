package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.CompilerErrorException;
import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class ErrorNode extends Node {
	
	private StringBuilder sb = null;

    public ErrorNode(String filename, String value) {
        fileName = filename;
        sb = new StringBuilder(value);
    }
    
    public void appendText(String txt) {
    	if (sb == null) {
    		sb = new StringBuilder();
    	}
    	sb.append(txt);
    }
    
    public void setValue(String value) {
    	sb = new StringBuilder(value);
    }
    
    public String getValue() {
    	return sb.toString();
    }
    
    @Override
    public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
        throw new JadeCompilerException(this, new CompilerErrorException(this));
    }
    
    public void addNode(Node node) {
        nodes.add(node);
    }
}
