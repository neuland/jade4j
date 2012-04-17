package de.neuland.jade4j.exceptions;

import ognl.OgnlException;

public class ExpressionException extends Exception {

	private static final long serialVersionUID = 1201110801125266239L;

	public ExpressionException(OgnlException e) {
		super(e.getMessage(), e);
	}

	public ExpressionException(String message) {
		super(message);
	}

}
