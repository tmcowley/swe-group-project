package app.controllers;

import app.App;
import app.DbConnection;
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

        DbConnection db = App.getInstance().getDbConnection();
        Validator v = App.getInstance().getValidator();

        request.session(true);
        if (request.session().isNew()) {
            return "Error:  HostHomeController:servePage: session does not exist";
        }

        // collect inputted host-code; get host by host-code
        String hostCode = request.session().attribute("hostCode");
        Host host = db.getHostByCode(hostCode);

        // validate input before database interaction
        if (!v.hostCodeIsValid(hostCode)) {
            return "Error:  HostHomeController:servePage: hostCode invalid";
        }

        if (!db.hostCodeExists(hostCode)){
            return "Error:  HostHomeController:servePage: hostCode does not exist";
        }

        host = db.getHostByCode(hostCode);

        if (!v.isHostValid(host)){
            return "Error:  HostHomeController:servePage: host invalid";
        }

        // host valid; store host
        request.session().attribute("host", host);

        // return host homepage
        Map<String, Object> model = new HashMap<>();
        model.put("fName", host.getFName());
        model.put("lName", host.getLName());
        return ViewUtil.render(request, model, "/velocity/host-home.vm");
    };

}
