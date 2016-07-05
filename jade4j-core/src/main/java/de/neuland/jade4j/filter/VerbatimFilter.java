package de.neuland.jade4j.filter;

import de.neuland.jade4j.parser.node.Attr;

import java.util.List;
import java.util.Map;

public class VerbatimFilter implements Filter {
    @Override
    public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
        return "\n" + source + "\n";
    }
}
