package de.neuland.jade4j.proto;

import de.neuland.jade4j.Jade4J;
import org.yaml.snakeyaml.Yaml;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static spark.Spark.*;

public class Jade4JProto {

    private static final String[] MODEL_EXTS = {"yml", "json"};


    public static void main(String[] args) {

        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            help();
            System.exit(0);
        }

        int port = 4567;
        String baseDirAux = System.getProperty("user.dir");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--port".equals(arg)) {
                i++;
                port = Integer.parseInt(args[i]);
            } else {
                baseDirAux = arg;
            }
        }

        final String baseDir = Paths.get(baseDirAux).normalize().toAbsolutePath().toString();

        System.out.println("=========== Jade4J :: PROTO ===========");
        System.out.println("PORT:     " + port);
        System.out.println("BASE DIR: " + baseDir);
        System.out.println("=======================================");

        setIpAddress("0.0.0.0");
        setPort(port);

        get(new Route("/*") {
            @Override
            public Object handle(Request request, Response response) {
                String relativePath = request.pathInfo();
                relativePath = relativePath.equals("/") ? "/index" : relativePath;
                System.out.println("RENDERING: " + relativePath);
                String absolutePath = baseDir + relativePath;
                String jadeFile = absolutePath + ".jade";
                System.out.println("\t\tJADE:  " + jadeFile);
                try {
                    response.type("text/html");
                    return Jade4J.render(jadeFile, load(absolutePath), true);
                } catch (IOException e) {
                    response.type("text/plain");
                    return "Error: " + e.getMessage();
                }
            }
        });
    }

    private static void help() {
        System.out.println("jade4j-proto [directory] [--port <port>]");
    }

    private static Map load(String name) {
        Map model = Collections.emptyMap();
        for (String ext : MODEL_EXTS) {
            File file = new File(name + "." + ext);
            if (file.exists()) {
                System.out.println("\t\tMODEL: " + file);
                model = load(file);
            }
        }
        if (model.isEmpty()) {
            System.out.println("\t\tModel is empty!");
        }
        return model;
    }

    private static Map load(File file) {
        try {
            Yaml yaml = new Yaml();
            return yaml.load(new FileReader(file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
