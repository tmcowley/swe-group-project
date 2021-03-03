package app.controllers;

import java.util.*;
import java.net.*;
import java.io.*;
import spark.*;
import app.util.*;

public class participantEventController {
    /** Serve the index page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("test1", "this is a test variable!");
        return ViewUtil.render(request, model, "/velocity/participant-event.vm");
    };

    public static Route eventEntryPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("test1", "this is a test variable!");
        return ViewUtil.render(request, model, "/velocity/joinEvent.vm");
    };

}
