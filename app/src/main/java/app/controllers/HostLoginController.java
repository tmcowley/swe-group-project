package app.controllers;

import java.util.*;
import spark.*;
import app.util.*;

public class HostLoginController {

    // TODO: comment
    public static Route servePage = (Request request, Response response) -> {
        // start session
        request.session(true);

        if (request.session().attribute("errorMessageLogin") == null)
            request.session().attribute("errorMessageLogin", "");
        
        if (request.session().attribute("errorMessageCreate") == null)
            request.session().attribute("errorMessageCreate", "");

        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageLogin", request.session().attribute("errorMessageLogin"));
        model.put("errorMessageCreate", request.session().attribute("errorMessageCreate"));

        return ViewUtil.render(request, model, "/velocity/create-host.vm");
    };

}
