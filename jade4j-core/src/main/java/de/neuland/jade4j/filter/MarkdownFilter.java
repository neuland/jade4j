package de.neuland.jade4j.filter;

import java.util.List;

import de.neuland.jade4j.parser.node.Attr;
import org.pegdown.PegDownProcessor;

public class MarkdownFilter extends CachingFilter {

	private PegDownProcessor pegdown = new PegDownProcessor();

	@Override
	protected String convert(String source, List<Attr> attributes) {
		return pegdown.markdownToHtml(source);
	}

}
