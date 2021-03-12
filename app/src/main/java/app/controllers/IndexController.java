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

        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageJoinEvent", request.session().attribute("errorMessageJoinEvent"));

        // unset session error attribute
        request.session().removeAttribute("errorMessageJoinEvent");

        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}
