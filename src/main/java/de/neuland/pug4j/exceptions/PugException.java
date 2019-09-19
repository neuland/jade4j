package de.neuland.pug4j.exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.neuland.pug4j.Pug4J;
import de.neuland.pug4j.template.TemplateLoader;

public abstract class PugException extends RuntimeException {

	private static final long serialVersionUID = -8189536050437574552L;
	private String filename;
	private int lineNumber;
	private TemplateLoader templateLoader;

	public PugException(String message, String filename, int lineNumber, TemplateLoader templateLoader, Throwable e) {
		super(message, e);
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.templateLoader = templateLoader;
	}

	public PugException(String message) {
		super(message);
	}

	public String getFilename() {
		return filename;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public List<String> getTemplateLines() {
		try {
			List<String> result = new ArrayList<String>();
			Reader reader = templateLoader.getReader(filename);
			BufferedReader in = new BufferedReader(reader);
			String line;
			while ((line = in.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return getClass() + ": " + getMessage();
	}

	/**
	 * Returns the detail message string of this throwable.
	 *
	 * @return the detail message string of this {@code Throwable} instance
	 * (which may be {@code null}).
	 */
	@Override
	public String getMessage() {
		return super.getMessage() + " in " + getFilename() + ":" + getLineNumber();
	}

	public String toHtmlString() {
		return toHtmlString(null);
	}

	public String toHtmlString(String generatedHtml) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("filename", filename);
		model.put("linenumber", lineNumber);
		model.put("message", getMessage());
		model.put("lines", getTemplateLines());
		model.put("exception", getName());
		if (generatedHtml != null) {
			model.put("html", generatedHtml);
		}

		try {
			URL url = PugException.class.getResource("/error.jade");
			return Pug4J.render(url, model, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getName() {
		return this.getClass().getSimpleName().replaceAll("([A-Z])", " $1").trim();
	}
}
