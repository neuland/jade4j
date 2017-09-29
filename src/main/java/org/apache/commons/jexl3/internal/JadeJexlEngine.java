package org.apache.commons.jexl3.internal;


import org.apache.commons.jexl3.JadeJexlArithmetic;
import org.apache.commons.jexl3.JadeJexlInterpreter;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.internal.introspection.Uberspect;
import org.apache.commons.jexl3.introspection.JexlUberspect;

public class JadeJexlEngine extends Engine {

	/*
	 * using a semi strict interpreter and non strict arithmetic
	 */
	public JadeJexlEngine(int cacheSize) {
		super(new JexlBuilder().arithmetic(new JadeJexlArithmetic(true)).uberspect(new Uberspect(null,
				JexlUberspect.MAP_STRATEGY)).strict(false).silent(false).cache(cacheSize));
	}

	@Override
	protected Interpreter createInterpreter(JexlContext context, Scope.Frame frame) {
		return new JadeJexlInterpreter(this, context == null ? EMPTY_CONTEXT : context, frame);
	}

}
