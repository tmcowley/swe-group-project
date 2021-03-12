package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import spark.*;
import app.App;
import app.DbConnection;
import app.objects.Host;
import app.objects.Template;
import app.util.*;

public class MyTemplatesController {
    
    // serve the page containing all host templates (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: MyTemplatesController:servePage recognized request");

        // get db conn from singleton App instance
        DbConnection db = App.getInstance().getDbConnection();

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  MyTemplatesController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  MyTemplatesController:servePage session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");
        int hostID = host.getHostID();

        // get each template against the host
        Template[] hostTemplates = db.getTemplatesByHostID(hostID);

        if (hostTemplates == null){
            System.out.println("Notice: MyTemplatesController:servePage hostTemplates array is null");
            // send to generic error page 
            return null;
        }

        //System.out.println("Notice: hostTemplates' array length: " + hostTemplates.length);

        int templateIndex = 0;
        for (Template template : hostTemplates){
            if (template == null) System.out.println("Error: templates[" +templateIndex+ "] is null");
            templateIndex++;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("hostTemplates", hostTemplates);
        model.put("errorMessageMyTemplates", session.attribute("errorMessageMyTemplates"));
        model.put("errorMessageDeleteTemplate", session.attribute("errorMessageDeleteTemplate"));

        // unset error messages
        session.removeAttribute("errorMessageMyTemplates");
        session.removeAttribute("errorMessageDeleteTemplate");

        return ViewUtil.render(request, model, "/velocity/templates.vm");
    };

}
