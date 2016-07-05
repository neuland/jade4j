package de.neuland.jade4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterParser {

    private Pattern pattern = Pattern.compile("^\\w+\\b");

    //    function parse(src, state, options) {
//      options = options || {};
//      state = state || exports.defaultState();
//      var start = options.start || 0;
//      var end = options.end || src.length;
//      var index = start;
//      while (index < end) {
//        if (state.roundDepth < 0 || state.curlyDepth < 0 || state.squareDepth < 0) {
//          throw new SyntaxError('Mismatched Bracket: ' + src[index - 1]);
//        }
//        exports.parseChar(src[index++], state);
//      }
//      return state;
//    }
    public static class SyntaxError extends Exception{
        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public SyntaxError(String message) {
            super(message);
        }
    }
    public State parse(String src) throws SyntaxError {
        Options options = new Options();
        options.setEnd(src.length());
        return this.parse(src,this.defaultState(),options);
    }
    public State parse(String src,State state) throws SyntaxError {
        Options options = new Options();
        options.setEnd(src.length());
        return this.parse(src,state,options);
    }

    public State parse(String src,State state,Options options) throws SyntaxError {
      if(options == null) {
          options = new Options();
          options.setEnd(src.length());
      }
      if(state == null)
        state = this.defaultState();
      int start = options.getStart();
      int end = options.getEnd();
      int index = start;
      while (index < end) {
        if (state.getRoundDepth() < 0 || state.getCurlyDepth() < 0 || state.getSquareDepth() < 0) {
          throw new SyntaxError("Mismatched Bracket: " + src.charAt(index - 1));
        }
        this.parseChar(src.charAt(index++), state);
      }
      return state;
    }
//    function parseMax(src, options) {
//      options = options || {};
//      var start = options.start || 0;
//      var index = start;
//      var state = exports.defaultState();
//      while (state.roundDepth >= 0 && state.curlyDepth >= 0 && state.squareDepth >= 0) {
//        if (index >= src.length) {
//          throw new Error('The end of the string was reached with no closing bracket found.');
//        }
//        exports.parseChar(src[index++], state);
//      }
//      var end = index - 1;
//      return {
//        start: start,
//        end: end,
//        src: src.substring(start, end)
//      };
//    }
    public Match parseMax(String src) throws SyntaxError {
        Options options = new Options();
        return this.parseMax(src, options);
    }
    public Match parseMax(String src,Options options) throws SyntaxError {
        if(options == null)
            options = new Options();
      int start = options.getStart();
      int index = start;
      State state = this.defaultState();
      while (state.getRoundDepth() >= 0 && state.getCurlyDepth() >= 0 && state.getSquareDepth() >= 0) {
        if (index >= src.length()) {
          throw new SyntaxError("The end of the string was reached with no closing bracket found.");
        }
        this.parseChar(src.charAt(index++), state);
      }
      int end = index - 1;
      return new Match(start,end,src.substring(start,end));
    }
//    var bracketToProp = {
//      ')': 'roundDepth',
//      '}': 'curlyDepth',
//      ']': 'squareDepth'
//    };
//
//    function parseMaxBracket(src, bracket, options) {
//      options = options || {};
//      var start = options.start || 0;
//      var index = start;
//      var state = exports.defaultState();
//      var prop = bracketToProp[bracket];
//      if (prop === undefined) {
//        throw new Error('Bracket specified (' + JSON.stringify(bracket) + ') is not one of ")", "]", or "}";');
//      }
//      while (state[prop] >= 0) {
//        if (index >= src.length) {
//          throw new Error('The end of the string was reached with no closing bracket "' + bracket + '" found.');
//        }
//        exports.parseChar(src[index++], state);
//      }
//      var end = index - 1;
//      return {
//        start: start,
//        end: end,
//        src: src.substring(start, end)
//      };
//    }
    private int getStateProp(State state, char bracket){
        if(')' == bracket)
            return state.getRoundDepth();
        if('}' == bracket)
            return state.getCurlyDepth();
        if(']' == bracket)
            return state.getSquareDepth();
        return -1;
    }
    public Match parseMaxBracket(String src,char bracket) throws SyntaxError {
        return this.parseMaxBracket(src,bracket,new Options());
    }
    public Match parseMaxBracket(String src,char bracket,Options options) throws SyntaxError {
        if (options == null)
            options = new Options();
        int start = options.getStart();
        int index = start;
        State state = this.defaultState();
        if (bracket != ')' && bracket != '}' && bracket != ']') {
            throw new SyntaxError("Bracket specified (" + String.valueOf(bracket) + ") is not one of \")\", \"]\", or \"}\"");
        }
        while (getStateProp(state,bracket) >= 0) {
            if (index >= src.length()) {
                throw new SyntaxError("The end of the string was reached with no closing bracket \"" + bracket + "\" found.");
            }
            this.parseChar(src.charAt(index++), state);
        }
        int end = index - 1;
        return new Match(start, end, src.substring(start, end));
    }

//    function parseUntil(src, delimiter, options) {
//      options = options || {};
//      var includeLineComment = options.includeLineComment || false;
//      var start = options.start || 0;
//      var index = start;
//      var state = exports.defaultState();
//      while (state.isString() || state.regexp || state.blockComment ||
//             (!includeLineComment && state.lineComment) || !startsWith(src, delimiter, index)) {
//        exports.parseChar(src[index++], state);
//      }
//      var end = index;
//      return {
//        start: start,
//        end: end,
//        src: src.substring(start, end)
//      };
//    }
    public Match parseUntil(String src,String delimiter) {
        return this.parseUntil(src,delimiter,new Options());
    }
    public Match parseUntil(String src,String delimiter,Options options){
        if (options == null)
            options = new Options();

        boolean includeLineComment = options.isIncludeLineComment();
        int start = options.getStart();
        int index = start;
        State state = this.defaultState();
        while (state.isString() || state.isRegexp() || state.isBlockComment() ||
                (!includeLineComment && state.isLineComment()) || !startsWith(src, delimiter, index)) {
            this.parseChar(src.charAt(index++), state);
        }
        int end = index;
        return new Match(start, end, src.substring(start, end));
    }

//    function parseChar(character, state) {
//      if (character.length !== 1) throw new Error('Character must be a string of length 1');
//      state = state || exports.defaultState();
//      state.src = state.src || '';
//      state.src += character;
//      var wasComment = state.blockComment || state.lineComment;
//      var lastChar = state.history ? state.history[0] : '';
//
//      if (state.regexpStart) {
//        if (character === '/' || character == '*') {
//          state.regexp = false;
//        }
//        state.regexpStart = false;
//      }
//      if (state.lineComment) {
//        if (character === '\n') {
//          state.lineComment = false;
//        }
//      } else if (state.blockComment) {
//        if (state.lastChar === '*' && character === '/') {
//          state.blockComment = false;
//        }
//      } else if (state.singleQuote) {
//        if (character === '\'' && !state.escaped) {
//          state.singleQuote = false;
//        } else if (character === '\\' && !state.escaped) {
//          state.escaped = true;
//        } else {
//          state.escaped = false;
//        }
//      } else if (state.doubleQuote) {
//        if (character === '"' && !state.escaped) {
//          state.doubleQuote = false;
//        } else if (character === '\\' && !state.escaped) {
//          state.escaped = true;
//        } else {
//          state.escaped = false;
//        }
//      } else if (state.regexp) {
//        if (character === '/' && !state.escaped) {
//          state.regexp = false;
//        } else if (character === '\\' && !state.escaped) {
//          state.escaped = true;
//        } else {
//          state.escaped = false;
//        }
//      } else if (lastChar === '/' && character === '/') {
//        state.history = state.history.substr(1);
//        state.lineComment = true;
//      } else if (lastChar === '/' && character === '*') {
//        state.history = state.history.substr(1);
//        state.blockComment = true;
//      } else if (character === '/' && isRegexp(state.history)) {
//        state.regexp = true;
//        state.regexpStart = true;
//      } else if (character === '\'') {
//        state.singleQuote = true;
//      } else if (character === '"') {
//        state.doubleQuote = true;
//      } else if (character === '(') {
//        state.roundDepth++;
//      } else if (character === ')') {
//        state.roundDepth--;
//      } else if (character === '{') {
//        state.curlyDepth++;
//      } else if (character === '}') {
//        state.curlyDepth--;
//      } else if (character === '[') {
//        state.squareDepth++;
//      } else if (character === ']') {
//        state.squareDepth--;
//      }
//      if (!state.blockComment && !state.lineComment && !wasComment) state.history = character + state.history;
//      state.lastChar = character; // store last character for ending block comments
//      return state;
//    }
        public State parseChar(char character,State state){
//            if (character.length !== 1) throw new Error('Character must be a string of length 1');
            if(state == null)
                state = this.defaultState();

            state.setSrc(state.getSrc() + character);
            boolean wasComment = state.isBlockComment() || state.isLineComment();
            Character lastChar = !state.getHistory().isEmpty() ? state.getHistory().charAt(0) : null;

            if (state.isRegexpStart()) {
                if ('/' == character || '*'==character) {
                    state.setRegexp(false);
                }
                state.setRegexpStart(false);
            }
            if (state.isLineComment()) {
                if ('\n' == character) {
                    state.setLineComment(false);
                }
            } else if (state.isBlockComment()) {
                if ('*' == state.getLastChar() && '/'==character) {
                    state.setBlockComment(false);
                }
            } else if (state.isSingleQuote()) {
                if ('\''==character && !state.isEscaped()) {
                    state.setSingleQuote(false);
                } else if ('\\'==character && !state.isEscaped()) {
                    state.setEscaped(true);
                } else {
                    state.setEscaped(false);
                }
            } else if (state.isDoubleQuote()) {
                if ('"'==character && !state.isEscaped()) {
                    state.setDoubleQuote(false);
                } else if ('\\'==character && !state.isEscaped()) {
                    state.setEscaped(true);
                } else {
                    state.setEscaped(false);
                }
            } else if (state.isRegexp()) {
                if ('/'==character && !state.isEscaped()) {
                    state.setRegexp(false);
                } else if ('\\'==character && !state.isEscaped()) {
                    state.setRegexp(true);
                } else {
                    state.setEscaped(false);
                }
            } else if (lastChar!=null && '/' == lastChar && '/'==character) {
                state.setHistory(state.getHistory().substring(1));
                state.setLineComment(true);
            } else if (lastChar!=null && '/'==lastChar && '*'==character) {
                state.setHistory(state.getHistory().substring(1));
                state.setBlockComment(true);
            } else if ('/'==character && !state.getHistory().isEmpty() &&isRegexp(state.getHistory())) {
                state.setRegexp(true);
                state.setRegexpStart(true);
            } else if ('\''==character) {
                state.setSingleQuote(true);
            } else if (character == '"') {
                state.setDoubleQuote(true);
            } else if (character == '(') {
                state.setRoundDepth(state.getRoundDepth()+1);
            } else if (character == ')') {
                state.setRoundDepth(state.getRoundDepth()-1);
            } else if (character == '{') {
                state.setCurlyDepth(state.getCurlyDepth()+1);
            } else if (character == '}') {
                state.setCurlyDepth(state.getCurlyDepth()-1);
            } else if (character == '[') {
                state.setSquareDepth(state.getSquareDepth()+1);
            } else if (character == ']') {
                state.setSquareDepth(state.getSquareDepth()-1);
            }
            if (!state.isBlockComment() && !state.isLineComment() && !wasComment) state.setHistory(character + state.getHistory());
            state.setLastChar(character); // store last character for ending block comments
            return state;

        }
//    exports.defaultState = function () { return new State() };
    public State defaultState(){
        return new State();
    }

    public static class State{
        private boolean lineComment = false;
        private boolean blockComment = false;

        private boolean singleQuote = false;
        private boolean doubleQuote = false;
        private boolean regexp = false;
        private boolean regexpStart = false;

        private boolean escaped = false;

        private int roundDepth = 0;
        private int curlyDepth = 0;
        private int squareDepth = 0;

        private String history = "";
        private Character lastChar = null;
        private String src = "";
        public boolean isString(){
            return this.singleQuote || this.doubleQuote;
        }
        public boolean isComment(){
            return this.lineComment || this.blockComment;
        }
        public boolean isNesting(){
            return this.isString() || this.isComment() || this.regexp || this.roundDepth > 0 || this.curlyDepth > 0 || this.squareDepth > 0;
        }

        public String getSrc() {
            return src;
        }

        public boolean isLineComment() {
            return lineComment;
        }

        public void setLineComment(boolean lineComment) {
            this.lineComment = lineComment;
        }

        public boolean isBlockComment() {
            return blockComment;
        }

        public void setBlockComment(boolean blockComment) {
            this.blockComment = blockComment;
        }

        public boolean isSingleQuote() {
            return singleQuote;
        }

        public void setSingleQuote(boolean singleQuote) {
            this.singleQuote = singleQuote;
        }

        public boolean isDoubleQuote() {
            return doubleQuote;
        }

        public void setDoubleQuote(boolean doubleQuote) {
            this.doubleQuote = doubleQuote;
        }

        public boolean isRegexp() {
            return regexp;
        }

        public void setRegexp(boolean regexp) {
            this.regexp = regexp;
        }

        public boolean isRegexpStart() {
            return regexpStart;
        }

        public void setRegexpStart(boolean regexpStart) {
            this.regexpStart = regexpStart;
        }

        public boolean isEscaped() {
            return escaped;
        }

        public void setEscaped(boolean escaped) {
            this.escaped = escaped;
        }

        public int getRoundDepth() {
            return roundDepth;
        }

        public void setRoundDepth(int roundDepth) {
            this.roundDepth = roundDepth;
        }

        public int getCurlyDepth() {
            return curlyDepth;
        }

        public void setCurlyDepth(int curlyDepth) {
            this.curlyDepth = curlyDepth;
        }

        public int getSquareDepth() {
            return squareDepth;
        }

        public void setSquareDepth(int squareDepth) {
            this.squareDepth = squareDepth;
        }

        public String getHistory() {
            return history;
        }

        public void setHistory(String history) {
            this.history = history;
        }

        public Character getLastChar() {
            return lastChar;
        }

        public void setLastChar(Character lastChar) {
            this.lastChar = lastChar;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }
    private boolean startsWith(String str, String start, int i){
        return start.equals(str.substring(i,i+start.length()));
    }
//    exports.isPunctuator = isPunctuator
//    function isPunctuator(c) {
//      if (!c) return true; // the start of a string is a punctuator
//      var code = c.charCodeAt(0)
//
//      switch (code) {
//        case 46:   // . dot
//        case 40:   // ( open bracket
//        case 41:   // ) close bracket
//        case 59:   // ; semicolon
//        case 44:   // , comma
//        case 123:  // { open curly brace
//        case 125:  // } close curly brace
//        case 91:   // [
//        case 93:   // ]
//        case 58:   // :
//        case 63:   // ?
//        case 126:  // ~
//        case 37:   // %
//        case 38:   // &
//        case 42:   // *:
//        case 43:   // +
//        case 45:   // -
//        case 47:   // /
//        case 60:   // <
//        case 62:   // >
//        case 94:   // ^
//        case 124:  // |
//        case 33:   // !
//        case 61:   // =
//          return true;
//        default:
//          return false;
//      }
//    }

    public boolean isPunctuator(Character character){
        Integer code = Character.codePointAt(character.toString(),0);
          switch (code) {
            case 46:   // . dot
            case 40:   // ( open bracket
            case 41:   // ) close bracket
            case 59:   // ; semicolon
            case 44:   // , comma
            case 123:  // { open curly brace
            case 125:  // } close curly brace
            case 91:   // [
            case 93:   // ]
            case 58:   // :
            case 63:   // ?
            case 126:  // ~
            case 37:   // %
            case 38:   // &
            case 42:   // *:
            case 43:   // +
            case 45:   // -
            case 47:   // /
            case 60:   // <
            case 62:   // >
            case 94:   // ^
            case 124:  // |
            case 33:   // !
            case 61:   // =
              return true;
            default:
              return false;
          }
    }
    public boolean isKeyword(String id) {
      return ("if".equals(id)) || ("in".equals(id)) || ("do".equals(id)) || ("var".equals(id)) || ("for".equals(id)) || ("new".equals(id)) ||
             ("try".equals(id)) || ("let".equals(id)) || ("this".equals(id)) || ("else".equals(id)) || ("case".equals(id)) ||
             ("void".equals(id)) || ("with".equals(id)) || ("enum".equals(id)) || ("while".equals(id)) || ("break".equals(id)) || ("catch".equals(id)) ||
             ("throw".equals(id)) || ("const".equals(id)) || ("yield".equals(id)) || ("class".equals(id)) || ("super".equals(id)) ||
             ("return".equals(id)) || ("typeof".equals(id)) || ("delete".equals(id)) || ("switch".equals(id)) || ("export".equals(id)) ||
             ("import".equals(id)) || ("default".equals(id)) || ("finally".equals(id)) || ("extends".equals(id)) || ("function".equals(id)) ||
             ("continue".equals(id)) || ("debugger".equals(id)) || ("package".equals(id)) || ("private".equals(id)) || ("interface".equals(id)) ||
             ("instanceof".equals(id)) || ("implements".equals(id)) || ("protected".equals(id)) || ("public".equals(id)) || ("static".equals(id));
    }

//    function isRegexp(history) {
//      //could be start of regexp or divide sign
//
//      history = history.replace(/^\s*/, '');
//
//      //unless its an `if`, `while`, `for` or `with` it's a divide, so we assume it's a divide
//      if (history[0] === ')') return false;
//      //unless it's a function expression, it's a regexp, so we assume it's a regexp
//      if (history[0] === '}') return true;
//      //any punctuation means it's a regexp
//      if (isPunctuator(history[0])) return true;
//      //if the last thing was a keyword then it must be a regexp (e.g. `typeof /foo/`)
//      if (/^\w+\b/.test(history) && isKeyword(/^\w+\b/.exec(history)[0].split('').reverse().join(''))) return true;
//
//      return false;
//    }
    public boolean isRegexp(String history){
      //could be start of regexp or divide sign

      history = history.replace("^\\s*", "");

      //unless its an `if`, `while`, `for` or `with` it's a divide, so we assume it's a divide
      if (history.charAt(0) == ')') return false;
      //unless it's a function expression, it's a regexp, so we assume it's a regexp
      if (history.charAt(0) == '}') return true;
      //any punctuation means it's a regexp
      if (isPunctuator(history.charAt(0))) return true;
      //if the last thing was a keyword then it must be a regexp (e.g. `typeof /foo/`)

        Matcher matcher = pattern.matcher(history);
        if (matcher.matches() && isKeyword(new StringBuilder(matcher.group(0)).reverse().toString())){
            return true;
        }
        return false;
    }

    public class Match {
        private int start;
        private int end;
        private String src;

        public Match(int start, int end, String src) {
            this.start = start;
            this.end = end;
            this.src = src;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getSrc() {
            return src;
        }
    }
}
