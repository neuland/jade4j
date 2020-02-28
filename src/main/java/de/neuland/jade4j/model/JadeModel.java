package de.neuland.jade4j.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.parser.node.MixinNode;

public class JadeModel implements Map<String, Object> {

	private static final String LOCALS = "locals";
	public static final String NON_LOCAL_VARS = "nonLocalVars";

	private Deque<Map<String, Object>> scopes = new LinkedList<Map<String, Object>>();
	private Map<String, MixinNode> mixins = new HashMap<String, MixinNode>();
	private Map<String, Filter> filter = new HashMap<String, Filter>();

	public JadeModel(Map<String, Object> defaults) {
		Map<String, Object> rootScope = new HashMap<String, Object>();
		scopes.add(rootScope);

		if (defaults != null) {
			putAll(defaults);
		}

		put(LOCALS, this);
	}

	public void pushScope() {
		HashMap<String, Object> scope = new HashMap<String, Object>();
		scopes.add(scope);
	}

	public void popScope() {
		// first copy non local vars in first matching scope
		Map<String, Object> lastScope = scopes.getLast();
		if (lastScope.containsKey(NON_LOCAL_VARS)) {
			Set<String> nonLocalVars = (Set<String>) lastScope.get(NON_LOCAL_VARS);
			Iterator<Map<String, Object>> scopesIterator = scopes.descendingIterator();
			scopesIterator.next();
			int countFoundNonLocalVars = 0;
			for (Iterator<Map<String, Object>> i = scopesIterator; i.hasNext();) {
				Map<String, Object> scope = i.next();
				for(String nonLocalVar : nonLocalVars) {
					if (scope.containsKey(nonLocalVar) && lastScope.containsKey(nonLocalVar)) {
						scope.put(nonLocalVar, lastScope.get(nonLocalVar));
						countFoundNonLocalVars++;
					}
				}
				if (nonLocalVars.size() == countFoundNonLocalVars) {
					break;
				}
			}
		}
		scopes.removeLast();
	}

	public void setMixin(String name, MixinNode node) {
		mixins.put(name, node);
	}

	public MixinNode getMixin(String name) {
		return mixins.get(name);
	}

	@Override
	public void clear() {
		scopes.clear();
		scopes.add(new HashMap<String, Object>());
	}

	@Override
	public boolean containsKey(Object key) {
		for (Iterator<Map<String, Object>> i = scopes.descendingIterator(); i.hasNext();) {
			Map<String, Object> scope = i.next();
			if (scope.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Iterator<Map<String, Object>> i = scopes.descendingIterator(); i.hasNext();) {
			Map<String, Object> scope = i.next();
			if (scope.containsValue(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String key : keySet()) {
			map.put(key, get(key));
		}
		return map.entrySet();
	}

	@Override
	// adds the object to the highest scope
	public Object get(Object key) {
		for (Iterator<Map<String, Object>> i = scopes.descendingIterator(); i.hasNext();) {
			Map<String, Object> scope = i.next();
			if (scope.containsKey(key)) {
				return scope.get(key);
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	@Override
	// returns a set of unique keys
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		for (Iterator<Map<String, Object>> i = scopes.descendingIterator(); i.hasNext();) {
			keys.addAll(i.next().keySet());
		}
		return keys;
	}

	@Override
	// adds the object to the current scope
	public Object put(String key, Object value) {
		Object currentValue = get(key);
		scopes.getLast().put(key, value);
		return currentValue;
	}

	@Override
	// addes all map entries to the current scope map
	public void putAll(Map<? extends String, ? extends Object> m) {
		scopes.getLast().putAll(m);
	}

	@Override
	// removes the scopes first object with the given key
	public Object remove(Object key) {
		for (Iterator<Map<String, Object>> i = scopes.descendingIterator(); i.hasNext();) {
			Map<String, Object> scope = i.next();
			if (scope.containsKey(key)) {
				Object object = scope.get(key);
				scope.remove(key);
				return object;
			}
		}
		return null;
	}

	@Override
	// returns the size of all unique keys
	public int size() {
		return keySet().size();
	}

	@Override
	// returns the size of all unique keys
	public Collection<Object> values() {
		List<Object> values = new ArrayList<Object>();
		for (String key : keySet()) {
			values.add(get(key));
		}
		return values;
	}

	public Filter getFilter(String name) {
		return filter.get(name);
	}

	public void addFilter(String name, Filter filter) {
		this.filter.put(name, filter);
	}
}
