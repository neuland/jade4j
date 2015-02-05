package de.neuland.jade4j.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dusan.zatkovsky, 2/5/15
 */
public class ArgumentSplitter {

    private static final char argumentDelimiter = ',';
    private final String arguments;
    private List<String> argList = new ArrayList<String>();
    private StringBuffer sb;

    public static List<String> split(String arguments) {
        return new ArgumentSplitter(arguments).splitArguments();
    }

    public ArgumentSplitter(String arguments) {
        this.arguments = arguments;
    }

    private List<String> splitArguments() {

        final int argLength = arguments.length();
        sb = new StringBuffer(argLength);
        boolean insideQuotas = false;

        for (int i = 0; i < argLength; i++) {
            char ch = arguments.charAt(i);

            // detect when pointer is inside quoted text
            if (ch == '"' || ch == '\'') {
                insideQuotas = !insideQuotas;
            }

            // detect argument delimiter
            if (ch == argumentDelimiter && !insideQuotas) {
                pushArg();
                i++;
            }
            sb.append(ch);
        }
        pushArg();
        return argList;
    }

    private void pushArg() {
        argList.add(sb.toString().trim().replaceAll("^,", ""));
        sb = new StringBuffer(arguments.length());
    }

}
