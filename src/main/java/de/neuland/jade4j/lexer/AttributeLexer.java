package de.neuland.jade4j.lexer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.exceptions.JadeLexerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.lexer.token.Attribute;
import de.neuland.jade4j.template.TemplateLoader;

public class AttributeLexer {

    private static final Set<String> KEYWORDS = new HashSet<String>();

    static {
	KEYWORDS.add("if");
	KEYWORDS.add("in");
	KEYWORDS.add("do");
	KEYWORDS.add("var");
	KEYWORDS.add("for");
	KEYWORDS.add("new");
	KEYWORDS.add("try");
	KEYWORDS.add("let");
	KEYWORDS.add("this");
	KEYWORDS.add("else");
	KEYWORDS.add("case");
	KEYWORDS.add("void");
	KEYWORDS.add("with");
	KEYWORDS.add("enum");
	KEYWORDS.add("while");
	KEYWORDS.add("break");
	KEYWORDS.add("catch");
	KEYWORDS.add("throw");
	KEYWORDS.add("const");
	KEYWORDS.add("yield");
	KEYWORDS.add("class");
	KEYWORDS.add("super");
	KEYWORDS.add("return");
	KEYWORDS.add("typeof");
	KEYWORDS.add("delete");
	KEYWORDS.add("switch");
	KEYWORDS.add("export");
	KEYWORDS.add("import");
	KEYWORDS.add("default");
	KEYWORDS.add("finally");
	KEYWORDS.add("extends");
	KEYWORDS.add("function");
	KEYWORDS.add("continue");
	KEYWORDS.add("debugger");
	KEYWORDS.add("package");
	KEYWORDS.add("private");
	KEYWORDS.add("interface");
	KEYWORDS.add("instanceof");
	KEYWORDS.add("implements");
	KEYWORDS.add("protected");
	KEYWORDS.add("public");
	KEYWORDS.add("static");
    }

    private enum Loc {
	KEY, KEY_CHAR, VALUE
    }

    private static final class State {
	boolean lineComment;
	boolean blockComment;

	boolean singleQuote;
	boolean doubleQuote;
	boolean regexp;
	boolean regexpStart;

	boolean escaped;

	int roundDepth;
	int curlyDepth;
	int squareDepth;

	Deque<Character> history = new ArrayDeque<Character>();
	char lastChar;

	public State() {
	    defaultState();
	}

	public void defaultState() {
	    lineComment = false;
	    blockComment = false;

	    singleQuote = false;
	    doubleQuote = false;
	    regexp = false;
	    regexpStart = false;

	    escaped = false;

	    roundDepth = 0;
	    curlyDepth = 0;
	    squareDepth = 0;

	    history.clear();
	    lastChar = '\0';
	}

	public boolean isNesting() {
	    return this.isString() || this.isComment() || this.regexp
		    || this.roundDepth > 0 || this.curlyDepth > 0
		    || this.squareDepth > 0;
	}

	private boolean isComment() {
	    return this.lineComment || this.blockComment;
	}

	private boolean isString() {
	    return this.singleQuote || this.doubleQuote;
	}
    }

    private static final Pattern cleanRe = Pattern.compile("^['\"]|['\"]$");
    private static final Pattern doubleQuotedRe = Pattern
	    .compile("^\"[^\"]*\"$");
    private static final Pattern quotedRe = Pattern.compile("^'[^']*'$");

    private final String str;
    private final String filename;
    private final int lineno;
    private final TemplateLoader templateLoader;
    private final StringBuilder key;
    private final StringBuilder val;
    private final State state;

    private Loc loc;
    private char quote;

    public AttributeLexer(String input, String filename, int lineno,
	    TemplateLoader templateLoader) {
	str = input;
	this.filename = filename;
	this.lineno = lineno;
	this.templateLoader = templateLoader;
	key = new StringBuilder();
	val = new StringBuilder();
	state = new State();
    }

