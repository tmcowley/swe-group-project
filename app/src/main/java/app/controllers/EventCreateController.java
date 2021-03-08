package app.controllers;

import java.util.*;

import org.apache.commons.collections.functors.IfClosure;

import java.net.*;
import java.io.*;
import spark.*;
import app.App;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

public class EventCreateController {
    /** Serve the index page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        //initialiss model to hold data
        Map<String, Object> model = new HashMap<>();
        //start session
        request.session(true);
        //get host from session
        Host host = request.session().attribute("host");
        //return not found if session is new
        if (request.session().isNew() || !App.getInstance().getValidator().isHostValid(host)) {
            return ViewUtil.notFound;
        }
        Template[] templates = App.getInstance().getDbConnection().getTemplatesByHostID(host.getHostID());
        int templateCount = 0;
        if (templates.length != 0) {
            String[] templateCodes = new String[templates.length];
            for (Template template : templates) {
                templateCodes[templateCount] = template.getTemplateCode();
                templateCount++;
            }
            model.put("templateCodes", templateCodes);
        }
        model.put("templateCount", templateCount);
        return ViewUtil.render(request, model, "/velocity/create-event.vm");
    };

}
