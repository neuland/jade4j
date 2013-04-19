package org.apache.commons.jexl2;


public class JadeJexlEngine extends JexlEngine {
    private static JexlEngine jexl = new JadeJexlEngine();
    private static final int MAX_ENTRIES = 5000;

    static {
        jexl.setCache(MAX_ENTRIES);
    }
    public static JexlEngine getInstance() {
        return jexl;
    }

    public static void setCache(boolean cache) {
        jexl.setCache(cache ? MAX_ENTRIES : 0);
    }

	/*
	 * using a semi strict interpreter and non strict arithmetic
	 */
	private JadeJexlEngine() {
		super(new JadeIntrospect(null), new JadeJexlArithmetic(true), null, null);
		setStrict(false);
	}

	@Override
	protected Interpreter createInterpreter(JexlContext context, boolean strictFlag, boolean silentFlag) {
		// always use strict
		strictFlag = true;
		return new JadeJexlInterpreter(this, context == null ? EMPTY_CONTEXT : context, strictFlag, silentFlag);
	}
}
