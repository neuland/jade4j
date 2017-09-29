package org.apache.commons.jexl3.internal.introspection;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.jexl3.JexlInfo;
import org.apache.commons.jexl3.introspection.JexlPropertyGet;
import org.apache.commons.logging.Log;

public class JadeIntrospect extends Uberspect {

	public JadeIntrospect(Log runtimeLogger, ResolverStrategy sty) {
		super(runtimeLogger,sty);
	}

	/**
	 * Overwriting method to replace "getGetExecutor" call with "getJadeGetExecutor"
	 */
	@SuppressWarnings("deprecation")
	@Override
	public JexlPropertyGet getPropertyGet(Object obj, Object identifier, JexlInfo info) {
		JexlPropertyGet get = getJadeGetExecutor(obj, identifier);
		if (get == null && obj != null && identifier != null) {
			get = getIndexedGet(obj, identifier.toString());
			if (get == null) {
				Field field = getField(obj, identifier.toString(), info);
				if (field != null) {
					return new FieldPropertyGet(field);
				}
			}
		}
		return get;
	}
	public JexlPropertyGet getPropertyGet(List<PropertyResolver> resolvers, Object obj, Object identifier) {

	}
	/**
	 * Identical to getGetExecutor, but does check for map first. Mainly to avoid problems with 'class' properties.
	 */
	public final AbstractExecutor.Get getJadeGetExecutor(Object obj, Object identifier) {
		final Class<?> claz = obj.getClass();
		final String property = AbstractExecutor.castString(identifier);
		JexlPropertyGet executor = null;
		Introspector is = this.base();
		// let's see if we are a map...
		executor = MapGetExecutor.discover(is, claz, identifier);
		if (executor.isAlive()) {
			return executor;
		}
		// first try for a getFoo() type of property (also getfoo() )
		if (property != null) {
			executor = PropertyGetExecutor.discover(is, claz, property);
			if (executor.isAlive()) {
				return executor;
			}
			// }
			// look for boolean isFoo()
			// if (property != null) {
			executor = BooleanGetExecutor.discover(is, claz, property);
			if (executor.isAlive()) {
				return executor;
			}
		}
		// let's see if we can convert the identifier to an int,
		// if obj is an array or a list, we can still do something
		Integer index = toInteger(identifier);
		if (index != null) {
			executor = ListGetExecutor.discover(is, claz, index);
			if (executor.isAlive()) {
				return executor;
			}
		}
		// if that didn't work, look for set("foo")
		executor = DuckGetExecutor.discover(is, claz, identifier);
		if (executor.isAlive()) {
			return executor;
		}
		// if that didn't work, look for set("foo")
		executor = DuckGetExecutor.discover(is, claz, property);
		if (executor.isAlive()) {
			return executor;
		}
		return null;
	}
}
