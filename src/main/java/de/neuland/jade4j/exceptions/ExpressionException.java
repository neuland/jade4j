package de.neuland.jade4j.exceptions;

public class ExpressionException extends Exception {

	private static final long serialVersionUID = 1201110801125266239L;

	public ExpressionException(String expression, Throwable e) {
		super("unable to evaluate [" + expression.trim() + "] - " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
	}

	public ExpressionException(String message) {
		super(message);
	}

}
