package de.neuland.jade4j.ognl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

public class OGNLRootProxyFactory {

	private static class OGNLRootProxy implements InvocationHandler {
		Map<String, Object> properties = new HashMap<String, Object>();

		public OGNLRootProxy(Map<String, Object> properties) {
			this.properties.putAll(properties);
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String meth = method.getName();
			if (meth.startsWith("get")) {
				String prop = meth.substring(3);
				return getProperty(prop, method);
			}
			else if (meth.startsWith("is")) {
				// Alternate version of get for boolean properties
				String prop = meth.substring(2);
				return getProperty(prop, method);
			}
			else if (meth.startsWith("set")) {
				String prop = meth.substring(3);
				properties.put(prop, args[0]);
				return null;
			}
			else {
				// Can dispatch non get/set/is methods as desired
				throw new OperationNotSupportedException();
			}
		}

		private Object getProperty(String prop, Method method) {
			Object o = properties.get(prop);
			if (o != null && !method.getReturnType().isInstance(o))
				throw new ClassCastException(o.getClass().getName() + 
						" is not a " + method.getReturnType().getName());
			return o;
		}
		
	}

	@SuppressWarnings("unchecked")
	public static<T> T getProxy(Class<T> intf, Map<String, Object> values) {
		return (T) Proxy.newProxyInstance(OGNLRootProxyFactory.class.getClassLoader(),
					new Class[] { intf }, new OGNLRootProxy(values));
	}
}