    public Attribute getToken() {
	quote = '\0';
	Attribute token = new Attribute(str, lineno);
	boolean escapedAttr = true;
	loc = Loc.KEY;

	for (int i = 0; i <= str.length(); i++) {
	    if (isEndOfAttribute(i)) {
		String strVal = val.toString().trim();
		if (!StringUtils.isBlank(strVal)) {
		    try {
			ExpressionHandler.assertExpression(strVal);
		    } catch (ExpressionException e) {
			throw new JadeLexerException(e.getMessage(), filename,
				lineno, templateLoader);
		    }
		}
		if (key.charAt(0) == ':' || key.charAt(key.length() - 1) == ':') {
		    throw new JadeLexerException(
			    "\":\" is not valid as the start or end of an un-quoted attribute.",
			    filename, lineno, templateLoader);
		}
		String strKey = key.toString().trim();
		strKey = cleanRe.matcher(strKey).replaceAll("");
		if ("".equals(strVal)) {
		    token.addBooleanAttribute(strKey, Boolean.TRUE);
		} else if (doubleQuotedRe.matcher(strVal).matches()
			|| quotedRe.matcher(strVal).matches()) {
		    token.addAttribute(strKey, cleanRe.matcher(strVal)
			    .replaceAll(""));
		} else {
		    token.addExpressionAttribute(strKey, strVal);
		}

		key.setLength(0);
		val.setLength(0);
		loc = Loc.KEY;
		escapedAttr = false;
	    } else {
		switch (loc) {
		case KEY_CHAR:
		    if (str.charAt(i) == quote) {
			loc = Loc.KEY;
			if (i + 1 < str.length()
				&& !isValidKeyCharFollowingQuote(str.charAt(i + 1)))
			    throw new JadeLexerException(
				    "Unexpected character \"" + str.charAt(i + 1)
					    + "\" expected ` `, `\\n`, `\\t`, `,`, `!` or `=`",
				    filename, lineno, templateLoader);
		    } else {
			key.append(str.charAt(i));
		    }
		    break;
		case KEY:
		    if (key.length() == 0 && isQuote(str.charAt(i))) {
			loc = Loc.KEY_CHAR;
			quote = str.charAt(i);
		    } else if (str.charAt(i) == '!' || str.charAt(i) == '=') {
			escapedAttr = str.charAt(i) != '!';
			if (str.charAt(i) == '!')
			    i++;
			if (str.charAt(i) != '=') {
			    throw new JadeLexerException(
				    "Unexpected character " + str.charAt(i)
					    + " expected `=`", filename,
				    lineno, templateLoader);
			}
			loc = Loc.VALUE;
			state.defaultState();
		    } else {
			key.append(str.charAt(i));
		    }
		    break;
		case VALUE:
		    parseChar(str.charAt(i), state);
		    val.append(str.charAt(i));
		    break;
		}
	    }
	}

	return token;
    }

    private boolean isValidKeyCharFollowingQuote(char c) {
	switch (c) {
	case ' ':
	case ',':
	case '!':
	case '=':
	case '\n':
	case '\t':
	    return true;
	default:
	    return false;
	}
    }

    private boolean isEndOfAttribute(int i) {
	// if the key is not started, then the attribute cannot be ended
	if (key.toString().trim().isEmpty()) {
	    return false;
	}
	// if there's nothing more then the attribute must be ended
	if (i == str.length()) {
	    return true;
	}

	if (loc.equals(Loc.KEY)) {
	    if (isWhitespace(str.charAt(i))) {
		// find the first non-whitespace character
		for (int x = i; x < str.length(); x++) {
		    char strX = str.charAt(x);
		    if (!isWhitespace(strX)) {
			// starts a `value`
			if (strX == '=' || strX == '!') {
			    return false;
			} else if (strX == ',') {
			    // will be handled when x === i
			    return false;
			} else {
			    // attribute ended
			    return true;
			}
		    }
		}
	    }
	    // if there's no whitespace and the character is not ',', the
	    // attribute did not end.
	    return str.charAt(i) == ',';
	} else if (loc.equals(Loc.VALUE)) {
	    // if the character is in a string or in parentheses/brackets/braces
	    if (state.isNesting() || state.isString()) {
		return false;
	    }
	    try {
		// if the current value expression is not valid JavaScript, then
		// assume that the user did not end the value
		if (val.toString().trim().isEmpty()) {
		    // a blank expression is not valid but passes
		    // ExpressionHandler.assertExpression
		    return false;
		}
		ExpressionHandler.assertExpression(val.toString());
	    } catch (Exception ex) {
		return false;
	    }
	    if (isWhitespace(str.charAt(i))) {
		// find the first non-whitespace character
		for (int x = i; x < str.length(); x++) {
		    if (!isWhitespace(str.charAt(x))) {
			// if it is a JavaScript punctuator, then assume that it
			// is a part of the value
			return !isPunctuator(str.charAt(x))
				|| isQuote(str.charAt(x));
		    }
		}
	    }
	    // if there's no whitespace and the character is not ',', the
	    // attribute did not end.
	    return str.charAt(i) == ',';
	}
	return false;
    }

    private boolean isQuote(char c) {
	switch (c) {
	case '\'':
	case '"':
	    return true;
	default:
	    return false;
	}
    }

