package app.controllers;

import java.util.*;
import spark.*;
import app.util.*;

public class HostLoginController {
    // serve index page (GET request)
    public static Route servePage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("test1", "this is a test variable!");
        return ViewUtil.render(request, model, "/velocity/create-host.vm");
    };

}
