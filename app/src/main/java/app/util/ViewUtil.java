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

    /**
     * unauthorized page access (HTTP_401)
     * 
     * Guide to make an error for this page:
     * response.redirect("/error/401");
     * return null;
     */
    public static Route unauthAccess = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:unauthAccess (401) called");
        response.status(HttpStatus.UNAUTHORIZED_401);

        // render unauthorised access page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/401-unauth-access.vm");
    };


    /**
     * page not found Route (HTTP_404)
     * 
     * Guide to make an error for this page:
     * response.redirect("/error/404");
     * return null;
     */
    public static Route notFound = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:notFound (404) called");
        response.status(HttpStatus.NOT_FOUND_404);

        // render page not found page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/404-not-found.vm");
    };

    /**
     * general error page: not acceptable (HTTP_406)
     * 
     * Guide to make an error for this page:
     * session.attribute("errorFrom", ""); 
     * session.attribute("errorMessage", ""); 
     * session.attribute("errorRedirect", "");
     * response.redirect("/error/406");
     * return null;
     * 
     */
    public static Route notAcceptable = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:notAcceptable (406) called");
        response.status(HttpStatus.NOT_ACCEPTABLE_406);

        // get current session; launch session if needed
        Session session = request.session(true);

        // generate general error page
        Map<String, Object> model = new HashMap<>();
        model.put("errorFrom", session.attribute("errorFrom"));
        model.put("errorMessage", session.attribute("errorMessage"));
        model.put("errorRedirect", session.attribute("errorRedirect"));

        // unset session error attributes
        session.removeAttribute("errorFrom");
        session.removeAttribute("errorMessage");
        session.removeAttribute("errorRedirect");

        // render general error page
        return ViewUtil.render(request, model, "/velocity/406-not-acceptable.vm");
    };

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}
