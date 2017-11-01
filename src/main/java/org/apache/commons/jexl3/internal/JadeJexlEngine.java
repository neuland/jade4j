package org.apache.commons.jexl3.internal;


import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.internal.introspection.JadeUeberspect;
import org.apache.commons.jexl3.internal.introspection.Uberspect;
import org.apache.commons.jexl3.introspection.JexlUberspect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JadeJexlEngine extends Engine {

	/*
	 * using a semi strict interpreter and non strict arithmetic
	 */
	public JadeJexlEngine(int cacheSize) {
		super(new JexlBuilder().arithmetic(new JadeJexlArithmetic(false)).uberspect(new Uberspect(null,
				new JexlUberspect.ResolverStrategy() {
                    public List<JexlUberspect.PropertyResolver> apply(JexlOperator op, Object obj) {
                        if(obj instanceof Map){
                            return JexlUberspect.MAP;
                        }
                        if (op == JexlOperator.ARRAY_GET) {
                            return JexlUberspect.MAP;
                        } else if (op == JexlOperator.ARRAY_SET) {
                            return JexlUberspect.MAP;
                        } else {
                            return op == null && obj instanceof Map ? JexlUberspect.MAP : JexlUberspect.POJO;
                        }
                    }
                })).strict(false).silent(false).cache(cacheSize));
	}

	@Override
	protected Interpreter createInterpreter(JexlContext context, Scope.Frame frame) {
		return new JadeJexlInterpreter(this, context == null ? EMPTY_CONTEXT : context, frame);
	}

}
