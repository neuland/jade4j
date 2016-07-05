package de.neuland.jade4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplacer {
	public static String replace(String input, Pattern regex, StringReplacerCallback callback) {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(input);
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(resultString, callback.replace(regexMatcher));
		}
		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}
}
