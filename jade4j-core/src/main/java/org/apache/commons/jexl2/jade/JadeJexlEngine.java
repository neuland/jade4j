package org.apache.commons.jexl2.jade;


import org.apache.commons.jexl2.Interpreter;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;

public class JadeJexlEngine extends JexlEngine {

	/*
	 * using a semi strict interpreter and non strict arithmetic
	 */
	public JadeJexlEngine() {
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
