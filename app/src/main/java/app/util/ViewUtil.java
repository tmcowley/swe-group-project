package app.util;

// for front-end model variable setting
import java.util.Map;
import java.util.HashMap;

import spark.*;
import spark.template.velocity.*;
import org.apache.velocity.app.*;
import org.eclipse.jetty.http.*;

public class ViewUtil {

    // Renders a template given a model and a request
    public static String render(Request request, Map<String, Object> model, String templatePath) {
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }

    /**
     * unauthorized page access (HTTP_401)
     * 
     */
    public static Route nonAuthenticatedAccess = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:unauthenticatedAccess (401) called");
        response.status(HttpStatus.UNAUTHORIZED_401);

        // render non-authenticated access page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/401-non-authenticated-access.vm");

        // guide to make an error for this page:
        //
        // response.redirect("/error/401");
        // return null;
        //
    };

    /**
     * page not found Route (HTTP_404)
     * 
     */
    public static Route notFound = (Request request, Response response) -> {
        System.out.println("\nNotice: ViewUtil:notFound (404) called");
        response.status(HttpStatus.NOT_FOUND_404);

        // render page not found page
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/404-not-found.vm");

        // guide to make an error for this page:
        //
        // response.redirect("/error/404");
        // return null;
        //
    };

    /**
     * general error page: not acceptable (HTTP_406)
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

        // guide to make an error for this page:
        //
        // session.attribute("errorFrom", ""); session.attribute("errorMessage", "");
        // session.attribute("errorRedirect", ""); response.redirect("/error/406");
        // return null;
        //
    };

    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
}
