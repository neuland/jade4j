package de.neuland.jade4j.exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import de.neuland.jade4j.template.TemplateLoader;

public abstract class JadeException extends RuntimeException {

	private static final long serialVersionUID = -8189536050437574552L;
	private String filename;
	private int lineNumber;
	private TemplateLoader templateLoader;

	public JadeException(String message, String filename, int lineNumber, TemplateLoader templateLoader, Throwable e) {
		super(message, e);
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.templateLoader = templateLoader;
	}

	public JadeException(String message) {
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
		return getClass() + " " + getFilename() + ":" + getLineNumber() + "\n" + getMessage();
	}

	public String toHtmlString() {
		// will be made pretty soon :)
		StringWriter stringWriter = new StringWriter();
		stringWriter.write(toString());
		PrintWriter writer = new PrintWriter(stringWriter);
		printStackTrace(writer);
		writer.flush();
		return "<pre>" + stringWriter.toString() + "</pre>";
	}
}
