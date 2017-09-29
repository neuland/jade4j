package org.apache.commons.jexl3.internal;


import org.apache.commons.jexl3.JadeJexlArithmetic;
import org.apache.commons.jexl3.JadeJexlInterpreter;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.Interpreter;
import org.apache.commons.jexl3.internal.Scope;
import org.apache.commons.jexl3.internal.introspection.JadeIntrospect;

public class JadeJexlEngine extends Engine {

	/*
	 * using a semi strict interpreter and non strict arithmetic
	 */
	public JadeJexlEngine() {
		super(new JexlBuilder().arithmetic(new JadeJexlArithmetic(true)).uberspect(new JadeIntrospect(null,null)).strict(false));
	}

	@Override
	protected Interpreter createInterpreter(JexlContext context, Scope.Frame frame) {
		return new JadeJexlInterpreter(this, context == null ? EMPTY_CONTEXT : context, frame);
	}

}
