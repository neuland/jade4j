package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.MarkdownFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.template.JadeTemplate;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OriginalJade20190911Test {
    private static String[] ignoredCases = new String[] {
        "filters.markdown",     // additional empty line
        "escape-test",          // html tag <textarea> closed on next line
        //"tag.interpolation",    // different attribute order
        "mixin-hoist",          // global variable `title = "Pug"` not present
        "filters.custom",


    };


    /*new String[]{"attrs", "attrs.js", "code.conditionals", "code.iteration", "comments",
            "escape-chars", "filters.coffeescript", "filters.less", "filters.markdown", "filters.stylus", "html", "include-only-text-body",
            "include-only-text", "include-with-text-head", "include-with-text", "mixin.blocks", "mixin.merge", "quotes", "script.whitespace", "scripts", "scripts.non-js",
            "source", "styles", "template", "text-block", "text", "vars", "yield-title", "doctype.default","comments.conditional","html5"};*/




    private File file;

    public OriginalJade20190911Test(String file) {
        this.file = new File(TestFileHelper.getOriginal20190911ResourcePath(file));
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        JadeConfiguration jade = new JadeConfiguration();
        jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());
        jade.setFilter("markdown", new MarkdownFilter());

        jade.setPrettyPrint(true);

        JadeTemplate template = jade.getTemplate(file.getPath());

        String html = jade.renderTemplate(template, new HashMap<String, Object>());

        String expected = readFile(asHtml(file));

        assertEquals(file.getName(), expected.trim(), html.trim());
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String[]> data() {
        File folder = new File(TestFileHelper.getOriginal20190911ResourcePath(""));
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"jade", "pug"}, false);

        Collection<String[]> data = new ArrayList<String[]>();
        for (File file : files) {
            if (!ArrayUtils.contains(ignoredCases, withoutEnding(file))) {
                data.add(new String[]{file.getName()});
            }

        }
        return data;
    }

    private static String withoutEnding(File file) {
        return file.getName()
            .replace(".jade", "")
            .replace(".pug", "");
    }

    private static String asHtml(File file) {
        return file.getPath()
            .replace(".jade", ".html")
            .replace(".pug", ".html");
    }

}
