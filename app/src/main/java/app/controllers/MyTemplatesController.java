package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import app.objects.Host;
import app.util.*;

public class MyTemplatesController {
    
    // serve the page containing all host templates (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: MyTemplatesController:servePage recognized request");

        // get current session; ensure session is live
        Session session = request.session(false);
        if (session == null) {
            System.out.println("Error:  MyTemplatesController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }

        // get host from session
        Host host = session.attribute("host");
        int hostID = host.getHostID();


        // get each template against the host


        Map<String, Object> model = new HashMap<>();
        model.put("test1", "this");
        return ViewUtil.render(request, model, "/velocity/templates.vm");
    };

}
