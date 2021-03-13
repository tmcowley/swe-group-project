package app.controllers;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class EventCreateController {

    // serve the event creation page for an authenticated host
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: EventCreateController:servePage recognized request");

        // get db conn, validator from singleton App instance
        Validator v = App.getInstance().getValidator();
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  EventCreateController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host is set
        if (session.attribute("host") == null){
            System.out.println("Error:  EventCreateController:servePage host not set");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = request.session().attribute("host");

        if (!v.isHostValid(host)) {
            return "Error:  host is invalid";
        }

        // model holds data to be sent to front-end variables (w/ Velocity)
        Map<String, Object> model = new HashMap<>();
        Template[] templates = db.getTemplatesByHostID(host.getHostID());

        int templateCount = 0;
        if (templates.length != 0) {
            int[] templateCounts = new int[templates.length];
            for (int i=0; i<templates.length;i++) {
                templateCounts[templateCount] = templateCount;
                templateCount++;
            }
            model.put("templateCounts", templateCounts);
        }
        model.put("templates", templates);
        model.put("templateCount", templateCount);
        model.put("errorMessageCreateEvent", session.attribute("errorMessageCreateEvent"));
        session.removeAttribute("errorMessageCreateEvent");
        return ViewUtil.render(request, model, "/velocity/create-event.vm");
    };
}
