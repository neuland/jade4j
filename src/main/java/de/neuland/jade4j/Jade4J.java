package de.neuland.jade4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.ReaderTemplateLoader;
import de.neuland.jade4j.template.TemplateLoader;

public class Jade4J {

	public enum Mode {
		HTML, XML, XHTML
	}

	public static String render(String filename, Map<String, Object> model) throws IOException, JadeCompilerException {
		return render(filename, model, false);
	}

	public static String render(String filename, Map<String, Object> model, boolean pretty) throws IOException, JadeCompilerException {
		JadeTemplate template = getTemplate(filename);
		template.setPrettyPrint(pretty);
		return templateToString(template, model);
	}

	public static void render(String filename, Map<String, Object> model, Writer writer) throws IOException, JadeCompilerException {
		render(filename, model, writer, false);
	}

	public static void render(String filename, Map<String, Object> model, Writer writer, boolean pretty) throws IOException,
			JadeCompilerException {
		JadeTemplate template = getTemplate(filename);
		template.setPrettyPrint(pretty);
		template.process(new JadeModel(model), writer);
	}

	public static String render(JadeTemplate template, Map<String, Object> model) throws JadeCompilerException {
		return render(template, model, false);
	}

	public static String render(JadeTemplate template, Map<String, Object> model, boolean pretty) throws JadeCompilerException {
		template.setPrettyPrint(pretty);
		return templateToString(template, model);
	}

	public static void render(JadeTemplate template, Map<String, Object> model, Writer writer) throws JadeCompilerException {
		render(template, model, writer, false);
	}

	public static void render(JadeTemplate template, Map<String, Object> model, Writer writer, boolean pretty) throws JadeCompilerException {
		template.setPrettyPrint(pretty);
		template.process(new JadeModel(model), writer);
	}

    public static String render(URL url, Map<String, Object> model) throws IOException, JadeCompilerException {
        return render(url, model, false);
    }

    public static String render(URL url, Map<String, Object> model, boolean pretty) throws IOException, JadeCompilerException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        JadeTemplate template = getTemplate(reader, url.getPath());
        return render(template, model, pretty);
    }

    public static String render(Reader reader, String filename, Map<String, Object> model) throws IOException, JadeCompilerException {
        return render(reader, filename, model, false);
    }

    public static String render(Reader reader, String filename, Map<String, Object> model, boolean pretty) throws IOException, JadeCompilerException {
        JadeTemplate template = getTemplate(reader, filename);
        return render(template, model, pretty);
    }

	public static JadeTemplate getTemplate(String filename) throws IOException {
		return createTemplate(filename, new FileTemplateLoader("", "UTF-8"));
	}

	private static JadeTemplate getTemplate(Reader reader, String name) throws IOException {
		return createTemplate(name, new ReaderTemplateLoader(reader, name));
	}

	private static JadeTemplate createTemplate(String filename, TemplateLoader loader) throws IOException {
		Parser parser = new Parser(filename, loader);
		Node root = parser.parse();
		JadeTemplate template = new JadeTemplate();
		template.setTemplateLoader(loader);
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