package de.neuland.pug4j.lexer;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugLexerException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.lexer.token.*;
import de.neuland.pug4j.template.TemplateLoader;
import de.neuland.pug4j.util.CharacterParser;
import de.neuland.pug4j.util.Options;
import de.neuland.pug4j.util.StringReplacer;
import de.neuland.pug4j.util.StringReplacerCallback;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static final Pattern cleanRe = Pattern.compile("^['\"]|['\"]$");
    private static final Pattern doubleQuotedRe = Pattern.compile("^\"[^\"]*\"$");
    private static final Pattern quotedRe = Pattern.compile("^'[^']*'$");
    public static final Pattern PATTERN_MIXIN_BLOCK = Pattern.compile("^block");
    public static final Pattern PATTERN_YIELD = Pattern.compile("^yield");
    public static final Pattern PATTERN_DOT = Pattern.compile("^\\.");
    public static final Pattern PATTERN_DEFAULT = Pattern.compile("^default");
    public static final Pattern PATTERN_CASE = Pattern.compile("^case +([^\\n]+)");
    public static final Pattern PATTERN_WHEN = Pattern.compile("^when +([^:\\n]+)");
    public static final Pattern PATTERN_PATH = Pattern.compile("^ ([^\\n]+)");
    public static final Pattern PATTERN_TEXT_1 = Pattern.compile("^(?:\\| ?| )([^\\n]+)");
    public static final Pattern PATTERN_TEXT_2 = Pattern.compile("^( )");
    public static final Pattern PATTERN_TEXT_3 = Pattern.compile("^\\|( ?)");
    public static final Pattern PATTERN_FILTER = Pattern.compile("^:([\\w\\-]+)");
    public static final Pattern PATTERN_COLON = Pattern.compile("^: +");
    public static final Pattern PATTERN_SLASH = Pattern.compile("^\\/");
    public static final Pattern PATTERN_TAG = Pattern.compile("^(\\w(?:[-:\\w]*\\w)?)");
    public static final Pattern PATTERN_INTERPOLATION = Pattern.compile("^#\\{");
    public static final Pattern PATTERN_BLANK = Pattern.compile("^\\n[ \\t]*\\n");
    public static final Pattern PATTERN_INCLUDE = Pattern.compile("^include(?=:| |$|\\n)");
    public static final Pattern PATTERN_CONDITIONAL = Pattern.compile("^(if|unless|else if|else)\\b([^\\n]*)");
    public static final Pattern PATTERN_EACH = Pattern.compile("^(?:each|for) +([a-zA-Z_$][\\w$]*)(?: *, *([a-zA-Z_$][\\w$]*))? * in *([^\\n]+)");
    public static final Pattern PATTERN_WHILE = Pattern.compile("^while +([^\\n]+)");
    public static final Pattern PATTERN_CODE = Pattern.compile("^(!?=|-)[ \\t]*([^\\n]+)");
    public static final Pattern PATTERN_ATTRIBUTES_BLOCK = Pattern.compile("^&attributes\\b");
    @SuppressWarnings("unused")
    private LinkedList<String> options;
    Scanner scanner;
    private LinkedList<Token> deferredTokens;
    private int lastIndents = -1;
    private int lineno = 1;
    private int colno = 1;
    private LinkedList<Token> tokens;
    private LinkedList<Integer> indentStack;
    private Pattern indentRe = null;
    private boolean pipeless = false;
    private boolean interpolationAllowed = true;
    @SuppressWarnings("unused")
    private boolean attributeMode;
    private final String filename;
    private final TemplateLoader templateLoader;
    private String indentType;
    private CharacterParser characterParser;
    private ExpressionHandler expressionHandler;
    private boolean ternary=false;
    private boolean ended=false;
    private boolean interpolated=false;

    public Lexer(String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.expressionHandler = expressionHandler;
        this.templateLoader = templateLoader;
        this.filename = ensurePugExtension(filename);
        Reader reader = templateLoader.getReader(this.filename);
        options = new LinkedList<String>();
        scanner = new Scanner(reader);
        deferredTokens = new LinkedList<Token>();
        tokens = new LinkedList<Token>();
        indentStack = new LinkedList<Integer>();
        lastIndents = 0;
        lineno = 1;
        characterParser = new CharacterParser();
        int x = 0;
    }
    public Lexer(String input,String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.expressionHandler = expressionHandler;
        this.templateLoader = templateLoader;
        this.filename = ensurePugExtension(filename);
        options = new LinkedList<String>();
        scanner = new Scanner(input);
        deferredTokens = new LinkedList<Token>();
        tokens = new LinkedList<Token>();
        indentStack = new LinkedList<Integer>();
        lastIndents = 0;
        lineno = 1;
        characterParser = new CharacterParser();
        int x = 0;
    }

    public boolean next() {
        Token token;
        if (blank()) {
            return true;
        }
        if (eos()) {
            return true;
        }
        if (endInterpolation()) {
            return true;
        }
        if (yield()) {
            return true;
        }
        if (doctype()) {
            return true;
        }
        if (interpolation()) {
            return true;
        }
        if (caseToken()) {
            return true;
        }
        if (when()) {
            return true;
        }
        if (defaultToken()) {
            return true;
        }
        if (extendsToken()) {
            return true;
        }
        if (append()) {
            return true;
        }
        if (prepend()) {
            return true;
        }
        if (block()) {
            return true;
        }
        if (mixinBlock()) {
            return true;
        }
        if (include()) {
            return true;
        }
        if (mixin()) {
            return true;
        }
        if (call()) {
            return true;
        }
        if (conditional()) {
            return true;
        }
        if (each()) {
            return true;
        }
        if (whileToken()) {
            return true;
        }
        if (tag()) {
            return true;
        }
        if (filter()) {
            return true;
        }
        if (blockCode()) {
            return true;
        }
        if (code()) {
            return true;
        }
        if (id()) {
            return true;
        }
        if (dot()) {
            return true;
        }
        if (className()) {
            return true;
        }
        if ((token = attrs()) != null) {
            pushToken(token);
            return true;
        }
        if (attributesBlock()) {
            return true;
        }
        if (indent()) {
            return true;
        }
        if (text()) {
            return true;
        }
        if (textHtml()) {
            return true;
        }
        if (comment()) {
            return true;
        }
        if (slash()) {
            return true;
        }
        if (colon()) {
            return true;
        }
//        if (textFail()) {
//            return true;
//        }
        return fail();
    }
    


    public void consume(int len) {
        scanner.consume(len);
    }

    public void defer(Token tok) {
        tokens.push(tok);
    }

    public Token lookahead(int index) {
        boolean found = true;
        while (tokens.size() <= index && found) {
            found = next();
        }

        if(this.tokens.size() <= index){
            throw new PugLexerException("Cannot read past the end of a stream",this.filename,this.lineno,templateLoader);
        }
        return this.tokens.get(index);
    }
