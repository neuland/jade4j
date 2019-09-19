package de.neuland.pug4j.filter;

import de.neuland.pug4j.parser.node.Attr;

import java.util.List;
import java.util.Map;

public class CDATAFilter implements Filter {

	@Override
	public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
		return "<![CDATA[" + source + "]]>";
	}

}
