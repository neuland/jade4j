package de.neuland.jade4j.benchmark;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.JadeConfigurationCaffeine;
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Fork(1)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class TemplateBenchmark {

    ClasspathTemplateLoader templateLoader = new ClasspathTemplateLoader();

    List<String> books = Arrays.asList("booka", "bookb", "bookc");

    @Param({ "0", "1" })
    public int templateId;

    HashMap<String, Object> model = new HashMap<>();

    JadeConfigurationCaffeine jadeCaffeine = new JadeConfigurationCaffeine();

    JadeConfiguration jade = new JadeConfiguration();

    @Setup(Level.Invocation)
    public void setUp() {
        jadeCaffeine.setTemplateLoader(templateLoader);
        jade.setTemplateLoader(templateLoader);
        model.put("pageName", "Jade");
        model.put("books", books);
    }

    @Benchmark
    public void templatesCaffeine() throws Exception {

        Writer writer = new StringWriter();
        JadeTemplate template =
            jadeCaffeine.getTemplate("benchmark/simple" + templateId);
        jadeCaffeine.renderTemplate(template, model, writer);

    }

    @Benchmark
    public void templates() throws Exception {

        Writer writer = new StringWriter();
        JadeTemplate template =
            jade.getTemplate("benchmark/simple" + templateId);
        jade.renderTemplate(template, model, writer);

    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}