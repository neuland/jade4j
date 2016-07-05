package de.neuland.jade4j.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.TestFileHelper;

public class JadeExceptionTest {

	@Test
	public void test() throws Exception {
		String errorJade = TestFileHelper.getCompilerResourcePath("exceptions/error.jade");
		String exceptionHtml = TestFileHelper.getCompilerResourcePath("exceptions/error.html");
		try {
			Jade4J.render(errorJade, new HashMap<String, Object>());
			fail();
		} catch (JadeException e) {
			assertTrue(e.getMessage().startsWith("unable to evaluate [non.existing.query()]"));
			assertEquals(9, e.getLineNumber());
			assertEquals(errorJade, e.getFilename());
			String expectedHtml = readFile(exceptionHtml);
			String html = e.toHtmlString("<html><head><title>broken");
			assertEquals(removeAbsolutePath(expectedHtml), removeAbsolutePath(html));
		}
	}

	@Test
	public void testMessage() throws Exception {
		try {
			throw new JadeLexerException("invalid indentation; expecting 2 spaces", "index.jade", 10, new FileTemplateLoader(TestFileHelper.getLexerResourcePath(""), "UTF-8"));
		}catch(Exception e){
			assertEquals("invalid indentation; expecting 2 spaces in index.jade:10",e.getMessage());
			assertEquals("class de.neuland.jade4j.exceptions.JadeLexerException: invalid indentation; expecting 2 spaces in index.jade:10",e.toString());
		}

	}

	private String removeAbsolutePath(String html) {
		html = html.replaceAll("(<h2>In ).*(compiler/exceptions/error\\.jade at line 9\\.</h2>)", "$1\\.\\./$2");
		html = html.replaceAll("(\\s)[^\\s]*(compiler/exceptions/error\\.jade:9)", "$1\\.\\./$2");
		return html;
	}

	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
