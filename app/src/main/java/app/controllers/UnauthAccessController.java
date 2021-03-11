package app.controllers;

import app.App;
import app.objects.Host;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class UnauthAccessController {

    // serve the generic unauthorised access page
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: participantEventController:servePage recognized request");

        return ViewUtil.render(request, new HashMap<>(), "/velocity/error.vm");
    };
    
}
