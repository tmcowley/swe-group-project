package app.controllers;

import java.util.*;
import spark.*;
import app.App;
import app.util.*;
import app.objects.*;


public class participantEventController {

    /** Serve the participant-event page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/landing.vm");
    };

}
