package de.neuland.jade4j.filter;

import de.neuland.jade4j.parser.node.Attr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CachingFilter implements Filter {

	private static final int MAX_ENTRIES = 1000;

	private static Map<String, String> cache = new LinkedHashMap<String, String>(MAX_ENTRIES + 1, .75F, true) {
		private static final long serialVersionUID = 618942552777647107L;

		public boolean removeEldestEntry(Map.Entry<String, String> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	@Override
	public String convert(String source, List<Attr> attributes, Map<String, Object> model) {
		String key = source.hashCode() + "-" + attributes.hashCode();
		if (!cache.containsKey(key)) {
			cache.put(key, convert(source, attributes));
		}
		return cache.get(key);
	}

	abstract protected String convert(String source, List<Attr> attributes);

}