//    /**
//     * Return the indexOf `(` or `{` or `[` / `)` or `}` or `]` delimiters.
//     *
//     * @return {Number}
//     * @api private
//     */
//
//    bracketExpression: function(skip){
//      skip = skip || 0;
//      var start = this.input[skip];
//      if (start != '(' && start != '{' && start != '[') throw new Error('unrecognized start character');
//      var end = ({'(': ')', '{': '}', '[': ']'})[start];
//      var range = characterParser.parseMax(this.input, {start: skip + 1});
//      if (this.input[range.end] !== end) throw new Error('start character ' + start + ' does not match end character ' + this.input[range.end]);
//      return range;
//    },
    private CharacterParser.Match bracketExpression(){
        return bracketExpression(0);
    }
    private CharacterParser.Match bracketExpression(int skip){
        char start = scanner.getInput().charAt(skip);
        if(start != '(' && start != '{' && start != '[') {
            throw new PugLexerException("unrecognized start character", filename, getLineno(), templateLoader);
        }
        Map<Character,Character> closingBrackets =  new HashMap<Character,Character>();
        closingBrackets.put('(',')');
        closingBrackets.put('{','}');
        closingBrackets.put('[',']');
        char end = closingBrackets.get(start);
        Options options = new Options();
        options.setStart(skip+1);
        CharacterParser.Match range;
        try {
            range = characterParser.parseMax(scanner.getInput(), options);
        }catch(CharacterParser.SyntaxError exception){
            throw new PugLexerException(exception.getMessage() + " See "+ StringUtils.substring(scanner.getInput(),0,5), filename, getLineno(), templateLoader);
        }
        if(scanner.getInput().charAt(range.getEnd()) != end)
            throw new PugLexerException("start character " + start + " does not match end character " + scanner.getInput().charAt(range.getEnd()), filename, getLineno(), templateLoader);
        return range;
    }

    public int getLineno() {
        return lineno;
    }

    public void setPipeless(boolean pipeless) {
        this.pipeless = pipeless;
    }

    public Token advance() {
        boolean found = true;
        while (tokens.size() <= 0 && found) {
            found = next();
        }

        return this.tokens.pollFirst();
    }

    private String scan(String regexp) {
        String result = null;
        Matcher matcher = scanner.getMatcherForPattern(regexp);
        if (matcher.find(0) && matcher.groupCount()>0) {
            int end = matcher.end();
            String val = matcher.group(1);
            int diff = end - (val!=null ? val.length() : 0);
            consume(end);
            incrementColumn(diff);
            return val;
        }
        return result;
    }

    private Token scan(Pattern pattern,Token token) {
        Matcher matcher = scanner.getMatcherForPattern(pattern);
        if (matcher.find(0)) {
            int end = matcher.end();
            String val = null;
            if(matcher.groupCount()>0)
                val = matcher.group(1);
            int diff = end - (val!=null ? val.length() : 0);
            token = tok(token);
            token.setValue(val);
            consume(end);
            incrementColumn(diff);
            return token;
        }
        return null;
    }

    private Token scanEndOfLine(Pattern pattern, Token token) {
        Matcher matcher = scanner.getMatcherForPattern(pattern);
        if (matcher.find(0)) {
            int whitespaceLength = 0;
            Pattern pattern1 = Pattern.compile("^([ ]+)([^ ]*)");
            Matcher whitespace = pattern1.matcher(matcher.group(0));
            if(whitespace.find(0)){
                whitespaceLength = whitespace.group(1).length();
                incrementColumn(whitespaceLength);
            }

            String newInput = scanner.getInput().substring(matcher.group(0).length());
            if(newInput.length()>0 && newInput.charAt(0) == ':'){
                scanner.consume(matcher.group(0).length());
                token = tok(token);
                if(matcher.groupCount()>0) {
                    token.setValue(matcher.group(1));
                }
                incrementColumn(matcher.group(0).length() - whitespaceLength);
                return token;
            }

            Pattern pattern2 = Pattern.compile("^[ \\t]*(\\n|$)");
            Matcher matcher1 = pattern2.matcher(newInput);
            if(matcher1.find(0)){
                Pattern pattern3 = Pattern.compile("^[ \\t]*");
                int length = matcher.group(0).length();
                Matcher matcher2 = pattern3.matcher(newInput);
                if(matcher2.find(0)) {
                    length = length + matcher2.end();
                }
                scanner.consume(length);
                token = tok(token);
                if(matcher.groupCount()>0) {
                    token.setValue(matcher.group(1));
                }
                incrementColumn(matcher.group(0).length() - whitespaceLength);
                return token;
            }
        }
        return null;
    }

    private Token stashed() {
        if (tokens.size() > 0) {
            return tokens.poll();
        }
        return null;
    }

    private Token deferred() {
        if (deferredTokens.size() > 0) {
            return deferredTokens.poll();
        }
        return null;
    }
    /**
     * Blank line. ok
     */

