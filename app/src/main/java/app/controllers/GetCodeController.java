package app.controllers;

import java.util.*;
import java.net.*;
import java.io.*;
import spark.*;
import app.App;
import app.objects.Host;
import app.util.*;

public class GetCodeController {
    /** Serve the index page (GET request) */
    public static Route servePage = (Request request, Response response) -> {
        // initialise event and input first name and last name
        Host host = null;
        String[] name = request.queryParams("hostName").split(" ");
        String e_address = request.queryParams("hostEmail");
        // validate input before interact with database
        if (App.getInstance().getValidator().eAddressIsValid(e_address)
                && App.getInstance().getValidator().nameIsValid(name[0])
                && App.getInstance().getValidator().nameIsValid(name[1])) {
            host = App.getInstance().getDbConnection().createHost(name[0], name[1], request.ip(), e_address);
        }
        // return participant event page if event is found
        if (App.getInstance().getValidator().isHostValid(host)) {
            request.session(true);
            request.session().attribute("host", host);
            Map<String, Object> model = new HashMap<>();
            model.put("hostCode", host.getHostCode());
            return ViewUtil.render(request, model, "/velocity/get-code.vm");
        }
        // return notfound if event is not found or input is not valid
        return ViewUtil.notFound;
    };

}
