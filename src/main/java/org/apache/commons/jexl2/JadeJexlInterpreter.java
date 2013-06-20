package org.apache.commons.jexl2;

import org.apache.commons.jexl2.Interpreter;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;

public class JadeJexlInterpreter extends Interpreter {

	public JadeJexlInterpreter(JexlEngine jexl, JexlContext aContext, boolean strictFlag, boolean silentFlag) {
		super(jexl, aContext, strictFlag, silentFlag);
	}

	/**
	 * Triggered when variable can not be resolved.
	 * 
	 * @param xjexl
	 *            the JexlException ("undefined variable " + variable)
	 * @return throws JexlException if strict, null otherwise
	 */
	protected Object unknownVariable(JexlException xjexl) {
		// don't throw the exception
		if (!silent) {
			logger.trace(xjexl.getMessage());
		}
		return null;
	}
}
