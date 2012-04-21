package de.neuland.jade4j.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

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
			System.out.println(e.toHtmlString());
			assertTrue(e.getMessage().startsWith("unable to evaluate [non.existing.query]"));
			assertEquals(9, e.getLineNumber());
			assertEquals(errorJade, e.getFilename());
			assertEquals(readFile(exceptionHtml), e.toHtmlString());
		}
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
