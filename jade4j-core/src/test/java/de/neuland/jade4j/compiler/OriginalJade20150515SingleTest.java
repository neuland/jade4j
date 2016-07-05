package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.filter.*;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;


@Ignore
public class OriginalJade20150515SingleTest {
	String templateName = "attrs2";

	@Test
	public void testCase() throws IOException, JadeCompilerException {
		JadeConfiguration jade = getJadeConfiguration();
		Lexer lexer = initLexer(templateName,jade);
		LinkedList<Token> tokens = lexer.getTokens();
		testJade(templateName, jade);
	}

	protected Lexer initLexer(String fileName, JadeConfiguration jade) {
     try {
         TemplateLoader loader = jade.getTemplateLoader();
		 ExpressionHandler expressionHandler = jade.getExpressionHandler();
		 return new Lexer(fileName+".jade", loader, expressionHandler);
     } catch (Exception e) {
         e.printStackTrace();
         return null;
     }
 }

	private void testJade(String path, JadeConfiguration jade) throws IOException {
		File file = new File(getResourcePath(path + ".jade"));
		JadeTemplate template = jade.getTemplate(path+".jade");
		Writer writer = new StringWriter();
		HashMap<String, Object> model = getModel();
		jade.renderTemplate(template, model, writer);
		String html = writer.toString();

		String expected = readFile(file.getPath().replace(".jade", ".html")).trim().replaceAll("\r", "");
		assertEquals(file.getName(), expected, html.trim());
	}


	private HashMap<String, Object> getModel() {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("title","Jade");
		return model;
	}

	private JadeConfiguration getJadeConfiguration() {
		JadeConfiguration jade = new JadeConfiguration();
		jade.setTemplateLoader(new FileTemplateLoader(getResourcePath(""), "UTF-8"));
		//		jade.setExpressionHandler(new JsExpressionHandler());
		jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
		jade.setFilter("plain", new PlainFilter());
		jade.setFilter("cdata", new CDATAFilter());
		jade.setFilter("marked", new MarkdownFilter());
		jade.setFilter("custom-filter", new CustomTestFilter());
		jade.setFilter("verbatim", new VerbatimFilter());
		jade.setPrettyPrint(true);
		return jade;
	}


	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return "";
	}
	private String getResourcePath(String filename) {
		try {
			return TestFileHelper.getLexerResourcePath(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
