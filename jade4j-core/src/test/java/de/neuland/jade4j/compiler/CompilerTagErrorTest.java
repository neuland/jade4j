package de.neuland.jade4j.compiler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import de.neuland.jade4j.expression.JexlExpressionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.filter.MarkdownFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.FileTemplateLoader;

public class CompilerTagErrorTest {

    @Test(expected=Exception.class)
    public void testTagsWithErrors() {
        run("tags_with_errors");
    }

    private void run(String testName) {
        run(testName, false);
    }

    private void run(String testName, boolean pretty) {
        JadeModel model = new JadeModel(getModelMap(testName));
        run(testName, pretty, model);
    }

    private void run(String testName, boolean pretty, JadeModel model) {
        Parser parser = null;
        try {
            FileTemplateLoader loader = new FileTemplateLoader(TestFileHelper.getCompilerErrorsResourcePath(""),
                    "UTF-8");
            parser = new Parser(testName, loader, new JexlExpressionHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Node root = parser.parse();
        Compiler compiler = new Compiler(root);
        compiler.setPrettyPrint(pretty);
        String expected = readFile(testName + ".html");
        model.addFilter("markdown", new MarkdownFilter());
        model.addFilter("plain", new PlainFilter());
        String html;
        try {
            html = compiler.compileToString(model);
            assertEquals(testName, expected.trim(), html.trim());
            fail();
        } catch (JadeCompilerException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getModelMap(String testName) {
        String json = readFile(testName + ".json");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> model = gson.fromJson(json, type);
        if (model == null) {
            model = new HashMap<String, Object>();
        }
        return model;
    }

    private String readFile(String fileName) {
        try {
            return FileUtils.readFileToString(new File(TestFileHelper.getCompilerErrorsResourcePath(fileName)));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return "";
    }

}
