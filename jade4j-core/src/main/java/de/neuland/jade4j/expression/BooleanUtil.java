package de.neuland.jade4j.expression;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BooleanUtil {

	public static Boolean convert(Object in) {
		if (in == null) {
			return Boolean.FALSE;
		} else if (in instanceof List) {
			return ((List<?>) in).size() != 0;
		} else if (in instanceof Boolean) {
			return (Boolean) in;
		} else if (in instanceof int[]) {
			return ((int[]) in).length != 0;
		} else if (in instanceof double[]) {
			return ((double[]) in).length != 0;
		} else if (in instanceof float[]) {
			return ((float[]) in).length != 0;
		} else if (in instanceof Object[]) {
			return ((Object[]) in).length != 0;
		} else if (in instanceof Number) {
			return ((Number) in).doubleValue() != 0;
		} else if (in instanceof String) {
			return !StringUtils.isEmpty((String) in);
		} else {
			return true;
		}

	}

}