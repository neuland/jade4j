package de.neuland.pug4j.lexer;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotConfigInjector;
import au.com.origin.snapshots.SnapshotMatcher;
import au.com.origin.snapshots.junit4.JUnit4Config;
import au.com.origin.snapshots.junit4.SnapshotClassRule;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.expression.JexlExpressionHandler;
import de.neuland.pug4j.lexer.token.Token;
import de.neuland.pug4j.template.FileTemplateLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.io.comparator.NameFileComparator.*;


@RunWith(Parameterized.class)
public class PugLexerTest {

    public class SnapshotRule implements TestRule {
        public SnapshotRule() {
        }

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                public void evaluate() throws Throwable {
                    SnapshotMatcher.setTestMethod(description.getTestClass().getMethod("shouldCompileJadeToHtml"));
                    base.evaluate();
                }
            };
        }
    }
    private static String[] ignoredCases = new String[]{
            "attr-es2015",
            "attrs-data",
            "attrs.js",
            "attrs",
            "tags.self-closing",
            "text"
    };
    // Ensure you instantiate these rules
    @ClassRule
    public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
    @Rule
    public SnapshotRule snapshotRule = new SnapshotRule();
    private String file;

    public PugLexerTest(String file) {
        this.file = file;
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        String filename = "/cases/" + file;
        String basePath = TestFileHelper.getLexerResourcePath("");
        FileTemplateLoader templateLoader = new FileTemplateLoader(basePath, "UTF-8", "pug");
        Lexer lexer = new Lexer(filename, templateLoader, new JexlExpressionHandler());
        LinkedList<Token> tokens = lexer.getTokens();

        SnapshotMatcher.expect(tokens).scenario(filename).toMatchSnapshot();
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String[]> data() {
        File folder = new File(TestFileHelper.getLexerResourcePath("/cases/"));
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"pug"}, false);
        File[] objects = files.stream().toArray(File[]::new);
        Arrays.sort(objects, NAME_COMPARATOR);


        Collection<String[]> data = new LinkedList<String[]>();
        for (File file : objects) {
            if (!ArrayUtils.contains(ignoredCases, file.getName().replace(".pug", ""))) {
                data.add(new String[]{file.getName()});
            }

        }
        return data;
    }

}
