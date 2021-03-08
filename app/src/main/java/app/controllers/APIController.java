package app.controllers;

import java.util.HashMap;
import java.util.Map;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.util.ViewUtil;
import app.util.emailController;

import spark.*;

public class APIController {

    // SEE https://sparkjava.com/documentation#path-groups FOR HELP

    // thread safe - no DB interaction
    static Validator v = App.getInstance().getValidator();
    static emailController e = new emailController();

    // form sent from front-end to back-end to create host
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("createHost API endpoint recognized request \n");
        String basePage = request.host();
        DbConnection db = App.getInstance().getDbConnection();

        // collect form attributes, validate attributes
        String f_name = request.queryParams("hostFName");
        String l_name = request.queryParams("hostLName");
        String e_address = request.queryParams("hostEmail");
        String ip_address = request.ip();
        if (!v.nameIsValid(f_name)) response.redirect(basePage);
        if (!v.nameIsValid(l_name)) response.redirect(basePage);
        if (!v.eAddressIsValid(e_address)) response.redirect(basePage);

        // ensure email address is unique
        if (db.emailExists(e_address)) response.redirect(basePage);

        System.out.println("Notice: createHost fields collected and validated");
        Host host = db.createHost(f_name, l_name, ip_address, e_address);
        if (v.isHostValid(host)) {
            request.session(true);
            request.session().attribute("host", host);
            Map<String, Object> model = new HashMap<>();
            model.put("hostCode", host.getHostCode());
            return ViewUtil.render(request, model, "/velocity/get-code.vm");
        } else {
            System.out.println("Error: Host creation failed");
            response.redirect(basePage);
        }
        // (broken) send email containing host-code to new host
        //e.sendEmail(Email, "Resmodus: Here's your host code", hostCode);    
        return response;
    };

    // form sent by host to create an event
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("createEvent API endpoint recognized request \n");
        String basePage = request.host();
        
        response.redirect(basePage);
        return null;
    };

    // form sent by participant to join an event
    public static Route joinEvent = (Request request, Response response) -> {
        System.out.println("joinEvent API endpoint recognized request \n");
        String basePage = request.host();

        // Get form attributes, ensure attributes are valid
        String FName = request.queryParams("participantFName");
        String LName = request.queryParams("participantLName");
        String eventCode = request.queryParams("eventCode");
        if (!v.nameIsValid(FName)) return basePage;
        if (!v.nameIsValid(LName)) return basePage;
        if (!v.eventCodeIsValid(eventCode)) return basePage;

        // create Participant object in DB -> link to event

        // redirect participant to event

        response.redirect(basePage);
        return null;
    };

    // form sent by user to create participant
    public static Route createParticipant = (Request request, Response response) -> {
        System.out.println("createParticipant API endpoint recognized request \n");
        String basePage = request.host();

        response.redirect(basePage);
        return null;
    };

    // form sent by participant (in event) to create an instance of feedback
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("createFeedback API endpoint recognized request \n");
        String basePage = request.host();

        response.redirect(basePage);
        return null;
    };

    /** Login host to host homepage */
    public static Route hostLogin = (Request request, Response response) -> {

        DbConnection db = App.getInstance().getDbConnection();
        //initialise host
        Host host = null;
        //start session
        request.session(true);
        if (request.session().isNew()) {
            String hostCode = request.queryParams("hostCode");
            //validate input before interact with database
            System.out.println(hostCode);
            if (v.hostCodeIsValid(hostCode)) {
                host = db.getHostByCode(hostCode);
            }
            request.session().attribute("host", host);
        } else {
            host = request.session().attribute("host");
        }
        //return host homepage if host is found
        if (v.isHostValid(host)) {
            Map<String, Object> model = new HashMap<>();
            model.put("fName", host.getFName());
            model.put("lName", host.getLName());
            return ViewUtil.render(request, model, "/velocity/host-home.vm");
        }
        //return notfound if host is not found or hostcode is not valid
        System.out.println("Host not found!");
        return ViewUtil.notFound;
    };
}