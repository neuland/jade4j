package de.neuland.jade4j.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.model.JadeModel;

public class JadeRunFullTemplateTest {
	
	@Test
	public void testFullRun() throws IOException {
		JadeConfiguration cfg = new JadeConfiguration();
		//	cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("hello", "world");
		root.put("hallo", null);
		JadeModel model = new JadeModel(root);

		JadeTemplate temp = cfg.getTemplate(getResourcePath("fullrun"));

//		Writer out = new OutputStreamWriter(System.err);
		StringWriter out = new StringWriter();
		try {
            temp.process(model, out);
        } catch (JadeCompilerException e) {
            e.printStackTrace();
            fail();
        }
		out.flush();
		assertEquals("<div><div>Hi everybody</div></div>", out.toString());
	}

    public String getResourcePath(String fileName) {
    	try {
			return TestFileHelper.getRootResourcePath() + "/template/" + fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }
}

