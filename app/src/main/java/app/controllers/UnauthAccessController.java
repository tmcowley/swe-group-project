package app.controllers;

import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class UnauthAccessController {

    // serve the generic unauthorised access page
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: participantEventController:servePage recognized request");

        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/error.vm");
    };
    
}
