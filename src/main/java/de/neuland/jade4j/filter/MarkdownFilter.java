package de.neuland.jade4j.filter;

import java.util.Map;

import org.pegdown.PegDownProcessor;

public class MarkdownFilter extends CachingFilter {

	private PegDownProcessor pegdown = new PegDownProcessor();

	@Override
	protected String convert(String source, Map<String, Object> attributes) {
		return pegdown.markdownToHtml(source);
	}

}
