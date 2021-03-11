package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import app.App;
import app.Validator;
import app.DbConnection;
import app.objects.Host;
import app.util.ViewUtil;
import spark.*;


public class AuthController {

    // authorise host (following POST request) and redirect to host home page
    public static Route authHost = (Request request, Response response) -> {
        System.out.println("\nNotice: hostLogin API endpoint recognized request");
        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

        // start session
        request.session(true);
        if (request.session().isNew()) {
            System.out.println("Error:  APIController:hostLogin: session not found");
            response.redirect("/");
            return null;
        }
        System.out.println(request.toString());
        String hostCode = request.queryParams("hostCode");
        //String hostCode = request.session().attribute("hostCode");
        System.out.println("Notice: hostCode:" + hostCode);

        // validate input before database interaction
        if (!v.hostCodeIsValid(hostCode)){
            request.session().attribute("errorMessageLogin", "Error: host-code is invalid");
            request.session().attribute("errorMessageCreate", "");
            response.redirect("/host/login");
            return null;
        }

        if (!db.hostCodeExists(hostCode)){
            request.session().attribute("errorMessageLogin", "Error: host-code does not yet exist");
            request.session().attribute("errorMessageCreate", "");
            response.redirect("/host/login");
            return null;
        }

        // get the host by valid, existing host-code
        Host host = db.getHostByCode(hostCode);
        request.session().attribute("host", host);

        // return host homepage if host is found
        if (v.isHostValid(host)) {
            // host is valid
            Map<String, Object> model = new HashMap<>();
            model.put("fName", host.getFName());
            model.put("lName", host.getLName());
            return ViewUtil.render(request, model, "/velocity/host-home.vm");
        }

        System.out.println("Error:  Host matched to host-code is invalid");
        request.session().attribute("errorMessageLogin", "Error: Host matched to host-code is invalid");
        request.session().attribute("errorMessageCreate", "");
        response.redirect("/host/login");
        return null;
    };

    // // authorise participant (following POST request)
    // public static Route authParticipant = (Request request, Response response) -> {
    //     return null;
    // };

}
