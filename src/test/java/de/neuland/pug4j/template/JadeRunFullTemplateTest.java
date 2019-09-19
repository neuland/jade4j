package de.neuland.pug4j.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import de.neuland.pug4j.helper.beans.IterableMap;
import org.junit.Before;
import org.junit.Test;

import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;

public class JadeRunFullTemplateTest {

    private PugConfiguration cfg = new PugConfiguration();


    @Before
    public void setUp() throws Exception {
        cfg.setTemplateLoader(new FileTemplateLoader("", "UTF-8","jade"));
    }

    @Test
    public void testFullRun() throws IOException {

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("hello", "world");
        root.put("hallo", null);
        PugModel model = new PugModel(root);

        PugTemplate temp = cfg.getTemplate(getResourcePath("fullrun"));

        StringWriter out = new StringWriter();
        try {
            temp.process(model, out);
        } catch (PugCompilerException e) {
            e.printStackTrace();
            fail();
        }
        out.flush();
        assertEquals("<div><div>Hi everybody</div></div>", out.toString());

    }

    @Test
    public void testEachLoopWithIterableMap() throws Exception {

        IterableMap users = new IterableMap();
        users.put("bob", "Robert Smith");
        users.put("alex", "Alex Supertramp");

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("users", users);
        PugModel model = new PugModel(root);

        PugTemplate temp = cfg.getTemplate(getResourcePath("each_loop"));

        StringWriter out = new StringWriter();
        try {
            temp.process(model, out);
        } catch (PugCompilerException e) {
            e.printStackTrace();
            fail();
        }
        out.flush();
        assertEquals("<ul><li>Robert Smith</li><li>Alex Supertramp</li></ul>", out.toString());

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

