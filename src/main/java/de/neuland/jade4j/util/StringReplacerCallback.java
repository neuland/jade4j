package de.neuland.jade4j.util;

import java.util.regex.Matcher;

public interface StringReplacerCallback {
	public String replace(Matcher match);
}