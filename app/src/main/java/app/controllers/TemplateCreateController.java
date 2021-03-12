package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import app.util.*;

public class TemplateCreateController {
    
    // serve the template initial-creation page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: TemplateCreateController:servePage recognized request");

        // get current session; ensure session is live
        request.session(true);
        Session session = request.session();
        if (session.isNew()) {
            System.out.println("Error:  TemplateCreateController:servePage session not found");
            response.redirect("/error/401");
            return null;
        }
        
        // generate model for front-end; include error message
        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageCreateEmptyTemplate", session.attribute("errorMessageCreateEmptyTemplate"));

        // unset session error attributes
        session.removeAttribute("errorMessageCreateEmptyTemplate");

        // render initial template creation page
        return ViewUtil.render(request, model, "/velocity/create-template.vm");
    };

}
