package de.neuland.pug4j.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by christoph on 22.10.15.
 */
public class PugEscape {

    public static HashMap<String,String> pug_encode_html_rules = new HashMap<String, String>();

    static {
        pug_encode_html_rules.put("&","&amp;");
        pug_encode_html_rules.put("<", "&lt;");
        pug_encode_html_rules.put(">", "&gt;");
        pug_encode_html_rules.put("\"", "&quot;");
    }
    static Pattern pug_match_html = Pattern.compile("[&<>\"]");

    private static String pug_encode_char(String c) {
        String s = pug_encode_html_rules.get(c);
        if(s == null)
            return c;
        return s;
    }

    public static String escape(String html){
        String result = StringReplacer.replace(html, pug_match_html, new StringReplacerCallback() {
            @Override
            public String replace(Matcher m) {
                return pug_encode_char(m.group(0));
            }
        });

      return result;
    }

}
