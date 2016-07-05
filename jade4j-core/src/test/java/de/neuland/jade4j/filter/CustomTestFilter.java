package de.neuland.jade4j.filter;

import de.neuland.jade4j.parser.node.Attr;
import de.neuland.jade4j.parser.node.ValueString;

import java.util.List;
import java.util.Map;

public class CustomTestFilter implements Filter {
    @Override
    public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
        for (Attr attribute : attributes) {
            if("foo".equals(attribute.getName())){
                Object foo = attribute.getValue();
                String test = null;
                if(foo instanceof String)
                    test = (String) foo;
                if("foo bar".equals(source) && "bar".equals(test)){
                    return "bar baz";
                }
            }
        }
        return source;
    }
}
