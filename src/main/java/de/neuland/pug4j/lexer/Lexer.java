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
    @SuppressWarnings("unused")
    private LinkedList<String> options;
    Scanner scanner;
    private LinkedList<Token> deferredTokens;
    private int lastIndents = -1;
    private int lineno;
    private int colno;
    private LinkedList<Token> stash;
    private LinkedList<Integer> indentStack;
    private String indentRe = null;
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
        stash = new LinkedList<Token>();
        indentStack = new LinkedList<Integer>();
        lastIndents = 0;
        lineno = 1;
        characterParser = new CharacterParser();
    }
    public Lexer(String input,String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.expressionHandler = expressionHandler;
        this.templateLoader = templateLoader;
        this.filename = ensurePugExtension(filename);
        options = new LinkedList<String>();
        scanner = new Scanner(input);
        deferredTokens = new LinkedList<Token>();
        stash = new LinkedList<Token>();
        indentStack = new LinkedList<Integer>();
        lastIndents = 0;
        lineno = 1;
        characterParser = new CharacterParser();
    }

    public Token next() {
        Token token;
        if ((token = deferred()) != null) {
        	return token;
        }

        if (blank()) {
        	return stashed();
        }

        if (eos()) {
           return stashed();
        }

//        if ((token = endInterpolation()) != null) {
//            return token;
//        }

        if ((token = pipelessText()) != null) {
           return token;
        }

        if ((token = yield()) != null) {
            return token;
        }
        
        if ((token = doctype()) != null) {
            return token;
        }
        
        if ((token = interpolation()) != null) {
            return token;
        }
        
        if (caseToken()) {
            return stashed();
        }

        if (when()) {
           return stashed();
        }
        
        if (defaultToken()) {
            return stashed();
        }
        
        if ((token = extendsToken()) != null) {
           return token;
        }
        
        if ((token = append()) != null) {
           return token;
        }
        
        if ((token = prepend()) != null) {
           return token;
        }
        
        if ((token = block()) != null) {
            return token;
        }
        
        if (mixinBlock()) {
            return stashed();
        }

        if ((token = include()) != null) {
            return token;
        }
        
        if ((token = includeFiltered()) != null) {
            return token;
        }

        if (mixin()) {
           return stashed();
        }
        
        if (call()) {
           return stashed();
        }
        
        if ((token = conditional()) != null) {
            return token;
        }
        
        if ((token = each()) != null) {
            return token;
        }
        
        if ((token = whileToken()) != null) {
            return token;
        }
        
        if ((token = tag()) != null) {
            return token;
        }

        if ((token = filter()) != null) {
            return token;
        }
        if ((token = blockCode()) != null) {
            return token;
        }

        if ((token = code()) != null) {
            return token;
        }
        
        if ((token = id()) != null) {
            return token;
        }

//        if ((token = dot()) != null) {
//            return token;
//        }

        if ((token = className()) != null) {
            return token;
        }
        
        if ((token = attrs()) != null) {
            return token;
        }
        
        if ((token = attributesBlock()) != null) {
            return token;
        }

        if ((token = indent()) != null) {
            return token;
        }
        
        if ((token = text()) != null) {
            return token;
        }

//        if ((token = textHtml()) != null) {
//            return token;
//        }

        if ((token = comment()) != null) {
            return token;
        }

        if ((token = colon()) != null) {
            return token;
        }

        if (dot()) {
            return stashed();
        }

        if ((token = assignment()) != null) {
            return token;
        }

        if ((token = textFail()) != null) {
            return token;
        }

        if ((token = fail()) != null) {
            return token;
        }
        return null;
    }
    


    public void consume(int len) {
        scanner.consume(len);
    }

    public void defer(Token tok) {
        deferredTokens.add(tok);
    }

    public Token lookahead(int n) {
        int fetch = n - stash.size();
        while (fetch > 0) {
            stash.add(next());
            fetch = fetch - 1;
        }
        n = n - 1;
        return this.stash.get(n);
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
        Token t = this.stashed();
        return t != null ? t : next();
    }

    // TODO: use multiscan?!
    private String scanOld(String regexp) {
        String result = null;
        Matcher matcher = scanner.getMatcherForPattern(regexp);
        if (matcher.find(0) && matcher.group(0)!=null) {
            int end = matcher.end();
            consume(end);
            return matcher.group(0);
        }
        return result;
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
//    scanEndOfLine: function (regexp, type) {
//        var captures;
//        if (captures = regexp.exec(this.input)) {
//            var whitespaceLength = 0;
//            var whitespace;
//            var tok;
//            if (whitespace = /^([ ]+)([^ ]*)/.exec(captures[0])) {
//                whitespaceLength = whitespace[1].length;
//                this.incrementColumn(whitespaceLength);
//            }
//            var newInput = this.input.substr(captures[0].length);
//            if (newInput[0] === ':') {
//                this.input = newInput;
//                tok = this.tok(type, captures[1]);
//                this.incrementColumn(captures[0].length - whitespaceLength);
//                return tok;
//            }
//            if (/^[ \t]*(\n|$)/.test(newInput)) {
//                this.input = newInput.substr(/^[ \t]*/.exec(newInput)[0].length);
//                tok = this.tok(type, captures[1]);
//                this.incrementColumn(captures[0].length - whitespaceLength);
//                return tok;
//            }
//        }
//    },

    private Token scanEndOfLine(String regexp, Token token) {
        Matcher matcher = scanner.getMatcherForPattern(regexp);
        if (matcher.find(0) && matcher.groupCount() > 0) {
            int whitespaceLength = 0;
            Pattern pattern = Pattern.compile("^([ ]+)([^ ]*)");
            Matcher whitespace = pattern.matcher(matcher.group(0));
            if(whitespace.find(0)){
                whitespaceLength = whitespace.group(0).length();
                incrementColumn(whitespaceLength);
            }

            String newInput = scanner.getInput().substring(matcher.group(0).length());
            if(newInput.charAt(0) == ':'){
                scanner.consume(matcher.group(0).length());
                token = tok(token);
                token.setValue(matcher.group(1));
                incrementColumn(matcher.group(0).length() - whitespaceLength);
                return token;
            }

            Pattern pattern2 = Pattern.compile("^[ \\t]*(\\n|$)");
            Matcher matcher1 = pattern2.matcher(newInput);
            if(matcher1.matches()){
                Pattern pattern3 = Pattern.compile("^[ \\t]*");
                scanner.consume(pattern3.matcher(newInput).group(0).length());
                token = tok(token);
                token.setValue(matcher.group(1));
                incrementColumn(matcher.group(0).length() - whitespaceLength);
                return token;
            }
        }
        return null;
    }
    // private int indexOfDelimiters(char start, char end) {
    // String str = scanner.getInput();
    // int nstart = 0;
    // int nend = 0;
    // int pos = 0;
    // for (int i = 0, len = str.length(); i < len; ++i) {
    // if (start == str.charAt(i)) {
    // nstart++;
    // } else if (end == str.charAt(i)) {
    // nend = nend + 1;
    // if (nend == nstart) {
    // pos = i;
    // break;
    // }
    // }
    // }
    // return pos;
    // }

    private Token stashed() {
        if (stash.size() > 0) {
            return stash.poll();
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
        Matcher matcher = scanner.getMatcherForPattern("^\\n *\\n");
        if (matcher.find(0)) {
            consume(matcher.end()-1);
            incrementLine(1);

            if(this.pipeless) {
                pushToken(new Text("", lineno));
                return true;
            }
            pushToken(this.next());
            return true;
        }
        return false;
    }

    /**
     * end-of-source. ok
     */

//    eos: function() {
//        if (this.input.length) return;
//        if (this.interpolated) {
//            this.error('NO_END_BRACKET', 'End of line was reached with no closing bracket for interpolation.');
//        }
//        for (var i = 0; this.indentStack[i]; i++) {
//            this.tokens.push(this.tokEnd(this.tok('outdent')));
//        }
//        this.tokens.push(this.tokEnd(this.tok('eos')));
//        this.ended = true;
//        return true;
//    },


    private boolean eos() {
        if (scanner.getInput().length() > 0) {
            return false;
        }
//        if(this.interpolated){
//            throw new PugLexerException("End of line was reached with no closing bracket for interpolation.",this.filename,this.lineno,templateLoader);
//        }
        if (indentStack.size() > 0) {
            indentStack.poll();
            pushToken(tok(new Outdent()));
        } else {
            pushToken(tok(new Eos("eos", lineno)));
            this.ended = true;
        }
        return true;
    }

    /**
     * Comment.
     */

//    comment: function() {
//        var captures;
//        if (captures = /^\/\/(-)?([^\n]*)/.exec(this.input)) {
//            this.consume(captures[0].length);
//            var tok = this.tok('comment', captures[2]);
//            tok.buffer = '-' != captures[1];
//            this.interpolationAllowed = tok.buffer;
//            this.tokens.push(tok);
//            this.incrementColumn(captures[0].length);
//            this.tokEnd(tok);
//            this.callLexerFunction('pipelessText');
//            return true;
//        }
//    },

    private Token comment() {
        Matcher matcher = scanner.getMatcherForPattern("^\\/\\/(-)?([^\\n]*)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            boolean buffer = !"-".equals(matcher.group(1));
            Comment comment = new Comment(matcher.group(2), lineno, buffer);
            consume(matcher.end());
            this.pipeless = true;
            return comment;
        }
        return null;
    }

    private Token code() {
        Matcher matcher = scanner.getMatcherForPattern("^(!?=|-)[ \\t]*([^\\n]+)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            consume(matcher.end());
            String flags = matcher.group(1);
            Expression code = new Expression(matcher.group(2), lineno);
            code.setEscape(flags.charAt(0) == '=');
            code.setBuffer(flags.charAt(0) == '=' || flags.length()>1 && flags.charAt(1) == '=');
            if(code.isBuffer())
                try {
                    expressionHandler.assertExpression(matcher.group(2));
                } catch (ExpressionException e) {
                    throw new PugLexerException(e.getMessage(), filename, lineno, templateLoader);
                }
            return code;
        }
        return null;
    }

//    /**
//     * Interpolated tag.
//     */
//
//    interpolation: function() {
//      if (/^#\{/.test(this.input)) {
//        var match;
//        try {
//          match = this.bracketExpression(1);
//        } catch (ex) {
//          return;//not an interpolation expression, just an unmatched open interpolation
//        }
//
//        this.consume(match.end + 1);
//        return this.tok('interpolation', match.src);
//      }
//    }
    private Token interpolation(){
        Matcher matcher = scanner.getMatcherForPattern("^#\\{");
        if (matcher.find(0)) {
            try {
                CharacterParser.Match match = this.bracketExpression(1);
                this.scanner.consume(match.getEnd()+1);
                return new Interpolation(match.getSrc(),lineno);
            } catch(Exception ex){
                return null; //not an interpolation expression, just an unmatched open interpolation
            }
        }
        return null;
    }
    // code: function() {
    // var captures;
    // if (captures = /^(!?=|-)([^\n]+)/.exec(this.input)) {
    // this.consume(captures[0].length);
    // var flags = captures[1];
    // captures[1] = captures[2];
    // var tok = this.tok('code', captures[1]);
    // tok.escape = flags[0] === '=';
    // tok.buffer = flags[0] === '=' || flags[1] === '=';
    // return tok;
    // }
    // },

    private Token tag() {
        Matcher matcher = scanner.getMatcherForPattern("^(\\w[-:\\w]*)(\\/?)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            consume(matcher.end());
            Tag tok;
            String name = matcher.group(1);
            if (':' == name.charAt(name.length() - 1)) {
                name = name.substring(0, name.length() - 1);
                tok = new Tag(name, lineno);
                this.defer(new Colon(lineno));
                while (' ' == scanner.getInput().charAt(0))
                    scanner.consume(1);
            } else {
                tok = new Tag(name, lineno);
            }
            if (!matcher.group(2).isEmpty()) {
                tok.setSelfClosing(true);
            }
            return tok;
        }
        return null;
    }

    private Token yield() {
        Matcher matcher = scanner.getMatcherForPattern("^yield *");
        if (matcher.find(0)) {
            matcher.group(0);
            int end = matcher.end();
            consume(end);
            return new Yield(lineno);
        }
        return null;
    }

    private Token filter() {
        String val = scan("^:([\\w\\-]+)");
        if (StringUtils.isNotBlank(val)) {
            this.pipeless = true;
            return new Filter(val, lineno);
        }
        return null;
    }

    private Token each() {
        Matcher matcher = scanner.getMatcherForPattern("^(?:- *)?(?:each|for) +([a-zA-Z_$][\\w$]*)(?: *, *([a-zA-Z_$][\\w$]*))? * in *([^\\n]+)");
        if (matcher.find(0) && matcher.groupCount() > 2) {
            consume(matcher.end());
            String value = matcher.group(1);
            String key = matcher.group(2);
            String code = matcher.group(3);
            Each each = new Each(value, lineno);
            each.setCode(code);
            each.setKey(key);
            return each;
        }
        return null;
        /*
		 * if (captures = /^(?:- *)?(?:each|for) +(\w+)(?: *, *(\w+))? * in
		 * *([^\n]+)/.exec(this.input)) { this.consume(captures[0].length); var
		 * tok = this.tok('each', captures[1]); tok.key = captures[2] ||
		 * '$index'; tok.code = captures[3]; return tok; }
		 */
    }

    private Token whileToken() {
        String val = scan("^while +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new While(val, lineno);
        }
        return null;
    }

    private Token conditional() {
        Matcher matcher = scanner.getMatcherForPattern("^(if|unless|else if|else)\\b([^\\n]*)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            String type = matcher.group(1);
            String condition = matcher.group(2);
            consume(matcher.end());
            if ("else".equals(type)) {
                return new Else(null, lineno);
            } else if ("else if".equals(type)) {
                return new ElseIf(condition, lineno);
            } else {
                If ifToken = new If(condition, lineno);
                ifToken.setInverseCondition("unless".equals(type));
                return ifToken;
            }
        }
        return null;
    }

	/*
	 * private Token conditionalElse() { String val = scan("^(else)"); if
	 * (StringUtils.isNotBlank(val)) { return new Filter(val, lineno); } return
	 * null; }
	 */
    /**
     * Doctype.
     */

//    doctype: function() {
//      if (this.scan(/^!!! *([^\n]+)?/, 'doctype')) {
//        throw new Error('`!!!` is deprecated, you must now use `doctype`');
//      }
//      var node = this.scan(/^(?:doctype) *([^\n]+)?/, 'doctype');
//      if (node && node.val && node.val.trim() === '5') {
//        throw new Error('`doctype 5` is deprecated, you must now use `doctype html`');
//      }
//      return node;
//    },

    private Token doctype() {
        String val = scan("^!!! *([^\\n]+)?");
        if (StringUtils.isNotBlank(val)) {
            throw new PugLexerException("`!!!` is deprecated, you must now use `doctype`", filename, getLineno(), templateLoader);
        }
        Matcher matcher = scanner.getMatcherForPattern("^(?:doctype) *([^\\n]+)?");
        if (matcher.find(0) && matcher.groupCount()>0) {
            int end = matcher.end();
            consume(end);
            String name = matcher.group(1);
            if(name != null && "5".equals(name.trim()))
                throw new PugLexerException("`doctype 5` is deprecated, you must now use `doctype html`", filename, getLineno(), templateLoader);
            return new Doctype(name, lineno);
        }

        return null;
    }

    private Token id() {
        String val = scan("^#([\\w-]+)");
        if (StringUtils.isNotBlank(val)) {
            return new CssId(val, lineno);
        }
        return null;
    }

    private Token className() {
        String val = scan("^\\.([\\w-]+)");
        if (StringUtils.isNotBlank(val)) {
            return new CssClass(val, lineno);
        }
        return null;
    }


//    endInterpolation: function () {
//        if (this.interpolated && this.input[0] === ']') {
//            this.input = this.input.substr(1);
//            this.ended = true;
//            return true;
//        }
//    },

    private Token endInterpolation(){
        if(interpolated && this.scanner.getInput().charAt(0) == ']'){
            this.consume(1);
            this.ended=true;
        }
        return null;
    }

//    text: function() {
//        var tok = this.scan(/^(?:\| ?| )([^\n]+)/, 'text') ||
//        this.scan(/^( )/, 'text') ||
//        this.scan(/^\|( ?)/, 'text');
//        if (tok) {
//            this.addText('text', tok.val);
//            return true;
//        }
//    },


    private Token text() {
        String val = scan("^(?:\\| ?| )([^\\n]+)");
        if (StringUtils.isEmpty(val)) {
            val = scan("^\\|?( )");
            if (StringUtils.isEmpty(val)) {
                val = scan("^(<[^\\n]*)");
            }
        }
        if (StringUtils.isNotEmpty(val)) {
            return new Text(val, lineno);
        }
        return null;
    }

//    textHtml: function () {
//        var tok = this.scan(/^(<[^\n]*)/, 'text-html');
//        if (tok) {
//            this.addText('text-html', tok.val);
//            return true;
//        }
//    },
    private Token textHtml() {
        String val = scan("^(<[^\\n]*)");
        if (StringUtils.isNotEmpty(val)) {
            return new TextHtml(val, lineno);
        }
        return null;
    }

    private Token textFail() {
        String val = scan("^([^\\.\\n][^\\n]+)");
        if (StringUtils.isNotEmpty(val)) {
            return new Text(val, lineno);
        }
        return null;
    }

    private Token fail() {
        throw new PugLexerException("unexpected text " + StringUtils.substring(scanner.getInput(),0,5), filename, getLineno(), templateLoader);
    }

    private Token extendsToken() {
        String val = scan("^extends? +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new ExtendsToken(val, lineno);
        }
        return null;
    }

    private Token prepend() {
        String name = scan("^prepend +([^\\n]+)");
        if (StringUtils.isNotBlank(name)) {
            Block tok = new Block(name, lineno);
            tok.setMode("prepend");
            return tok;
        }
        return null;
    }

    private Token append() {
        String name = scan("^append +([^\\n]+)");
        if (StringUtils.isNotBlank(name)) {
            Block tok = new Block(name, lineno);
            tok.setMode("append");
            return tok;
        }
        return null;
    }

    private Token block() {
        Matcher matcher = scanner.getMatcherForPattern("^block\\b *(?:(prepend|append) +)?([^\\n]+)");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            String val = matcher.group(1);
            String mode = StringUtils.isNotBlank(val) ? val : "replace";
            String name = matcher.group(2);
            Block tok = new Block(name, lineno);
            tok.setMode(mode);
            consume(matcher.end());
            return tok;
        }
        return null;
    }

    private boolean mixinBlock() {
        Matcher matcher = scanner.getMatcherForPattern("^block[ \\t]*(\\n|$)");
        if (matcher.find(0) && matcher.groupCount() > 0) {
            consume(matcher.end()-matcher.group(1).length());
            MixinBlock mixinBlock = new MixinBlock(lineno);
            pushToken(tokEnd(tok(mixinBlock)));
            return true;
        }
        return false;
    }

    private Token blockCode() {
        Matcher matcher = scanner.getMatcherForPattern("^-\\n");
        if (matcher.find(0)) {
            consume(matcher.end()-1);
            BlockCode blockCode = new BlockCode(lineno);
            this.pipeless = true;
            return blockCode;
        }
        return null;
    }

    private Token include() {
        String val = scan("^include +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new Include(val, lineno);
        }
        return null;
    }

    private Token includeFiltered() {
        Matcher matcher = Pattern.compile("^include:([\\w\\-]+)([\\( ])").matcher(scanner.getInput());
        if(matcher.find(0) && matcher.groupCount()>1){
            this.consume(matcher.end()-1);
            String filter = matcher.group(1);
            Token attrs = matcher.group(2).equals("(") ? this.attrs():null;
            if(!(matcher.group(2).equals(" ") || scanner.getInput().charAt(0) == ' ')){
                throw new PugLexerException("expected space after include:filter but got " + String.valueOf(scanner.getInput().charAt(0)), filename, getLineno(), templateLoader);
            }
            matcher = Pattern.compile("^ *([^\\n]+)").matcher(scanner.getInput());
            if(!(matcher.find(0)&&matcher.groupCount()>0) || matcher.group(1).trim().equals("")){
                throw new PugLexerException("missing path for include:filter", filename, getLineno(), templateLoader);
            }
            this.consume(matcher.end());
            String path = matcher.group(1);
            Include tok = new Include(path, lineno);
            tok.setFilter(filter);
            tok.setAttrs(attrs);
            return tok;

        }
        return null;
    }
    /**
     * Path
     * TODO: check if val.trim is correct.
     */
//
//    path: function() {
//        var tok = this.scanEndOfLine(/^ ([^\n]+)/, 'path');
//        if (tok && (tok.val = tok.val.trim())) {
//            this.tokens.push(this.tokEnd(tok));
//            return true;
//        }
//    },
    private boolean path(){
        String val = scan("^ ([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            Token token = tok(new Path(val.trim()));
            pushToken(tokEnd(token));
            return true;
        }
        return false;
    }
    /**
     * Case.
     * TODO: add error check
     */
    private boolean caseToken() {
        String val = scan("^case +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            Token tok = tok(new CaseToken(val, lineno));
            incrementColumn(-val.length());
            try {
                expressionHandler.assertExpression(val);
            } catch (ExpressionException e) {
                throw new PugLexerException(e.getMessage(), filename, getLineno(), templateLoader);
            }
            pushToken(tokEnd(tok));
            incrementColumn(val.length());
            return true;
        }
        return false;
    }

    /**
     * When.
     * TODO: add error check
     */
    private boolean when() {
        String val = scan("^when +([^:\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            try {
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
                pushToken(new When(val, lineno));
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
        String val = scan("^(default *)");
        if (StringUtils.isNotBlank(val)) {
            pushToken(tok(new Default(val, lineno)));
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
        Matcher matcher = scanner.getMatcherForPattern("^\\.");
        if (matcher.find(0)) {
            Token tok = tok(new Dot(lineno));
            consume(matcher.end());
            pushToken(tokEnd(tok));
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
    private Token attributesBlock() {
        Matcher matcher = scanner.getMatcherForPattern("^&attributes\\b");
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
            return tokEnd(attributesBlock);
        }
        return null;
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

    private Token indent() {
        Matcher matcher;
        String re;

        if (indentRe != null) {
            matcher = scanner.getMatcherForPattern(indentRe);
        } else {
            // tabs
            re = "^\\n(\\t*) *";
            String indentType = "tabs";
            matcher = scanner.getMatcherForPattern(re);

            // spaces
            if (matcher.find(0) && matcher.group(1).length() == 0) {
                re = "^\\n( *)";
                indentType = "spaces";
                matcher = scanner.getMatcherForPattern(re);
            }

            // established
            if (matcher.find(0) && matcher.group(1).length() > 0)
                this.indentRe = re;
            this.indentType = indentType;
        }

        if (matcher.find(0) && matcher.groupCount() > 0) {
            Token tok;
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
                this.pipeless = false;
                this.interpolationAllowed = true;
                return tokEnd(tok(new Newline()));
            }

            // outdent
            if (indentStack.size() > 0 && indents < indentStack.get(0)) {
                while (indentStack.size() > 0 && indentStack.get(0) > indents) {
                    this.colno = 1;
                    Token token = tok(new Outdent());
                    this.colno = indentStack.get(0) + 1;
                    pushToken(tokEnd(token));
                    indentStack.poll();
                }
                tok = this.stash.pollLast();
                // indent
            } else if (indents > 0 && (indentStack.size() == 0 || indents != indentStack.get(0))) {
                tok = tok(new Indent(String.valueOf(indents), lineno));
                this.colno = 1 + indents;
                indentStack.push(indents);
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
            }
            this.pipeless = false;
            return tokEnd(tok);
        }
        return null;
    }

    private Token pushToken(Token token){
        stash.push(token);
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

    private Token pipelessText() {
        if (!this.pipeless) return null;
        Matcher matcher;

        // established regexp
        if (this.indentRe != null) {
            matcher = scanner.getMatcherForPattern(indentRe);
        } else {
            // tabs
            String re = "^\\n(\\t*) *";
            matcher = scanner.getMatcherForPattern(re);
            indentType = "tabs";

            // spaces
            if (matcher.find(0) && matcher.group(1).length() == 0) {
                re = "^\\n( *)";
                matcher = scanner.getMatcherForPattern(re);
                indentType = "spaces";
            }
            // established
            if (matcher.find(0) && matcher.group(1).length() > 0) {
                this.indentRe = re;
            }
        }

        if (matcher.find(0) && matcher.group(1).length() > 0) {
            int indents = calculateIndents(matcher);

            if (indents > 0 && (this.indentStack.size() == 0 || indents > this.indentStack.get(0))) {
                String indent = scanner.getInput().substring(1, indents + 1);
                ArrayList<String> tokens = new ArrayList<String>();
                boolean isMatch = false;

                do {
                    // text has `\n` as a prefix
                    int nextLineBreak = scanner.getInput().substring(1).indexOf('\n');
                    if (-1 == nextLineBreak)
                        nextLineBreak = scanner.getInput().length() - 1;

                    String line = scanner.getInput().substring(1,nextLineBreak+1);

                    if(line.length() <= indents) {
                        indents = line.length();
                    }

                    isMatch = line.substring(0, indents).equals(indent) || !(line.trim().length() > 0);
                    if (isMatch) {
                        // consume test along with `\n` prefix if match
                        this.consume(line.length() + 1);
                        incrementLine(1);
                        tokens.add(line.substring(indents));
                    }
                } while (scanner.getInput().length() > 0 && isMatch);
                while (scanner.getInput().length() == 0 && tokens.get(tokens.size() - 1).equals(""))
                    tokens.remove(tokens.size() - 1);
                PipelessText pipelessText = new PipelessText();
                pipelessText.setValues(tokens);
                return tok(pipelessText);
            }
        }
        return null;
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
//    slash: function() {
//        var tok = this.scan(/^\//, 'slash');
//        if (tok) {
//            this.tokens.push(this.tokEnd(tok));
//            return true;
//        }
//    },

    private Token slash() {
        String val = scan("^\\/");
        if (StringUtils.isNotBlank(val)) {
            return tokEnd(new Slash());
        }
        return null;
    }

    private Token colon() {
        String val = scanOld("^: +");
        if (StringUtils.isNotBlank(val)) {
            return tokEnd(new Colon());
        }
        return null;
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