    private boolean isWhitespace(char c) {
	switch (c) {
	case ' ':
	case '\t':
	case '\n':
	    return true;
	default:
	    return false;
	}
    }

    private State parseChar(char character, State state) {
	boolean wasComment = state.blockComment || state.lineComment;
	char lastChar = state.history.isEmpty() ? '\0' : state.history.peek();

	if (state.regexpStart) {
	    if (character == '/' || character == '*') {
		state.regexp = false;
	    }
	    state.regexpStart = false;
	}
	if (state.lineComment) {
	    if (character == '\n') {
		state.lineComment = false;
	    }
	} else if (state.blockComment) {
	    if (state.lastChar == '*' && character == '/') {
		state.blockComment = false;
	    }
	} else if (state.singleQuote) {
	    if (character == '\'' && !state.escaped) {
		state.singleQuote = false;
	    } else if (character == '\\' && !state.escaped) {
		state.escaped = true;
	    } else {
		state.escaped = false;
	    }
	} else if (state.doubleQuote) {
	    if (character == '"' && !state.escaped) {
		state.doubleQuote = false;
	    } else if (character == '\\' && !state.escaped) {
		state.escaped = true;
	    } else {
		state.escaped = false;
	    }
	} else if (state.regexp) {
	    if (character == '/' && !state.escaped) {
		state.regexp = false;
	    } else if (character == '\\' && !state.escaped) {
		state.escaped = true;
	    } else {
		state.escaped = false;
	    }
	} else if (lastChar == '/' && character == '/') {
	    state.history.pop();
	    state.lineComment = true;
	} else if (lastChar == '/' && character == '*') {
	    state.history.pop();
	    state.blockComment = true;
	} else if (character == '/' && isRegexp(state.history)) {
	    state.regexp = true;
	    state.regexpStart = true;
	} else if (character == '\'') {
	    state.singleQuote = true;
	} else if (character == '"') {
	    state.doubleQuote = true;
	} else if (character == '(') {
	    state.roundDepth++;
	} else if (character == ')') {
	    state.roundDepth--;
	} else if (character == '{') {
	    state.curlyDepth++;
	} else if (character == '}') {
	    state.curlyDepth--;
	} else if (character == '[') {
	    state.squareDepth++;
	} else if (character == ']') {
	    state.squareDepth--;
	}
	if (!state.blockComment && !state.lineComment && !wasComment) {
	    state.history.push(character);
	}
	state.lastChar = character; // store last character for ending block
				    // comments
	return state;
    }

    private final static Pattern WORD_REGEXP = Pattern.compile("\\b\\w+$");

    private boolean isRegexp(Deque<Character> history) {
	// could be start of regexp or divide sign
	Iterator<Character> iter = history.descendingIterator();
	StringBuilder sbHistory = new StringBuilder(history.size());
	while (iter.hasNext()) {
	    sbHistory.append(iter.next());
	}
	String strHistory = StringUtils.stripEnd(sbHistory.toString(), null);

	char lastHistoryChar = strHistory.charAt(strHistory.length() - 1);
	// unless its an `if`, `while`, `for` or `with` it's a divide, so we
	// assume it's a divide
	if (lastHistoryChar == ')') {
	    return false;
	}
	// unless it's a function expression, it's a regexp, so we assume it's a
	// regexp
	if (lastHistoryChar == '}') {
	    return true;
	}
	// any punctuation means it's a regexp
	if (isPunctuator(lastHistoryChar)) {
	    return true;
	}
	// if the last thing was a keyword then it must be a regexp (e.g.
	// `typeof /foo/`)
	if (WORD_REGEXP.matcher(strHistory).matches()
		&& isKeyword(WORD_REGEXP.matcher(strHistory).group())) {
	    return true;
	}

	return false;
    }

    private boolean isPunctuator(char c) {

	switch (c) {
	case 46: // . dot
	case 40: // ( open bracket
	case 41: // ) close bracket
	case 59: // ; semicolon
	case 44: // , comma
	case 123: // { open curly brace
	case 125: // } close curly brace
	case 91: // [
	case 93: // ]
	case 58: // :
	case 63: // ?
	case 126: // ~
	case 37: // %
	case 38: // &
	case 42: // *:
	case 43: // +
	case 45: // -
	case 47: // /
	case 60: // <
	case 62: // >
	case 94: // ^
	case 124: // |
	case 33: // !
	case 61: // =
	    return true;
	default:
	    return false;
	}
    }

    private boolean isKeyword(String id) {
	return KEYWORDS.contains(id);
    }
}
