package app.controllers;

// for ViewUtil velocity models
import java.util.HashMap;
import java.util.Map;

import app.App;
import app.Validator;
import app.DbConnection;
import app.objects.Host;
import spark.*;


public class AuthController {

    // authorise host (following POST request) and redirect to host home page
    public static Route authHost = (Request request, Response response) -> {

        System.out.println("\nNotice: AuthController:authHost API endpoint recognized request");

        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

        // start session
        request.session(true);

        // collect host-code from request
        String hostCode = request.queryParams("hostCode");
        System.out.println("Notice: hostCode:" + hostCode);

        // ensure host-code is valid
        if (!v.hostCodeIsValid(hostCode)){
            System.out.println("Error: host-code is invalid");
            request.session().attribute("errorMessageLogin", "Error: host-code is invalid");
            request.session().attribute("errorMessageCreate", "");
            response.redirect("/host/login");
            return null;
        }

        // ensure host-code exists in the system
        if (!db.hostCodeExists(hostCode)){
            System.out.println("Error: host-code does not yet exist");
            request.session().attribute("errorMessageLogin", "Error: host-code does not yet exist");
            request.session().attribute("errorMessageCreate", "");
            response.redirect("/host/login");
            return null;
        }

        // get the host by valid, existing host-code
        Host host = db.getHostByCode(hostCode);
        request.session().attribute("host", host);

        // ensure the host object by host-code is valid
        // this should never happen since stored hosts must be valid
        if (!v.isHostValid(host)){
            System.out.println("Error:  host matched to host-code is invalid");
            request.session().attribute("errorMessageLogin", "Error: host matched to host-code is invalid");
            request.session().attribute("errorMessageCreate", "");
            response.redirect("/host/login");
            return null;
        }

        // redirect user to host homepage
        response.redirect("/host/home");
        return null;
    };

}
