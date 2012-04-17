package de.neuland.jade4j.parser.node;

import de.neuland.jade4j.compiler.IndentWriter;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.lexer.token.Doctypes;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.template.JadeTemplate;

public class DoctypeNode extends Node {

	@Override
	public void execute(IndentWriter writer, JadeModel model, JadeTemplate template) throws JadeCompilerException {
		String name = getValue();
		if (name == null) {
			name = "5";
		}
		String doctypeLine = Doctypes.get(name);
		if (doctypeLine == null) {
			doctypeLine = "<!DOCTYPE " + name + ">";
		}
		template.setTerse(isTerseDoctype(name));
		template.setXml(isXmlDoctype(doctypeLine));
		writer.append(doctypeLine);
	}

	private boolean isXmlDoctype(String doctype) {
		return doctype.startsWith("<?xml");
	}

	private boolean isTerseDoctype(String name) {
		return "5".equals(name) || "html".equals(name);
	}
}
