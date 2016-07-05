package de.neuland.jade4j.util;

import de.neuland.jade4j.expression.ExpressionHandler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by christoph on 22.10.15.
 */
public class JadeEscape {

    public static HashMap<String,String> jade_encode_html_rules = new HashMap<String, String>();

    static {
        jade_encode_html_rules.put("&","&amp;");
        jade_encode_html_rules.put("<", "&lt;");
        jade_encode_html_rules.put(">", "&gt;");
        jade_encode_html_rules.put("\"", "&quot;");
    }
    static Pattern jade_match_html = Pattern.compile("[&<>\"]");

    private static String jade_encode_char(String c) {
        String s = jade_encode_html_rules.get(c);
        if(s == null)
            return c;
        return s;
    }

    public static String escape(String html){
        String result = StringReplacer.replace(html, jade_match_html, new StringReplacerCallback() {
            @Override
            public String replace(Matcher m) {
                return jade_encode_char(m.group(0));
            }
        });

      return result;
    }

}
