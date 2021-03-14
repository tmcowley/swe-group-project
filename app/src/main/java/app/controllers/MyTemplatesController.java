package app.controllers;

import app.App;
import app.DbConnection;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class MyTemplatesController {

    // serve the page containing all host templates (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: MyTemplatesController:servePage recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null) {
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");
        int hostID = host.getHostID();

        // get each template against the host
        Template[] hostTemplates = db.getTemplatesByHostID(hostID);

        // ensure the host template array is non-null
        if (hostTemplates == null) {
            System.out.println("Notice:  hostTemplates array is null");
            // send to generic error page
            session.attribute("errorRedirect", request.contextPath());
            session.attribute("errorMessage", "host template array is null");
            session.attribute("errorRedirect", "/host/template");
            response.redirect("/error/406");
            return null;
        }

        // ensure each template is not-null
        int templateIndex = 0;
        for (Template template : hostTemplates) {
            if (template == null) {
                System.out.println("Error: templates[" + templateIndex + "] is null");
                // send to generic error page
                session.attribute("errorRedirect", request.contextPath());
                session.attribute("errorMessage", "Error: templates[" + templateIndex + "] is null");
                session.attribute("errorRedirect", "/host/home");
                response.redirect("/error/406");
                return null;
            }
            templateIndex++;
        }

        // generate template overview page
        Map<String, Object> model = new HashMap<>();
        model.put("hostTemplates", hostTemplates);
        model.put("errorMessageMyTemplates", session.attribute("errorMessageMyTemplates"));
        model.put("errorMessageDeleteTemplate", session.attribute("errorMessageDeleteTemplate"));
        model.put("errorMessageDeleteTemplateComponent", session.attribute("errorMessageDeleteTemplateComponent"));

        // unset error messages
        session.removeAttribute("errorMessageMyTemplates");
        session.removeAttribute("errorMessageDeleteTemplate");
        session.removeAttribute("errorMessageDeleteTemplateComponent");

        // render template overview page
        return ViewUtil.render(request, model, "/velocity/templates.vm");
    };

}
