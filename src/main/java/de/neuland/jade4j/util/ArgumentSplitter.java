
package de.neuland.jade4j.util;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

/**
 * Split arguments passed as single String into list of strings, preserve quotes when argument is not simple string constant.
 * For example:
 * foo('a'),'b' -&gt; [ "foo('a')", "b" ]
 *
 * @author dusan.zatkovsky, 2/5/15
 */
public class ArgumentSplitter {
    private static final char ARGUMENT_DELIMITER = ',';

    private final String arguments;
    private List<String> argList = new ArrayList<String>();

    /**
     * Split arguments passed as single String into list
     * @param arguments
     * @return Parsed arguments
     */
    public static List<String> split(String arguments) {
        if(arguments == null) {
            return Collections.emptyList();
        }
        return new ArgumentSplitter(arguments).splitArguments();
    }

    private ArgumentSplitter(String arguments) {
        this.arguments = arguments;
    }

    private List<String> splitArguments() {

        final int argLength = arguments.length();
        StringBuilder builder = new StringBuilder(argLength);
        boolean insideQuotas = false;
        int bracesBlock = 0;

        for (int i = 0; i < argLength; i++) {
            char ch = arguments.charAt(i);

            if (isQuoted(ch)) {
                insideQuotas = !insideQuotas;
            }

            else if (isOpeningBraces(ch)) {
                bracesBlock++;
            }

            else if (isClosingBraces(ch)) {
                bracesBlock--;
            }

            // detect argument delimiter, then push argument
            else if (ch == ARGUMENT_DELIMITER && !insideQuotas && bracesBlock == 0) {
                addArgument(builder.toString());
                builder = new StringBuilder(argLength - i);
            }
            builder.append(ch);
        }
        addArgument(builder.toString());
        return argList;
    }

    private boolean isClosingBraces(char ch) {
        return ch == ')' || ch == ']' || ch == '}';
    }

    private boolean isOpeningBraces(char ch) {
        return ch == '(' || ch == '[' || ch == '{';
    }

    private boolean isQuoted(char ch) {
        return ch == '"' || ch == '\'';
    }

    private void addArgument(String argument) {
        argList.add(argument.trim().replaceAll("^,", "").trim());
    }

}
