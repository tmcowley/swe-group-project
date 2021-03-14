package app.controllers;

import app.App;
import app.Validator;
import app.objects.Host;
import app.util.*;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import spark.*;

public class HostHomeController {

    // serve the host homepage (in response to GET request)
    public static Route servePage = (Request request, Response response) -> {

        System.out.println("\nNotice: HostHomeController:servePage recognized request");

        // get validator from singleton App instance
        Validator v = App.getInstance().getValidator();

        // get current session; ensure session is live
        Session session = request.session(true);
        if (session.isNew()) {
            System.out.println("Error:  session not found");
            response.redirect("/error/401");
            return null;
        }

        // ensure host exists in current session
        if (session.attribute("host") == null){
            System.out.println("Error:  session found, host not in session");
            response.redirect("/error/401");
            return null;
        }

        // collect stored (valid) host; ensure host is valid
        Host host = session.attribute("host");
        if (!v.isHostValid(host)){
            System.out.println("Error:  host is null");
            response.redirect("/error/401");
            return null;
        }

        // return host homepage
        Map<String, Object> model = new HashMap<>();
        model.put("fName", host.getFName());
        model.put("lName", host.getLName());
        return ViewUtil.render(request, model, "/velocity/host-home.vm");
    };

}
