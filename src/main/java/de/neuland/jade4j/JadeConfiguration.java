package de.neuland.jade4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.neuland.jade4j.Jade4J.Mode;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.exceptions.JadeException;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;

public class JadeConfiguration {

	private static final String FILTER_CDATA = "cdata";

	private static final String FILTER_PLAIN = "plain";

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(JadeConfiguration.class);

	private boolean prettyPrint = false;
	private Mode mode = Jade4J.Mode.HTML;

	private Map<String, Filter> filters = new HashMap<String, Filter>();
	private Map<String, Object> sharedVariables = new HashMap<String, Object>();
	private TemplateLoader templateLoader = new FileTemplateLoader("", "UTF-8");
	protected static final int MAX_ENTRIES = 1000;

	public JadeConfiguration() {
		setFilter(FILTER_PLAIN, new PlainFilter());
		setFilter(FILTER_CDATA, new CDATAFilter());
	}

	private Map<String, JadeTemplate> cache = new LinkedHashMap<String, JadeTemplate>(MAX_ENTRIES + 1, .75F, true) {
		private static final long serialVersionUID = -2234660416692828706L;

		public boolean removeEldestEntry(Map.Entry<String, JadeTemplate> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public JadeTemplate getTemplate(String name) throws IOException, JadeException {

		long lastModified = templateLoader.getLastModified(name);

		String key = name + "-" + lastModified;
		if (!cache.containsKey(key)) {
			cache.put(key, createTemplate(name, lastModified));
		}
		return cache.get(key);
	}

	public void renderTemplate(JadeTemplate template, Map<String, Object> model, Writer writer) throws JadeCompilerException {
		JadeModel jadeModel = new JadeModel(sharedVariables);
		for (String filterName : filters.keySet()) {
			jadeModel.addFilter(filterName, filters.get(filterName));
		}
		jadeModel.putAll(model);
		template.process(jadeModel, writer);
	}

	public String renderTemplate(JadeTemplate template, Map<String, Object> model) throws JadeCompilerException {
		StringWriter writer = new StringWriter();
		renderTemplate(template, model, writer);
		return writer.toString();
	}

	private JadeTemplate createTemplate(String name, long lastModified) throws JadeException, IOException {
		JadeTemplate template = new JadeTemplate();
		template.setLastmodified(lastModified);

		Parser parser = new Parser(name, templateLoader);
		Node root = parser.parse();
		template.setTemplateLoader(templateLoader);
		template.setRootNode(root);
		template.setPrettyPrint(prettyPrint);
		template.setMode(getMode());
		return template;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public void setFilter(String name, Filter filter) {
		filters.put(name, filter);
	}

	public void removeFilter(String name) {
		filters.remove(name);
	}

	public Map<String, Object> getSharedVariables() {
		return sharedVariables;
	}

	public void setSharedVariables(Map<String, Object> sharedVariables) {
		this.sharedVariables = sharedVariables;
	}

	public TemplateLoader getTemplateLoader() {
		return templateLoader;
	}

	public void setTemplateLoader(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean templateExists(String url) {
		try {
			return templateLoader.getReader(url) != null;
		} catch (IOException e) {
			return false;
		}
	}

}
