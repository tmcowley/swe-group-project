package app.controllers;

import java.util.*;
import spark.*;
import app.util.*;

public class HostLoginController {
    // serve index page (GET request)
    public static Route servePage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return ViewUtil.render(request, model, "/velocity/create-host.vm");
    };

}
