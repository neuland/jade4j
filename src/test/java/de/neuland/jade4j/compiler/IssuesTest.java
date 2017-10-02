package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.expression.JsExpressionHandler;
import de.neuland.jade4j.filter.*;
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class IssuesTest {
    private static String[] ignoredCases = new String[]{"100","131","153"};

    private String file;

    public IssuesTest(String file) {
        this.file = file;
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        FileTemplateLoader templateLoader = new FileTemplateLoader(TestFileHelper.getIssuesResourcePath(""), "UTF-8");
        String templateName = file;

        compareJade(templateLoader, templateName);
    }

    @Test
    public void shouldCompileJadeToHtmlWithClasspathTemplateLoader() throws Exception {
        ClasspathTemplateLoader templateLoader = new ClasspathTemplateLoader();
        String templateName = "issues/" + file;

        compareJade(templateLoader, templateName);
    }

    private void compareJade(TemplateLoader templateLoader, String templateName) throws IOException {
        JadeConfiguration jade = new JadeConfiguration();
        jade.setTemplateLoader(templateLoader);
//        jade.setExpressionHandler(new JsExpressionHandler());
        jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());
        jade.setFilter("custom-filter", new CustomTestFilter());
        jade.setFilter("marked", new MarkdownFilter());
        jade.setFilter("markdown", new MarkdownFilter());
        jade.setFilter("verbatim", new VerbatimFilter());
        jade.setFilter("js", new JsFilter());
        jade.setFilter("css", new CssFilter());
//        jade.setFilter("coffee-script", new CoffeeScriptFilter());

        jade.setPrettyPrint(true);

        JadeTemplate template = jade.getTemplate(templateName);
        Writer writer = new StringWriter();
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("title","Jade");
        jade.renderTemplate(template,model, writer);
        String html = writer.toString();

        String expected = readFile(file.replace(".jade", ".html")).trim().replaceAll("\r", "");

        assertEquals(file, expected, html.trim());
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(TestFileHelper.getIssuesResourcePath(fileName)));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String[]> data() {
        File folder = new File(TestFileHelper.getIssuesResourcePath(""));
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"jade"}, false);

        Collection<String[]> data = new ArrayList<String[]>();
        for (File file : files) {
            if (!ArrayUtils.contains(ignoredCases, file.getName().replace(".jade", "")) && !file.getName().startsWith("_")) {
                data.add(new String[]{file.getName()});
            }

        }
        return data;
    }
}
