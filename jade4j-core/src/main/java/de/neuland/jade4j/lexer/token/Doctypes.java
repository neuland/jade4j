package de.neuland.jade4j.lexer.token;

import java.util.HashMap;
import java.util.Map;

public class Doctypes {
    private static Map<String, String> doctypes = new HashMap<String, String>();
    
    static {
        doctypes.put("default", "<!DOCTYPE html>");
        doctypes.put("xml", "<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        doctypes.put("transitional", "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        doctypes.put("strict", "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        doctypes.put("frameset", "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
        doctypes.put("1.1", "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        doctypes.put("basic", "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.1//EN\" \"http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd\">");
        doctypes.put("mobile", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
    }

    public static String get(String jadeDoctype) {
        return doctypes.get(jadeDoctype);
    }
}