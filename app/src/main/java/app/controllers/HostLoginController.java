package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;
import app.util.*;

public class HostLoginController {

    // serve the host login and sign-up page (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: HostLoginController:servePage recognized request");

        // start session
        request.session(true);

        if (request.session().attribute("errorMessageLogin") == null)
            request.session().attribute("errorMessageLogin", "");
        
        if (request.session().attribute("errorMessageCreate") == null)
            request.session().attribute("errorMessageCreate", "");

        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageLogin", request.session().attribute("errorMessageLogin"));
        model.put("errorMessageCreate", request.session().attribute("errorMessageCreate"));

        // unset session error attributes
        request.session().removeAttribute("errorMessageLogin");
        request.session().removeAttribute("errorMessageCreate");

        return ViewUtil.render(request, model, "/velocity/create-host.vm");
    };

}
