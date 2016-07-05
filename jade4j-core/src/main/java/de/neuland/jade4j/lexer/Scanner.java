package de.neuland.jade4j.lexer;

import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Scanner {

    private String input;

    public Scanner(Reader reader) {
    	initFromReader(reader);
    }
    public Scanner(String input) {
    	this.input = input;
    }

    public void consume(int length) {
        input = input.substring(length);
    }

    public String findInLine(String re) {
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find(0)) {
            int end = matcher.end();
            return input.substring(0, end);
        }
        return null;
    }

    private void initFromReader(Reader reader) {
        try {
            StringBuilder sb = new StringBuilder();
            LineNumberReader in = new LineNumberReader(reader);
            String s = "";
            boolean first = true;
//            int c = 0;
//            while ((c = in.read()) != -1) {
//                if(c != '\uFEFF' && !first) {
//                    sb.append((char) c);
//                }
//                first = false;
//            }
            while ((s = in.readLine()) != null) {
                if(!first) {
                    sb.append("\n");
                }else{
                    s = s.replaceAll("^\\uFEFF", "");
                }
                first=false;
                if (StringUtils.isNotEmpty(s)) {
                    sb.append(s);
                }
            }
            input = sb.toString();
            if (StringUtils.isNotBlank(input)) {
                input = sb.toString().replaceAll("\\r\\n|\\r", "\\n");
            }
            in.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public char charAt(int i) {
        return input.charAt(i);
    }

    public boolean beginnsWithWhitespace() {
        return (input.charAt(0) == ' ' || input.charAt(0) == '\t');
    }

    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(input);
    }

    private boolean isEmpty() {
        return !isNotEmpty();
    }

    public boolean isNotLineBreak() {
        return isEmpty() || input.charAt(0) != '\n';
    }

    public String getPipelessText() {
        int i = input.indexOf('\n');
        if (-1 == i)
            i = input.length();
        String str = input.substring(0, i);
        consume(str.length());
        return str.trim();
    }

    public String getInput() {
        return input;
    }

    public Matcher getMatcherForPattern(String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        return pattern.matcher(input);
    }

    public boolean isIntendantionViolated() {
        return input != null && input.length() > 0
                && (' ' == input.charAt(0) || '\t' == input.charAt(0));
    }

    public boolean isBlankLine() {
        return input != null && input.length() > 0 && '\n' == input.charAt(0);
    }

    public boolean isAdditionalBlankline() {
        return input.length() > 2 && input.charAt(0) == '\n' && input.charAt(1) == '\n';
    }

}
