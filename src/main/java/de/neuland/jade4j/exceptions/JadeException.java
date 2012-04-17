package de.neuland.jade4j.exceptions;

public abstract class JadeException extends RuntimeException {

	private static final long serialVersionUID = -8189536050437574552L;
	private String filename;
	private int lineNumber;

	public JadeException(String message, Throwable e) {
		super(message, e);
	}

	public JadeException(String message) {
		super(message);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String toString() {
		return getClass() + " " + getFilename() + ":" + getLineNumber() + "\n" + getMessage();
	}
}
