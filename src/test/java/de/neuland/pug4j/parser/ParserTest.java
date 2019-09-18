package de.neuland.pug4j.parser;

import java.io.IOException;

import de.neuland.pug4j.expression.JexlExpressionHandler;
import org.junit.Assert;

import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.FileTemplateLoader;

public class ParserTest {

	protected Parser parser;
	protected Node root;

	protected void loadInParser(String fileName) {
		loadInParser(fileName,"jade");
	}
	protected void loadInParser(String fileName,String extension) {

		try {
			FileTemplateLoader loader = new FileTemplateLoader(
					TestFileHelper.getParserResourcePath(""), "UTF-8",extension);
			parser = new Parser(fileName, "", loader, new JexlExpressionHandler());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("template " + fileName + " was not found");
		}
		root = parser.parse();
	}
}
