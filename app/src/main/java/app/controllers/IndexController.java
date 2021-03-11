package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import app.util.*;

public class IndexController {
    
    // Serve the landing (index) page following GET request
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: IndexController:servePage recognized request");

        // start session
        request.session(true);

        if (request.session().attribute("errorMessageJoinEvent") == null)
            request.session().attribute("errorMessageJoinEvent", "");

        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageJoinEvent", request.session().attribute("errorMessageJoinEvent"));
        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}
