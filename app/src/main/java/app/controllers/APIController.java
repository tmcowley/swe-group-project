package app.controllers;

import java.util.*;

import app.App;
import app.DbConnection;
import app.Validator;
import app.objects.*;
import app.util.ViewUtil;
import app.util.emailController;

import java.sql.SQLException;
import spark.*;
import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class APIController {

    // thread safe - no DB interaction
    static Validator v = new Validator();
    static emailController e = new emailController();

    // form sent from front-end to back-end to create host
    public static Route createHost = (Request request, Response response) -> {
        System.out.println("createHost API endpoint recognised request \n");
        Route basePage = HostLoginController.servePage;
        DbConnection db = App.getInstance().getDbConnection();
        if (db == null) {
            System.out.println("PANIK - db null");
            return basePage;
        }

        // collect form attributes, validate attributes
        String FName = request.queryParams("hostFName");
        String LName = request.queryParams("hostLName");
        String Email = request.queryParams("hostEmail");
        if (!v.nameIsValid(FName)) return basePage;
        if (!v.nameIsValid(LName)) return basePage;
        if (!v.eAddressIsValid(Email)) return basePage;

        System.out.println("Notice: createHost fields collected and validated");
        Host host = db.createHost(FName,LName,"192.168.1.1",Email);
        if (host == null) {
            System.out.println("PANIK");
            return basePage;
        }
        String HostCode = host.getHostCode();
        System.out.println("Notice: createHost Route Host created successfully");

        //TODO: get IP
        // 2. create Host, get Host, get Host code
        // 3. send host-code back somehow
        return basePage;
    };

    // form sent by host to create an event
    public static Route createEvent = (Request request, Response response) -> {
        System.out.println("createEvent API endpoint recognised request \n");
        Route basePage = null;
        
        return basePage;
    };

    // form sent by participant to join an event
    public static Route joinEvent = (Request request, Response response) -> {
        System.out.println("joinEvent API endpoint recognised request \n");
        Route basePage = IndexController.servePage;

        // Get form attributes, ensure attributes are valid
        String FName = request.queryParams("participantFName");
        String LName = request.queryParams("participantLName");
        String eventCode = request.queryParams("eventCode");
        if (!v.nameIsValid(FName)) return basePage;
        if (!v.nameIsValid(LName)) return basePage;
        if (!v.eventCodeIsValid(eventCode)) return basePage;

        // create Participant object in DB -> link to event

        // redirect participant to event

        return basePage;
    };

    // form sent by user to create participant
    public static Route createParticipant = (Request request, Response response) -> {
        System.out.println("createParticipant API endpoint recognised request \n");
        Route basePage = null;

        return basePage;
    };

    // form sent by participant (in event) to create an instance of feedback
    public static Route createFeedback = (Request request, Response response) -> {
        System.out.println("createFeedback API endpoint recognised request \n");
        Route basePage = null;

        return basePage;
    };
}