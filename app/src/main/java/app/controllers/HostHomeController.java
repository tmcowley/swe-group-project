package app.controllers;

import app.App;
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


        // initialise host and inputted hostcode
        Host host = null;
        String hostCode = request.queryParams("hostCode");
        // validate input before interact with database
        if (App.getInstance().getValidator().hostCodeIsValid(hostCode)) {
            host = App.getInstance().getDbConnection().getHostByCode(hostCode);
        }
        // return host homepage if host is found
        if (App.getInstance().getValidator().isHostValid(host)) {
            request.session(true);
            if (request.session().isNew()) {
                request.session().attribute("host", host);
            }
            Map<String, Object> model = new HashMap<>();
            model.put("fName", host.getFName());
            model.put("lName", host.getLName());
            return ViewUtil.render(request, model, "/velocity/host-home.vm");
        }
        // return notfound if host is not found or hostcode is not valid
        return ViewUtil.notFound;
    };

}