//    blank: function() {
//        var captures;
//        if (captures = /^\n[ \t]*\n/.exec(this.input)) {
//            this.consume(captures[0].length - 1);
//            this.incrementLine(1);
//            return true;
//        }
//    },
    private boolean blank(){
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_BLANK);
        if (matcher.find(0)) {
            consume(matcher.end()-1);
            incrementLine(1);
            //TODO: remove pipeless
            if(this.pipeless) {
                pushToken(new Text("", lineno));
                return true;
            }
            this.next();
            return true;
        }
        return false;
    }

    /**
     * end-of-source. ok
     */

    private boolean eos() {
        if (scanner.getInput().length() > 0) {
            return false;
        }
        if(this.interpolated){
            throw new PugLexerException("End of line was reached with no closing bracket for interpolation.",this.filename,this.lineno,templateLoader);
        }
        while (indentStack.size() > 0) {
            indentStack.poll();
            pushToken(tokEnd(tok(new Outdent())));
        }
        pushToken(tokEnd(tok(new Eos("eos", lineno))));
        this.ended = true;
        return true;
    }

    /**
     * Comment.
     */

    private boolean comment() {
        Matcher matcher = scanner.getMatcherForPattern("^\\/\\/(-)?([^\\n]*)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            consume(matcher.end());
            boolean buffer = !"-".equals(matcher.group(1));
            this.interpolationAllowed = buffer;
            Comment comment = new Comment(matcher.group(2), lineno, buffer);
            incrementColumn(matcher.end());
            pushToken(tokEnd(comment));
            pipelessText();
            return true;
        }
        return false;
    }

    private boolean code() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_CODE);
        if (matcher.find(0) && matcher.groupCount() > 1) {
            String flags = matcher.group(1);
            String code = matcher.group(2);
            int shortend = 0;
            if(this.interpolated){
                CharacterParser.Match parsed = characterParser.parseUntil(code, "]");
                shortend = code.length() - parsed.getEnd();
                code = parsed.getSrc();
            }
            int consumed = matcher.end() - shortend;
            consume(consumed);
            Expression expression = (Expression) tok(new Expression(code, lineno));
            expression.setEscape(flags.charAt(0) == '=');
            expression.setBuffer(flags.charAt(0) == '=' || flags.length()>1 && flags.charAt(1) == '=');
            incrementColumn(matcher.end()-matcher.group(2).length());
            if(expression.isBuffer()) {
                try {
                    expressionHandler.assertExpression(matcher.group(2));
                } catch (ExpressionException e) {
                    throw new PugLexerException(e.getMessage(), filename, lineno, templateLoader);
                }
            }
            incrementColumn(code.length());
            pushToken(tokEnd(expression));
            return true;
        }
        return false;
    }

    private boolean interpolation(){
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_INTERPOLATION);
        if (matcher.find(0)) {
            try {
                CharacterParser.Match match = this.bracketExpression(1);
                this.scanner.consume(match.getEnd()+1);
                Token tok = tok(new Interpolation(match.getSrc(), lineno));
                incrementColumn(2); // '#{'
                try {
                    expressionHandler.assertExpression(match.getSrc());
                } catch (ExpressionException e) {
                    throw new PugLexerException(e.getMessage(), filename, lineno, templateLoader);
                }
                String[] splitted = StringUtils.split(match.getSrc(), '\n');
                int lines = splitted.length-1;
                incrementLine(lines);
                incrementColumn(splitted[lines].length()+1); // + 1 → '}'
                pushToken(tokEnd(tok));
                return true;
            } catch(Exception ex){
                return false; //not an interpolation expression, just an unmatched open interpolation
            }
        }
        return false;
    }

    private boolean tag() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_TAG);
        if(matcher.find(0) && matcher.groupCount() > 0){
            String name = matcher.group(1);
            int length = matcher.group(0).length();
            consume(length);
            Token token = tok(new Tag(name));
            incrementColumn(length);
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private boolean yield() {
        Token token = scanEndOfLine(PATTERN_YIELD, new Yield());
        if (token!=null) {
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private boolean filter(){
        return filter(false);
    }

    private boolean filter(boolean inInclude) {
        Token token = scan(PATTERN_FILTER,new Filter());
        if (token!=null) {
            incrementColumn(token.getValue().length());
            this.pipeless = true; //TODO: remove
            pushToken(tokEnd(token));
            attrs();
            if(!inInclude){
                this.interpolationAllowed = false;
                pipelessText();
            }
            return true;
        }
        return false;
    }

    private boolean each() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_EACH);
        if (matcher.find(0) && matcher.groupCount() > 2) {
            consume(matcher.end());
            String value = matcher.group(1);
            Each each = (Each) tok(new Each(value, lineno));
            String key = matcher.group(2);
            each.setKey(key);
            String code = matcher.group(3);
            this.incrementColumn(matcher.end() - code.length());
            try {
                expressionHandler.assertExpression(code);
            } catch (ExpressionException e) {
                throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
            }
            each.setCode(code);
            this.incrementColumn(code.length());
            pushToken(tokEnd(each));

            return true;
        }
        //TODO: add error checks
        return false;
    }

    private boolean whileToken() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_WHILE);
        if (matcher.find(0) && matcher.groupCount()>0) {
            consume(matcher.end());
            try {
                expressionHandler.assertExpression(matcher.group(1));
            } catch (ExpressionException e) {
                throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
            }
            Token token = tok(new While(matcher.group(1)));
            incrementColumn(matcher.end());
            pushToken(tokEnd(token));
            return true;
        }
        //TODO: add error check
        return false;
    }

    private boolean conditional() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_CONDITIONAL);
        if (matcher.find(0) && matcher.groupCount() > 1) {
            consume(matcher.end());
            String type = matcher.group(1).replace(' ','-');
            String js = matcher.group(2);
            if(js!=null)
                js = js.trim();

            Token token = null;
            switch (type){
                case "if":
                    try {
                        expressionHandler.assertExpression(js);
                    } catch (ExpressionException e) {
                        throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
                    }
                    If ifToken = new If(js, lineno);
                    token = tok(ifToken);
                    break;
                case "else-if":
                    try {
                        expressionHandler.assertExpression(js);
                    } catch (ExpressionException e) {
                        throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
                    }
                    token = tok(new ElseIf(js, lineno));
                    break;
                case "unless":
                    try {
                        expressionHandler.assertExpression(js);
                    } catch (ExpressionException e) {
                        throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
                    }
                    If unlessToken = new If("!("+js+")", lineno);
                    token = tok(unlessToken);
                    break;
                case "else":
                    if(js!=null && js.length()>0){
                        throw new PugLexerException("`else` cannot have a condition, perhaps you meant `else if`",this.filename,this.lineno,templateLoader);
                    }
                    token = tok(new Else(null, lineno));
                    break;
            }
            this.incrementColumn(matcher.end()-js.length());
            this.incrementColumn(js.length());
            if(token!=null) {
                pushToken(tokEnd(token));
            }else{
                throw new PugLexerException("type "+type+" no allowed here",this.filename,this.lineno,templateLoader);
            }
            return true;
        }
        return false;
    }

	/*
	 * private Token conditionalElse() { String val = scan("^(else)"); if
	 * (StringUtils.isNotBlank(val)) { return new Filter(val, lineno); } return
	 * null; }
	 */
    /**
     * Doctype.
     */

    private boolean doctype(){
        Token token = scanEndOfLine(Pattern.compile("^doctype *([^\\n]*)"),new Doctype());
        if(token!=null){
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    /**
     * Id.
     */

    private boolean id() {
        Token token = scan(Pattern.compile("^#([\\w-]+)"),new CssId());
        if (token!=null) {
            incrementColumn(token.getValue().length());
            pushToken(tokEnd(token));
            return true;
        }
        //TODO: add invalid id check
        return false;
    }

    /**
     * Class.
     */

    private boolean className() {
        Token token = scan(Pattern.compile("^\\.([_a-z0-9\\-]*[_a-z][_a-z0-9\\-]*)",Pattern.CASE_INSENSITIVE),new CssClass());
        if (token!=null) {
            incrementColumn(token.getValue().length());
            pushToken(tokEnd(token));
            return true;
        }
        //TODO: Add invalid classname checks
        return false;
    }


//    endInterpolation: function () {
//        if (this.interpolated && this.input[0] === ']') {
//            this.input = this.input.substr(1);
//            this.ended = true;
//            return true;
//        }
//    },

    private boolean endInterpolation(){
        if(interpolated && this.scanner.getInput().charAt(0) == ']'){
            this.consume(1);
            this.ended=true;
            return true;
        }
        return false;
    }


//    addText: function (type, value, prefix, escaped) {
//        var tok;
//        if (value + prefix === '') return;
//        prefix = prefix || '';
//        escaped = escaped || 0;
//        var indexOfEnd = this.interpolated ? value.indexOf(']') : -1;
//        var indexOfStart = this.interpolationAllowed ? value.indexOf('#[') : -1;
//        var indexOfEscaped = this.interpolationAllowed ? value.indexOf('\\#[') : -1;
//        var matchOfStringInterp = /(\\)?([#!]){((?:.|\n)*)$/.exec(value);
//            var indexOfStringInterp = this.interpolationAllowed && matchOfStringInterp ? matchOfStringInterp.index : Infinity;
//
//            if (indexOfEnd === -1) indexOfEnd = Infinity;
//            if (indexOfStart === -1) indexOfStart = Infinity;
//            if (indexOfEscaped === -1) indexOfEscaped = Infinity;
//
//            if (indexOfEscaped !== Infinity && indexOfEscaped < indexOfEnd && indexOfEscaped < indexOfStart && indexOfEscaped < indexOfStringInterp) {
//                prefix = prefix + value.substring(0, indexOfEscaped) + '#[';
//                return this.addText(type, value.substring(indexOfEscaped + 3), prefix, escaped + 1);
//            }
//            if (indexOfStart !== Infinity && indexOfStart < indexOfEnd && indexOfStart < indexOfEscaped && indexOfStart < indexOfStringInterp) {
//                tok = this.tok(type, prefix + value.substring(0, indexOfStart));
//                this.incrementColumn(prefix.length + indexOfStart + escaped);
//                this.tokens.push(this.tokEnd(tok));
//                tok = this.tok('start-pug-interpolation');
//                this.incrementColumn(2);
//                this.tokens.push(this.tokEnd(tok));
//                var child = new this.constructor(value.substr(indexOfStart + 2), {
//                        filename: this.filename,
//                        interpolated: true,
//                        startingLine: this.lineno,
//                        startingColumn: this.colno
//      });
//                var interpolated;
//                try {
//                    interpolated = child.getTokens();
//                } catch (ex) {
//                    if (ex.code && /^PUG:/.test(ex.code)) {
//                        this.colno = ex.column;
//                        this.error(ex.code.substr(4), ex.msg);
//                    }
//                    throw ex;
//                }
//                this.colno = child.colno;
//                this.tokens = this.tokens.concat(interpolated);
//                tok = this.tok('end-pug-interpolation');
//                this.incrementColumn(1);
//                this.tokens.push(this.tokEnd(tok));
//                this.addText(type, child.input);
//                return;
//            }
//            if (indexOfEnd !== Infinity && indexOfEnd < indexOfStart && indexOfEnd < indexOfEscaped && indexOfEnd < indexOfStringInterp) {
//                if (prefix + value.substring(0, indexOfEnd)) {
//                    this.addText(type, value.substring(0, indexOfEnd), prefix);
//                }
//                this.ended = true;
//                this.input = value.substr(value.indexOf(']') + 1) + this.input;
//                return;
//            }
//            if (indexOfStringInterp !== Infinity) {
//                if (matchOfStringInterp[1]) {
//                    prefix = prefix + value.substring(0, indexOfStringInterp) + '#{';
//                    return this.addText(type, value.substring(indexOfStringInterp + 3), prefix, escaped + 1);
//                }
//                var before = value.substr(0, indexOfStringInterp);
//                if (prefix || before) {
//                    before = prefix + before;
//                    tok = this.tok(type, before);
//                    this.incrementColumn(before.length + escaped);
//                    this.tokens.push(this.tokEnd(tok));
//                }
//
//                var rest = matchOfStringInterp[3];
//                var range;
//                tok = this.tok('interpolated-code');
//                this.incrementColumn(2);
//                try {
//                    range = characterParser.parseUntil(rest, '}');
//                } catch (ex) {
//                    if (ex.index !== undefined) {
//                        this.incrementColumn(ex.index);
//                    }
//                    if (ex.code === 'CHARACTER_PARSER:END_OF_STRING_REACHED') {
//                        this.error('NO_END_BRACKET', 'End of line was reached with no closing bracket for interpolation.');
//                    } else if (ex.code === 'CHARACTER_PARSER:MISMATCHED_BRACKET') {
//                        this.error('BRACKET_MISMATCH', ex.message);
//                    } else {
//                        throw ex;
//                    }
//                }
//                tok.mustEscape = matchOfStringInterp[2] === '#';
//                tok.buffer = true;
//                tok.val = range.src;
//                this.assertExpression(range.src);
//
//                if (range.end + 1 < rest.length) {
//                    rest = rest.substr(range.end + 1);
//                    this.incrementColumn(range.end + 1);
//                    this.tokens.push(this.tokEnd(tok));
//                    this.addText(type, rest);
//                } else {
//                    this.incrementColumn(rest.length);
//                    this.tokens.push(this.tokEnd(tok));
//                }
//                return;
//            }
//
//            value = prefix + value;
//            tok = this.tok(type, value);
//            this.incrementColumn(value.length + escaped);
//            this.tokens.push(this.tokEnd(tok));
//        },

    private void addText(Token token, String value){
        addText(token,value,"",0);
    }
    private void addText(Token token, String value, String prefix,int escaped) {
        Token newToken;
        if ("".equals(value + prefix))
            return;
        int indexOfEnd = this.interpolated ? value.indexOf(']') : -1;
        int indexOfStart = this.interpolationAllowed ? value.indexOf("#[") : -1;
        int indexOfEscaped = this.interpolationAllowed ? value.indexOf("\\#[") : -1;
        Matcher matchOfStringInterp = Pattern.compile("(\\\\)?([#!])\\{((?:.|\\n)*)$").matcher(value);
        int indexOfStringInterp = this.interpolationAllowed && matchOfStringInterp.find(0) ? matchOfStringInterp.start() : -1;
//        if (indexOfEnd == -1) indexOfEnd = Infinity;
//        if (indexOfStart == -1) indexOfStart = Infinity;
//        if (indexOfEscaped == -1) indexOfEscaped = Infinity;

        if (indexOfEscaped != -1 && indexOfEscaped < indexOfEnd && indexOfEscaped < indexOfStart && indexOfEscaped < indexOfStringInterp) {
            prefix = prefix + value.substring(0, indexOfEscaped) + "#[";
            this.addText(token, StringUtils.substring(value, indexOfEscaped + 3), prefix, escaped + 1);
            return;
        }
        if (indexOfStart != -1 && indexOfStart < indexOfEnd && indexOfStart < indexOfEscaped && indexOfStart < indexOfStringInterp) {
            token.setValue(prefix + StringUtils.substring(value, 0, indexOfStart));
            newToken = tok(token);
            incrementColumn(prefix.length() + indexOfStart + escaped);
            pushToken(tokEnd(newToken));
            newToken = this.tok(new StartPugInterpolation());
            this.incrementColumn(2);
            pushToken(this.tokEnd(newToken));
//            Lexer lexer = new Lexer(value.substring(indexOfStart + 2), templateLoader, expressionHandler);
//            var child = new Lexer(value.substr(indexOfStart + 2), {
//                    filename: this.filename,
//                    interpolated: true,
//                    startingLine: this.lineno,
//                    startingColumn: this.colno
//      });
//            var interpolated;
//            try {
//                interpolated = child.getTokens();
//            } catch (ex) {
//                if (ex.code && /^PUG:/.test(ex.code)) {
//                    this.colno = ex.column;
//                    this.error(ex.code.substr(4), ex.msg);
//                }
//                throw ex;
//            }
//            this.colno = child.colno;
//            this.tokens = this.tokens.concat(interpolated);
//            newToken = this.tok('end-pug-interpolation');
//            this.incrementColumn(1);
//            this.tokens.push(this.tokEnd(newToken));
//            this.addText(type, child.input);
//            return;
        }
//        if (indexOfEnd !== Infinity && indexOfEnd < indexOfStart && indexOfEnd < indexOfEscaped && indexOfEnd < indexOfStringInterp) {
//            if (prefix + value.substring(0, indexOfEnd)) {
//                this.addText(type, value.substring(0, indexOfEnd), prefix);
//            }
//            this.ended = true;
//            this.input = value.substr(value.indexOf(']') + 1) + this.input;
//            return;
//        }
//        if (indexOfStringInterp !== Infinity) {
//            if (matchOfStringInterp[1]) {
//                prefix = prefix + value.substring(0, indexOfStringInterp) + '#{';
//                return this.addText(type, value.substring(indexOfStringInterp + 3), prefix, escaped + 1);
//            }
//


        String before = StringUtils.substring(value, 0, 0 + indexOfStringInterp);
        if (prefix != null || before != null) {
            before = prefix + before;
            token.setValue(before);
            Token tok = this.tok(token);
            this.incrementColumn(before.length() + escaped);
            pushToken(this.tokEnd(tok));
        }

        String rest = matchOfStringInterp.group(3);
        int range;
        newToken = this.tok(new InterpolatedCode());
        this.incrementColumn(2);
//        try {
//            range = characterParser.parseUntil(rest, '}');
//        } catch (ex) {
//            if (ex.index !== undefined) {
//                this.incrementColumn(ex.index);
//            }
//            if (ex.code === 'CHARACTER_PARSER:END_OF_STRING_REACHED') {
//                this.error('NO_END_BRACKET', 'End of line was reached with no closing bracket for interpolation.');
//            } else if (ex.code === 'CHARACTER_PARSER:MISMATCHED_BRACKET') {
//                this.error('BRACKET_MISMATCH', ex.message);
//            } else {
//                throw ex;
//            }
//        }
//        tok.mustEscape = matchOfStringInterp[2] === '#';
//        tok.buffer = true;
//        tok.val = range.src;
//        this.assertExpression(range.src);
//
//        if (range.end + 1 < rest.length) {
//            rest = rest.substr(range.end + 1);
//            this.incrementColumn(range.end + 1);
//            this.tokens.push(this.tokEnd(tok));
//            this.addText(type, rest);
//        } else {
//            this.incrementColumn(rest.length);
//            this.tokens.push(this.tokEnd(tok));
//        }
//        return;

//    }
//        value = prefix + value;
//        tok = this.tok(type, value);
//        this.incrementColumn(value.length + escaped);
//        this.tokens.push(this.tokEnd(tok));

    }

    private boolean text() {
        Text textToken = new Text();
        Token token = scan(PATTERN_TEXT_1, textToken);
        if (token==null) {
            token = scan(PATTERN_TEXT_2,textToken);
        }
        if (token==null) {
            token = scan(PATTERN_TEXT_3,textToken);
        }
        if (token!=null) {
            //TODO:implement addText
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

//    textHtml: function () {
//        var tok = this.scan(/^(<[^\n]*)/, 'text-html');
//        if (tok) {
//            this.addText('text-html', tok.val);
//            return true;
//        }
//    },
    private boolean textHtml() {
        Token token = scan(Pattern.compile("^(<[^\\n]*)"), new TextHtml());
        if (token!=null) {
            //TODO:implement addText
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private boolean textFail() {
        String val = scan("^([^\\.\\n][^\\n]+)");
        if (StringUtils.isNotEmpty(val)) {
            pushToken(tokEnd(tok(new Text(val, lineno))));
            return true;
        }
        return false;
    }

    private boolean fail() {
        throw new PugLexerException("unexpected text " + StringUtils.substring(scanner.getInput(),0,5), filename, getLineno(), templateLoader);
    }
//  "extends": function() {
//        var tok = this.scan(/^extends?(?= |$|\n)/, 'extends');
//        if (tok) {
//            this.tokens.push(this.tokEnd(tok));
//            if (!this.callLexerFunction('path')) {
//                this.error('NO_EXTENDS_PATH', 'missing path for extends');
//            }
//            return true;
//        }
//        if (this.scan(/^extends?\b/)) {
//            this.error('MALFORMED_EXTENDS', 'malformed extends');
//        }
//    },
    private boolean extendsTokenOld() {
        String val = scan("^extends? +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            pushToken(tokEnd(tok(new ExtendsToken(val, lineno))));
            return true;
        }
        return false;
    }
    private boolean extendsToken() {
        Token token = scan(Pattern.compile("^extends?(?= |$|\\n)"), new ExtendsToken());
        if (token != null) {
            pushToken(tokEnd(token));
            if(!path()){
                throw new PugLexerException("missing path for extends",this.filename,this.lineno,templateLoader);
            }
            return true;
        }
        //TODO: add error check
        return false;
    }

    private boolean prepend() {
        Matcher matcher = scanner.getMatcherForPattern(Pattern.compile("^(?:block +)?prepend +([^\\n]+)"));
        if (matcher.find(0)) {
            String name = matcher.group(1).trim();
            String comment = "";
            if(name.indexOf("//") != -1){
                String[] split = StringUtils.split(name, "//");
                comment = "//" + StringUtils.join(Arrays.copyOfRange(split,1,split.length),"//");
                name = StringUtils.split(name,"//")[0].trim();
            }
            if(StringUtils.isNotBlank(name)) {
                Token token = tok(new Block(name));
                int len = matcher.group(0).length() - comment.length();
                Pattern whitespaceRe = Pattern.compile("[ \\n\\t]");
                while(whitespaceRe.matcher(String.valueOf(scanner.getInput().charAt(len-1))).find(0)) {
                    len--;
                }
                incrementColumn(len);
                token.setMode("prepend");
                pushToken(tokEnd(token));
                consume(matcher.end() - comment.length());
                incrementColumn(matcher.end() - comment.length() - len);
                return true;
            }
        }
        return false;
    }

    private boolean append() {
        Matcher matcher = scanner.getMatcherForPattern(Pattern.compile("^(?:block +)?append +([^\\n]+)"));
        if (matcher.find(0)) {
            String name = matcher.group(1).trim();
            String comment = "";
            if(name.indexOf("//") != -1){
                String[] split = StringUtils.split(name, "//");
                comment = "//" + StringUtils.join(Arrays.copyOfRange(split,1,split.length),"//");
                name = StringUtils.split(name,"//")[0].trim();
            }
            if(StringUtils.isNotBlank(name)) {
                Token token = tok(new Block(name));
                int len = matcher.group(0).length() - comment.length();
                Pattern whitespaceRe = Pattern.compile("[ \\n\\t]");
                while(whitespaceRe.matcher(String.valueOf(scanner.getInput().charAt(len-1))).find(0)) {
                    len--;
                }
                incrementColumn(len);
                token.setMode("append");
                pushToken(tokEnd(token));
                consume(matcher.end() - comment.length());
                incrementColumn(matcher.end() - comment.length() - len);
                return true;
            }
        }
        return false;
    }

    private boolean block() {
        Matcher matcher = scanner.getMatcherForPattern(Pattern.compile("^block +([^\\n]+)"));
        if (matcher.find(0)) {
            String name = matcher.group(1).trim();
            String comment = "";
            if(name.indexOf("//") != -1){
                String[] split = StringUtils.split(name, "//");
                comment = "//" + StringUtils.join(Arrays.copyOfRange(split,1,split.length),"//");
                name = StringUtils.split(name,"//")[0].trim();
            }
            if(StringUtils.isNotBlank(name)) {
                Token token = tok(new Block(name));
                int len = matcher.group(0).length() - comment.length();
                Pattern whitespaceRe = Pattern.compile("[ \\n\\t]");
                while(whitespaceRe.matcher(String.valueOf(scanner.getInput().charAt(len-1))).find(0)) {
                    len--;
                }
                incrementColumn(len);
                token.setMode("replace");
                pushToken(tokEnd(token));
                consume(matcher.end() - comment.length());
                incrementColumn(matcher.end() - comment.length() - len);
                return true;
            }
        }
        return false;
    }

    private boolean mixinBlock() {
        Token token = scanEndOfLine(PATTERN_MIXIN_BLOCK, new MixinBlock());
        if (token!=null) {
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private boolean blockCode() {
        Token token = scanEndOfLine(Pattern.compile("^-"),new BlockCode());
        if(token != null){
            pushToken(tokEnd(token));
            this.interpolationAllowed=false;
            pipelessText();
            return true;
        }
        return false;
    }

    private boolean include() {
        Token token = scan(PATTERN_INCLUDE,new Include());
        if (token!=null) {
            pushToken(tokEnd(token));
            while (filter(true));
            if(!path()){
                if(Pattern.compile("^[^ \\n]+").matcher(scanner.getInput()).find(0)){
                    fail();
                } else {
                    throw new PugLexerException("missing path for include",this.filename,this.lineno,templateLoader);
                }
            }
            return true;
        }
        //TODO: add error check
        return false;
    }

    /**
     * Path. ok.
     */
    private boolean path(){
        Token token = scanEndOfLine(PATTERN_PATH,new Path());
        if (token != null) {
            token.setValue(token.getValue().trim());
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }
    /**
     * Case. ok.
     * TODO: add error check
     */
    private boolean caseToken() {
        Token token = scanEndOfLine(PATTERN_CASE,new CaseToken());
        if (token!=null) {
            incrementColumn(-token.getValue().length());
            try {
                expressionHandler.assertExpression(token.getValue());
            } catch (ExpressionException e) {
                throw new PugLexerException(e.getMessage(), filename, getLineno(), templateLoader);
            }
            incrementColumn(token.getValue().length());
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    /**
     * When. ok.
     * TODO: add error check
     */
    private boolean when() {
        Token token = scanEndOfLine(PATTERN_WHEN,new When());
        if (token!=null) {
            try {
                String val = token.getValue();
                CharacterParser.State parse = characterParser.parse(val);
                while(parse.isNesting() || parse.isString()){
                    Matcher matcher = scanner.getMatcherForPattern(":([^:\\n]+)");
                    if(!matcher.find(0))
                        break;

                    val += matcher.group(0);
                    int increment = matcher.group(0).length();
                    consume(increment);
                    incrementColumn(increment);
                    parse = characterParser.parse(val);
                }

                incrementColumn(-val.length());
                try {
                    expressionHandler.assertExpression(val);
                } catch (ExpressionException e) {
                    throw new PugLexerException(e.getMessage(), filename, getLineno(), templateLoader);
                }
                incrementColumn(val.length());
                token.setValue(val);
                pushToken(tokEnd(token));
                return true;
            } catch (CharacterParser.SyntaxError syntaxError) {
                throw new PugLexerException(syntaxError.getMessage(), filename, getLineno(), templateLoader);
            }
        }
        return false;
    }

    /**
     * Default.
     * TODO: add error check
     */
    private boolean defaultToken() {
        Token token = scanEndOfLine(PATTERN_DEFAULT,new Default());
        if (token!=null) {
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private Token assignment() {
        Matcher matcher = scanner.getMatcherForPattern("^(\\w+) += *([^;\\n]+)( *;? *)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            String name = matcher.group(1);
            String val = matcher.group(2);
            consume(matcher.end());
            Assignment assign = new Assignment(val, lineno);
            assign.setName(name);
            return assign;
        }
        return null;
    }

    /**
     * Dot.
     * Todo: add piplesstext
     */

    private boolean dot() {
        this.pipeless = true;
        Token token = scanEndOfLine(PATTERN_DOT, new Dot());
        if (token!=null) {
            pushToken(tokEnd(token));
//            pipelessText();
            return true;
        }
        return false;
    }

    /**
     * Mixin. ok and done.
     */

    private boolean mixin() {
        Matcher matcher = scanner.getMatcherForPattern("^mixin +([-\\w]+)(?: *\\((.*)\\))? *");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            consume(matcher.end());
            Mixin tok = (Mixin) tok(new Mixin(matcher.group(1), lineno));
            tok.setArguments(matcher.group(2));
            incrementColumn(matcher.group(0).length());
            pushToken(tokEnd(tok));
            return true;
        }
        return false;
    }

    /**
     * Call mixin. ok and done.
     */
    private boolean call() {
        Call tok;
        int increment;
        Matcher matcher = scanner.getMatcherForPattern("^\\+(\\s*)(([-\\w]+)|(#\\{))");
        if (matcher.find(0) && matcher.groupCount() > 3) {
            // try to consume simple or interpolated call
            if(matcher.group(3)!=null) {
                // simple call
                increment = matcher.end();
                consume(increment);
                tok = (Call) tok(new Call(matcher.group(3), lineno));
            }else{
                // interpolated call
                CharacterParser.Match match = this.bracketExpression(2 + matcher.group(1).length());
                increment = match.getEnd() + 1;
                this.consume(increment);
                try {
                    expressionHandler.assertExpression(match.getSrc());
                } catch (ExpressionException e) {
                    throw new PugLexerException(e.getMessage(),this.filename,this.lineno,templateLoader);
                }
                tok = (Call) tok(new Call("#{"+match.getSrc()+"}", lineno));
            }

            incrementColumn(increment);

            matcher = scanner.getMatcherForPattern("^ *\\(");
            if (matcher.find(0)) {
                CharacterParser.Match range = this.bracketExpression(matcher.group(0).length() - 1);
                matcher = Pattern.compile("^\\s*[-\\w]+ *=").matcher(range.getSrc());
                if (!matcher.find(0)) { // not attributes
                    incrementColumn(1);
                    this.consume(range.getEnd() + 1);
                    tok.setArguments(range.getSrc());
                }
                if (tok.getArguments()!=null) {
                    try {
                        expressionHandler.assertExpression("[" + tok.getArguments() + "]");
                    } catch (ExpressionException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i< tok.getArguments().length();i++) {
                        if(tok.getArguments().charAt(i) == '\n'){
                            incrementLine(1);
                        }else{
                            incrementColumn(1);
                        }
                    }
                }
            }
            pushToken(tokEnd(tok));
            return true;
        }
        return false;
    }

    public boolean isEndOfAttribute(int i, String str, String key, String val, Loc loc, CharacterParser.State state) {
        if (key.trim().isEmpty()) return false;
        if (i == str.length()) return true;
        if (Loc.KEY.equals(loc)) {
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n') {
                for (int x = i; x < str.length(); x++) {
                    if (str.charAt(x) != ' ' && str.charAt(x) != '\n') {
                        if (str.charAt(x) == '=' || str.charAt(x) == '!' || str.charAt(x) == ',') return false;
                        else return true;
                    }
                }
            }
            return str.charAt(i) == ',';
        } else if (Loc.VALUE.equals(loc) && !state.isNesting()) {
            try {
                expressionHandler.assertExpression(val);
                if (str.charAt(i) == ' ' || str.charAt(i) == '\n') {
                    for (int x = i; x < str.length(); x++) {
                        if (str.charAt(x) != ' ' && str.charAt(x) != '\n') {
                            if (characterParser.isPunctuator(str.charAt(x)) && str.charAt(x) != '"' && str.charAt(x) != '\''){
                                if (str.charAt(x) == '?')
                                    ternary = true;
                                if (str.charAt(x) == ':' && !ternary)
                                    return true;
                                return false;
                            }else{
                                ternary = false;
                                return true;
                            }
                        }
                    }
                }
                return str.charAt(i) == ',';
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    private String interpolate(String attr, final String quote) {
        Pattern regex = Pattern.compile("(\\\\)?#\\{(.+)");

        return StringReplacer.replace(attr, regex, new StringReplacerCallback() {
            @Override
            public String replace(Matcher m) {
                String match = m.group(0);
                String escape = m.group(1);
                String expr = m.group(2);
                if (escape != null) return match;
                try {
                    try {
                        CharacterParser.Match range = characterParser.parseMax(expr);
                        if (expr.charAt(range.getEnd()) != '}')
                            return substr(match, 0, 2) + interpolate(match.substring(2), quote);
                        expressionHandler.assertExpression(range.getSrc());
                        return quote + " + (" + range.getSrc() + ") + " + quote + interpolate(expr.substring(range.getEnd() + 1), quote);
                    } catch (ExpressionException ex) {
                        return substr(match, 0, 2) + interpolate(match.substring(2), quote);
                    }
                }catch(CharacterParser.SyntaxError e){
                    throw new PugLexerException(e.getMessage()+ " See " + match, filename, getLineno(), templateLoader);
                }

            }
        });
    }


    private String substr(String str, int start, int length) {
        return str.substring(start, start + length);
    }

    private boolean assertNestingCorrect(String exp) {
        //this verifies that code is properly nested, but allows
        //invalid JavaScript such as the contents of `attributes`
        try {
            CharacterParser.State res = characterParser.parse(exp);
            if (res.isNesting()) {
                throw new PugLexerException("Nesting must match on expression `" + exp + "`", filename, getLineno(), templateLoader);
            }
        } catch (CharacterParser.SyntaxError syntaxError) {
            throw new PugLexerException("Nesting must match on expression `" + exp + "`", filename, getLineno(), templateLoader);
        }
        return true;
    }
    private enum Loc {
    	KEY, KEY_CHAR, VALUE, STRING
    }
    /**
     * Attributes.
     */

    private Token attrs() {
        if ('(' == scanner.getInput().charAt(0)) {
//            AttributeList tok = new AttributeFinder(scanner,lineno).find(); //TODO: Attribute Finder führt aktuell zur endlosschleife bei einigen tests.
            int index = this.bracketExpression().getEnd();
            String str = scanner.getInput().substring(1, index);
            AttributeList tok = new AttributeList();
            tok(tok);

            assertNestingCorrect(str);

            String quote = "";
            scanner.consume(index + 1);

            boolean escapedAttr = true;
            String key = "";
            String val = "";
            String interpolatable = "";
            CharacterParser.State state = characterParser.defaultState();
            Loc loc = Loc.KEY;

            this.lineno += str.split("\n").length - 1;

            for (int i = 0; i <= str.length(); i++) {
                if (isEndOfAttribute(i, str, key, val, loc, state)) {
                    val = val.trim();
                    val = val.replaceAll("\\n","");
                    if (!val.isEmpty())
                        try {
                            expressionHandler.assertExpression(val);
                        } catch (ExpressionException e) {
                            throw new PugLexerException(e.getMessage(), filename, lineno, templateLoader);
                        }
                    val = StringEscapeUtils.unescapeJson(val);
                    key = key.trim();
                    key = key.replaceAll("^['\"]|['\"]$", "");
                    if ("".equals(val)) {
                        tok.addBooleanAttribute(key, Boolean.TRUE);
                    } else if (doubleQuotedRe.matcher(val).matches()
                            || quotedRe.matcher(val).matches()) {
                        tok.addAttribute(key, cleanRe.matcher(val).replaceAll(""),escapedAttr);
                    } else {
                        tok.addExpressionAttribute(key, val,escapedAttr);
                    }
                    key = val = "";
                    loc = Loc.KEY;
                    escapedAttr = false;
                } else {
                    switch (loc) {
                        case KEY_CHAR:
                            if (String.valueOf(str.charAt(i)).equals(quote)) {
                                loc = Loc.KEY;
                                List<Character> expectedCharacter = Arrays.asList(' ', ',', '!', '=', '\n');
                                if (i + 1 < str.length() && expectedCharacter.indexOf(str.charAt(i + 1)) == -1)
                                    throw new PugLexerException("Unexpected character " + str.charAt(i + 1) + " expected ` `, `\\n`, `,`, `!` or `=`", filename, getLineno(), templateLoader);
                            } else {
                                key += str.charAt(i);
                            }
                            break;
                        case KEY:
                            if (key.isEmpty() && !str.isEmpty() && (str.charAt(i) == '"' || str.charAt(i) == '\'')) {
                                loc = Loc.KEY_CHAR;
                                quote = String.valueOf(str.charAt(i));
                            } else if (!str.isEmpty() &&(str.charAt(i) == '!' || str.charAt(i) == '=')) {
                                escapedAttr = str.charAt(i) != '!';
                                if (str.charAt(i) == '!') i++;
                                if (str.charAt(i) != '=')
                                    throw new PugLexerException("Unexpected character " + str.charAt(i) + " expected `=`", filename, getLineno(), templateLoader);
                                loc = Loc.VALUE;
                                state = characterParser.defaultState();
                            } else if(!str.isEmpty()){
                                key += str.charAt(i);
                            }
                            break;
                        case VALUE:
                            state = characterParser.parseChar(str.charAt(i), state);
                            if (state.isString()) {
                                loc = Loc.STRING;
                                quote = String.valueOf(str.charAt(i));
                                interpolatable = String.valueOf(str.charAt(i));
                            } else {
                                val += str.charAt(i);
                            }
                            break;
                        case STRING:
                            state = characterParser.parseChar(str.charAt(i), state);
                            interpolatable += str.charAt(i);
                            if (!state.isString()) {
                                loc = Loc.VALUE;
                                val += interpolate(interpolatable, quote);
                            }
                            break;
                    }
                }
            }

            if (scanner.getInput().length()>0 && '/' == scanner.getInput().charAt(0)) {
                this.consume(1);
                tok.setSelfClosing(true);
            }

            return tokEnd(tok);
        }
        return null;
    }

//      var captures;
//      if (/^&attributes\b/.test(this.input)) {
//        this.consume(11);
//        var args = this.bracketExpression();
//        this.consume(args.end + 1);
//        return this.tok('&attributes', args.src);
//      }
//    },

    /**
     * &attributes block
     */
    private boolean attributesBlock() {
        Matcher matcher = scanner.getMatcherForPattern(PATTERN_ATTRIBUTES_BLOCK);
        if (matcher.find(0) && matcher.group(0) != null) {
            int consumed = 11;
            this.scanner.consume(consumed);
            Token attributesBlock = tok(new AttributesBlock());
            incrementColumn(consumed);
            CharacterParser.Match match = this.bracketExpression();
            consumed = match.getEnd() + 1;
            this.scanner.consume(consumed);
            attributesBlock.setValue(match.getSrc());
            incrementColumn(consumed);
            pushToken(tokEnd(attributesBlock));
            return true;
        }
        return false;
    }
    private int indexOfDelimiters(char start, char end) {
        String str = scanner.getInput();
        int nstart = 0;
        int nend = 0;
        int pos = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            if (start == str.charAt(i)) {
                nstart++;
            } else if (end == str.charAt(i)) {
                if (++nend == nstart) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

	/*
	 * private Token attributes() { Attribute tok = new Attribute(null, lineno);
	 * Matcher matcher = scanner.getMatcherForPattern("^\\("); if
	 * (matcher.find(0)) { consume(matcher.end()); attributeMode = true; } else
	 * { return null; }
	 * 
	 * StringBuilder sb = new StringBuilder(); String regexp =
	 * "^[, ]*?([-_\\w]+)? *?= *?(\"[^\"]*?\"|'[^']*?'|[.-_\\w]+)"; matcher =
	 * scanner.getMatcherForPattern(regexp); if (matcher.find(0)) { while
	 * (matcher.find(0)) { String name = matcher.group(1); String value =
	 * matcher.group(2); tok.addAttribute(name, value);
	 * sb.append(matcher.group(0)); consume(matcher.end()); matcher =
	 * scanner.getMatcherForPattern(regexp); } tok.setValue(sb.toString()); }
	 * else { return null; }
	 * 
	 * matcher = scanner.getMatcherForPattern("^ *?\\)"); if (matcher.find(0)) {
	 * consume(matcher.end()); attributeMode = false; } else { throw new
	 * JadeLexerException
	 * ("Error while parsing attribute. Missing closing bracket ", filename,
	 * getLineno(), scanner.getInput()); } return tok; }
	 */
    private Matcher scanIndentation(){
        Matcher matcher;
        Pattern re;

        if (indentRe != null) {
            matcher = scanner.getMatcherForPattern(indentRe);
        } else {
            // tabs
            re = Pattern.compile("^\\n(\\t*) *");
            String indentType = "tabs";
            matcher = scanner.getMatcherForPattern(re);

            // spaces
            if (matcher.find(0) && matcher.group(1).length() == 0) {
                re = Pattern.compile("^\\n( *)");
                indentType = "spaces";
                matcher = scanner.getMatcherForPattern(re);
            }

            // established
            if (matcher.find(0) && matcher.group(1).length() > 0)
                this.indentRe = re;
            this.indentType = indentType;
        }
        return matcher;
    }

    private boolean indent() {
        Matcher matcher = scanIndentation();
        Token tok;
        if (matcher.find(0) && matcher.groupCount() > 0) {
            int indents = matcher.group(1).length();
            incrementLine(1);
            consume(indents + 1);

            if(scanner.getInput().length() > 0  && (scanner.getInput().charAt(0) == ' ' || scanner.getInput().charAt(0) == '\t')){
                throw new PugLexerException("Invalid indentation, you can use tabs or spaces but not both", filename, getLineno(), templateLoader);
            }
//            if (lastIndents <= 0 && indents > 0)
//                lastIndents = indents;
//            if ((indents > 0 && lastIndents > 0 && indents % lastIndents != 0) || scanner.isIntendantionViolated()) {
//                throw new JadeLexerException("invalid indentation; expecting " + indents + " " + indentType, filename, getLineno(), templateLoader);
//            }

            // blank line
            if (scanner.isBlankLine()) {
                this.pipeless = false; //TODO: remove
                this.interpolationAllowed = true;
                pushToken(tokEnd(tok(new Newline())));
                return true;
            }

            // outdent
            if (indentStack.size() > 0 && indents < indentStack.get(0)) {
                int outdent_count = 0;
                while (indentStack.size() > 0 && indentStack.get(0) > indents) {
                    if(indentStack.size() > 1 && indentStack.get(1) < indents){
                        throw new PugLexerException("Inconsistent indentation. Expecting either " + indentStack.get(1) + " or " + indentStack.get(0) + " spaces/tabs.", filename, getLineno(), templateLoader);
                    }
                    outdent_count++;
                    pushToken(tokEnd(new Outdent()));
                    indentStack.poll();
                }
                while(outdent_count--!=0){
                    colno=1;
                    tok = tok(new Outdent());
                    if(indentStack.size()>0)
                        colno = indentStack.get(0) + 1;
                    else {
                        colno = 1;
                    }
                    pushToken(tokEnd(tok));
                }
            // indent
            } else if (indents > 0 && (indentStack.size() == 0 || indents != indentStack.get(0))) {
                tok = tok(new Indent(String.valueOf(indents), lineno));
                this.colno = 1 + indents;
                pushToken(tokEnd(tok));
                indentStack.push(indents); //TODO: check unshift
                tok.setIndents(indents);
                // newline
            } else {
                tok = tok(new Newline());
                Integer indentStack0 = 0;
                if(indentStack.size()>0) {
                    indentStack0 = indentStack.get(0);
                }
                if(indentStack0==null)
                    indentStack0 = 0;
                this.colno = 1 + Math.min(indentStack0,indents);
                pushToken(tokEnd(tok));
            }
            this.interpolationAllowed = true;
            this.pipeless = false; //TODO: remove
            return true;
        }
        return false;
    }

    private Token pushToken(Token token){
        tokens.add(token); // Append to an Array
        return token;
    }
    private Token tok(Token token){
        token.setStartLineNumber(this.lineno);
        token.setStartColumn(this.colno);
        token.setFileName(this.filename);
        return token;
    }

    //    tokEnd: function(tok){
//        tok.loc.end = {
//                line: this.lineno,
//                column: this.colno
//    };
//        return tok;
//    },

    private Token tokEnd(Token token){
        token.setEndLineNumber(this.lineno);
        token.setEndColumn(this.colno);
        return token;
    }
    private void incrementLine(int increment) {
        lineno+=increment;
        if(increment>0)
            colno = 1;
    }

    private void incrementColumn(int increment){
        this.colno += increment;
    }
    private boolean pipelessText() {
        return pipelessText(null);
    }
    private boolean pipelessText(Integer indents) {
        while (blank());
        Matcher matcher = scanIndentation();

        if (matcher.find(0) && matcher.group(1).length() > 0) {

            if(indents==null && matcher.groupCount()>0)
                indents = matcher.group(1).length();
            if(indents==null)
                indents=0;

            if (indents > 0 && (this.indentStack.size() == 0 || indents > this.indentStack.get(0))) {
                pushToken(tokEnd(tok(new StartPipelessText())));
                LinkedList tokenList = new LinkedList();
                ArrayList<Boolean> token_indent = new ArrayList<Boolean>();

                String indent = scanner.getInput().substring(1, indents + 1);
                ArrayList<String> tokens = new ArrayList<String>();
                boolean isMatch = false;

                int stringPtr = 0;
                do {
                    // text has `\n` as a prefix
                    int nextLineBreak = scanner.getInput().substring(stringPtr + 1).indexOf('\n');
                    if (-1 == nextLineBreak)
                        nextLineBreak = scanner.getInput().length() - stringPtr - 1;

                    String line = scanner.getInput().substring(stringPtr + 1,nextLineBreak+1);
                    Matcher lineCaptures = indentRe.matcher("\n"+line);
                    int lineIndents = 0;
                    if(lineCaptures.groupCount()>0) {
                        lineIndents = lineCaptures.group(1).length();
                    }

                    isMatch = lineIndents >= indents;
                    token_indent.add(isMatch);
                    //TODO: isMatch = isMatch || !str.trim();???
                    if (isMatch) {
                        // consume test along with `\n` prefix if match
                        stringPtr += line.length() + 1;
                        tokens.add(line.substring(indents));
                    }else if(lineIndents > this.indentStack.get(0)){
                        // line is indented less than the first line but is still indented
                        // need to retry lexing the text block
                        this.tokens.pollLast();
                        return pipelessText(lineCaptures.group(1).length());
                    }
                } while (scanner.getInput().length() - stringPtr > 0 && isMatch);
                this.consume(stringPtr);

                while (scanner.getInput().length() == 0 && tokens.get(tokens.size() - 1).equals(""))
                    tokens.remove(tokens.size() - 1);
                for (int i = 0; i<=tokens.size(); i++) {
                    Token token = null;
                    String tokenString = tokens.get(i);
                    incrementLine(1);
                    if(i!=0){
                        token = tok(new Newline());
                    }
                    if(token_indent.get(i)){
                        incrementColumn(indents);
                    }
                    if(token!=null){
                        pushToken(tokEnd(token));
                    }
//                    this.addText();

                }
                pushToken(tokEnd(tok(new EndPipelessText())));
                return true;
            }
        }
        return false;
    }

    private int calculateIndents(Matcher matcher) {
        int indents;
        int groupLength = matcher.group(1).length();
        int stackSize = this.indentStack.size();

        if(indentType.equals("tabs")) {
            indents = Math.min(stackSize + 1, groupLength);

        } else if(groupLength > 1) {
            indents = Math.min((stackSize +1) * 2,  groupLength);
        } else {
            indents = -1;    // invalid indentation
        }
        return indents;
    }

    private boolean slash() {
        Token token = scan(PATTERN_SLASH,new Slash());
        if (token != null) {
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private boolean colon() {
        Token token = scan(PATTERN_COLON,new Colon());
        if (token != null) {
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }

    private String ensurePugExtension(String templateName) {
        if ( StringUtils.isBlank(FilenameUtils.getExtension(templateName))) {
            return templateName + "." + templateLoader.getExtension();
        }
        return templateName;
    }

    public boolean getPipeless() {
        return pipeless;
    }

    public LinkedList<Token> getTokens(){
        Token t = null;
        LinkedList<Token> list = new LinkedList<Token>();
        while((t = this.advance()) != null){
            list.add(t);
            if(t instanceof Eos)
                break;
        }
        return list;
    }
}
