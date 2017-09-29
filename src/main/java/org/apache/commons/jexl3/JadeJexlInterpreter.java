package org.apache.commons.jexl3;

import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.Interpreter;
import org.apache.commons.jexl3.internal.Scope;

public class JadeJexlInterpreter extends Interpreter {
	public JadeJexlInterpreter(Engine engine, JexlContext aContext, Scope.Frame eFrame) {
		super(engine, aContext, eFrame);
	}

	/**
	 * Triggered when variable can not be resolved.
	 * 
	 * @param xjexl
	 *            the JexlException ("undefined variable " + variable)
	 * @return throws JexlException if strict, null otherwise
	 */
//	protected Object unknownVariable(JexlException xjexl) {
//		// don't throw the exception
//		if (!silent) {
//			logger.trace(xjexl.getMessage());
//		}
//		return null;
//	}
}
