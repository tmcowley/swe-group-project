package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import app.util.*;

public class TemplateEditController {
    
    // serve the Template editor page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: TemplateEditController:servePage recognized request");

        request.session(true);
        if (request.session().isNew()) {
            System.out.println("Error:  HostEventController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }

        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/edit-template.vm");
    };

}
