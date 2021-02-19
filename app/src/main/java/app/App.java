package app;

// /**
//  * Hello world!
//  *
//  */
// public class App 
// {
//     public static void main( String[] args )
//     {
//         System.out.println( "Hello World!" );
//     }
// }

import static spark.Spark.*;

import org.apache.velocity.app.*;
import java.util.*;
import spark.*;
import spark.template.velocity.*;

import org.eclipse.jetty.http.*;

import java.net.*;
import app.*;

import spark.Spark;
import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.nio.file.Paths;

import org.eclipse.jetty.http.*;

import app.Event;

public class App {
    public static void main(String[] args) {
        get("/", (req, res) -> "Hello World");

        // Validator v = new Validator();

        //get("/", servePage);
    }

    public static Route servePage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();

        String[] products = {"beans", "carrots", "pears"};
        model.put("products", products);

        return render(request, model, "/velocity/index.vm");
    };

    public static String render(Request request, Map<String, Object> model, String templatePath) {
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }

    // TODO: somehow reference location of vm files in /resources/velocity/
    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }

}
