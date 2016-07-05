package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.lexer.token.Doctypes;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

import java.util.HashMap;
import java.util.Map;

public class DoctypeNode extends Node {
	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		String name = getValue();
		template.setDoctype(name);
		writer.append(template.getDoctypeLine());
		writer.setCompiledDoctype(true);
	}

}
