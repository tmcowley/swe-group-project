package app.controllers;

import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class participantEventController {

    // serve the participant event page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: participantEventController:servePage recognized request");

        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}
