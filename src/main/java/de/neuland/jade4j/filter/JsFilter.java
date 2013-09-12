package de.neuland.jade4j.filter;

import java.util.Map;

public class JsFilter extends CachingFilter {

    @Override
    protected String convert(String source, Map<String, Object> attributes) {
        return "<script type=\"text/javascript\">" + source + "</script>";
    }

}
