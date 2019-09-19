package de.neuland.pug4j.filter;


import de.neuland.pug4j.parser.node.Attr;

import java.util.List;

public class JsFilter extends CachingFilter {

    @Override
    protected String convert(String source, List<Attr> attributes) {
        return "<script type=\"text/javascript\">" + source + "</script>";
    }

}
