package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.CssFilter;
import de.neuland.jade4j.filter.CustomTestFilter;
import de.neuland.jade4j.filter.JsFilter;
import de.neuland.jade4j.filter.MarkdownFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.filter.VerbatimFilter;
import de.neuland.jade4j.helper.FormatHelper;
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.ReaderTemplateLoader;
import de.neuland.jade4j.template.TemplateLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class IssuesTest {
    private static final Charset FILES_ENCODING = Charset.forName("UTF-8");
    private static String[] ignoredCases = new String[]{"131"};

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

    @Test
    public void shouldCompileJadeToHtmlWithReaderTemplateLoader() throws Exception {
        List<String> additionalIgnoredCases = Arrays.asList("52", "74", "100", "104a", "104b", "123", "135");
        if (additionalIgnoredCases.contains(file.replace(".jade", ""))) {
            return;
        }
        String issuesResourcePath = TestFileHelper.getIssuesResourcePath("");
        String pathToFile = issuesResourcePath + File.separator + file;
        InputStreamReader reader = new InputStreamReader(new FileInputStream(pathToFile), FILES_ENCODING);
        String templateName = file;
        ReaderTemplateLoader templateLoader = new ReaderTemplateLoader(reader, templateName);

        compareJade(templateLoader, templateName);
    }

    private void compareJade(TemplateLoader templateLoader, String templateName) throws IOException {
        JadeConfiguration jade = new JadeConfiguration();
        jade.setTemplateLoader(templateLoader);
        jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());
        jade.setFilter("custom-filter", new CustomTestFilter());
        jade.setFilter("marked", new MarkdownFilter());
        jade.setFilter("markdown", new MarkdownFilter());
        jade.setFilter("verbatim", new VerbatimFilter());
        jade.setFilter("js", new JsFilter());
        jade.setFilter("css", new CssFilter());

        jade.setPrettyPrint(true);

        JadeTemplate template = jade.getTemplate(templateName);
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("title", "Jade");
        model.put("format", new FormatHelper());
        String html = jade.renderTemplate(template, model);

        String expected = readFile(file.replace(".jade", ".html"))
                .trim()
                .replaceAll("\r", "");

        assertEquals(file, expected, html.trim());
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(TestFileHelper.getIssuesResourcePath(fileName)), "UTF-8");
    }

    @Parameterized.Parameters(name = "{0}")
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
