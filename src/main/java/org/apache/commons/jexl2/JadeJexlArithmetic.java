package org.apache.commons.jexl2;

import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

public class JadeJexlArithmetic extends JexlArithmetic {

    public JadeJexlArithmetic(boolean lenient) {
        super(lenient);
    }

    protected int compare(Object left, Object right, String operator) {
        if (left != null && right != null) {
            if (left instanceof String || right instanceof String) {
                return toString(left).compareTo(toString(right));
            } else if (left instanceof BigDecimal || right instanceof BigDecimal) {
                BigDecimal l = toBigDecimal(left);
                BigDecimal r = toBigDecimal(right);
                return l.compareTo(r);
            } else if (left instanceof BigInteger || right instanceof BigInteger) {
                BigInteger l = toBigInteger(left);
                BigInteger r = toBigInteger(right);
                return l.compareTo(r);
            } else if (isFloatingPoint(left) || isFloatingPoint(right)) {
                double lhs = toDouble(left);
                double rhs = toDouble(right);
                if (Double.isNaN(lhs)) {
                    if (Double.isNaN(rhs)) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (Double.isNaN(rhs)) {
                    // lhs is not NaN
                    return +1;
                } else if (lhs < rhs) {
                    return -1;
                } else if (lhs > rhs) {
                    return +1;
                } else {
                    return 0;
                }
            } else if (isNumberable(left) || isNumberable(right)) {
                long lhs = toLong(left);
                long rhs = toLong(right);
                if (lhs < rhs) {
                    return -1;
                } else if (lhs > rhs) {
                    return +1;
                } else {
                    return 0;
                }
            } else if ("==".equals(operator)) {
                return left.equals(right) ? 0 : -1;
            } else if (left instanceof Comparable<?>) {
                @SuppressWarnings("unchecked") // OK because of instanceof check above
                final Comparable<Object> comparable = (Comparable<Object>) left;
                return comparable.compareTo(right);
            } else if (right instanceof Comparable<?>) {
                @SuppressWarnings("unchecked") // OK because of instanceof check above
                final Comparable<Object> comparable = (Comparable<Object>) right;
                return comparable.compareTo(left);
            }
        }
        throw new ArithmeticException("Object comparison:(" + left + " " + operator + " " + right + ")");
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
            return (Boolean) val;
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

    @Override
    public Object add(Object left, Object right) {
        if (left instanceof String && right instanceof String) {
            return (String)left + right;
        }
        else {
            return super.add(left, right);
        }
    }
}
