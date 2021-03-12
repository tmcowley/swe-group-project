package app.util;

import org.apache.velocity.app.*;
import org.eclipse.jetty.http.*;
import spark.*;
import spark.template.velocity.*;
import java.util.*;

public class ViewUtil {

    // Renders a template given a model and a request
    public static String render(Request request, Map<String, Object> model, String templatePath) {
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }

    // unauthorized page access (HTTP_401)
    public static Route unauthAccess = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:unauthAccess (401) called");
        response.status(HttpStatus.UNAUTHORIZED_401);

        // render unauthorised access page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/401-unauth-access.vm");
    };


    // page not found Route (HTTP_404)
    public static Route notFound = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:notFound (404) called");
        response.status(HttpStatus.NOT_FOUND_404);

        // render page not found page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/404-not-found.vm");
    };

    // input not acceptable (HTTP_406)
    public static Route notAcceptable = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:notAcceptable (406) called");
        response.status(HttpStatus.NOT_ACCEPTABLE_406);
        return "Not acceptable.";

        // // render page not found page
        // Map<String, Object> model = new HashMap<>();
        // return ViewUtil.render(request, model, "/velocity/406-not-acceptable.vm");
    };

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}
