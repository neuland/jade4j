package de.neuland.jade4j.filter;

import de.neuland.jade4j.parser.node.Attr;

import java.util.List;

public class CssFilter extends CachingFilter {

    @Override
    protected String convert(String source, List<Attr> attributes) {
        return "<style type=\"text/css\">" + source + "</style>";
    }

}
