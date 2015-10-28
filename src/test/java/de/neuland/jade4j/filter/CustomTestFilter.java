package de.neuland.jade4j.filter;

import de.neuland.jade4j.parser.node.ValueString;

import java.util.Map;

public class CustomTestFilter implements Filter {
    @Override
    public String convert(String source, Map<String, Object> attributes, Map<String, Object> model) {
        Object foo = attributes.get("foo");
        String test = null;
        if(foo instanceof ValueString)
            test = ((ValueString) foo).getValue();
        if("foo bar".equals(source) && "bar".equals(test)){
            return "bar baz";
        }
        return source;
    }
}
