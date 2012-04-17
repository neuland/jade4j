package de.neuland.jade4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;

public class Jade4J {

	public static String render(String filename, Map<String, Object> model) throws IOException, JadeCompilerException {
		return templateToString(getTemplate(filename), model);
	}

	public static void render(String filename, Map<String, Object> model, Writer writer) throws IOException, JadeCompilerException {
		getTemplate(filename).process(new JadeModel(model), writer);
	}

	public static String render(JadeTemplate template, Map<String, Object> model) throws JadeCompilerException {
		return templateToString(template, model);
	}

	public static void render(JadeTemplate template, Map<String, Object> model, Writer writer) throws JadeCompilerException {
		template.process(new JadeModel(model), writer);
	}

	public static JadeTemplate getTemplate(String filename) throws IOException {
		TemplateLoader loader = new FileTemplateLoader("", "UTF-8");

		Parser parser = new Parser(filename, loader);
		Node root = parser.parse();
		JadeTemplate template = new JadeTemplate();
		template.setRootNode(root);
		return template;
	}

	private static String templateToString(JadeTemplate template, Map<String, Object> model) throws JadeCompilerException {
		JadeModel jadeModel = new JadeModel(model);
		StringWriter writer = new StringWriter();

		template.process(jadeModel, writer);
		return writer.toString();
	}

}