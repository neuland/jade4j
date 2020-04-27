package de.neuland.pug4j.lexer;

import com.google.gson.Gson;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.expression.JexlExpressionHandler;
import de.neuland.pug4j.lexer.token.*;
import de.neuland.pug4j.template.FileTemplateLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OriginalLexer_0_0_8_Test {
    private static class ExpectedToken {


        String type;

        int line;
        public int col;

        String name;

        Object val;

        Boolean selfClosing;

        Boolean escape;
        Boolean escaped;

        Boolean buffer;
        String args;
        String mode;
    }

    private static String[] ignoredCases = new String[]{"regression.784"};

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
        mappedTypes.put("startpuginterpolation", "start-jade-interpolation");
        mappedTypes.put("endpuginterpolation", "end-jade-interpolation");
        mappedTypes.put("startpipelesstext", "start-pipeless-text");
        mappedTypes.put("endpipelesstext", "end-pipeless-text");
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
        return new Gson().newBuilder().disableHtmlEscaping().create().fromJson(expected, ExpectedToken.class);
    }

    private String tokenToJsonLine(ExpectedToken expected) {
        return new Gson().newBuilder().disableHtmlEscaping().create().toJson(expected);
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(TestFileHelper.getLexer_0_0_8_ResourcePath("cases/" + fileName)), Charset.forName("UTF-8"));
    }

    private String file;

    public OriginalLexer_0_0_8_Test(String file) {
        this.file = file;
    }

    @Test
    public void shouldLexJadeToTokens() throws Exception {
        FileTemplateLoader loader1 = new FileTemplateLoader(TestFileHelper.getLexer_0_0_8_ResourcePath("cases/"));
        Lexer lexer1 = new Lexer(file, loader1, new JexlExpressionHandler());
        LinkedList<Token> tokens = lexer1.getTokens();
        String[] expected = readFile(file.replace(".jade", ".expected.json")).split("\\n");
        ArrayList<String> actual = new ArrayList<String>();

        for (Token token : tokens) {
            ExpectedToken expectedToken = new ExpectedToken();
            expectedToken.type=typeOf(token);
            if(token instanceof Tag) {
                expectedToken.selfClosing = token.isSelfClosing();
            }
            if(token instanceof Attribute) {
                expectedToken.val = ((Attribute) token).getAttributeValue();
                expectedToken.name = token.getName();
//                expectedToken.escaped = ((Attribute) token).isEscaped();
            }else{
                expectedToken.val=token.getValue();
            }
            if(token instanceof Mixin){
                expectedToken.args=((Mixin) token).getArguments();
            }
            if(token instanceof Call){
                expectedToken.args=((Call) token).getArguments();
            }
            if(token instanceof Expression) {
                expectedToken.buffer = token.isBuffer();
                expectedToken.escape = ((Expression) token).isEscape();
            }
            if(token instanceof Block) {
                expectedToken.mode = token.getMode();
            }
            expectedToken.line=token.getStartLineNumber();
            expectedToken.col=token.getStartColumn();
            String s = tokenToJsonLine(expectedToken);
            actual.add(s);
        }
        assertToken(expected,actual);
//        for (int i = 0; i < expected.length; i++) {
//            Token token = tokens.get(i);
//            if (breakOnEndOfStreamTokens(token)) {
//                break;
//            }
//            ExpectedToken expectedToken = tokenFromJsonLine(expected[i]);
//            if (breakOnTextTokens(expectedToken)) {
//                break;
//            }
//            assertToken(token, expectedToken);
//        }
    }
    private void assertToken(String[] expected,List<String>actual){
        StringBuffer expectedString = new StringBuffer();
        for (String s : expected) {
            expectedString.append(s).append("\n");

        }
        StringBuffer actualString = new StringBuffer();
        for (String s : actual) {
            actualString.append(s).append("\n");
        }
        String expected1 = expectedString.toString();
        String actual1 = actualString.toString();
        assertEquals(expected1, actual1);
//        assertThat(actualString).isEqualTo(expectedString);
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
