package de.neuland.pug4j.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.neuland.pug4j.Pug4J;
import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.filter.CDATAFilter;
import de.neuland.pug4j.filter.MarkdownFilter;
import de.neuland.pug4j.filter.PlainFilter;
import de.neuland.pug4j.template.FileTemplateLoader;
import de.neuland.pug4j.template.PugTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@Ignore("Replaced By Pug2 Tests")
public class OriginalJade20190911Test {
    private static String[] ignoredCases = new String[] {
        "filter-in-include",
        "filters.markdown",     // additional empty line
        "filters-empty",
        "filters.custom",
        "filters.include",
        "filters.include.custom",
        "filters.stylus",
        "filters.inline",       // wrong line break, probably same as "inline-tag"
        "filters.less",
        "filters.coffeescript",


        "escape-test",          // html tag <textarea> closed on next line
        "mixin-hoist",          // global variable `title = "Pug"` not present
        "mixin-block-with-space",  //piped text does not appear
        "inline-tag",              // fails to treat pipes as new lines
        "includes",                 // different line breaks


        "styles",               // JSON representation compiles to Objects, not CSS !


        // try to read files in ../
        "mixin-via-include",
        "layout.append.without-block",
        "layout.prepend.without-block",
        "layout.multi.append.prepend.block",
        "include-extends-relative",
        "layout.prepend",

    };


    /*new String[]{"attrs", "attrs.js", "code.conditionals", "code.iteration", "comments",
            "escape-chars", "filters.coffeescript", "filters.less", "filters.markdown", "filters.stylus", "html", "include-only-text-body",
            "include-only-text", "include-with-text-head", "include-with-text", "mixin.blocks", "mixin.merge", "quotes", "script.whitespace", "scripts", "scripts.non-js",
            "source", "styles", "template", "text-block", "text", "vars", "yield-title", "doctype.default","comments.conditional","html5"};*/




    private File file;
    private static final String RESOURCE_PATH = TestFileHelper.getOriginal20190911ResourcePath("");

    public OriginalJade20190911Test(String file) {
        this.file = new File(file);
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        PugConfiguration jade = new PugConfiguration();
        jade.setMode(Pug4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setTemplateLoader(new FileTemplateLoader(RESOURCE_PATH,  "pug"));

        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());
        jade.setFilter("markdown", new MarkdownFilter());

        jade.setPrettyPrint(true);

        PugTemplate template = jade.getTemplate(file.getPath());

        String html = jade.renderTemplate(template, new HashMap<String, Object>());

        String expected = readFile(asHtml(file));

        assertEquals(file.getName(), expected.trim(), html.trim());
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String> data() {
        File folder = new File(RESOURCE_PATH);
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"jade", "pug"}, false);

        Collection<String> data = new ArrayList<String>();
        for (File file : files) {
            if (!ArrayUtils.contains(ignoredCases, withoutEnding(file))) {
                data.add(file.getName());
                System.out.println(file.getName());
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
        return RESOURCE_PATH + file.getPath()
            .replace(".jade", ".html")
            .replace(".pug", ".html");
    }

}
