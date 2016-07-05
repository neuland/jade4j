package de.neuland.jade4j.lexer;

import com.google.gson.Gson;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.expression.JexlExpressionHandler;
import de.neuland.jade4j.lexer.token.Eos;
import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.template.FileTemplateLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OriginalLexer_0_0_8_Test {
    private static class ExpectedToken {

        String type;

        int line;

        String val;

        boolean selfClosing;
    }

    private static String[] ignoredCases = new String[]{"html", "yield-before-conditional-head", "each.else", "inheritance.extend.mixins", "while", "mixin-block-with-space"};

    private static Map<String, String> mappedTypes = new HashMap<String, String>();

    static {
        mappedTypes.put("attributelist", "attrs");
        mappedTypes.put("cssid", "id");
        mappedTypes.put("cssclass", "class");
        mappedTypes.put("colon", ":");
        mappedTypes.put("expression", "code");
        mappedTypes.put("extendstoken", "extends");
        mappedTypes.put("mixinblock", "mixin-block");
        mappedTypes.put("pipelesstext", "start-pipeless-text");
        mappedTypes.put("attributesblock", "&attributes");
        mappedTypes.put("casetoken", "case");
    }


    //TODO: Handle Text Tokens correct
    private boolean breakOnTextTokens(ExpectedToken expectedToken) {
        return "text".equals(expectedToken.type);
    }

    //TODO:  FileReader do not reads last line correct => Scanner.java
    private boolean breakOnEndOfStreamTokens(Token token) {
        return token instanceof Eos;
    }

    private String typeOf(Token token) {
        String simpleClassName = token.getClass().getSimpleName().toLowerCase();
        if (mappedTypes.containsKey(simpleClassName)) {
            return mappedTypes.get(simpleClassName);
        }
        return simpleClassName;
    }

    private ExpectedToken tokenFromJsonLine(String expected) {
        return new Gson().fromJson(expected, ExpectedToken.class);
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(TestFileHelper.getLexer_0_0_8_ResourcePath("cases/" + fileName)));
    }

    private String file;

    public OriginalLexer_0_0_8_Test(String file) {
        this.file = file;
    }

    @Test
    public void shouldLexJadeToTokens() throws Exception {
        FileTemplateLoader loader1 = new FileTemplateLoader(TestFileHelper.getLexer_0_0_8_ResourcePath("cases/"), "UTF-8");
        Lexer lexer1 = new Lexer(file, loader1, new JexlExpressionHandler());
        LinkedList<Token> tokens = lexer1.getTokens();
        String[] expected = readFile(file.replace(".jade", ".expected.json")).split("\\n");

        for (int i = 0; i < expected.length; i++) {
            Token token = tokens.get(i);
            if (breakOnEndOfStreamTokens(token)) {
                break;
            }
            ExpectedToken expectedToken = tokenFromJsonLine(expected[i]);
            if (breakOnTextTokens(expectedToken)) {
                break;
            }
            assertToken(token, expectedToken);
        }
    }

    private void assertToken(Token token, ExpectedToken expectedToken) {
        assertThat(typeOf(token)).isEqualTo(expectedToken.type);
        assertThat(token.isSelfClosing()).isEqualTo(expectedToken.selfClosing);
        assertThat(token.getValue()).isEqualTo(expectedToken.val);
        //TODO: FileReader do not reads last line correct => Scanner.java
        //assertThat(token.getLineNumber()).isEqualTo(expectedToken.line);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> data() throws FileNotFoundException {
        File folder = new File(TestFileHelper.getLexer_0_0_8_ResourcePath("cases/"));
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
