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
    private StringBuilder sb;

    public static List<String> split(String arguments) {
        return new ArgumentSplitter(arguments).splitArguments();
    }

    private ArgumentSplitter(String arguments) {
        this.arguments = arguments;
    }

    private List<String> splitArguments() {

        final int argLength = arguments.length();
        sb = new StringBuilder(argLength);
        boolean insideQuotas = false;

        for (int i = 0; i < argLength; i++) {
            char ch = arguments.charAt(i);

            // detect when pointer is inside quoted text
            if (ch == '"' || ch == '\'') {
                insideQuotas = !insideQuotas;
            }

            // detect argument delimiter
            else if (ch == argumentDelimiter && !insideQuotas) {
                pushArg();
            }
            sb.append(ch);
        }
        pushArg();
        return argList;
    }

    private void pushArg() {
        String tmp = sb.toString().trim().replaceAll("^,", "").trim();
        for (String s : new String[]{"\"", "'"}) {
            if ( tmp.startsWith(s) && tmp.endsWith(s)) {
                tmp = tmp.substring(1, tmp.length()-1);
                break;
            }
        }
        argList.add(tmp);
        sb = new StringBuilder(arguments.length());
    }

}
