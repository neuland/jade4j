package org.apache.commons.jexl3.internal.introspection;

import org.apache.commons.jexl3.JexlOperator;
import org.apache.commons.jexl3.introspection.JexlUberspect;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.Map;

public class JadeUeberspect extends Uberspect {
    public JadeUeberspect(Log runtimeLogger, ResolverStrategy sty) {
        super(runtimeLogger, sty);
    }
}
