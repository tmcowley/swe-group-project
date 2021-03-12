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
        Session session = request.session(true);

        if (session.attribute("errorMessageLogin") == null)
            session.attribute("errorMessageLogin", "");
        
        if (session.attribute("errorMessageCreate") == null)
            session.attribute("errorMessageCreate", "");

        Map<String, Object> model = new HashMap<>();
        model.put("errorMessageLogin", session.attribute("errorMessageLogin"));
        model.put("errorMessageCreate", session.attribute("errorMessageCreate"));

        // unset session error attributes
        session.removeAttribute("errorMessageLogin");
        session.removeAttribute("errorMessageCreate");

        return ViewUtil.render(request, model, "/velocity/create-host.vm");
    };

}
