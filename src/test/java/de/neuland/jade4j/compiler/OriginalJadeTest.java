package de.neuland.jade4j.compiler;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.template.JadeTemplate;

public class OriginalJadeTest {

	private String[] manualCompared = new String[] { "attrs", "attrs.js", "code.conditionals", "code.iteration", "comments",
			"escape-chars", "filters.coffeescript", "filters.less", "filters.markdown", "filters.stylus", "html", "include-only-text-body",
			"include-only-text", "include-with-text-head", "include-with-text", "mixin.blocks", "mixin.merge", "quotes", "script.whitespace", "scripts", "scripts.non-js",
			"source", "styles", "template", "text-block", "text", "vars", "yield-title", "doctype.default" };

	@Test
	public void test() throws IOException, JadeCompilerException {
		File folder = new File(TestFileHelper.getOriginalResourcePath(""));
		Collection<File> files = FileUtils.listFiles(folder, new String[] { "jade" }, false);

		JadeConfiguration jade = new JadeConfiguration();
		jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
		jade.setFilter("plain", new PlainFilter());
		jade.setFilter("cdata", new CDATAFilter());

		for (File file : files) {
			JadeTemplate template = jade.getTemplate(file.getPath());
			Writer writer = new StringWriter();
			jade.renderTemplate(template, new HashMap<String, Object>(), writer);
			String html = writer.toString();

			String expected = readFile(file.getPath().replace(".jade", ".html"));
			// System.out.println("\n>> " + file.getName());
			// System.out.println(html);
			// System.out.println("-- " + file.getName());
			// System.out.println(expected);
			// System.out.println("<< " + file.getName());

			if (!ArrayUtils.contains(manualCompared, file.getName().replace(".jade", ""))) {
				assertEquals(file.getName(), expected, html);
			}
		}
	}

	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return "";
	}
}
