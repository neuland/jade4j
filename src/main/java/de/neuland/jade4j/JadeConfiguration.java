package de.neuland.jade4j;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import de.neuland.jade4j.Jade4J.Mode;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.exceptions.JadeException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.expression.JexlExpressionHandler;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.CssFilter;
import de.neuland.jade4j.filter.Filter;
import de.neuland.jade4j.filter.JsFilter;
import de.neuland.jade4j.model.JadeModel;
import de.neuland.jade4j.parser.Parser;
import de.neuland.jade4j.parser.node.Node;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class JadeConfiguration {

    private static final String FILTER_CDATA = "cdata";
    private static final String FILTER_STYLE = "css";
    private static final String FILTER_SCRIPT = "js";

    private boolean prettyPrint = false;
    private boolean caching = true;
    private Mode mode = Jade4J.Mode.HTML;

    private Map<String, Filter> filters = new HashMap<String, Filter>();
    private Map<String, Object> sharedVariables = new HashMap<String, Object>();
    private TemplateLoader templateLoader = new FileTemplateLoader("", "UTF-8");
    private ExpressionHandler expressionHandler = new JexlExpressionHandler();
    protected static final int MAX_ENTRIES = 1000;

    public JadeConfiguration() {
        setFilter(FILTER_CDATA, new CDATAFilter());
        setFilter(FILTER_SCRIPT, new JsFilter());
        setFilter(FILTER_STYLE, new CssFilter());
    }

    private Map<String, JadeTemplate> cache = new ConcurrentLinkedHashMap.Builder<String, JadeTemplate>().maximumWeightedCapacity(
            MAX_ENTRIES + 1).build();
    private Map<String, String> lockCache = new ConcurrentLinkedHashMap.Builder<String, String>().maximumWeightedCapacity(
            MAX_ENTRIES + 1).build();

    public JadeTemplate getTemplate(String name) throws IOException, JadeException {
        if (caching) {
            long lastModified = templateLoader.getLastModified(name);
            JadeTemplate template = cache.get(getKeyValue(name, lastModified));
            if (template != null) {
                return template;
            }

            String key = getCachedKey(name, lastModified);
            synchronized (key) {
                JadeTemplate newTemplate = createTemplate(name);
                cache.put(key, newTemplate);
                return newTemplate;
            }
        }

        return createTemplate(name);
    }

    private synchronized String getCachedKey(String name, long lastModified) {
        String key = getKeyValue(name, lastModified);
        String cachedKey = lockCache.get(name);
        if (key.equals(cachedKey)) {
            return cachedKey;
        } else if (cachedKey != null) {
            cache.remove(cachedKey);
        }
        lockCache.put(name, key);
        return key;
    }

    private String getKeyValue(String name, long lastModified) {
        return name + "-" + lastModified;
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

    private JadeTemplate createTemplate(String name) throws JadeException, IOException {
        JadeTemplate template = new JadeTemplate();

        Parser parser = new Parser(name, templateLoader, expressionHandler);
        Node root = parser.parse();
        template.setTemplateLoader(templateLoader);
        template.setExpressionHandler(expressionHandler);
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

    public Map<String, Filter> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Filter> filters) {
        this.filters = filters;
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

    public void setExpressionHandler(ExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }

    public ExpressionHandler getExpressionHandler() {
        return expressionHandler;
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

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean cache) {
        if (cache != this.caching) {
            expressionHandler.setCache(cache);
            this.caching = cache;
        }
    }

    public void clearCache() {
        expressionHandler.clearCache();
        cache.clear();
    }
}
