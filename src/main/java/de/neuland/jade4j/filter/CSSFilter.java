package de.neuland.jade4j.filter;

import java.util.Map;

public class CssFilter extends CachingFilter {

    @Override
    protected String convert(String source, Map<String, Object> attributes) {
        return "<style type=\"text/css\">" + source + "</style>";
    }

}
