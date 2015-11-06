package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.template.JadeTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OriginalJadeTest {
    private static String[] ignoredCases = new String[]{"attrs", "attrs.js", "code.conditionals", "code.iteration", "comments",
            "escape-chars", "filters.coffeescript", "filters.less", "filters.markdown", "filters.stylus", "html", "include-only-text-body",
            "include-only-text", "include-with-text-head", "include-with-text", "mixin.blocks", "mixin.merge", "quotes", "script.whitespace", "scripts", "scripts.non-js",
            "source", "styles", "template", "text-block", "text", "vars", "yield-title", "doctype.default","comments.conditional","html5"};

    private File file;

    public OriginalJadeTest(String file) {
        this.file = new File(TestFileHelper.getOriginalResourcePath(file));
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        JadeConfiguration jade = new JadeConfiguration();
        jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());

        JadeTemplate template = jade.getTemplate(file.getPath());
        Writer writer = new StringWriter();
        jade.renderTemplate(template, new HashMap<String, Object>(), writer);
        String html = writer.toString();

        String expected = readFile(file.getPath().replace(".jade", ".html")).trim().replaceAll("\r", "");

        assertEquals(file.getName(), expected, html.trim());
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String[]> data() {
        File folder = new File(TestFileHelper.getOriginalResourcePath(""));
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"jade"}, false);

        Collection<String[]> data = new ArrayList<String[]>();
        for (File file : files) {
            if (!ArrayUtils.contains(ignoredCases, file.getName().replace(".jade", ""))) {
                data.add(new String[]{file.getName()});
            }

        }
        return data;
    }
}
