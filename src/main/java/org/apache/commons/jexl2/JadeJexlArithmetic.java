package org.apache.commons.jexl2;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import java.util.Collection;

public class JadeJexlArithmetic extends JexlArithmetic {
    public JadeJexlArithmetic(boolean lenient) {
        super(lenient);
    }

    /**
     * using the original implementation
     * added check for empty lists
     * defaulting to "true"
     */
    @Override
    public boolean toBoolean(Object val) {
        if (val == null) {
            controlNullOperand();
            return false;
        } else if (val instanceof Boolean) {
            return ((Boolean) val).booleanValue();
        } else if (val instanceof Number) {
            double number = toDouble(val);
            return !Double.isNaN(number) && number != 0.d;
        } else if (val instanceof String) {
            String strval = val.toString();
            return strval.length() > 0 && !"false".equals(strval);
        } else if (val instanceof Collection) {
            return CollectionUtils.isNotEmpty((Collection) val);
        }

        return true;
    }
}
