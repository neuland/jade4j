package de.neuland.jade4j.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.expression.JexlExpressionHandler;
import de.neuland.jade4j.util.CharacterParser;
import org.apache.commons.lang3.StringEscapeUtils;

import de.neuland.jade4j.exceptions.ExpressionException;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.node.ExpressionString;

public class Utils {
	public static Pattern interpolationPattern = Pattern.compile("(\\\\)?([#!])\\{");
	public static CharacterParser characterParser = new CharacterParser();
	public static List<Object> prepareInterpolate(String string, boolean xmlEscape) {
		List<Object> result = new LinkedList<Object>();

		Matcher matcher = interpolationPattern.matcher(string);
		int start = 0;
		while (matcher.find()) {
			String before = string.substring(start, matcher.start(0));
			if (xmlEscape) {
				before = escapeHTML(before);
			}
			result.add(before);

			boolean escape = matcher.group(1) != null;
			String flag = matcher.group(2);

			int openBrackets = 1;
			boolean closingBracketFound = false;
			int closingBracketIndex = matcher.end();
			while (!closingBracketFound && closingBracketIndex < string.length()) {
				char currentChar = string.charAt(closingBracketIndex);
				if (currentChar == '{') {
					openBrackets ++;
				}
				else if (currentChar == '}') {
					openBrackets --;
					if (openBrackets == 0) {
						closingBracketFound = true;
					}
				}
				closingBracketIndex++;
			}
			String code = string.substring(matcher.end(), closingBracketIndex -1);

			if (escape) {
				String escapedExpression = string.substring(matcher.start(0), closingBracketIndex).substring(1);
				if (xmlEscape) {
					escapedExpression = escapeHTML(escapedExpression);
				}
				result.add(escapedExpression);
			} else {
				ExpressionString expression = new ExpressionString(code);
				if (flag.equals("#")) {
					expression.setEscape(true);
				}
				result.add(expression);
			}
			start = closingBracketIndex;
		}
		String last = string.substring(start);
		if (xmlEscape) {
			last = escapeHTML(last);
		}
		result.add(last);

		return result;
	}
//	public static String interpolate(String str,boolean interpolate){
//		StringBuilder sb = new StringBuilder();
//		if (interpolate) {
//			Matcher matcher = Pattern.compile("(\\\\)?([#!])\\{((?:.|\\n)*)$").matcher(str);
//			if(matcher.find(0))
//      			sb.append(str.substring(0, matcher.start()));
//    		if (matcher.group(1)!=null) { // escape
//        		sb.append(matcher.group(2) + "{");
//        		sb.append(matcher.group(3));
//				return sb.toString();
//      		} else {
//				String rest = matcher.group(3);
//				CharacterParser.Match range = characterParser.parseMax(rest);
//				String code = (matcher.group(2).equals("!") ? "" : 'jade.escape') + "((jade_interp = " + range.src + ") == null ? '' : jade_interp)";
//				this.bufferExpression(code);
//				sb.append(rest.substring(range.getEnd() + 1));
//				return sb.toString();
//		  	}
//    	}
//
//	  str = utils.stringify(str);
//  		str = str.substr(1, str.length - 2);
//
//	}
	public static String interpolate(List<Object> prepared, JadeModel model, ExpressionHandler expressionHandler) throws ExpressionException {

		StringBuffer result = new StringBuffer();

		for (Object entry : prepared) {
			if (entry instanceof String) {
				result.append(entry);
			} else if (entry instanceof ExpressionString) {
				ExpressionString expression = (ExpressionString) entry;
				String stringValue = "";
				String value = expressionHandler.evaluateStringExpression(expression.getValue(), model);
				if (value != null) {
					stringValue = value;
				}
				if (expression.isEscape()) {
					stringValue = escapeHTML(stringValue);
				}
				result.append(stringValue);
			}
		}

		return result.toString();
	}

	private static String escapeHTML(String string) {
		return StringEscapeUtils.escapeHtml4(string);
	}

	public static String interpolate(String string, JadeModel model, boolean escape, ExpressionHandler expressionHandler) throws ExpressionException {
		return interpolate(prepareInterpolate(string, escape), model,expressionHandler);
	}
}
