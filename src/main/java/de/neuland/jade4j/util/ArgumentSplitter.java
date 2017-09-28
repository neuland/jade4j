package de.neuland.jade4j.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Split arguments passed as single String into list of strings, preserve quotes when argument is not simple string constant.
 * For example:
 * foo('a'),'b' -&gt; [ "foo('a')", "b" ]
 *
 * @author dusan.zatkovsky, 2/5/15
 */
public class ArgumentSplitter {

    private static final char argumentDelimiter = ',';
    private final String arguments;
    private List<String> argList = new ArrayList<String>();

    /**
     * Split arguments passed as single String into list
     * @param arguments
     * @return  Parsed arguments
     */
    public static List<String> split(String arguments) {
        return new ArgumentSplitter(arguments).splitArguments();
    }

    private ArgumentSplitter(String arguments) {
        this.arguments = arguments;
    }

    private List<String> splitArguments() {

        final int argLength = arguments.length();
        StringBuilder sb = new StringBuilder(argLength);
        boolean insideQuotas = false;
        int bracesBlock = 0;

        for (int i = 0; i < argLength; i++) {
            char ch = arguments.charAt(i);

            // detect when pointer is inside quoted text
            if (ch == '"' || ch == '\'') {
                insideQuotas = !insideQuotas;
            }

            else if (ch == '(') {
                bracesBlock++;
            }

            else if (ch == ')') {
                bracesBlock--;
            }
            else if (ch == '[') {
                bracesBlock++;
            }

            else if (ch == ']') {
                bracesBlock--;
            }

            // detect argument delimiter, then push argument
            else if (ch == argumentDelimiter && !insideQuotas && bracesBlock == 0) {
                pushArg(sb);
                sb = new StringBuilder(argLength);
            }
            sb.append(ch);
        }
        pushArg(sb);
        return argList;
    }

    private void pushArg(StringBuilder sb) {
        argList.add(sb.toString().trim().replaceAll("^,", "").trim());
        sb = new StringBuilder(arguments.length());
    }

}
