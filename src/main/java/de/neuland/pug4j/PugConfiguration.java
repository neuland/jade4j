package de.neuland.pug4j;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import de.neuland.pug4j.Pug4J.Mode;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.exceptions.PugException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.expression.NashornExpressionHandler;
import de.neuland.pug4j.filter.CDATAFilter;
import de.neuland.pug4j.filter.CssFilter;
import de.neuland.pug4j.filter.Filter;
import de.neuland.pug4j.filter.JsFilter;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.parser.Parser;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.FileTemplateLoader;
import de.neuland.pug4j.template.PugTemplate;
import de.neuland.pug4j.template.TemplateLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class PugConfiguration {

    private static final String FILTER_CDATA = "cdata";
    private static final String FILTER_STYLE = "css";
    private static final String FILTER_SCRIPT = "js";

    private boolean prettyPrint = false;
    private boolean caching = true;
    private Mode mode = Pug4J.Mode.XHTML;

    private Map<String, Filter> filters = new HashMap<String, Filter>();
    private Map<String, Object> sharedVariables = new HashMap<String, Object>();
    private TemplateLoader templateLoader = new FileTemplateLoader("", "UTF-8");
    private ExpressionHandler expressionHandler = new NashornExpressionHandler();
    protected static final int MAX_ENTRIES = 1000;

    public PugConfiguration() {
        setFilter(FILTER_CDATA, new CDATAFilter());
        setFilter(FILTER_SCRIPT, new JsFilter());
        setFilter(FILTER_STYLE, new CssFilter());
    }

    private Map<String, PugTemplate> cache = new ConcurrentLinkedHashMap.Builder<String, PugTemplate>().maximumWeightedCapacity(
            MAX_ENTRIES + 1).build();
    private Map<String, String> lockCache = new ConcurrentLinkedHashMap.Builder<String, String>().maximumWeightedCapacity(
            MAX_ENTRIES + 1).build();

    public PugTemplate getTemplate(String name) throws IOException, PugException {
        if (caching) {
            long lastModified = templateLoader.getLastModified(name);
            PugTemplate template = cache.get(getKeyValue(name, lastModified));
            if (template != null) {
                return template;
            }

            String key = getCachedKey(name, lastModified);
            synchronized (key) {
                PugTemplate newTemplate = createTemplate(name);
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

    public void renderTemplate(PugTemplate template, Map<String, Object> model, Writer writer) throws PugCompilerException {
        PugModel pugModel = new PugModel(sharedVariables);
        for (String filterName : filters.keySet()) {
            pugModel.addFilter(filterName, filters.get(filterName));
        }
        pugModel.putAll(model);
        template.process(pugModel, writer);
    }

    public String renderTemplate(PugTemplate template, Map<String, Object> model) throws PugCompilerException {
        StringWriter writer = new StringWriter();
        renderTemplate(template, model, writer);
        return writer.toString();
    }

    private PugTemplate createTemplate(String name) throws PugException, IOException {
        PugTemplate template = new PugTemplate();

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
