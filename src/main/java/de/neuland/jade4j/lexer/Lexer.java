package de.neuland.jade4j.lexer;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeLexerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.lexer.token.*;
import de.neuland.jade4j.template.TemplateLoader;
import de.neuland.jade4j.util.CharacterParser;
import de.neuland.jade4j.util.Options;
import de.neuland.jade4j.util.StringReplacer;
import de.neuland.jade4j.util.StringReplacerCallback;
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
    private LinkedList<Token> stash;
    private LinkedList<Integer> indentStack;
    private String indentRe = null;
    private boolean pipeless = false;
    @SuppressWarnings("unused")
    private boolean attributeMode;
    private final String filename;
    private final TemplateLoader templateLoader;
    private String indentType;
    private CharacterParser characterParser;
    private ExpressionHandler expressionHandler;

    public Lexer(String filename, TemplateLoader templateLoader,ExpressionHandler expressionHandler) throws IOException {
        this.expressionHandler = expressionHandler;
        this.templateLoader = templateLoader;
        this.filename = ensureJadeExtension(filename,templateLoader);
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
        this.filename = ensureJadeExtension(filename,templateLoader);
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

        if ((token = blank()) != null) {
        	return token;
        }

        if ((token = eos()) != null) {
           return token;
        }
        
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
        
        if ((token = caseToken()) != null) {
            return token;
        }

        if ((token = when()) != null) {
           return token;
        }
        
        if ((token = defaultToken()) != null) {
            return token;
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
        
        if ((token = mixinBlock()) != null) {
            return token;
        }

        if ((token = include()) != null) {
            return token;
        }
        
        if ((token = includeFiltered()) != null) {
            return token;
        }

        if ((token = mixin()) != null) {
           return token;
        }
        
        if ((token = call()) != null) {
           return token;
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

        if ((token = comment()) != null) {
            return token;
        }
        
        if ((token = colon()) != null) {
            return token;
        }
        
        if ((token = dot()) != null) {
            return token;
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
            throw new JadeLexerException("unrecognized start character", filename, getLineno(), templateLoader);
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
            throw new JadeLexerException(exception.getMessage() + " See "+ StringUtils.substring(scanner.getInput(),0,5), filename, getLineno(), templateLoader);
        }
        if(scanner.getInput().charAt(range.getEnd()) != end)
            throw new JadeLexerException("start character " + start + " does not match end character " + scanner.getInput().charAt(range.getEnd()), filename, getLineno(), templateLoader);
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
    private String scan(String regexp) {
        String result = null;
        Matcher matcher = scanner.getMatcherForPattern(regexp);
        if (matcher.find(0) && matcher.group(0)!=null) {
            int end = matcher.end();
            consume(end);
            return matcher.group(0);
        }
        return result;
    }
    private String scan1(String regexp) {
        String result = null;
        Matcher matcher = scanner.getMatcherForPattern(regexp);
        if (matcher.find(0) && matcher.groupCount()>0) {
            int end = matcher.end();
            consume(end);
            return matcher.group(1);
        }
        return result;
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
     * Blank line.
     */

//    blank: function() {
//      var captures;
//      if (captures = /^\n *\n/.exec(this.input)) {
//        this.consume(captures[0].length - 1);
//        ++this.lineno;
//        if (this.pipeless) return this.tok('text', '');
//        return this.next();
//      }
//    },
    private Token blank(){
        Matcher matcher = scanner.getMatcherForPattern("^\\n *\\n");
        if (matcher.find(0)) {
            consume(matcher.end()-1);
            ++this.lineno;

            if(this.pipeless)
                return new Text("",lineno);
            return this.next();
        }
        return null;
    }
    private Token eos() {
        if (scanner.getInput().length() > 0) {
            return null;
        }
        if (indentStack.size() > 0) {
            indentStack.poll();
            return new Outdent(lineno);
        } else {
            return new Eos("eos", lineno);
        }
    }

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
                    throw new JadeLexerException(e.getMessage(), filename, lineno, templateLoader);
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
        String val = scan1("^:([\\w\\-]+)");
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
        String val = scan1("^while +([^\\n]+)");
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
        String val = scan1("^!!! *([^\\n]+)?");
        if (StringUtils.isNotBlank(val)) {
            throw new JadeLexerException("`!!!` is deprecated, you must now use `doctype`", filename, getLineno(), templateLoader);
        }
        Matcher matcher = scanner.getMatcherForPattern("^(?:doctype) *([^\\n]+)?");
        if (matcher.find(0) && matcher.groupCount()>0) {
            int end = matcher.end();
            consume(end);
            String name = matcher.group(1);
            if(name != null && "5".equals(name.trim()))
                throw new JadeLexerException("`doctype 5` is deprecated, you must now use `doctype html`", filename, getLineno(), templateLoader);
            return new Doctype(name, lineno);
        }

        return null;
    }

    private Token id() {
        String val = scan1("^#([\\w-]+)");
        if (StringUtils.isNotBlank(val)) {
            return new CssId(val, lineno);
        }
        return null;
    }

    private Token className() {
        String val = scan1("^\\.([\\w-]+)");
        if (StringUtils.isNotBlank(val)) {
            return new CssClass(val, lineno);
        }
        return null;
    }
//    text: function() {
//        return this.scan(/^(?:\| ?| )([^\n]+)/, 'text') ||
//          this.scan(/^\|?( )/, 'text') ||
//          this.scan(/^(<[^\n]*)/, 'text');
//      },
    private Token text() {
        String val = scan1("^(?:\\| ?| )([^\\n]+)");
        if (StringUtils.isEmpty(val)) {
            val = scan1("^\\|?( )");
            if (StringUtils.isEmpty(val)) {
                val = scan1("^(<[^\\n]*)");
            }
        }
        if (StringUtils.isNotEmpty(val)) {
            return new Text(val, lineno);
        }
        return null;
    }
    private Token textFail() {
        String val = scan1("^([^\\.\\n][^\\n]+)");
        if (StringUtils.isNotEmpty(val)) {
            return new Text(val, lineno);
        }
        return null;
    }

    private Token fail() {
        throw new JadeLexerException("unexpected text " + StringUtils.substring(scanner.getInput(),0,5), filename, getLineno(), templateLoader);
    }

    private Token extendsToken() {
        String val = scan1("^extends? +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new ExtendsToken(val, lineno);
        }
        return null;
    }

    private Token prepend() {
        String name = scan1("^prepend +([^\\n]+)");
        if (StringUtils.isNotBlank(name)) {
            Block tok = new Block(name, lineno);
            tok.setMode("prepend");
            return tok;
        }
        return null;
    }

    private Token append() {
        String name = scan1("^append +([^\\n]+)");
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

    private Token mixinBlock() {
        Matcher matcher = scanner.getMatcherForPattern("^block[ \\t]*(\\n|$)");
        if (matcher.find(0) && matcher.groupCount() > 0) {
            consume(matcher.end()-matcher.group(1).length());
            return new MixinBlock(lineno);
        }
        return null;
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
        String val = scan1("^include +([^\\n]+)");
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
                throw new JadeLexerException("expected space after include:filter but got " + String.valueOf(scanner.getInput().charAt(0)), filename, getLineno(), templateLoader);
            }
            matcher = Pattern.compile("^ *([^\\n]+)").matcher(scanner.getInput());
            if(!(matcher.find(0)&&matcher.groupCount()>0) || matcher.group(1).trim().equals("")){
                throw new JadeLexerException("missing path for include:filter", filename, getLineno(), templateLoader);
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

    private Token caseToken() {
        String val = scan1("^case +([^\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new CaseToken(val, lineno);
        }
        return null;
    }

    private Token when() {
        String val = scan1("^when +([^:\\n]+)");
        if (StringUtils.isNotBlank(val)) {
            return new When(val, lineno);
        }
        return null;
    }

    private Token defaultToken() {
        String val = scan1("^(default *)");
        if (StringUtils.isNotBlank(val)) {
            return new Default(val, lineno);
        }
        return null;
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

    private Token dot() {
        this.pipeless = true;
        Matcher matcher = scanner.getMatcherForPattern("^\\.");
        if (matcher.find(0)) {
            Dot tok = new Dot(lineno);
            consume(matcher.end());
            return tok;
        }
        return null;
    }

    private Token mixin() {
        Matcher matcher = scanner.getMatcherForPattern("^mixin +([-\\w]+)(?: *\\((.*)\\))? *");
        if (matcher.find(0) && matcher.groupCount() > 1) {
            Mixin tok = new Mixin(matcher.group(1), lineno);
            tok.setArguments(matcher.group(2));
            consume(matcher.end());
            return tok;
        }
        return null;
    }

    private Token call() {
        Call tok;
        Matcher matcher = scanner.getMatcherForPattern("^\\+(\\s*)(([-\\w]+)|(#\\{))");
        if (matcher.find(0) && matcher.groupCount() > 3) {
            // try to consume simple or interpolated call
            if(matcher.group(3)!=null) {
                // simple call
                consume(matcher.end());
                tok = new Call(matcher.group(3), lineno);
            }else{
                // interpolated call
                CharacterParser.Match match = this.bracketExpression(2 + matcher.group(1).length());
                this.consume(match.getEnd() + 1);
                try {
                    expressionHandler.assertExpression(match.getSrc());
                } catch (ExpressionException e) {
                    e.printStackTrace();
                }
                tok = new Call("#{"+match.getSrc()+"}", lineno);
            }
            matcher = scanner.getMatcherForPattern("^ *\\(");
            if (matcher.find(0)) {
                CharacterParser.Match range = this.bracketExpression(matcher.group(0).length() - 1);
                matcher = Pattern.compile("^\\s*[-\\w]+ *=").matcher(range.getSrc());
                if (!matcher.find(0)) { // not attributes
                    this.consume(range.getEnd() + 1);
                    tok.setArguments(range.getSrc());
                }
                if (tok.getArguments()!=null) {
                    try {
                        expressionHandler.assertExpression("[" + tok.getArguments() + "]");
                    } catch (ExpressionException e) {
                        e.printStackTrace();
                    }
                }
            }
            return tok;
        }
        return null;
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
                            if (characterParser.isPunctuator(str.charAt(x)) && str.charAt(x) != '"' && str.charAt(x) != '\'')
                                return false;
                            else
                                return true;
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
                    throw new JadeLexerException(e.getMessage()+ " See " + match, filename, getLineno(), templateLoader);
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
                throw new JadeLexerException("Nesting must match on expression `" + exp + "`", filename, getLineno(), templateLoader);
            }
        } catch (CharacterParser.SyntaxError syntaxError) {
            throw new JadeLexerException("Nesting must match on expression `" + exp + "`", filename, getLineno(), templateLoader);
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
            int index = this.bracketExpression().getEnd();
            String str = scanner.getInput().substring(1, index);
            AttributeList tok = new AttributeList(getLineno());

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
                        throw new JadeLexerException(e.getMessage(), filename, lineno, templateLoader);
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
                                    throw new JadeLexerException("Unexpected character " + str.charAt(i + 1) + " expected ` `, `\\n`, `,`, `!` or `=`", filename, getLineno(), templateLoader);
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
                                    throw new JadeLexerException("Unexpected character " + str.charAt(i) + " expected `=`", filename, getLineno(), templateLoader);
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

            return tok;
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
            this.scanner.consume(11);
            CharacterParser.Match match = this.bracketExpression();
            this.scanner.consume(match.getEnd()+1);
            return new AttributesBlock(match.getSrc(),lineno);
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
            lineno++;
            consume(indents + 1);

            if(scanner.getInput().length() > 0  && (scanner.getInput().charAt(0) == ' ' || scanner.getInput().charAt(0) == '\t')){
                throw new JadeLexerException("Invalid indentation, you can use tabs or spaces but not both", filename, getLineno(), templateLoader);
            }
//            if (lastIndents <= 0 && indents > 0)
//                lastIndents = indents;
//            if ((indents > 0 && lastIndents > 0 && indents % lastIndents != 0) || scanner.isIntendantionViolated()) {
//                throw new JadeLexerException("invalid indentation; expecting " + indents + " " + indentType, filename, getLineno(), templateLoader);
//            }

            // blank line
            if (scanner.isBlankLine()) {
                this.pipeless = false;
                return new Newline(lineno);
            }

            // outdent
            if (indentStack.size() > 0 && indents < indentStack.get(0)) {
                while (indentStack.size() > 0 && indentStack.get(0) > indents) {
                    stash.add(new Outdent(lineno));
                    indentStack.poll();
                }
                tok = this.stash.pollLast();
                // indent
            } else if (indents > 0 && (indentStack.size() == 0 || indents != indentStack.get(0))) {
                indentStack.push(indents);
                tok = new Indent(String.valueOf(indents), lineno);
                tok.setIndents(indents);
                // newline
            } else {
                tok = new Newline(lineno);
            }
            this.pipeless = false;
            return tok;
        }
        return null;
    }
    private Token pipelessText(){
        if (!this.pipeless) return null;
        Matcher matcher;
        String re;

        // established regexp
        if (this.indentRe != null) {
            matcher = scanner.getMatcherForPattern(indentRe);
            // determine regexp
        } else {
            // tabs
            re = "^\\n(\\t*) *";
            matcher = scanner.getMatcherForPattern(re);

            // spaces
            if (matcher.find(0) && matcher.group(1).length() == 0) {
                re = "^\\n( *)";
                matcher = scanner.getMatcherForPattern(re);
            }
            // established
            if (matcher.find(0) && matcher.group(1).length() > 0)
                this.indentRe = re;
        }
        if (matcher.find(0) && matcher.group(1).length() > 0) {
            int indents = matcher.group(1).length();
            if (indents > 0 && (this.indentStack.size() == 0 || indents > this.indentStack.get(0))) {
                String indent = matcher.group(1);
                ArrayList<String> tokens = new ArrayList<String>();
                boolean isMatch = false;

                do {
                    // text has `\n` as a prefix
                    int i = scanner.getInput().substring(1).indexOf('\n');
                    if (-1 == i)
                        i = scanner.getInput().length() - 1;
                    String str;
                    str = scanner.getInput().substring(1,i+1);
                    int indentLength = indent.length();
                    if(str.length()<=indentLength)
                        indentLength = str.length();
                    isMatch = str.substring(0, indentLength).equals(indent) || !(str.trim().length() > 0);
                    if (isMatch) {
                        // consume test along with `\n` prefix if match
                        this.consume(str.length() + 1);
                        lineno++;
                        tokens.add(str.substring(indentLength));
                    }
                } while (scanner.getInput().length() > 0 && isMatch);
                while (scanner.getInput().length() == 0 && tokens.get(tokens.size() - 1).equals(""))
                    tokens.remove(tokens.size() - 1);
                PipelessText pipelessText = new PipelessText(lineno);
                pipelessText.setValues(tokens);
                return pipelessText;
            }
        }
        return null;
    }

    private Token colon() {
        String val = scan("^: *");
        if (StringUtils.isNotBlank(val)) {
            return new Colon(lineno);
        }
        return null;
    }

    private String ensureJadeExtension(String templateName,TemplateLoader templateLoader) {
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
