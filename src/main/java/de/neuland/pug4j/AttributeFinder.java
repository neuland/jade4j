package de.neuland.pug4j;

import de.neuland.pug4j.lexer.Scanner;
import de.neuland.pug4j.lexer.token.AttributeList;
import java.util.Stack;
import java.util.regex.Matcher;
import org.apache.commons.lang3.CharUtils;

import static de.neuland.pug4j.AttributeFinder.State.CURLY_BRACES;
import static de.neuland.pug4j.AttributeFinder.State.DOUBLE_QUOTED;
import static de.neuland.pug4j.AttributeFinder.State.EDGY_BRACES;
import static de.neuland.pug4j.AttributeFinder.State.EQUALS;
import static de.neuland.pug4j.AttributeFinder.State.KEY;
import static de.neuland.pug4j.AttributeFinder.State.NORMAL_BRACES;
import static de.neuland.pug4j.AttributeFinder.State.SINGLE_QUOTED;
import static de.neuland.pug4j.AttributeFinder.State.VALUE;
import static de.neuland.pug4j.AttributeFinder.State.LITERAL;


public class AttributeFinder {


    enum State {
        KEY,
        EQUALS,

        VALUE,
        SINGLE_QUOTED,
        DOUBLE_QUOTED,

        CURLY_BRACES,
        NORMAL_BRACES,
        EDGY_BRACES,

        LITERAL;

    }

    private final Scanner scanner;
    private final Stack<State> stack = new Stack<>();

    private final AttributeList token;
    private String key = "";
    private String value = "";

    public AttributeFinder(Scanner scanner, int lineNo) {
        this.scanner = scanner;
        stack.push(KEY);
        scanner.consume(1);
        token = new AttributeList(lineNo);
    }

    public AttributeList find() {
        while (!stack.isEmpty() && scanner.isNotEmpty()) {
            switch (stack.pop()) {
                case KEY:
                    findKey();
                    break;

                case EQUALS:
                    handleEquals();
                    break;

                case VALUE:
                    handleValue();
                    break;
            }

            if (stack.isEmpty()) {
                skipWhitespace();


                char character = scanner.charAt(0);
                if (character == ')') {
                    scanner.consume(1);
                    addAttribute();
                    break;
                }

                if (character == '+') {
                    value += character;
                    scanner.consume(1);
                    stack.push(VALUE);
                    skipWhitespace();
                    continue;
                }

                if (character == ',') {
                    scanner.consume(1);
                }
                stack.push(KEY);
                addAttribute();
                skipWhitespace();
            }
        }

        return token;
    }

    private void handleValue() {
        char character = scanner.charAt(0);

        State last = null;
        if(!stack.isEmpty()){
            last = stack.peek();
        }

        if (!isQuote(character, last)) {

            if(isLiteral(character)) {
                if(last != LITERAL) {
                    stack.push(LITERAL);
                }
            } else if(last == LITERAL) {
                stack.pop();
            }

            switch (character) {
                case '\'':
                    handleQuotes(SINGLE_QUOTED, last);
                    break;
                case '\"':
                    handleQuotes(DOUBLE_QUOTED, last);
                    break;

                case '(':
                    stack.push(NORMAL_BRACES);
                    break;
                case '{':
                    stack.push(CURLY_BRACES);
                    break;
                case '[':
                    stack.push(EDGY_BRACES);
                    break;

                case ')':
                    handleClosingBraces(NORMAL_BRACES, last);
                    break;
                case '}':
                    handleClosingBraces(CURLY_BRACES, last);
                    break;
                case ']':
                    handleClosingBraces(EDGY_BRACES, last);
                    break;
            }
        }

        value += character;
        scanner.consume(1);
        if (!stack.isEmpty()) {
            stack.push(VALUE);
        }
    }

    private boolean isLiteral(char character) {
        return CharUtils.isAsciiAlphanumeric(character) || character == '_';
    }

    private boolean isQuote(char character, State last) {
        return (last == SINGLE_QUOTED && character != '\'') || (last == DOUBLE_QUOTED && character != '\"');
    }

    private void handleQuotes(State state, State last) {
        if (last == state) {
            stack.pop();

        } else {
            stack.push(state);
        }
    }

    private void handleClosingBraces(State state, State last) {
        if (state == last) {
            stack.pop();
        } else {
            throw new RuntimeException("dddddd");
        }
    }

    private void skipWhitespace() {
        Matcher matcher = scanner.getMatcherForPattern(" *");
        if (matcher.find()) {
            scanner.consume(matcher.group(0).length());
        }
    }


    private void findKey() {
        Matcher matcher = scanner.getMatcherForPattern("[\\w-_]+");
        String scannedText = scanner.getInput();

        if (matcher.find() && scannedText.startsWith(matcher.group(0))) {       // TODO anderen weg finden?
            key = matcher.group(0);
            scanner.consume(key.length());
            stack.push(EQUALS);
        }
    }


    private void handleEquals() {
        Matcher matcher = scanner.getMatcherForPattern(" *= *");
        String scannedText = scanner.getInput();

        if (matcher.find(0) && scannedText.startsWith(matcher.group(0))) {       // TODO anderen weg finden?
            scanner.consume(matcher.group(0).length());
            stack.push(VALUE);
        }
    }

    private void addAttribute() {
        if(value.isEmpty()) {
            token.addBooleanAttribute(key, true);
        } else if(isDoubleQuotedValue()) {
            token.addAttribute(key, value, false);
        } else {
            token.addExpressionAttribute(key, value, false);
        }
        key = "";
        value = "";
    }

    private boolean isDoubleQuotedValue() {
        return value.charAt(0) == '\"' && value.charAt(value.length() -1) == '\"';
    }

}
