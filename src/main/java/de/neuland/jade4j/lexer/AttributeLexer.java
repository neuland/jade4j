package de.neuland.jade4j.lexer;

import de.neuland.jade4j.lexer.token.AttributeList;

import java.util.Deque;
import java.util.LinkedList;

public class AttributeLexer {

    public enum State {
        KEY, KEY_CHAR, VALUE, EXPRESSION, ARRAY, STRING, OBJECT
    }

	/*
     * len = str.length , colons = this.colons , states = ['key'] , key = '' ,
	 * val = '' , quote , c;
	 */

    private String key = "";
    private String value = "";
    private AttributeList token;
    private Deque<State> states = new LinkedList<State>();
    private char quote = ' ';

    public AttributeLexer() {
        states.add(State.KEY);
    }

    public AttributeList getToken(String input, int lineno) {
        token = new AttributeList(lineno);
        for (int i = 0; i < input.length(); i++) {
            parse(input.charAt(i));
        }
        parse(',');
        return token;
    }

    private State state() {
        return states.getFirst();
    }

    private void parse(char c) {
        char real = c;
        switch (c) {
            case ',':
            case '\n':
                switch (state()) {
                    case EXPRESSION:
                    case ARRAY:
                    case STRING:
                    case OBJECT:
                        value += c;
                        break;
                    default:
                        states.push(State.KEY);
                        value = value.trim();
                        key = key.trim();
                        if ("".equals(key)) {
                            return;
                        }
                        String name = key.replaceAll("^['\"]|['\"]$", "");
                        String cleanValue = value.replaceAll("^['\"]|['\"]$", "");

                        if ("".equals(cleanValue) && quote == ' ') {
                            token.addBooleanAttribute(name, Boolean.TRUE);
                        } else if (value.matches("^\"[^\"]*\"$") || value.matches("^'[^']*'$")) {
                            token.addAttribute(name, cleanValue, false);
                        } else {
                            token.addExpressionAttribute(name, value, false);
                        }
                        key = "";
                        value = "";
                        quote = ' ';
                        break;
                }
                break;
            case '=':
                parseAssign(real);
                break;
            case '(':
                parseExpressionStart(c);
                break;
            case ')':
                parseExpressionEnd(c);
                break;
            case '{':
                parseObjectStart(c);
                break;
            case '}':
                parseObjectEnd(c);
                break;
            case '[':
                parseArrayStart(c);
                break;
            case ']':
                parseArrayEnd(c);
                break;
            case '"':
            case '\'':
                parseQuotes(c);
                break;
            default:
                parseDefaults(c);
                break;
        }
    }

    private void parseAssign(char real) {
        switch (state()) {
            case KEY_CHAR:
                key += real;
                break;
            case VALUE:
            case EXPRESSION:
            case ARRAY:
            case STRING:
            case OBJECT:
                value += real;
                break;
            default:
                states.push(State.VALUE);
                break;
        }
    }

    private void parseExpressionStart(char c) {
        if (state() == State.VALUE || state() == State.EXPRESSION) {
            states.push(State.EXPRESSION);
        }
        value += c;
    }

    private void parseExpressionEnd(char c) {
        if (state() == State.VALUE || state() == State.EXPRESSION) {
            states.pop();
        }
        value += c;
    }

    private void parseObjectStart(char c) {
        if (state() == State.VALUE) {
            states.push(State.OBJECT);
        }
        value += c;
    }

    private void parseObjectEnd(char c) {
        if (state() == State.OBJECT) {
            states.pop();
        }
        value += c;
    }

    private void parseArrayStart(char c) {
        if (state() == State.VALUE) {
            states.push(State.ARRAY);
        }
        value += c;
    }

    private void parseArrayEnd(char c) {
        if (state() == State.ARRAY) {
            states.pop();
        }
        value += c;
    }

    private void parseQuotes(char c) {
        switch (state()) {
            case KEY:
                states.push(State.KEY_CHAR);
                break;
            case KEY_CHAR:
                states.pop();
                break;
            case STRING:
                if (c == quote) {
                    states.pop();
                }
                value += c;
                break;
            default:
                states.push(State.STRING);
                value += c;
                quote = c;
                break;
        }
    }

    private void parseDefaults(char c) {
        switch (state()) {
            case KEY:
            case KEY_CHAR:
                key += c;
                break;
            default:
                value += c;
                break;
        }
    }
}
